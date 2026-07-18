import { useEffect, useRef, useState } from 'react'
import type { ConnStatus } from '../lib/robot'
import {
  ACTION_KEY_LABELS,
  CONTROL_HELP,
  DRIVE_KEYCAPS,
  EMPTY_BUTTONS,
  KEY_BINDINGS,
  buildCmd,
  type RemoteButtons,
  type RemoteCmd,
} from '../lib/remoteCmd'

type Props = {
  status: ConnStatus
  canSend: boolean
  onSend: (cmd: RemoteCmd) => boolean
  robotState: {
    shooting: boolean
    shooterSpinning: boolean
    intakeRunning: boolean
    transferRunning: boolean
    turretTracking: boolean
  }
}

const SEND_HZ = 20
const UI_HZ = 10

const ZERO_CMD: RemoteCmd = {
  type: 'cmd',
  drive: { lx: 0, ly: 0, rx: 0 },
  buttons: { ...EMPTY_BUTTONS },
}

function cmdChanged(a: RemoteCmd | null, b: RemoteCmd): boolean {
  if (!a) return true
  if (a.drive.lx !== b.drive.lx || a.drive.ly !== b.drive.ly || a.drive.rx !== b.drive.rx) {
    return true
  }
  for (const k of Object.keys(EMPTY_BUTTONS) as (keyof RemoteButtons)[]) {
    if (a.buttons[k] !== b.buttons[k]) return true
  }
  return false
}

