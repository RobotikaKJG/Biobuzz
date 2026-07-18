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
      // Optional fields (v2 currents, v3 drive/transfer/turret) — drop non-finite.
      for (const key of ['i1', 'i2', 'id0', 'id1', 'id2', 'id3', 'ii', 'it', 'ta'] as const) {
        const v = sample[key]
        if (typeof v !== 'number' || !Number.isFinite(v)) delete sample[key]
      }
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
