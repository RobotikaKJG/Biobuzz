/** Mirrors the JSONL emitted by the robot's ShooterLogger / streamed by ShooterTelemetryServer. */

export interface SessionHeader {
  type: 'header'
  version: number
  epochMs: number
  opMode: string
  alliance: string
  ticksPerRev: number
}

/** One sample line. Velocities are encoder ticks/sec; convert with ticksToRpm(). */
export interface Sample {
  /** ms since session start */
  t: number
  /** outtake1 velocity, ticks/s */
  v1: number
  /** outtake2 velocity, ticks/s */
  v2: number
  /** outtake1 current, amps (added in log version 2) */
  i1?: number
  /** outtake2 current, amps (added in log version 2) */
  i2?: number
  /** drive currents FL/BL/FR/BR, amps (log version 3) */
  id0?: number
  id1?: number
  id2?: number
  id3?: number
  /** intake motor current, amps (log version 3+) */
  ii?: number
  /** transfer motor current, amps (log version 3) */
  it?: number
  /** commanded velocity, ticks/s (absent while power-controlled) */
  tg?: number
  /** AutoCycleShootStates name */
  ss: string
  /** OuttakeMotorStates name */
  ms: string
  /** Intake/transfer motor states and turret tracking (log version 3+) */
  ims?: string
  tms?: string
  tr?: 0 | 1
  ib: 0 | 1
  tb: 0 | 1
  /** battery volts (throttled — absent on most samples) */
  bat?: number
  /** distance to goal, inches (localizer; carried forward in UI when absent) */
  d?: number
  /** turret angle, degrees (log version 3) */
  ta?: number
  /** localizer pose X/Y inches + heading deg (calibration overlay) */
  px?: number
  py?: number
  ph?: number
}

export interface Session {
  name: string
  header: SessionHeader | null
  samples: Sample[]
}

export interface SessionInfo {
  name: string
  size: number
  mtimeMs: number
}

export const DEFAULT_TICKS_PER_REV = 28

export function ticksToRpm(ticksPerSec: number, ticksPerRev: number): number {
  return (ticksPerSec / ticksPerRev) * 60
}

/** Carry last-known optional fields forward without rescanning the full session. */
export function latestCarried(samples: Sample[]) {
  let tg: number | null = null
  let d: number | null = null
  let bat: number | null = null
  let ta: number | null = null
  let i1: number | null = null
  let i2: number | null = null
  let ii: number | null = null
  let it: number | null = null
  let id0: number | null = null
  let id1: number | null = null
  let id2: number | null = null
  let id3: number | null = null
  for (let i = samples.length - 1; i >= 0; i--) {
    const s = samples[i]
    if (tg == null && typeof s.tg === 'number') tg = s.tg
    if (d == null && typeof s.d === 'number' && Number.isFinite(s.d)) d = s.d
    if (bat == null && typeof s.bat === 'number') bat = s.bat
    if (ta == null && typeof s.ta === 'number') ta = s.ta
    if (i1 == null && typeof s.i1 === 'number') i1 = s.i1
    if (i2 == null && typeof s.i2 === 'number') i2 = s.i2
    if (ii == null && typeof s.ii === 'number') ii = s.ii
    if (it == null && typeof s.it === 'number') it = s.it
    if (id0 == null && typeof s.id0 === 'number') id0 = s.id0
    if (id1 == null && typeof s.id1 === 'number') id1 = s.id1
    if (id2 == null && typeof s.id2 === 'number') id2 = s.id2
    if (id3 == null && typeof s.id3 === 'number') id3 = s.id3
    if (
      tg != null && d != null && bat != null && ta != null
      && i1 != null && i2 != null && ii != null && it != null
      && id0 != null && id1 != null && id2 != null && id3 != null
    ) break
  }
  const last = samples.length ? samples[samples.length - 1] : null
  return { last, tg, d, bat, ta, i1, i2, ii, it, id0, id1, id2, id3 }
}
