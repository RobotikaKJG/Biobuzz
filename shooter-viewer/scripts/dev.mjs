#!/usr/bin/env node
/** Start API (:5174) + Vite (:5173) together; kill both on exit. */
import { spawn } from 'node:child_process'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const kids = []

function run(cmd, args) {
  const child = spawn(cmd, args, {
    cwd: root,
    stdio: 'inherit',
    env: process.env,
    shell: process.platform === 'win32',
  })
  kids.push(child)
  child.on('exit', (code, signal) => {
    if (signal) return
    // If either dies, tear down the other.
    for (const k of kids) {
      if (k !== child && !k.killed) k.kill('SIGTERM')
    }
    process.exit(code ?? 1)
  })
}

function shutdown() {
  for (const k of kids) {
    if (!k.killed) k.kill('SIGTERM')
  }
  process.exit(0)
}

process.on('SIGINT', shutdown)
process.on('SIGTERM', shutdown)

run(process.execPath, ['server/index.mjs'])
run(path.join(root, 'node_modules/.bin/vite'), [])
