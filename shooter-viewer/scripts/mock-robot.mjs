// Mock of the robot's ShooterTelemetryServer for developing/testing the viewer
// without hardware. Same HTTP + WS API on the same port (8765).
//
// Run: npm run mock-robot            (default port 8766)
//      npm run mock-robot -- 9000    (custom port)
// Then in the viewer connect to: localhost:8766
// (8766, not the robot's 8765, to avoid colliding with local tooling; the vite dev
//  proxy also targets 8766 so connecting the viewer to "localhost:5173" works too.)
//
// Behavior: keeps 3 pre-recorded synthetic sessions in /api/sessions; every ~6s of
// idle it "starts an OpMode" and live-streams a session (header -> 100ms batches ->
// sessionEnd), then adds it to the session list. Loops forever.
import { createServer } from 'node:http'
import { WebSocketServer } from 'ws'
import { makeBurst, makeHeader, makeIdle, toJsonl } from './synth.mjs'

const PORT = Number(process.env.PORT ?? process.argv[2] ?? 8766)

// --- pre-recorded sessions ---
const recorded = new Map() // name -> jsonl string
function record(name, epochMs, burstCount) {
  const header = makeHeader(name.replace(/^.*_(.*)\.jsonl$/, '$1'), epochMs)
  const all = []
  let t = 0
  for (let b = 0; b < burstCount; b++) {
    const idle = makeIdle(t, 2500)
    all.push(...idle.samples)
    t = idle.endMs
    const burst = makeBurst(t, 1750)
    all.push(...burst.samples)
    t = burst.endMs
  }
  recorded.set(name, toJsonl(header, all))
}
const now = Date.now()
record('2026-07-16_18-02-11_GeneralBlueTeleOp.jsonl', now - 2 * 86400e3, 4)
record('2026-07-17_17-45-03_GeneralRedTeleOp.jsonl', now - 1 * 86400e3, 3)
record('2026-07-17_18-10-42_ShooterDistanceTuningTeleOp.jsonl', now - 86000e3, 2)

// --- HTTP ---
const server = createServer((req, res) => {
  const cors = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, OPTIONS',
    'Access-Control-Allow-Headers': '*',
  }
  if (req.method === 'OPTIONS') {
    res.writeHead(204, cors)
    return res.end()
  }
  const url = new URL(req.url, `http://${req.headers.host}`)
  if (url.pathname === '/api/status') {
    res.writeHead(200, { ...cors, 'Content-Type': 'application/json' })
    return res.end(
      JSON.stringify({ app: 'shooter-telemetry-mock', opModeRunning: liveRunning, opMode: liveRunning ? 'MockTeleOp' : null }),
    )
  }
  if (url.pathname === '/api/sessions') {
    const list = [...recorded.entries()].map(([name, text]) => ({
      name,
      size: Buffer.byteLength(text),
      mtimeMs: now,
    }))
    res.writeHead(200, { ...cors, 'Content-Type': 'application/json' })
    return res.end(JSON.stringify(list.reverse()))
  }
  if (url.pathname.startsWith('/api/sessions/')) {
    const name = decodeURIComponent(url.pathname.slice('/api/sessions/'.length))
    const text = recorded.get(name)
    if (!text) {
      res.writeHead(404, cors)
      return res.end('no such session')
    }
    res.writeHead(200, { ...cors, 'Content-Type': 'application/x-ndjson' })
    return res.end(text)
  }
  res.writeHead(404, cors)
  res.end('not found')
})

// --- WebSocket live stream ---
const wss = new WebSocketServer({ server, path: '/ws' })
let liveRunning = false
let liveHeader = null

const broadcast = (obj) => {
  const msg = typeof obj === 'string' ? obj : JSON.stringify(obj)
  for (const c of wss.clients) if (c.readyState === 1) c.send(msg)
}

wss.on('connection', (ws) => {
  console.log('viewer connected')
  ws.send(JSON.stringify({ type: 'hello', opModeRunning: liveRunning, opMode: liveRunning ? 'MockTeleOp' : null }))
  if (liveRunning && liveHeader) ws.send(JSON.stringify(liveHeader))
  ws.on('message', (data) => {
    try {
      const msg = JSON.parse(String(data))
      if (msg?.type === 'cmd') {
        const d = msg.drive ?? {}
        const b = msg.buttons ?? {}
        const pressed = Object.entries(b)
          .filter(([, v]) => v)
          .map(([k]) => k)
          .join(',')
        console.log(
          `cmd drive lx=${(d.lx ?? 0).toFixed(2)} ly=${(d.ly ?? 0).toFixed(2)} rx=${(d.rx ?? 0).toFixed(2)}` +
            (pressed ? ` buttons=${pressed}` : ''),
        )
      }
    } catch {
      /* ignore */
    }
  })
})

const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

async function liveSessionLoop() {
  for (;;) {
    await sleep(6000)
    // "OpMode started"
    const epochMs = Date.now()
    liveHeader = makeHeader('MockLiveTeleOp', epochMs)
    liveRunning = true
    broadcast(liveHeader)
    console.log('live session started')

    const liveSamples = []
    let t = 0
    // 3 bursts with idle gaps, streamed in real time as 100ms batches
    for (let b = 0; b < 3; b++) {
      for (const chunk of [makeIdle(t, 3000), makeBurst(t + 3000, 1750 + b * 50)]) {
        let i = 0
        while (i < chunk.samples.length) {
          const batch = []
          const batchEndT = chunk.samples[i].t + 100
          while (i < chunk.samples.length && chunk.samples[i].t < batchEndT) {
            batch.push(chunk.samples[i])
            i++
          }
          broadcast({ type: 'batch', samples: batch })
          liveSamples.push(...batch)
          await sleep(100)
        }
        t = chunk.endMs
      }
    }

    liveRunning = false
    liveHeader = null
    broadcast({ type: 'sessionEnd' })
    // the finished run becomes a downloadable "file"
    const stamp = new Date(epochMs)
      .toISOString()
      .slice(0, 19)
      .replace('T', '_')
      .replaceAll(':', '-')
    record(`${stamp}_MockLiveTeleOp.jsonl`, epochMs, 3)
    console.log('live session ended')
  }
}

server.listen(PORT, () => {
  console.log(`mock robot on http://localhost:${PORT}  (ws: /ws)`)
  console.log(`connect the viewer to: localhost:${PORT}`)
  void liveSessionLoop()
})
