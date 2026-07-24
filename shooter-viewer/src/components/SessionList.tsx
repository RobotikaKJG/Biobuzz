import type { SessionInfo } from '../lib/types'

interface Props {
  sessions: SessionInfo[]
  selected: string | null
  canFetch: boolean
  onRefresh: () => void
  onOpen: (name: string) => void
  onOpenFiles: (files: FileList) => void
  onLoadDemo: () => void
}

function fmtSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

function fmtTime(ms: number): string {
  const d = new Date(ms)
  return d.toLocaleString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default function SessionList({
  sessions,
  selected,
  canFetch,
  onRefresh,
  onOpen,
  onOpenFiles,
  onLoadDemo,
}: Props) {
  return (
    <div
      className="session-list"
      onDragOver={(e) => e.preventDefault()}
      onDrop={(e) => {
        e.preventDefault()
        if (e.dataTransfer.files.length) onOpenFiles(e.dataTransfer.files)
      }}
    >
      <div className="session-list-header">
        <span>Sessions on robot</span>
        <button onClick={onRefresh} disabled={!canFetch} title="Refresh list from robot">
          ↻
        </button>
      </div>
      {sessions.length === 0 && (
        <div className="session-list-empty">
          {canFetch ? 'No sessions on the robot yet.' : 'Connect to the robot to list sessions.'}
        </div>
      )}
      <ul>
        {sessions.map((s) => (
          <li
            key={s.name}
            className={selected === s.name ? 'selected' : ''}
            onClick={() => onOpen(s.name)}
            title={s.name}
          >
            <span className="session-name">{s.name.replace(/\.jsonl$/, '')}</span>
            <span className="session-meta">
              {fmtTime(s.mtimeMs)} · {fmtSize(s.size)}
            </span>
          </li>
        ))}
      </ul>
      <div className="session-list-footer">
        <label className="file-btn">
          Open .jsonl file…
          <input
            type="file"
            accept=".jsonl,.json,.txt"
            multiple={false}
            onChange={(e) => {
              if (e.target.files?.length) onOpenFiles(e.target.files)
              e.target.value = ''
            }}
          />
        </label>
        <button onClick={onLoadDemo}>Load demo data</button>
        <div className="hint">…or drag &amp; drop a log file anywhere here</div>
      </div>
    </div>
  )
}
