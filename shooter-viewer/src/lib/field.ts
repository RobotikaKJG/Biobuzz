/** DECODE field math — Pedro corner inches is the map truth. */

export const FIELD_INCHES = 144
export const TILE_INCHES = 24
export const INCH_TO_CM = 2.54

export type Alliance = 'red' | 'blue' | 'either'

export interface FieldMeta {
  version: number
  field: {
    sizeInches: number
    sizeCm: number
    tiles: number
    tileInches: number
    tileCm: number
  }
  image: {
    path: string
    pixelWidth: number
    pixelHeight: number
    imageYOrigin: 'top' | 'bottom'
  }
  goalsInches: {
    teleopCenter: { red: { x: number; y: number }; blue: { x: number; y: number } }
    pedroCorner: { red: { x: number; y: number }; blue: { x: number; y: number } }
  }
  distancePipeline: {
    goalDistanceOffsetCm: number
    closeCurve: {
      anchorDistanceCm: number
      anchorTicksPerSec: number
      minDistanceCm: number
      maxDistanceCm: number
      minTicksPerSec: number
      maxTicksPerSec: number
      ticksPerRev: number
    }
  }
  robotDraft: {
    lengthInches: number
    widthInches: number
    shooterOffsetInches: { x: number; y: number }
    limelightOffsetInches: { x: number; y: number }
  }
}

export type BallResult =
  | 'unknown'
  | 'undershot'
  | 'overshot'
  | 'backspin_out'
  | 'scored'

export interface TrialTelemetry {
  capturedAt?: string
  robotHeadingDeg?: number | null
  turretAngleDeg?: number | null
  commandedTicksPerSec?: number | null
  measuredRpm1?: number | null
  measuredRpm2?: number | null
  batteryV?: number | null
  distanceCm?: number | null
}

export interface CalibTrial {
  id: string
  rpm?: number | null
  balls: {
    first: BallResult
    second: BallResult
    third: BallResult
  }
  telemetry?: TrialTelemetry
  notes?: string
}

export interface CalibPoint {
  id: string
  label: string
  alliance: Alliance
  pose: { x: number; y: number; headingDeg?: number | null; notes?: string }
  distance: {
    /** Value streamed by SensorControl.getDistanceFromLocalizer(). */
    robotEstimateCm?: number | null
    /** Physical shooter-center-to-target measurement. */
    tapeMeasuredCm?: number | null
  }
  turret: {
    /** Actual turret angle streamed by the robot, relative to robot heading. */
    robotEstimateDeg?: number | null
  }
  /** Raw attempts. Duplicates are intentionally allowed. */
  trials?: CalibTrial[]
  notes?: string
  source?: string
}

export interface CalibFile {
  version: number
  updatedAt?: string
  ticksPerRev: number
  coordinateFrame: 'pedroCornerInches' | 'teleopCenterInches'
  points: CalibPoint[]
}

export function ticksToRpm(ticks: number, ticksPerRev = 28) {
  return (ticks / ticksPerRev) * 60
}

export function rpmToTicks(rpm: number, ticksPerRev = 28) {
  return (rpm * ticksPerRev) / 60
}

/**
 * Image pixel (top-left origin) → Pedro corner inches.
 *
 * The MeepMeep art is rotated 90° counter-clockwise relative to Pedro's
 * conventional field view: Pedro red/blue goal coordinates map to the
 * top-left/bottom-left image corners respectively.
 */
export function pixelToPedro(
  px: number,
  py: number,
  imgW: number,
  imgH: number,
): { x: number; y: number } {
  return {
    x: ((imgH - py) / imgH) * FIELD_INCHES,
    y: ((imgW - px) / imgW) * FIELD_INCHES,
  }
}

/** Pedro corner inches → image pixel (top-left origin). */
export function pedroToPixel(
  x: number,
  y: number,
  imgW: number,
  imgH: number,
): { px: number; py: number } {
  return {
    px: ((FIELD_INCHES - y) / FIELD_INCHES) * imgW,
    py: ((FIELD_INCHES - x) / FIELD_INCHES) * imgH,
  }
}

export function pedroToTeleop(x: number, y: number) {
  return { x: x - FIELD_INCHES / 2, y: y - FIELD_INCHES / 2 }
}

export function teleopToPedro(x: number, y: number) {
  return { x: x + FIELD_INCHES / 2, y: y + FIELD_INCHES / 2 }
}

/**
 * Guess whether a streamed pose is already Pedro-corner or TeleOp-center.
 * Corner poses live mostly in [0,144]; cold TeleOp is near 0 with |xy|≲72.
 */
export function toPedroPose(x: number, y: number): { x: number; y: number; frame: string } {
  if (x >= -2 && x <= 146 && y >= -2 && y <= 146 && (x > 8 || y > 8)) {
    // Likely already corner (auton carry) — keep
    if (x >= 0 && y >= 0 && x <= 144 && y <= 144) {
      return { x, y, frame: 'pedroCornerInches' }
    }
  }
  const p = teleopToPedro(x, y)
  return { x: p.x, y: p.y, frame: 'teleopCenterInches→pedro' }
}

export function distanceInches(
  ax: number,
  ay: number,
  bx: number,
  by: number,
): number {
  const dx = bx - ax
  const dy = by - ay
  return Math.sqrt(dx * dx + dy * dy)
}

/** Independent UI geometry estimate; deliberately does not apply robot tuning offsets. */
export function softwareGoalDistanceCm(
  point: Pick<CalibPoint, 'pose'>,
  goal: { x: number; y: number },
): number {
  return distanceInches(point.pose.x, point.pose.y, goal.x, goal.y) * INCH_TO_CM
}

/** Independent UI aim estimate relative to the robot's saved heading. */
export function softwareTurretAngleDeg(
  point: Pick<CalibPoint, 'pose'>,
  goal: { x: number; y: number },
): number | null {
  if (point.pose.headingDeg == null) return null
  const worldBearing =
    (Math.atan2(goal.y - point.pose.y, goal.x - point.pose.x) * 180) / Math.PI
  let relative = worldBearing - point.pose.headingDeg
  while (relative > 180) relative -= 360
  while (relative <= -180) relative += 360
  return relative
}

/** Match SensorControl.getDistanceFromLocalizer axis swap in TeleOp center frame. */
export function teleopGoalDistanceInches(
  poseX: number,
  poseY: number,
  goalX: number,
  goalY: number,
): number {
  const robotX = poseY
  const robotY = poseX
  return distanceInches(robotX, robotY, goalX, goalY)
}

export function newPointId() {
  return `pt-${Date.now().toString(36)}`
}

export function newTrialId() {
  return `trial-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 6)}`
}
