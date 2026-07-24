// Writes public/demo.jsonl — a synthetic session with 3 three-ball bursts.
// Run: npm run gen-demo
import { mkdirSync, writeFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { makeSession, toJsonl } from './synth.mjs'

const root = join(dirname(fileURLToPath(import.meta.url)), '..')
const { header, samples, expectedDrops } = makeSession('DemoTeleOp', Date.now(), 3)

mkdirSync(join(root, 'public'), { recursive: true })
writeFileSync(join(root, 'public', 'demo.jsonl'), toJsonl(header, samples))

console.log(`wrote public/demo.jsonl (${samples.length} samples)`)
console.log('expected per-burst drops (ticks/s):')
expectedDrops.forEach((d, i) =>
  console.log(`  burst ${i + 1}: ${d.map((x) => x.toFixed(1)).join(', ')}`),
)
console.log('(RPM = ticks/s / 28 * 60 — compare against the ShotStats table)')
