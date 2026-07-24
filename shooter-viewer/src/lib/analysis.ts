import type { Sample } from './types'

/**
 * Per-shot RPM-drop analysis.
 *
 * A "burst" is a stretch where the auto-cycle-shoot sequence is active (ss != idle),
 * extended by a recovery tail. Within a burst we watch mean flywheel velocity vs the
 * commanded target: each dip below the target band = one ball through the flywheel.
 *
 * Spin-up guard: dips are only counted once velocity has first reached ~98% of the
 * target inside the burst ("armed"), so the initial spin-up ramp isn't scored as a
 * giant fake dip.
 *
 * All velocities here are ticks/s (raw log units); the UI converts to RPM.
 */

export interface Shot {
  /** dip start (crossed below enter threshold), ms */
  tStartMs: number
  /** time of minimum velocity, ms */
  tMinMs: number
  /** min mean velocity during dip, ticks/s */
  minV: number
  /** target at dip time, ticks/s */
  targetV: number
  /** targetV - minV, ticks/s */
  dropV: number
  /** per-motor minimums over the dip window, ticks/s */
  minV1: number
  minV2: number
  /** time from min back up to exit threshold, ms (null if never recovered in window) */
  recoveryMs: number | null
  /** marker distance from the previous shot marker, ms (null for first shot of burst) */
  spacingMs: number | null
  /** chart marker time; current peak when paired, otherwise RPM minimum */
  markerMs: number
  /** peak currents at marker time, when current telemetry is available */
  peakI1?: number
  peakI2?: number
  /** rise of mean motor current above the local pre-shot baseline */
  currentRiseA?: number
  detection: 'current+rpm' | 'rpm'
}

export interface Burst {
  startMs: number
  endMs: number
  /** representative target for the burst (max carried target seen), ticks/s */
  targetV: number
  shots: Shot[]
}

export interface AnalysisOptions {
  /** dips only counted after v first reaches armFrac*target within the burst */
  armFrac: number
  /** dip begins when v < enterFrac*target */
  enterFrac: number
  /** dip ends (recovered) when v >= exitFrac*target */
  exitFrac: number
  /** ignore dips shallower than this (ticks/s) — sensor noise */
  minDropTicks: number
  /** merge active windows separated by less than this */
  mergeGapMs: number
  /** extend each window by this much to capture the last ball's recovery */
  tailMs: number
  /** with current telemetry, begin tracking shallower candidate RPM dips */
  currentEnterFrac: number
  /** minimum mean-current rise for a current/RPM paired shot */
  minCurrentRiseA: number
  /** current may lead the RPM dip by this much */
  currentPairBeforeMs: number
  /** current peak search tail after the RPM minimum/recovery */
  currentPairAfterMs: number
  /** pre-dip window used to estimate current baseline */
  currentBaselineMs: number
}

export const DEFAULT_ANALYSIS: AnalysisOptions = {
  armFrac: 0.98,
  enterFrac: 0.97,
  exitFrac: 0.985,
  minDropTicks: 25,
  mergeGapMs: 1000,
  tailMs: 1500,
  currentEnterFrac: 0.99,
  minCurrentRiseA: 0.35,
  currentPairBeforeMs: 250,
  currentPairAfterMs: 200,
  currentBaselineMs: 800,
}

const meanV = (s: Sample) => (s.v1 + s.v2) / 2

const meanCurrent = (s: Sample): number | null => {
  const values = [s.i1, s.i2].filter((v): v is number => typeof v === 'number')
  return values.length ? values.reduce((sum, v) => sum + v, 0) / values.length : null
}

const median = (values: number[]): number | null => {
  if (!values.length) return null
  values.sort((a, b) => a - b)
  const mid = Math.floor(values.length / 2)
  return values.length % 2 ? values[mid] : (values[mid - 1] + values[mid]) / 2
}

/**
 * Contiguous ranges where the shoot sequence is active.
 * [startIdx, coreEndIdx, extEndIdx]: core = ss active; ext adds tailMs for recovery
 * tracking only (dips may not START in the tail — spin-down there isn't a shot).
 */
function findActiveWindows(samples: Sample[], opts: AnalysisOptions): Array<[number, number, number]> {
  const windows: Array<[number, number]> = []
  let start = -1
  for (let i = 0; i < samples.length; i++) {
    const active = samples[i].ss !== 'idle'
    if (active && start < 0) start = i
    if (!active && start >= 0) {
      windows.push([start, i - 1])
      start = -1
    }
  }
  if (start >= 0) windows.push([start, samples.length - 1])

  // merge close windows, then extend each end by tailMs
  const merged: Array<[number, number]> = []
  for (const w of windows) {
    const last = merged[merged.length - 1]
    if (last && samples[w[0]].t - samples[last[1]].t < opts.mergeGapMs) last[1] = w[1]
    else merged.push([...w] as [number, number])
  }
  return merged.map(([s, coreEnd]) => {
    let extEnd = coreEnd
    const tEnd = samples[coreEnd].t + opts.tailMs
    while (extEnd + 1 < samples.length && samples[extEnd + 1].t <= tEnd) extEnd++
    return [s, coreEnd, extEnd] as [number, number, number]
  })
}

