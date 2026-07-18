// Download all shooter-log sessions from the robot into ./logs/ over WiFi.
// Run:  npm run pull            (default host 192.168.43.1:8765)
//       npm run pull -- <host>  (e.g. npm run pull -- localhost:8765)
import { existsSync, mkdirSync, writeFileSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

const host = process.argv[2] ?? '192.168.43.1:8765'
const outDir = join(dirname(fileURLToPath(import.meta.url)), '..', 'logs')
mkdirSync(outDir, { recursive: true })

try {
  const res = await fetch(`http://${host}/api/sessions`)
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  const sessions = await res.json()
  if (sessions.length === 0) {
    console.log('no sessions on the robot')
    process.exit(0)
  }
  let pulled = 0
  for (const s of sessions) {
    const dest = join(outDir, s.name)
    if (existsSync(dest)) continue // already pulled
    const file = await fetch(`http://${host}/api/sessions/${encodeURIComponent(s.name)}`)
    if (!file.ok) {
      console.error(`  FAILED ${s.name}: HTTP ${file.status}`)
      continue
    }
    writeFileSync(dest, await file.text())
    console.log(`  pulled ${s.name} (${(s.size / 1024).toFixed(1)} KB)`)
    pulled++
  }
  console.log(`done — ${pulled} new, ${sessions.length - pulled} already local, in ${outDir}`)
} catch (e) {
  console.error(`Could not reach robot at ${host}: ${e.message ?? e}`)
  console.error('Are you on the robot WiFi? Is an OpMode-initialized RC app running?')
  process.exit(1)
}
