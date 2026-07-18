import type { Sample, SessionHeader, SessionInfo } from './types'

export type ConnStatus = 'disconnected' | 'connecting' | 'connected' | 'live'

export interface RobotCallbacks {
  onStatus: (status: ConnStatus) => void
  onHeader: (header: SessionHeader) => void
  onSamples: (samples: Sample[]) => void
  onSessionEnd: () => void
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
  host = ''
  status: ConnStatus = 'disconnected'

  constructor(private cb: RobotCallbacks) {}

  connect(host: string) {
    this.host = host
    this.wantConnect = true
    this.backoffMs = 1000
    this.open()
  }

  disconnect() {
    this.wantConnect = false
    if (this.retryTimer) clearTimeout(this.retryTimer)
    this.retryTimer = null
    this.ws?.close()
    this.ws = null
    this.setStatus('disconnected')
  }

  private setStatus(s: ConnStatus) {
    if (this.status !== s) {
      this.status = s
      this.cb.onStatus(s)
    }
  }

  private scheduleRetry() {
    if (!this.wantConnect || this.retryTimer) return
    this.retryTimer = setTimeout(() => {
      this.retryTimer = null
      this.open()
    }, this.backoffMs)
    this.backoffMs = Math.min(this.backoffMs * 1.5, 5000)
  }

  private open() {
    if (!this.wantConnect) return
    this.setStatus('connecting')
    let ws: WebSocket
    try {
      ws = new WebSocket(`ws://${this.host}/ws`)
    } catch {
      this.scheduleRetry()
      return
    }
    this.ws = ws

    ws.onopen = () => {
      this.backoffMs = 1000
      this.setStatus('connected')
    }
    ws.onclose = () => {
      if (this.ws === ws) {
        this.ws = null
        this.setStatus(this.wantConnect ? 'connecting' : 'disconnected')
        this.scheduleRetry()
      }
    }
    ws.onerror = () => ws.close()
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
