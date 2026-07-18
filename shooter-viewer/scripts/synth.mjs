// Synthetic shooter-telemetry generator shared by mock-robot.mjs and gen-demo.mjs.
// Produces the same JSONL sample shape as the robot's ShooterLogger.

export const TICKS_PER_REV = 28

export function makeHeader(opMode, epochMs) {
  return {
    type: 'header',
    version: 1,
    epochMs,
    opMode,
    alliance: 'Blue',
    ticksPerRev: TICKS_PER_REV,
  }
}

const noise = (amp) => (Math.random() - 0.5) * 2 * amp

/**
 * Generate one 3-ball burst as an array of samples at ~100 Hz.
 * @param {number} t0 start time (ms since session start)
 * @param {number} target flywheel target, ticks/s
 * @returns {{samples: object[], endMs: number, drops: number[]}}
 */
export function makeBurst(t0, target = 1750) {
  const samples = []
  const dt = 10 // 100 Hz
  let bat = 12.8 - Math.random() * 0.4
  let lastBatT = -1000

  // per-shot true drop magnitudes (what the analysis should recover)
  const drops = [120 + noise(20), 165 + noise(25), 200 + noise(30)]

  const spinupMs = 800
  const settleMs = 900
  const feedStartMs = spinupMs + settleMs
  const shotSpacing = 420
  const recoverMs = 320
  const feedMs = 3 * shotSpacing + 500
  const tailMs = 1600
  const totalMs = feedStartMs + feedMs + tailMs

  const shotTimes = [0, 1, 2].map((i) => feedStartMs + 250 + i * shotSpacing)

  for (let ms = 0; ms <= totalMs; ms += dt) {
    // base velocity profile
    let v
    if (ms < spinupMs) {
      v = target * (1 - Math.exp(-3.2 * (ms / spinupMs))) * 1.02
    } else {
      v = target
    }
    // superimpose shot dips: sharp drop, exponential recovery
    for (let i = 0; i < 3; i++) {
      const dtShot = ms - shotTimes[i]
      if (dtShot >= 0) {
        const dipDown = 60 // ms to reach bottom
        if (dtShot < dipDown) v -= drops[i] * (dtShot / dipDown)
        else v -= drops[i] * Math.exp(-(dtShot - dipDown) / (recoverMs / 3))
      }
    }
    const inFeed = ms >= feedStartMs && ms < feedStartMs + feedMs
    const ss =
      ms < feedStartMs
        ? ms < spinupMs
          ? 'activate'
          : 'activate'
        : inFeed
          ? 'turnTransfer'
          : ms < feedStartMs + feedMs + 400
            ? 'turnTransferBack'
            : 'idle'
    const ballsLeft = shotTimes.filter((t) => ms < t).length
    const s = {
      t: t0 + ms,
      v1: Math.round((v + noise(8)) * 100) / 100,
      v2: Math.round((v + noise(8) - 6) * 100) / 100,
      tg: target,
      ss,
      ms: 'forwardClose',
      ib: ballsLeft >= 2 ? 1 : 0,
      tb: ballsLeft >= 1 && inFeed ? (Math.random() > 0.3 ? 1 : 0) : ballsLeft > 0 ? 1 : 0,
    }
    if (ms - lastBatT >= 250) {
      bat -= inFeed ? 0.015 : -0.002
      s.bat = Math.round(bat * 100) / 100
      lastBatT = ms
    }
    if (inFeed || ms < feedStartMs) s.d = Math.round((38 + noise(0.5)) * 100) / 100
    samples.push(s)
  }
  return { samples, endMs: t0 + totalMs, drops }
}

/** ~10 Hz idle keepalive samples between bursts. */
export function makeIdle(t0, durMs) {
  const samples = []
  for (let ms = 0; ms <= durMs; ms += 100) {
    samples.push({
      t: t0 + ms,
      v1: Math.round(noise(3) * 100) / 100,
      v2: Math.round(noise(3) * 100) / 100,
      tg: 0,
      ss: 'idle',
      ms: 'idle',
      ib: 0,
      tb: 0,
      ...(ms % 250 < 100 ? { bat: 12.75 } : {}),
    })
  }
  return { samples, endMs: t0 + durMs }
}

/** A full session: idle, then `burstCount` bursts with idle gaps. */
export function makeSession(opMode, epochMs, burstCount = 3) {
  const header = makeHeader(opMode, epochMs)
  const all = []
  let t = 0
  let expectedDrops = []
  for (let b = 0; b < burstCount; b++) {
    const idle = makeIdle(t, 2500)
    all.push(...idle.samples)
    t = idle.endMs
    const burst = makeBurst(t, 1750 + b * 50)
    all.push(...burst.samples)
    expectedDrops.push(burst.drops)
    t = burst.endMs
  }
  const tail = makeIdle(t, 1500)
  all.push(...tail.samples)
  return { header, samples: all, expectedDrops }
}

export function toJsonl(header, samples) {
  return [JSON.stringify(header), ...samples.map((s) => JSON.stringify(s))].join('\n') + '\n'
}
