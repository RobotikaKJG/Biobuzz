import { useState } from 'react'
import type { ConnStatus } from '../lib/robot'

interface Props {
  status: ConnStatus
  defaultHost: string
  onConnect: (host: string) => void
  onDisconnect: () => void
}

const LABEL: Record<ConnStatus, string> = {
  disconnected: 'disconnected',
  connecting: 'connecting…',
  connected: 'connected (idle)',
  live: 'LIVE',
}

export default function ConnectionBar({ status, defaultHost, onConnect, onDisconnect }: Props) {
  const [host, setHost] = useState(defaultHost)
  const connected = status !== 'disconnected'

  return (
    <div className="connection-bar">
      <span className={`status-dot ${status}`} title={LABEL[status]} />
      <span className={`status-label ${status}`}>{LABEL[status]}</span>
      <input
        value={host}
        onChange={(e) => setHost(e.target.value)}
        disabled={connected}
        placeholder="192.168.43.1:8765"
        spellCheck={false}
      />
      {connected ? (
        <button onClick={onDisconnect}>Disconnect</button>
      ) : (
        <button className="primary" onClick={() => onConnect(host.trim())}>
          Connect
        </button>
      )}
    </div>
  )
}
