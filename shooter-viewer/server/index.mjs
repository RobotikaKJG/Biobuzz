#!/usr/bin/env node
/**
 * Local API for the shooter viewer (ftc-decode dashboard pattern).
 * - SSE /api/live-connect     → hub-live.sh connect-live (auto-skips WiFi if already on hub)
 * - SSE /api/live-disconnect  → restore WiFi only if connect switched
 * - GET /api/net-status       → JSON from hub-live.sh status (for UI badge)
 */
import { spawn, execFile } from 'node:child_process'
import fs from 'node:fs'
import http from 'node:http'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { promisify } from 'node:util'

const execFileAsync = promisify(execFile)
const __dirname = path.dirname(fileURLToPath(import.meta.url))
const VIEWER_ROOT = path.resolve(__dirname, '..')
const HUB_LIVE = path.resolve(VIEWER_ROOT, '../FtcRobotController-11.1/hub-live.sh')
const PORT = Number(process.env.VIEWER_API_PORT || 5174)
const IS_WINDOWS = process.platform === 'win32'

/** Git Bash interpreter, needed to run hub-live.sh on Windows. */
function findBash() {
  if (process.env.VIEWER_BASH) return process.env.VIEWER_BASH
  const candidates = [
    'C:\\Program Files\\Git\\bin\\bash.exe',
    'C:\\Program Files (x86)\\Git\\bin\\bash.exe',
    'C:\\Program Files\\Git\\usr\\bin\\bash.exe',
  ]
  return candidates.find((p) => fs.existsSync(p)) || 'bash.exe'
}

/**
 * Windows cannot exec a .sh directly (EFTYPE) — run it through Git Bash.
 * hub-live.sh already handles MINGW/MSYS, so it works once bash drives it.
 * Returns [cmd, args] for spawn/execFile.
 */
function shellScriptCmd(script, args) {
  if (!IS_WINDOWS) return [script, args]
  return [findBash(), [script.replace(/\\/g, '/'), ...args]]
}

function streamCommand(res, cmd, args, cwd) {
  res.writeHead(200, {
    'Content-Type': 'text/event-stream',
    'Cache-Control': 'no-cache',
    Connection: 'keep-alive',
    'Access-Control-Allow-Origin': '*',
  })

  const send = (data) => {
    res.write(`data: ${JSON.stringify(data)}\n\n`)
  }

  send(`[server] $ ${path.basename(cmd)} ${args.join(' ')}\n`)

  const proc = spawn(cmd, args, {
    cwd,
    env: process.env,
    shell: false,
  })

  proc.stdout.on('data', (chunk) => send(chunk.toString()))
  proc.stderr.on('data', (chunk) => send(chunk.toString()))

  proc.on('error', (err) => {
    send(`[server] failed to spawn: ${err.message}\n`)
    res.write(`event: done\ndata: ${JSON.stringify({ code: 1 })}\n\n`)
    res.end()
  })

  proc.on('close', (code) => {
    send(`[server] exit ${code ?? 1}\n`)
    res.write(`event: done\ndata: ${JSON.stringify({ code: code ?? 1 })}\n\n`)
    res.end()
  })

  res.on('close', () => {
    try {
      proc.kill()
    } catch {
      /* ignore */
    }
  })
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url || '/', `http://${req.headers.host}`)

  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET,OPTIONS',
    })
    res.end()
    return
  }

  if (url.pathname === '/api/live-connect') {
    streamCommand(res, ...shellScriptCmd(HUB_LIVE, ['connect-live']), path.dirname(HUB_LIVE))
    return
  }
  if (url.pathname === '/api/live-disconnect') {
    streamCommand(res, ...shellScriptCmd(HUB_LIVE, ['disconnect-live']), path.dirname(HUB_LIVE))
    return
  }
  if (url.pathname === '/api/net-status') {
    // Wrapped: execFile can throw synchronously (EFTYPE on a bad interpreter),
    // which would escape .catch() below and take down the whole server.
    Promise.resolve()
      .then(() =>
        execFileAsync(...shellScriptCmd(HUB_LIVE, ['status']), {
          cwd: path.dirname(HUB_LIVE),
          timeout: 8000,
        }),
      )
      .then(({ stdout }) => {
        res.writeHead(200, {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
        res.end(stdout.trim() || '{}')
      })
      .catch((err) => {
        res.writeHead(500, {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        })
        res.end(JSON.stringify({ error: String(err.message || err) }))
      })
    return
  }
  if (url.pathname === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json', 'Access-Control-Allow-Origin': '*' })
    res.end(JSON.stringify({ ok: true, hubLive: HUB_LIVE }))
    return
  }

  res.writeHead(404, { 'Content-Type': 'text/plain' })
  res.end('not found')
})

server.listen(PORT, '127.0.0.1', () => {
  console.log(`[shooter-viewer api] http://127.0.0.1:${PORT}`)
  console.log(`[shooter-viewer api] hub-live: ${HUB_LIVE}`)
})
