import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import ChartView from './components/ChartView'
import ConnectionBar from './components/ConnectionBar'
import SessionList from './components/SessionList'
import ShotStats from './components/ShotStats'
import { analyzeSession } from './lib/analysis'
import { parseJsonl, toJsonl } from './lib/parse'
import type { ConnStatus } from './lib/robot'
import { fetchSessionFile, fetchSessions, RobotConnection } from './lib/robot'
import type { Sample, Session, SessionHeader, SessionInfo } from './lib/types'
import { DEFAULT_TICKS_PER_REV } from './lib/types'

const DEFAULT_HOST = '192.168.43.1:8765'

export default function App() {
  const [status, setStatus] = useState<ConnStatus>('disconnected')
  const [robotSessions, setRobotSessions] = useState<SessionInfo[]>([])
  const [loadedSession, setLoadedSession] = useState<Session | null>(null)
  const [viewingLive, setViewingLive] = useState(false)
  const [liveVersion, setLiveVersion] = useState(0)
  const [follow, setFollow] = useState(true)
  const [showBattery, setShowBattery] = useState(false)
  const [ticksPerRevOverride, setTicksPerRevOverride] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  // Live session lives in a ref (mutated per batch); liveVersion triggers renders.
  const liveRef = useRef<Session>({ name: 'live', header: null, samples: [] })
  const connRef = useRef<RobotConnection | null>(null)

  const refreshSessions = useCallback(async (host?: string) => {
    const h = host ?? connRef.current?.host
    if (!h) return
    try {
      setRobotSessions(await fetchSessions(h))
      setError(null)
    } catch (e) {
      setError(`Failed to list sessions: ${e}`)
    }
  }, [])

  useEffect(() => {
    const conn = new RobotConnection({
      onStatus: (s) => {
        setStatus(s)
        if (s === 'connected') void refreshSessions()
      },
      onHeader: (header: SessionHeader) => {
        liveRef.current = {
          name: `live: ${header.opMode}`,
          header,
          samples: [],
        }
        setViewingLive(true)
        setFollow(true)
        setLiveVersion((v) => v + 1)
      },
      onSamples: (samples: Sample[]) => {
        // joined mid-session with no header yet — still record
        liveRef.current.samples.push(...samples)
        setLiveVersion((v) => v + 1)
      },
      onSessionEnd: () => {
        setLiveVersion((v) => v + 1)
        void refreshSessions() // the new file is now on disk
      },
    })
    connRef.current = conn
    return () => conn.disconnect()
  }, [refreshSessions])

  const openRobotSession = useCallback(
    async (name: string) => {
      const h = connRef.current?.host
      if (!h) return
      try {
        const text = await fetchSessionFile(h, name)
        setLoadedSession(parseJsonl(name, text))
        setViewingLive(false)
        setFollow(false)
        setError(null)
      } catch (e) {
        setError(`Failed to load ${name}: ${e}`)
      }
    },
    [],
  )

  const openFiles = useCallback((files: FileList) => {
    const file = files[0]
    void file.text().then((text) => {
      setLoadedSession(parseJsonl(file.name, text))
      setViewingLive(false)
      setFollow(false)
    })
  }, [])

  const loadDemo = useCallback(async () => {
    const res = await fetch('/demo.jsonl')
    setLoadedSession(parseJsonl('demo.jsonl', await res.text()))
    setViewingLive(false)
    setFollow(false)
  }, [])

  const session: Session | null = viewingLive ? liveRef.current : loadedSession
  const samples = session?.samples ?? []
  const ticksPerRev =
    ticksPerRevOverride ?? session?.header?.ticksPerRev ?? DEFAULT_TICKS_PER_REV

  const bursts = useMemo(
    () => analyzeSession(samples),
    // liveVersion forces recompute while the live array is mutated in place
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [samples, liveVersion],
  )

  const saveSession = useCallback(() => {
    if (!session) return
    const blob = new Blob([toJsonl(session)], { type: 'application/x-ndjson' })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = session.name.startsWith('live')
      ? `live_${new Date().toISOString().replace(/[:.]/g, '-')}.jsonl`
      : session.name
    a.click()
    URL.revokeObjectURL(a.href)
  }, [session])

  const liveHasData = liveRef.current.samples.length > 0

  return (
    <div className="app">
      <header>
        <h1>Shooter Telemetry</h1>
        <ConnectionBar
          status={status}
          defaultHost={DEFAULT_HOST}
          onConnect={(host) => connRef.current?.connect(host)}
          onDisconnect={() => connRef.current?.disconnect()}
        />
      </header>

      {error && <div className="error-bar">{error}</div>}

      <div className="body">
        <aside>
          {liveHasData && (
            <button
              className={`live-session-btn ${viewingLive ? 'selected' : ''}`}
              onClick={() => {
                setViewingLive(true)
                setFollow(status === 'live')
              }}
            >
              ● {liveRef.current.name} ({liveRef.current.samples.length} samples)
            </button>
          )}
          <SessionList
            sessions={robotSessions}
            selected={!viewingLive && loadedSession ? loadedSession.name : null}
            canFetch={status === 'connected' || status === 'live'}
            onRefresh={() => void refreshSessions()}
            onOpen={(name) => void openRobotSession(name)}
            onOpenFiles={openFiles}
            onLoadDemo={() => void loadDemo()}
          />
        </aside>

        <main>
          {session ? (
            <>
              <div className="toolbar">
                <span className="session-title">
                  {session.name}
                  {session.header && (
                    <span className="session-sub">
                      {' '}
                      — {session.header.opMode} · {session.header.alliance} ·{' '}
                      {new Date(session.header.epochMs).toLocaleString()}
                    </span>
                  )}
                </span>
                <span className="spacer" />
                {viewingLive && status === 'live' && (
                  <label>
                    <input
                      type="checkbox"
                      checked={follow}
                      onChange={(e) => setFollow(e.target.checked)}
                    />
                    follow
                  </label>
                )}
                <label>
                  <input
                    type="checkbox"
                    checked={showBattery}
                    onChange={(e) => setShowBattery(e.target.checked)}
                  />
                  battery
                </label>
                <label className="tpr">
                  ticks/rev
                  <input
                    type="number"
                    value={ticksPerRev}
                    min={1}
                    onChange={(e) => setTicksPerRevOverride(Number(e.target.value) || null)}
                  />
                </label>
                <button onClick={saveSession}>Save .jsonl</button>
              </div>
              <ChartView
                samples={samples}
                ticksPerRev={ticksPerRev}
                bursts={bursts}
                showBattery={showBattery}
                follow={viewingLive && follow && status === 'live'}
                followWindowSec={30}
                dataVersion={liveVersion}
                onUserZoom={() => setFollow(false)}
              />
              <div className="chart-hint">
                drag = zoom · wheel = zoom at cursor · double-click = reset
              </div>
              <ShotStats bursts={bursts} ticksPerRev={ticksPerRev} />
            </>
          ) : (
            <div className="placeholder">
              <p>
                Connect to the robot (default <code>{DEFAULT_HOST}</code> on robot WiFi) to stream
                live, or open a pulled <code>.jsonl</code> session file.
              </p>
              <p>
                Robot logs live in <code>/sdcard/FIRST/shooter-logs/</code> — fetch them here over
                WiFi via the session list, or <code>npm run pull</code>.
              </p>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
