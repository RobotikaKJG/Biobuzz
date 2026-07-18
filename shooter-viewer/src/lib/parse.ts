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
    else if (typeof obj.t === 'number') {
      const sample = obj as Sample
      // i1/i2 were added in log v2. Keep them optional so v1 JSONL continues
      // through the exact same analysis path without synthetic zero-current data.
      if (typeof sample.i1 !== 'number' || !Number.isFinite(sample.i1)) delete sample.i1
      if (typeof sample.i2 !== 'number' || !Number.isFinite(sample.i2)) delete sample.i2
      samples.push(sample)
    }
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
