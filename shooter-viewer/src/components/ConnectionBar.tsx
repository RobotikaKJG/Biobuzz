import { useCallback, useEffect, useState } from 'react'
import ConnectConsole from './ConnectConsole'
import { useSSE } from '../hooks/useSSE'
import type { ConnStatus } from '../lib/robot'

export type ConnectPhase = 'idle' | 'wifi' | 'ws' | 'connected' | 'disconnecting'

interface NetStatus {
  onHubSubnet?: boolean
  ip?: string
  ssid?: string
  hubReachable?: boolean
  telemetryOk?: boolean
  hub?: string
}

interface Props {
  status: ConnStatus
  defaultHost: string
  wsError: string | null
  onWsConnect: (host: string) => void
  onWsDisconnect: () => void
}

const STATUS_LABEL: Record<ConnStatus, string> = {
  disconnected: 'disconnected',
  connecting: 'connecting…',
  connected: 'connected (idle)',
  live: 'LIVE',
}

/**
 * ftc-decode-style connect — always runs hub-live.sh, which auto-skips WiFi
 * when already on 192.168.43.x (no manual checkbox).
 */
export default function ConnectionBar({
  status,
  defaultHost,
  wsError,
  onWsConnect,
  onWsDisconnect,
}: Props) {
  const [host, setHost] = useState(defaultHost)
  const [phase, setPhase] = useState<ConnectPhase>('idle')
  const [localError, setLocalError] = useState<string | null>(null)
  const [net, setNet] = useState<NetStatus | null>(null)
  const [consoleOpen, setConsoleOpen] = useState(false)
  const sse = useSSE()

  const busy = phase === 'wifi' || phase === 'ws' || phase === 'disconnecting'
  const linked = phase === 'connected' || status === 'connected' || status === 'live'
  const isLocalHost =
    host.trim().startsWith('localhost') || host.trim().startsWith('127.0.0.1')

  const refreshNet = useCallback(async () => {
    try {
      const res = await fetch('/api/net-status', { signal: AbortSignal.timeout(10000) })
      if (!res.ok) return
      setNet(await res.json())
    } catch {
      /* ignore — API may be restarting */
    }
  }, [])

  useEffect(() => {
    void refreshNet()
    const id = setInterval(() => {
      if (!busy && !linked) void refreshNet()
    }, 5000)
    return () => clearInterval(id)
  }, [refreshNet, busy, linked])

  const handleConnect = useCallback(() => {
    setLocalError(null)
    const h = host.trim()
    if (!h) {
      setLocalError('Enter host (e.g. 192.168.43.1:8765)')
      return
    }

    // Mock robot only — skip WiFi orchestration.
    if (isLocalHost) {
      setPhase('ws')
      onWsConnect(h)
      return
    }

    // One button: UI backend runs hub-live (WiFi/ping/:8765), then we open WS.
    // You never run hub-live.sh yourself.
    setPhase('wifi')
    setConsoleOpen(true)
    sse.clear()
    void (async () => {
      try {
        const health = await fetch('/api/health', { signal: AbortSignal.timeout(2000) })
        if (!health.ok) throw new Error(`HTTP ${health.status}`)
      } catch {
        setLocalError(
          'Viewer backend not running. Start with: cd shooter-viewer && npm run dev',
        )
        setPhase('idle')
        return
      }
      sse.start('/api/live-connect')
    })()
  }, [host, isLocalHost, onWsConnect, sse])

  const handleDisconnect = useCallback(() => {
    setPhase('disconnecting')
    onWsDisconnect()
    setLocalError(null)
    if (isLocalHost) {
      setPhase('idle')
      return
    }
    // hub-live disconnect restores WiFi only if connect actually switched.
    sse.clear()
    sse.start('/api/live-disconnect')
  }, [isLocalHost, onWsDisconnect, sse])

  // hub-live finished → open WS (script already probed :8765)
  useEffect(() => {
    if (phase !== 'wifi' || sse.running || sse.exitCode === null) return
    if (sse.exitCode !== 0) {
      setLocalError(`Connect failed (exit ${sse.exitCode}). See console.`)
      setPhase('idle')
      void refreshNet()
      return
    }
    setPhase('ws')
    void refreshNet()
    onWsConnect(host.trim())
  }, [phase, sse.running, sse.exitCode, host, onWsConnect, refreshNet])

  useEffect(() => {
    if (phase === 'ws' && (status === 'connected' || status === 'live')) {
      setPhase('connected')
      setLocalError(null)
      setConsoleOpen(false)
    }
  }, [phase, status])

  useEffect(() => {
    if (phase === 'disconnecting' && !sse.running) {
      setPhase('idle')
      void refreshNet()
    }
  }, [phase, sse.running, refreshNet])

  const err = localError || (wsError && wsError.length ? wsError : null)
  const phaseLabel =
    phase === 'wifi'
      ? net?.onHubSubnet
        ? 'checking hub…'
        : 'switching WiFi…'
      : phase === 'ws'
        ? 'opening WebSocket…'
        : phase === 'disconnecting'
          ? 'disconnecting…'
          : STATUS_LABEL[status]

  const netBadge = (() => {
    if (!net || isLocalHost) return null
    if (net.telemetryOk) return { text: 'hub + telemetry', cls: 'ok' }
    if (net.hubReachable) return { text: 'hub reachable (start OpMode)', cls: 'warn' }
    if (net.onHubSubnet) return { text: `on hub WiFi (${net.ip})`, cls: 'warn' }
    return { text: 'not on robot WiFi', cls: 'muted' }
  })()

  return (
    <div className="connection-panel">
      <div className="connection-bar">
        <span className={`status-dot ${status === 'disconnected' && busy ? 'connecting' : status}`} />
        <span className={`status-label ${status === 'live' ? 'live' : ''}`}>{phaseLabel}</span>
        {netBadge && <span className={`net-badge ${netBadge.cls}`}>{netBadge.text}</span>}
        <input
          value={host}
          onChange={(e) => setHost(e.target.value)}
          disabled={linked || busy}
          placeholder="192.168.43.1:8765"
          spellCheck={false}
        />
        {!linked && !busy ? (
          <button className="primary" onClick={handleConnect}>
            Connect to Hub
          </button>
        ) : (
          <button onClick={handleDisconnect} disabled={phase === 'disconnecting'}>
            Disconnect
          </button>
        )}
      </div>
      {err && <div className="connection-error">{err}</div>}
      {sse.lines.length > 0 && (
        <button className="console-toggle" onClick={() => setConsoleOpen((open) => !open)}>
          {consoleOpen ? 'Hide' : 'Show'} connect log ({sse.lines.length})
        </button>
      )}
      {consoleOpen && <ConnectConsole lines={sse.lines} />}
    </div>
  )
}