export function analyzeSession(
  samples: Sample[],
  opts: AnalysisOptions = DEFAULT_ANALYSIS,
): Burst[] {
  if (samples.length === 0) return []

  // carry the commanded target forward across samples that omit it (power-push phases)
  const carriedTg = new Float64Array(samples.length)
  let tg = NaN
  for (let i = 0; i < samples.length; i++) {
    if (typeof samples[i].tg === 'number') tg = samples[i].tg!
    carriedTg[i] = tg
  }

  const bursts: Burst[] = []
  for (const [i0, iCore, i1] of findActiveWindows(samples, opts)) {
    let burstTarget = 0
    for (let i = i0; i <= iCore; i++) {
      if (!Number.isNaN(carriedTg[i])) burstTarget = Math.max(burstTarget, carriedTg[i])
    }
    if (burstTarget <= 0) continue // never velocity-controlled — nothing to measure against

    const hasCurrent = samples
      .slice(i0, i1 + 1)
      .some((s) => typeof s.i1 === 'number' || typeof s.i2 === 'number')

    const shots: Shot[] = []
    let armed = false
    let inDip = false
    let dipStart = 0
    let dipMinV = Infinity
    let dipMinT = 0
    let dipMinV1 = Infinity
    let dipMinV2 = Infinity

    const currentPair = () => {
      if (!hasCurrent) return null

      const baselineValues: number[] = []
      const candidates: Array<{ sample: Sample; current: number }> = []
      const baselineStart = dipStart - opts.currentBaselineMs
      const baselineEnd = dipStart - opts.currentPairBeforeMs
      const pairStart = dipStart - opts.currentPairBeforeMs
      const pairEnd = dipMinT + opts.currentPairAfterMs

      for (let i = i0; i <= i1; i++) {
        const s = samples[i]
        const current = meanCurrent(s)
        if (current === null) continue
        if (s.t >= baselineStart && s.t < baselineEnd) baselineValues.push(current)
        if (s.t >= pairStart && s.t <= pairEnd) candidates.push({ sample: s, current })
      }
      if (!candidates.length) return null

      const peak = candidates.reduce((best, candidate) =>
        candidate.current > best.current ? candidate : best,
      )
      // A burst may arm too quickly to provide a full baseline. In that case the
      // local minimum still gives a conservative estimate of the observed rise.
      const base = median(baselineValues) ?? Math.min(...candidates.map((c) => c.current))
      const rise = peak.current - base
      if (rise < opts.minCurrentRiseA) return null
      return {
        markerMs: peak.sample.t,
        peakI1: peak.sample.i1,
        peakI2: peak.sample.i2,
        currentRiseA: rise,
      }
    }

    const finishDip = (recoveredAtMs: number | null) => {
      const drop = burstTarget - dipMinV
      const pair = currentPair()
      if (drop >= opts.minDropTicks || pair) {
        const prev = shots[shots.length - 1]
        const markerMs = pair?.markerMs ?? dipMinT
        shots.push({
          tStartMs: dipStart,
          tMinMs: dipMinT,
          minV: dipMinV,
          targetV: burstTarget,
          dropV: drop,
          minV1: dipMinV1,
          minV2: dipMinV2,
          recoveryMs: recoveredAtMs === null ? null : recoveredAtMs - dipMinT,
          spacingMs: prev ? markerMs - prev.markerMs : null,
          markerMs,
          peakI1: pair?.peakI1,
          peakI2: pair?.peakI2,
          currentRiseA: pair?.currentRiseA,
          detection: pair ? 'current+rpm' : 'rpm',
        })
      }
      inDip = false
    }

    for (let i = i0; i <= i1; i++) {
      const s = samples[i]
      const v = meanV(s)
      if (!armed) {
        if (v >= opts.armFrac * burstTarget) armed = true
        continue
      }
      if (!inDip) {
        // A dip may only start in the core window while the flywheel is still
        // commanded at the burst target — velocity falling after the command drops
        // (or in the recovery tail) is spin-down, not a ball.
        const commanded = !Number.isNaN(carriedTg[i]) && carriedTg[i] >= 0.9 * burstTarget
        const enterFrac = hasCurrent ? Math.max(opts.enterFrac, opts.currentEnterFrac) : opts.enterFrac
        if (i <= iCore && commanded && v < enterFrac * burstTarget) {
          inDip = true
          dipStart = s.t
          dipMinV = v
          dipMinT = s.t
          dipMinV1 = s.v1
          dipMinV2 = s.v2
        }
      } else {
        // command dropped mid-dip -> everything after is spin-down; close it out
        if (Number.isNaN(carriedTg[i]) || carriedTg[i] < 0.9 * burstTarget) {
          finishDip(null)
          continue
        }
        if (v < dipMinV) {
          dipMinV = v
          dipMinT = s.t
        }
        dipMinV1 = Math.min(dipMinV1, s.v1)
        dipMinV2 = Math.min(dipMinV2, s.v2)
        if (v >= opts.exitFrac * burstTarget) finishDip(s.t)
      }
    }
    if (inDip) finishDip(null)

    if (shots.length > 0) {
      bursts.push({
        startMs: samples[i0].t,
        endMs: samples[i1].t,
        targetV: burstTarget,
        shots,
      })
    }
  }
  return bursts
}

/** Intervals (ms) for chart background bands. */
export function stateBands(samples: Sample[]): {
  active: Array<[number, number]>
  feeding: Array<[number, number]>
} {
  const collect = (pred: (s: Sample) => boolean) => {
    const out: Array<[number, number]> = []
    let start = -1
    for (let i = 0; i < samples.length; i++) {
      const on = pred(samples[i])
      if (on && start < 0) start = samples[i].t
      if (!on && start >= 0) {
        out.push([start, samples[i].t])
        start = -1
      }
    }
    if (start >= 0 && samples.length) out.push([start, samples[samples.length - 1].t])
    return out
  }
  return {
    active: collect((s) => s.ss !== 'idle'),
    feeding: collect((s) => s.ss === 'turnTransfer'),
  }
}