export default function ControlPanel({ status, canSend, onSend, robotState }: Props) {
  /** Safety gate: keyboard commands only sent while enabled (separate from Hub connect). */
  const [armed, setArmed] = useState(false)
  const [focused, setFocused] = useState(false)
  const [held, setHeld] = useState<string[]>([])
  const [lastSent, setLastSent] = useState<RemoteCmd | null>(null)
  const [showMap, setShowMap] = useState(false)
  const [sendFault, setSendFault] = useState(false)

  const padRef = useRef<HTMLDivElement>(null)
  const heldRef = useRef(new Set<string>())
  const armedRef = useRef(false)
  const canSendRef = useRef(canSend)
  const onSendRef = useRef(onSend)
  const lastSentRef = useRef<RemoteCmd | null>(null)
  const lastUiMsRef = useRef(0)
  const failedSendsRef = useRef(0)
  const sendFaultRef = useRef(false)

  armedRef.current = armed
  canSendRef.current = canSend
  onSendRef.current = onSend

  const setSendFaultValue = (value: boolean) => {
    if (sendFaultRef.current === value) return
    sendFaultRef.current = value
    setSendFault(value)
  }

  const recordSend = (ok: boolean) => {
    if (ok) {
      failedSendsRef.current = 0
      setSendFaultValue(false)
      return
    }
    failedSendsRef.current++
    if (failedSendsRef.current >= 3) setSendFaultValue(true)
  }

  const flush = (opts?: { forceUi?: boolean; zero?: boolean }) => {
    const cmd = opts?.zero ? ZERO_CMD : buildCmd(heldRef.current)
    // Always try to send while armed (or when explicitly zeroing). Do not gate on
    // React `status` flicker — onSend already checks the WebSocket.
    if (armedRef.current || opts?.zero) {
      recordSend(onSendRef.current(cmd))
    }
    const now = performance.now()
    const dueUi = opts?.forceUi || now - lastUiMsRef.current >= 1000 / UI_HZ
    if (dueUi && cmdChanged(lastSentRef.current, cmd)) {
      lastSentRef.current = cmd
      lastUiMsRef.current = now
      setLastSent(cmd)
    }
  }

  const stopAll = () => {
    heldRef.current.clear()
    setHeld([])
    lastSentRef.current = ZERO_CMD
    setLastSent(ZERO_CMD)
    recordSend(onSendRef.current(ZERO_CMD))
  }

  // Steady command stream while armed. Cleanup must NOT send zero — that was
  // racing with holds whenever the effect restarted and produced 5cm "pulses".
  useEffect(() => {
    if (!armed) return
    const id = window.setInterval(() => {
      if (!armedRef.current) return
      flush()
    }, 1000 / SEND_HZ)
    return () => window.clearInterval(id)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [armed])

  // Zero once when disarming / unmounting.
  useEffect(() => {
    if (!armed) {
      recordSend(onSendRef.current(ZERO_CMD))
      lastSentRef.current = ZERO_CMD
      setLastSent(ZERO_CMD)
    }
  }, [armed])

  useEffect(() => {
    if (!armed) return

    const isTypingTarget = (t: EventTarget | null) => {
      const el = t as HTMLElement | null
      if (!el) return false
      const tag = el.tagName
      return tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT' || el.isContentEditable
    }

    const onKeyDown = (e: KeyboardEvent) => {
      if (isTypingTarget(e.target)) return
      if (!(e.code in KEY_BINDINGS)) return
      // Ignore OS key-repeat events for set membership, but keep streaming via interval.
      if (e.repeat) {
        e.preventDefault()
        return
      }
      e.preventDefault()
      heldRef.current.add(e.code)
      setHeld([...heldRef.current])
      flush({ forceUi: true }) // immediate — don't wait for next interval tick
    }

    const onKeyUp = (e: KeyboardEvent) => {
      if (!(e.code in KEY_BINDINGS)) return
      // Only clear keys we actually tracked — avoids spurious keyups wiping state.
      if (!heldRef.current.has(e.code)) return
      e.preventDefault()
      heldRef.current.delete(e.code)
      setHeld([...heldRef.current])
      flush({ forceUi: true })
    }

    // window "blur" fires too easily (charts, OS UI) and was clearing holds mid-drive.
    // Only stop when the tab is actually hidden.
    const onVis = () => {
      if (document.visibilityState === 'hidden') stopAll()
    }

    window.addEventListener('keydown', onKeyDown, { capture: true })
    window.addEventListener('keyup', onKeyUp, { capture: true })
    document.addEventListener('visibilitychange', onVis)
    return () => {
      window.removeEventListener('keydown', onKeyDown, { capture: true })
      window.removeEventListener('keyup', onKeyUp, { capture: true })
      document.removeEventListener('visibilitychange', onVis)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [armed])

  const pulseButton = (button: keyof RemoteButtons) => {
    if (!canSend || !armed) return
    const code = Object.entries(KEY_BINDINGS).find(
      ([, a]) => a.kind === 'button' && a.button === button,
    )?.[0]
    if (!code) return
    heldRef.current.add(code)
    setHeld([...heldRef.current])
    flush({ forceUi: true })
    window.setTimeout(() => {
      heldRef.current.delete(code)
      setHeld([...heldRef.current])
      flush({ forceUi: true })
    }, 180)
  }

  const drive = lastSent?.drive
  const live = status === 'live' || status === 'connected'
  const isHeld = (codes: string[]) => codes.some((c) => held.includes(c))

  return (
    <div className="control-panel">
      <div className="control-toolbar">
        <label
          className={`arm-toggle ${armed ? 'armed' : ''}`}
          title="Enable keyboard driving after you Connect to Hub above. This does not connect — it only allows keys to move the robot."
        >
          <input
            type="checkbox"
            checked={armed}
            disabled={!canSend}
            onChange={(e) => {
              const next = e.target.checked
              setArmed(next)
              if (!next) stopAll()
              else {
                padRef.current?.focus()
                flush({ forceUi: true })
              }
            }}
          />
          {armed ? 'Keyboard on' : 'Enable keyboard'}
        </label>
        <span className={`control-status ${sendFault ? 'fault' : ''}`}>
          {sendFault
            ? 'Command link lost — reconnect'
            : !live
            ? 'Connect to Hub first'
            : status !== 'live'
              ? 'Start a TeleOp'
              : armed
                ? 'Keys active'
                : 'Hub linked — enable keyboard to drive'}
        </span>
      </div>

      <div className="subsystem-lights" aria-label="Robot subsystem states">
        <StateLight label="Shooter" on={robotState.shooterSpinning} />
        <StateLight label="Shooting" on={robotState.shooting} />
        <StateLight label="Intake" on={robotState.intakeRunning} />
        <StateLight label="Transfer" on={robotState.transferRunning} />
        <StateLight label="Turret" on={robotState.turretTracking} />
      </div>

      <div
        ref={padRef}
        className={`control-pad ${armed ? 'armed' : ''} ${focused ? 'focused' : ''}`}
        tabIndex={armed ? 0 : -1}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        onClick={() => armed && padRef.current?.focus()}
      >
        <p className="control-hint">
          {armed
            ? focused
              ? 'Keys live · hover for tips'
              : 'Click pad to capture keys'
            : 'Enable keyboard, then click pad'}
        </p>

        <div className="key-cluster" aria-label="Drive keys">
          <div className="key-row">
            {DRIVE_KEYCAPS.slice(0, 3).map((k) => (
              <KeyCap key={k.code} {...k} active={held.includes(k.code)} />
            ))}
          </div>
          <div className="key-row">
            {DRIVE_KEYCAPS.slice(3).map((k) => (
              <KeyCap key={k.code} {...k} active={held.includes(k.code)} />
            ))}
          </div>
        </div>

        <div className="stick-readout" title="Stick values sent to the robot (−1…1).">
          <div className={Math.abs(drive?.lx ?? 0) > 0.05 ? 'lit' : ''}>
            <span>lx</span>
            <strong>{(drive?.lx ?? 0).toFixed(2)}</strong>
          </div>
          <div className={Math.abs(drive?.ly ?? 0) > 0.05 ? 'lit' : ''}>
            <span>ly</span>
            <strong>{(drive?.ly ?? 0).toFixed(2)}</strong>
          </div>
          <div className={Math.abs(drive?.rx ?? 0) > 0.05 ? 'lit' : ''}>
            <span>rx</span>
            <strong>{(drive?.rx ?? 0).toFixed(2)}</strong>
          </div>
        </div>

        <div className="action-buttons">
          {ACTION_KEY_LABELS.map(({ button, keys, label, tip, codes }) => {
            const active = isHeld(codes)
            const systemOn =
              button === 'leftBumper' ? robotState.shooting
              : button === 'rightTrigger' ? robotState.intakeRunning
              : button === 'leftTrigger' ? robotState.shooterSpinning
              : button === 'rightBumper' ? robotState.turretTracking
              : false
            return (
              <button
                key={button}
                type="button"
                className={`${active ? 'active pressed' : ''} ${systemOn ? 'system-on' : ''}`}
                disabled={!armed || !canSend}
                title={tip}
                aria-pressed={active}
                onClick={() => pulseButton(button)}
              >
                <span className="action-label">{label}</span>
                <kbd>{keys}</kbd>
              </button>
            )
          })}
        </div>
      </div>

      <button type="button" className="keymap-toggle" onClick={() => setShowMap((v) => !v)}>
        {showMap ? 'Hide key map' : 'Show key map'}
      </button>

      {showMap && (
        <table className="control-help">
          <tbody>
            {CONTROL_HELP.map((row) => {
              const active = isHeld(row.codes)
              return (
                <tr key={row.keys} className={active ? 'pressed' : ''} title={row.tip}>
                  <td>
                    <kbd className={active ? 'pressed' : ''}>{row.keys}</kbd>
                  </td>
                  <td>{row.action}</td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </div>
  )
}

function StateLight({ label, on }: { label: string; on: boolean }) {
  return (
    <span className={`subsystem-light ${on ? 'on' : ''}`}>
      <i aria-hidden="true" />
      {label}
    </span>
  )
}

function KeyCap({
  label,
  active,
  sub,
  tip,
}: {
  label: string
  active: boolean
  sub: string
  tip: string
}) {
  return (
    <div
      className={`keycap ${active ? 'active pressed' : ''}`}
      title={tip}
      aria-pressed={active}
    >
      <span className="keycap-main">{label}</span>
      <span className="keycap-sub">{sub}</span>
    </div>
  )
}
