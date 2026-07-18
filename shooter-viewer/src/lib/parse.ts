import type { Sample, Session, SessionHeader } from './types'

/** Parse a .jsonl session file (header line + sample lines). Bad lines are skipped. */
export function parseJsonl(name: string, text: string): Session {
  let header: SessionHeader | null = null
  const samples: Sample[] = []
  for (const line of text.split('\n')) {
    const trimmed = line.trim()
    if (!trimmed) continue
    let obj: any
    try {
      obj = JSON.parse(trimmed)
    } catch {
      continue // truncated tail line etc.
    }
    if (obj.type === 'header') header = obj as SessionHeader
    else if (typeof obj.t === 'number') samples.push(obj as Sample)
  }
  samples.sort((a, b) => a.t - b.t)
  return { name, header, samples }
}

/** Serialize a session back to JSONL (for "save live session"). */
export function toJsonl(session: Session): string {
  const lines: string[] = []
  if (session.header) lines.push(JSON.stringify(session.header))
  for (const s of session.samples) lines.push(JSON.stringify(s))
  return lines.join('\n') + '\n'
}
