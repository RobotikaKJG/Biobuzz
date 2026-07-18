/** Keyboard → robot gamepad command (matches RemoteControl.java). */

export type RemoteButtons = {
  leftBumper: boolean // shoot (LB)
  rightBumper: boolean // turret track (RB)
  leftTrigger: boolean // outtake spin (LT)
  rightTrigger: boolean // intake (RT)
  square: boolean // reset pose
  circle: boolean // path / gate
  dpadUp: boolean // reverse intake
  dpadRight: boolean // lock servo
  rightStickButton: boolean // far toggle
  share: boolean // robot/driver oriented
}

export type RemoteCmd = {
  type: 'cmd'
  drive: { lx: number; ly: number; rx: number }
  buttons: RemoteButtons
}

export const EMPTY_BUTTONS: RemoteButtons = {
  leftBumper: false,
  rightBumper: false,
  leftTrigger: false,
  rightTrigger: false,
  square: false,
  circle: false,
  dpadUp: false,
  dpadRight: false,
  rightStickButton: false,
  share: false,
}

/** Physical key → held action. Drive uses gamepad stick convention (W = ly −1). */
export type KeyAction =
  | { kind: 'drive'; axis: 'lx' | 'ly' | 'rx'; value: number }
  | { kind: 'button'; button: keyof RemoteButtons }

export const KEY_BINDINGS: Record<string, KeyAction> = {
  KeyW: { kind: 'drive', axis: 'ly', value: -1 },
  KeyS: { kind: 'drive', axis: 'ly', value: 1 },
  KeyA: { kind: 'drive', axis: 'lx', value: -1 },
  KeyD: { kind: 'drive', axis: 'lx', value: 1 },
  Comma: { kind: 'drive', axis: 'rx', value: -1 }, // ,  (<)
  Period: { kind: 'drive', axis: 'rx', value: 1 }, // .  (>)
  ArrowUp: { kind: 'drive', axis: 'ly', value: -1 },
  ArrowDown: { kind: 'drive', axis: 'ly', value: 1 },
  ArrowLeft: { kind: 'drive', axis: 'lx', value: -1 },
  ArrowRight: { kind: 'drive', axis: 'lx', value: 1 },
  Space: { kind: 'button', button: 'leftBumper' },
  ShiftLeft: { kind: 'button', button: 'rightTrigger' },
  ShiftRight: { kind: 'button', button: 'rightTrigger' },
  KeyO: { kind: 'button', button: 'leftTrigger' },
  KeyT: { kind: 'button', button: 'rightBumper' },
  KeyR: { kind: 'button', button: 'square' },
  KeyC: { kind: 'button', button: 'circle' },
  KeyU: { kind: 'button', button: 'dpadUp' },
  KeyL: { kind: 'button', button: 'dpadRight' },
  KeyF: { kind: 'button', button: 'rightStickButton' },
  KeyM: { kind: 'button', button: 'share' },
}

export type ActionBinding = {
  button: keyof RemoteButtons
  keys: string
  label: string
  /** Longer explanation for tooltips */
  tip: string
  /** Key codes that light this action */
  codes: string[]
}

/** Preferred key label shown on action buttons (first matching code wins for pulse). */
export const ACTION_KEY_LABELS: ActionBinding[] = [
  {
    button: 'leftBumper',
    keys: 'Space',
    label: 'Shoot',
    tip: 'Start/stop the auto-cycle shoot sequence (gamepad left bumper). Feeds balls through transfer while the flywheel is ready.',
    codes: ['Space'],
  },
  {
    button: 'rightTrigger',
    keys: 'Shift',
    label: 'Intake',
    tip: 'Toggle auto-intake / transfer (gamepad right trigger). Pulls balls into the robot; press again to stop.',
    codes: ['ShiftLeft', 'ShiftRight'],
  },
  {
    button: 'leftTrigger',
    keys: 'O',
    label: 'Spin',
    tip: 'Toggle flywheel spin-up for close/far outtake (gamepad left trigger). Spins the shooter up without feeding yet.',
    codes: ['KeyO'],
  },
  {
    button: 'rightBumper',
    keys: 'T',
    label: 'Turret',
    tip: 'Toggle turret auto-tracking toward the goal (gamepad right bumper).',
    codes: ['KeyT'],
  },
  {
    button: 'square',
    keys: 'R',
    label: 'Reset',
    tip: 'Reset robot pose / outtake position helpers (gamepad square). Use if localization or hood position looks wrong.',
    codes: ['KeyR'],
  },
  {
    button: 'rightStickButton',
    keys: 'F',
    label: 'Far',
    tip: 'Toggle far vs close shooting mode (gamepad right stick click). Far uses the long-shot outtake profile.',
    codes: ['KeyF'],
  },
]

