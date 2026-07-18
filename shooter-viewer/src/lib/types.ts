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
  /** commanded velocity, ticks/s (absent while power-controlled) */
  tg?: number
  /** AutoCycleShootStates name */
  ss: string
  /** OuttakeMotorStates name */
  ms: string
  ib: 0 | 1
  tb: 0 | 1
  /** battery volts (throttled — absent on most samples) */
  bat?: number
  /** distance to goal, inches (only while shooting) */
  d?: number
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
