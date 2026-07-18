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
import { DEFAULT_TICKS_PER_REV, ticksToRpm } from './lib/types'

const DEFAULT_HOST = '192.168.43.1:8765'

export default function App() {
  const [status, setStatus] = useState<ConnStatus>('disconnected')
  const [robotSessions, setRobotSessions] = useState<SessionInfo[]>([])
  const [loadedSession, setLoadedSession] = useState<Session | null>(null)
  const [activeTab, setActiveTab] = useState<'live' | 'diagnose'>('live')
  const [diagnoseLive, setDiagnoseLive] = useState(true)
  const [liveVersion, setLiveVersion] = useState(0)
  const [follow, setFollow] = useState(true)
  const [showBattery, setShowBattery] = useState(false)
  const [ticksPerRevOverride, setTicksPerRevOverride] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [wsError, setWsError] = useState<string | null>(null)

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
        if (s === 'connected' || s === 'live') void refreshSessions()
      },
      onHeader: (header: SessionHeader) => {
        liveRef.current = {
          name: `live: ${header.opMode}`,
          header,
          samples: [],
        }
        setActiveTab('live')
        setDiagnoseLive(true)
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
      onError: (msg) => setWsError(msg || null),
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
        setDiagnoseLive(false)
        setActiveTab('diagnose')
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
      setDiagnoseLive(false)
      setActiveTab('diagnose')
      setFollow(false)
    })
  }, [])

  const loadDemo = useCallback(async () => {
    const res = await fetch('/demo.jsonl')
    setLoadedSession(parseJsonl('demo.jsonl', await res.text()))
    setDiagnoseLive(false)
    setActiveTab('diagnose')
    setFollow(false)
  }, [])

  const liveHasData = liveRef.current.samples.length > 0
  const liveSession = liveHasData ? liveRef.current : null
  const session: Session | null =
    activeTab === 'live' ? liveSession : diagnoseLive ? liveSession : loadedSession
  const samples = session?.samples ?? []
  const sessionVersion = session === liveRef.current ? liveVersion : 0
  const ticksPerRev =
    ticksPerRevOverride ?? session?.header?.ticksPerRev ?? DEFAULT_TICKS_PER_REV

  const bursts = useMemo(
    () => (activeTab === 'diagnose' ? analyzeSession(samples) : []),
    // sessionVersion forces recompute while the live array is mutated in place
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [activeTab, samples, sessionVersion],
  )

  const latest = samples.length ? samples[samples.length - 1] : null
  const latestTarget = (() => {
    for (let i = samples.length - 1; i >= 0; i--) {
      if (typeof samples[i].tg === 'number') return samples[i].tg!
    }
    return null
  })()
  /** Localizer goal range (inches); carry last known when sample omits `d`. */
  const latestDistanceIn = (() => {
    for (let i = samples.length - 1; i >= 0; i--) {
      if (typeof samples[i].d === 'number' && Number.isFinite(samples[i].d)) return samples[i].d!
    }
    return null
  })()
  const hasCurrent = samples.some(
    (sample) => typeof sample.i1 === 'number' || typeof sample.i2 === 'number',
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

  return (
    <div className="app">
      <header className="app-header">
        <div className="app-header-row">
          <h1>Shooter Telemetry</h1>
          <nav className="view-tabs" aria-label="Telemetry view">
            <button
              className={activeTab === 'live' ? 'active' : ''}
              onClick={() => setActiveTab('live')}
            >
              Live
            </button>
            <button
              className={activeTab === 'diagnose' ? 'active' : ''}
              onClick={() => setActiveTab('diagnose')}
            >
              Diagnose
            </button>
          </nav>
        </div>
        <ConnectionBar
          status={status}
          defaultHost={DEFAULT_HOST}
          wsError={wsError}
          onWsConnect={(host) => connRef.current?.connect(host)}
          onWsDisconnect={() => connRef.current?.disconnect()}
        />
      </header>

      {error && <div className="error-bar">{error}</div>}

      <div className={`body ${activeTab}`}>
        {activeTab === 'diagnose' && <aside>
          {liveHasData && (
            <button
              className={`live-session-btn ${diagnoseLive ? 'selected' : ''}`}
              onClick={() => {
                setDiagnoseLive(true)
              }}
            >
              ● {liveRef.current.name} ({liveRef.current.samples.length} samples)
            </button>
          )}
          <SessionList
            sessions={robotSessions}
            selected={!diagnoseLive && loadedSession ? loadedSession.name : null}
            canFetch={status === 'connected' || status === 'live'}
            onRefresh={() => void refreshSessions()}
            onOpen={(name) => void openRobotSession(name)}
            onOpenFiles={openFiles}
            onLoadDemo={() => void loadDemo()}
          />
        </aside>}

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
                {activeTab === 'live' && (
                  <label>
                    <input
                      type="checkbox"
                      checked={follow}
                      onChange={(e) => setFollow(e.target.checked)}
                    />
                    follow
                  </label>
                )}
                {activeTab === 'diagnose' && <label>
                  <input
                    type="checkbox"
                    checked={showBattery}
                    onChange={(e) => setShowBattery(e.target.checked)}
                  />
                  battery
                </label>}
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
              {latest && (
                <div className="live-readouts">
                  <div>
                    <span>RPM 1</span>
                    <strong>{ticksToRpm(latest.v1, ticksPerRev).toFixed(0)}</strong>
                  </div>
                  <div>
                    <span>RPM 2</span>
                    <strong>{ticksToRpm(latest.v2, ticksPerRev).toFixed(0)}</strong>
                  </div>
                  <div>
                    <span>Target</span>
                    <strong>
                      {latestTarget === null ? '—' : ticksToRpm(latestTarget, ticksPerRev).toFixed(0)}
                    </strong>
                  </div>
                  <div>
                    <span>Distance</span>
                    <strong>
                      {latestDistanceIn === null
                        ? '—'
                        : `${(latestDistanceIn * 2.54).toFixed(0)} cm`}
                    </strong>
                  </div>
                  {activeTab === 'live' && (
                    <div>
                      <span>Δ (1−2)</span>
                      <strong>
                        {(() => {
                          const d =
                            ticksToRpm(latest.v1, ticksPerRev) -
                            ticksToRpm(latest.v2, ticksPerRev)
                          return `${d >= 0 ? '+' : ''}${d.toFixed(0)}`
                        })()}
                      </strong>
                    </div>
                  )}
                  {hasCurrent && (
                    <div>
                      <span>Motor current</span>
                      <strong>
                        {typeof latest.i1 === 'number' ? latest.i1.toFixed(1) : '—'} /{' '}
                        {typeof latest.i2 === 'number' ? latest.i2.toFixed(1) : '—'} A
                      </strong>
                    </div>
                  )}
                  {activeTab === 'live' && (
                    <div>
                      <span>Samples</span>
                      <strong>{samples.length}</strong>
                    </div>
                  )}
                </div>
              )}
              <ChartView
                samples={samples}
                ticksPerRev={ticksPerRev}
                bursts={bursts}
                variant={activeTab}
                showCurrent={activeTab === 'diagnose' && hasCurrent}
                showBattery={activeTab === 'diagnose' && showBattery}
                follow={activeTab === 'live' && follow && status === 'live'}
                followWindowSec={25}
                dataVersion={sessionVersion}
                onUserZoom={() => setFollow(false)}
              />
              <div className="chart-hint">
                drag = zoom · wheel = zoom at cursor · double-click = reset
              </div>
              {activeTab === 'diagnose' && (
                <ShotStats bursts={bursts} ticksPerRev={ticksPerRev} />
              )}
            </>
          ) : (
            <div className="placeholder">
              {activeTab === 'live' ? (
                <p>
                  Connect to the Hub and start an OpMode. The rolling 25-second RPM graph will
                  appear as soon as telemetry arrives.
                </p>
              ) : (
                <>
                  <p>Choose a live or saved session from the sidebar to analyze shots.</p>
                  <p>
                    Robot logs: <code>/sdcard/FIRST/shooter-logs/</code>.
                  </p>
                </>
              )}
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