export type DriveKeyCap = {
  label: string
  code: string
  sub: string
  tip: string
}

export const DRIVE_KEYCAPS: DriveKeyCap[] = [
  {
    label: ',',
    code: 'Comma',
    sub: '↺',
    tip: 'Rotate left in place (same as right stick left on the gamepad).',
  },
  {
    label: 'W',
    code: 'KeyW',
    sub: '↑',
    tip: 'Drive forward (robot-oriented, like left stick up).',
  },
  {
    label: '.',
    code: 'Period',
    sub: '↻',
    tip: 'Rotate right in place (same as right stick right on the gamepad).',
  },
  {
    label: 'A',
    code: 'KeyA',
    sub: '←',
    tip: 'Strafe left (mecanum slide, like left stick left).',
  },
  {
    label: 'S',
    code: 'KeyS',
    sub: '↓',
    tip: 'Drive backward (robot-oriented, like left stick down).',
  },
  {
    label: 'D',
    code: 'KeyD',
    sub: '→',
    tip: 'Strafe right (mecanum slide, like left stick right).',
  },
]

export function buildCmd(held: ReadonlySet<string>): RemoteCmd {
  let lx = 0
  let ly = 0
  let rx = 0
  const buttons = { ...EMPTY_BUTTONS }

  for (const code of held) {
    const action = KEY_BINDINGS[code]
    if (!action) continue
    if (action.kind === 'drive') {
      if (action.axis === 'lx') lx += action.value
      else if (action.axis === 'ly') ly += action.value
      else rx += action.value
    } else {
      buttons[action.button] = true
    }
  }

  const clamp = (v: number) => Math.max(-1, Math.min(1, v))
  return {
    type: 'cmd',
    drive: { lx: clamp(lx), ly: clamp(ly), rx: clamp(rx) },
    buttons,
  }
}

export type HelpRow = {
  keys: string
  action: string
  tip: string
  /** Any of these key codes highlight this row */
  codes: string[]
}

export const CONTROL_HELP: HelpRow[] = [
  {
    keys: 'W / S',
    action: 'Forward / back',
    tip: 'Translate along the robot’s front/back axis (left stick Y).',
    codes: ['KeyW', 'KeyS', 'ArrowUp', 'ArrowDown'],
  },
  {
    keys: 'A / D',
    action: 'Strafe left / right',
    tip: 'Slide sideways without turning (left stick X).',
    codes: ['KeyA', 'KeyD', 'ArrowLeft', 'ArrowRight'],
  },
  {
    keys: ', / .',
    action: 'Rotate left / right',
    tip: 'Spin in place (right stick X). Comma = left, period = right.',
    codes: ['Comma', 'Period'],
  },
  {
    keys: 'Space',
    action: 'Shoot (LB)',
    tip: 'Auto-cycle shoot: unlocks latch and feeds while the flywheel is ready.',
    codes: ['Space'],
  },
  {
    keys: 'Shift',
    action: 'Intake (RT)',
    tip: 'Toggle intake + transfer to collect balls into the robot.',
    codes: ['ShiftLeft', 'ShiftRight'],
  },
  {
    keys: 'O',
    action: 'Outtake spin (LT)',
    tip: 'Spin the flywheel up/down without starting a feed cycle.',
    codes: ['KeyO'],
  },
  {
    keys: 'T',
    action: 'Turret track (RB)',
    tip: 'Enable or disable turret auto-aim at the goal.',
    codes: ['KeyT'],
  },
  {
    keys: 'R',
    action: 'Reset pose (□)',
    tip: 'Run the reset-position helper (same as gamepad square).',
    codes: ['KeyR'],
  },
  {
    keys: 'C',
    action: 'Gate path (○)',
    tip: 'Toggle the Pedro path that drives to the gate (gamepad circle).',
    codes: ['KeyC'],
  },
  {
    keys: 'U',
    action: 'Reverse intake',
    tip: 'Run intake motors backward to spit a jam (dpad up).',
    codes: ['KeyU'],
  },
  {
    keys: 'L',
    action: 'Lock servo',
    tip: 'Toggle the intake lock / latch servo (dpad right).',
    codes: ['KeyL'],
  },
  {
    keys: 'F',
    action: 'Far toggle',
    tip: 'Switch between close and far shooting velocity profiles.',
    codes: ['KeyF'],
  },
  {
    keys: 'M',
    action: 'Drive mode (share)',
    tip: 'Toggle robot-oriented vs field/driver-oriented driving (share).',
    codes: ['KeyM'],
  },
]
