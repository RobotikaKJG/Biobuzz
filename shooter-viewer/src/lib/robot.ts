import type { Sample, SessionHeader, SessionInfo } from './types'

export type ConnStatus = 'disconnected' | 'connecting' | 'connected' | 'live'

export interface RobotCallbacks {
  onStatus: (status: ConnStatus) => void
  onHeader: (header: SessionHeader) => void
  onSamples: (samples: Sample[]) => void
  onSessionEnd: () => void
  onError?: (message: string) => void
}

/**
 * WebSocket client for the robot's ShooterTelemetryServer with auto-reconnect.
 * Status: connecting -> connected (server reachable, no OpMode) -> live (session streaming).
 */
export class RobotConnection {
  private ws: WebSocket | null = null
  private retryTimer: ReturnType<typeof setTimeout> | null = null
  private backoffMs = 1000
  private wantConnect = false
  private intentionalClose = false
  host = ''
  status: ConnStatus = 'disconnected'
  lastError: string | null = null

  constructor(private cb: RobotCallbacks) {}

  connect(host: string) {
    this.host = host.replace(/^https?:\/\//, '').replace(/\/$/, '')
    this.wantConnect = true
    this.intentionalClose = false
    this.backoffMs = 1000
    this.lastError = null
    this.open()
  }

  disconnect() {
    this.wantConnect = false
    this.intentionalClose = true
    if (this.retryTimer) clearTimeout(this.retryTimer)
    this.retryTimer = null
    if (this.ws) {
      this.ws.onopen = null
      this.ws.onclose = null
      this.ws.onerror = null
      this.ws.onmessage = null
      this.ws.close()
      this.ws = null
    }
    this.setStatus('disconnected')
  }

  private setStatus(s: ConnStatus) {
    if (this.status !== s) {
      this.status = s
      this.cb.onStatus(s)
    }
  }

  private reportError(msg: string) {
    this.lastError = msg
    this.cb.onError?.(msg)
  }

  private scheduleRetry() {
    if (!this.wantConnect || this.intentionalClose || this.retryTimer) return
    this.retryTimer = setTimeout(() => {
      this.retryTimer = null
      this.open()
    }, this.backoffMs)
    this.backoffMs = Math.min(this.backoffMs * 1.5, 8000)
  }

  private open() {
    if (!this.wantConnect) return
    this.setStatus('connecting')
    const url = `ws://${this.host}/ws`
    let ws: WebSocket
    try {
      ws = new WebSocket(url)
    } catch (e) {
      this.reportError(`Failed to open ${url}: ${e}`)
      this.scheduleRetry()
      return
    }
    this.ws = ws

    ws.onopen = () => {
      this.backoffMs = 1000
      this.lastError = null
      this.cb.onError?.('') // clear
      this.setStatus('connected')
    }
    ws.onclose = (ev) => {
      if (this.ws === ws) {
        this.ws = null
        if (!this.intentionalClose && this.wantConnect) {
          this.reportError(
            `WebSocket closed (code ${ev.code}${ev.reason ? `: ${ev.reason}` : ''}) — retrying ${url}`,
          )
          this.setStatus('connecting')
          this.scheduleRetry()
        } else {
          this.setStatus('disconnected')
        }
      }
    }
    ws.onerror = () => {
      // browsers hide the real reason; onclose follows with a code
      this.reportError(
        `WebSocket error connecting to ${url}. On robot WiFi? OpMode started once? Server on :8765?`,
      )
    }
    ws.onmessage = (ev) => {
      let msg: any
      try {
        msg = JSON.parse(ev.data as string)
      } catch {
        return
      }
      switch (msg.type) {
        case 'hello':
          this.setStatus(msg.opModeRunning ? 'live' : 'connected')
          break
        case 'header':
          this.setStatus('live')
          this.cb.onHeader(msg as SessionHeader)
          break
        case 'batch':
          this.setStatus('live')
          if (Array.isArray(msg.samples) && msg.samples.length) this.cb.onSamples(msg.samples)
          break
        case 'sessionEnd':
          this.setStatus('connected')
          this.cb.onSessionEnd()
          break
      }
    }
  }
}

export async function fetchSessions(host: string): Promise<SessionInfo[]> {
  const res = await fetch(`http://${host}/api/sessions`)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.json()
}

export async function fetchSessionFile(host: string, name: string): Promise<string> {
  const res = await fetch(`http://${host}/api/sessions/${encodeURIComponent(name)}`)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.text()
}

/** Quick HTTP probe before opening WS (surfaces clearer errors than bare WS). */
export async function probeRobot(host: string): Promise<{ ok: boolean; detail: string }> {
  const h = host.replace(/^https?:\/\//, '').replace(/\/$/, '')
  try {
    const res = await fetch(`http://${h}/api/status`, { signal: AbortSignal.timeout(3000) })
    if (!res.ok) return { ok: false, detail: `HTTP ${res.status} from /api/status` }
    const body = await res.json()
    return {
      ok: true,
      detail: `status ok — opModeRunning=${body.opModeRunning} opMode=${body.opMode ?? 'none'}`,
    }
  } catch (e) {
    return { ok: false, detail: `Cannot reach http://${h}/api/status: ${e}` }
  }
}
