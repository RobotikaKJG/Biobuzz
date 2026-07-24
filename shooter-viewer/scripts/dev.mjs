#!/usr/bin/env node
/** Start API (:5174) + Vite (:5173) together; kill both on exit. */
import { spawn } from 'node:child_process'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const kids = []

function run(cmd, args) {
  // No shell: with shell:true Windows concatenates argv unquoted, so a node
  // installed at "C:\Program Files\nodejs" splits at the space. Both children
  // are plain .js run through node, so a shell was never needed.
  const child = spawn(cmd, args, {
    cwd: root,
    stdio: 'inherit',
    env: process.env,
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
// vite/bin/vite.js, not .bin/vite: the latter is a shell script on Windows.
run(process.execPath, [path.join(root, 'node_modules/vite/bin/vite.js')])
