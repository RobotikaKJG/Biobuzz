# Vilnius-Lyceum Repo Review — Threading & Loop-Hz Optimizations

**Reviewed repo:** [`Vilnius-Lyceum-Robotics/into-the-deep-fullauto`](https://github.com/Vilnius-Lyceum-Robotics/into-the-deep-fullauto)
(another team, *Into the Deep* season). Cloned to `/tmp/lyceum-itd` for this review.
**Reviewed for:** ideas to make **our** bot (`litbot` / `DecodeSDK`, current season) run at
**significantly higher Hz / lower loop times.**
**Date:** 2026-06-06. Paths below are relative to `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.

---

## 0. The headline finding (and the drivebase cross-check you asked for)

**Lyceum runs its whole control loop on a *background thread*, and keeps the drivebase on the main
OpMode thread.** That is the *inverse* of "put the drivebase on its own thread" — but it achieves the
same end goal (driving is decoupled from slow subsystem/sensor work).

- `VLRLinearOpMode` (their base OpMode) spins up an `ExecutorService` and submits a **`CommandRunner`
  `Runnable`** onto a background thread (`VLRLinearOpMode.java:33,38-39`).
- `CommandRunner` is the *real* control loop: it sets **MANUAL bulk caching**, then loops
  `clearBulkCache()` → `CommandScheduler.run()` (every subsystem's `periodic()` — arm, slides,
  intake, all their hardware I/O) → `telemetry.update()` (`CommandRunner.java:49-78`).
- The **main OpMode thread** (`VLRTeleOp.run()`) does only the light, latency-critical work: read
  gamepad, **drive the chassis**, update the Pedro path follower, update pose, telemetry.
- The **drivebase is therefore NOT on its own thread** — `Chassis.drive(x,y,z)` writes the four
  drive motors directly from the **main thread** (`Chassis.java:102-114`, called from the teleop main
  loop). The chassis subsystem's `periodic()` (`Chassis.java:163-175`) only reads distance sensors and
  runs on the *background* command thread.

### So: is our drivebase implemented "the same way"?

**No — and that's fine, because ours is functionally equivalent.** We are putting *the drivebase* on a
dedicated thread and leaving everything else on the main loop; lyceum puts *everything else* on a
background thread and leaves the drivebase on the main loop. Both designs converge on the same
invariant:

> One thread owns the drive motors and runs tight; the other thread owns all the slow
> subsystem/sensor hardware I/O. Driving never waits for vision/outtake/Pinpoint.

| | Lyceum (`into-the-deep`) | litbot (this change) |
|---|---|---|
| Thread that writes **drive motors** | main OpMode thread | dedicated `DriveThread` |
| Thread that runs **other subsystems + bulk-cache clear** | background `CommandRunner` | main OpMode thread (`IterativeController`) |
| Decoupling achieved? | Yes | Yes (equivalent) |
| Drive on its *own* thread? | **No** | **Yes** |
| Telemetry thread | both threads call `telemetry.update()` | single-threaded (main only) — cleaner |
| Invasiveness to our code | n/a | low — `IterativeController` keeps its shape |

**Why we keep our (inverse) split rather than copying lyceum exactly:** lyceum's split would require
moving all of `IterativeController.TeleOp()` into a worker `Runnable` and calling `telemetry.update()`
from two threads. Our split adds a small `DriveThread` and leaves `IterativeController` almost
untouched, and telemetry stays on one thread. Same latency win, smaller blast radius. The two are
interchangeable; if we ever adopt a command-scheduler architecture we'd flip to lyceum's shape.

**The safety invariant both rely on (and that we must preserve):** the two threads touch **disjoint
hardware**. Lyceum: main writes drive motors, command thread reads sensors + drives every other motor.
Ours: `DriveThread` writes `MotorConstants.allDrive` (motors 0–3); the main loop writes
`MotorConstants.notDrive` (4–7) and does all the reads. No motor and no `MotorControl` cache slot is
shared; all hub *reads* + the single `clearBulkCache()` stay on one thread. (This split is already
idiomatic here — autonomous uses `setMotors(notDrive)` while RoadRunner drives,
`Autonomous/AutonomousControl.java:74`.)

---

## 1. Threading model in detail

### 1.1 Two-thread architecture
- `Executors.newCachedThreadPool()` in `VLRLinearOpMode.java:33`; `CommandRunner` submitted at
  `:38-39`. Teardown at `:42-53`: `GlobalLoopTimeMonitor.shutDown()`, `CommandScheduler.reset()`,
  `executorService.shutdownNow()`, bounded `awaitTermination(100ms)`. **Bounded teardown** so OpMode
  stop never hangs — worth mirroring.
- `CommandRunner.run()` waits for START with a `while(!isOpModeRunning()) sleep(0)` spin
  (`CommandRunner.java:57-63`), then runs until the OpMode ends (`:65`). The "is running" check is a
  functional-interface `OpModeRunningInterface` bound to `this::opModeIsActive`.

### 1.2 Cross-thread communication = lock-free volatiles/atomics (no queues, no locks)
- `GlobalLoopTimeMonitor`: `volatile AtomicLong mainLoopDurationNs` + `volatile long
  commandThreadDurationNs` + `volatile boolean shutdownRequested` (`:8,10,12`). Each thread publishes
  its own loop duration; the reader computes Hz.
- Subsystems publish encoder state via `volatile double encoderPosition` so the cheap reader side
  never blocks on I2C: `ArmSlideSubsystem` (encoder cached once per `periodic()`),
  `ArmRotatorSubsystem` (`volatile encoderPosition`/`encoderOffset`). `ArmState.currentState` is
  `volatile`; `PoseSaver.pedroPose` and `AllianceSaver.alliance` are `volatile`.
- **No `synchronized`, no blocking queues in the hot path.** Visibility via `volatile`; that's enough
  because each datum has a single writer.

### 1.3 Vision on its own thread
- `subsystems/limelight/LimelightYoloReader.java` runs the Limelight read/parse off the control loop
  (its own thread), publishing results via shared fields — so a slow vision frame never stalls control.
  (Direct parallel to our `SensorControl` Limelight calls, which currently sit inline on the loop.)

---

## 2. Loop-Hz / hardware-I/O optimizations

### 2.1 Bulk caching — MANUAL, cleared once per loop
`CommandRunner.java:52-54` sets `BulkCachingMode.MANUAL` on every hub; `:68-71` does a single
`clearBulkCache()` per loop before running the scheduler. **litbot already does exactly this**
(`GeneralBlueTeleOp.java:44-46`). Parity. (Note: lyceum clears on the *command* thread because that's
where its reads live; we clear on the main thread for the same reason.)

### 2.2 Read expensive sensors rarely / off-loop
- `getCurrent()` is explicitly flagged in their arm code as **"TANKS PERFORMANCE, THESE READS TAKE
  3ms EACH"** and is only read in a specific power-pull mode, not every loop.
  **litbot already throttles `getCurrent` to every 150 ms** (`MotorControl.getMotorCurrent`,
  `CURRENT_REFRESH_MS`). Parity / litbot is arguably cleaner here.
- Encoder position read **once** per `periodic()` into a cached field, then reused by all math
  (`updateEncoderPosition()` pattern). litbot relies on bulk caching for the same effect.

### 2.3 Don't reconfigure motors every loop
- Run mode set once at init (`STOP_AND_RESET_ENCODER` → `RUN_WITHOUT_ENCODER`); PID coefficients only
  re-applied on mode change / in `DEBUG_MODE`. **litbot already caches `lastMode`/`lastPidf`/
  `lastWrittenVelocity`** and skips redundant `setMode`/`setPIDFCoefficients`/`setVelocity`
  (`MotorControl.setMotorRPM`). Parity.
- Chassis motors use `Motor.RunMode.RawPower` (`Chassis.java:70-73`) — no SDK velocity PID on drive,
  so writes are cheap. litbot's drive motors use `RUN_WITHOUT_ENCODER` — equivalent.

### 2.4 Cheap input shaping & allocation discipline
- `AsymmetricLowPassFilter` — O(1), single `prevPower` double, no history buffer; one for X, one for Y
  (`Chassis.java:46-47,104`). Separate accel/decel gains for snappy-but-smooth stick response.
- `MecanumDriveController` precomputes wheel speeds in its constructor (no per-call matrix inversion).
- `clampPower()` static-friction gate zeroes drive commands `< 0.05` (`Chassis.java:125-128`) — avoids
  buzzing motors with sub-threshold power. (litbot has no equivalent; it's a feel tweak, not a Hz win.)

### 2.5 Telemetry once per loop
`telemetry.update()` is called exactly once per loop on each thread (`CommandRunner.java:76`,
`VLRTeleOp` main loop). Heavy dashboard telemetry gated behind `DEBUG_MODE`. litbot currently calls
`telemetry` only at OpMode end; this change adds one `telemetry.update()` per main loop for the Hz
readout (cheap — the SDK rate-limits transmission).

### 2.6 Loop-time instrumentation (mirrors what you asked me to add)
- `GlobalLoopTimeMonitor.logLoopTimes()` prints **`Main thread: X hz`** and **`Command thread: Y hz`**
  — i.e. the Hz of *both* loops, computed from durations each thread publishes via volatile/atomic
  (`GlobalLoopTimeMonitor.java:31-43`). This is precisely the "avg time of the 2 separate loops"
  telemetry we're adding to litbot — our `LoopTimer` + `volatile avgMs` is the same idea, reported as
  ms instead of Hz.
- `LoopTimeMonitor` keeps loop times in a `TreeSet` and averages the **worst** top-N (or top
  percentile) — good for hunting spikes. We use a simpler last-10 ring buffer per your request.

---

## 3. Where litbot already matches or beats lyceum

litbot is **already well-optimized at the per-call level** — most of lyceum's micro-opts are already
present here:

| Optimization | Lyceum | litbot |
|---|---|---|
| MANUAL bulk caching, 1 clear/loop | ✔ | ✔ |
| Skip redundant motor power writes (epsilon gate) | ✘ (writes every loop) | ✔ `POWER_EPSILON` |
| Skip redundant servo writes (epsilon gate) | ✘ | ✔ `SERVO_POS_EPSILON` |
| Cache run-mode / PIDF, avoid re-issue | ✔ | ✔ |
| Throttle `getCurrent()` | partial (mode-gated) | ✔ 150 ms |
| Batch all motor writes once/loop | per-subsystem | ✔ single `setMotors` |
| Control loop off the OpMode-overhead path | ✔ (bg thread) | ✔ (after this change) |

**Conclusion:** the per-call I/O in litbot is already lean. The one structural lever lyceum has that we
didn't is **parallelism** — running drive independently of the slow subsystem/sensor I/O. That is
exactly the win this change delivers.

---

## 4. Recommended actions for litbot (ordered by Hz impact / effort)

1. **[doing now] Drivebase on a dedicated `DriveThread`** writing only `allDrive`; main loop writes
   `notDrive`. Disjoint motor ownership; all reads + the single `clearBulkCache()` stay on the main
   thread; field-centric heading read from the cached localizer pose. Mirror lyceum's lifecycle
   discipline: start after `waitForStart`, stop in a `finally` (volatile flag → `interrupt()` →
   bounded `join`), then zero the drive motors.
2. **[doing now] Two-loop Hz/ms telemetry** via a tiny `LoopTimer` (last-10 rolling average); the drive
   thread publishes its average in a `volatile double` the main thread reads — same lock-free pattern
   as lyceum's `GlobalLoopTimeMonitor`. Telemetry stays single-threaded.
3. **[doing now] Delete `LoopTimeLogger`** (the CSV/section profiler) per your request — it's the
   heavyweight per-section logger we no longer want; the lightweight `LoopTimer` replaces it.
4. **[next, optional, real Hz win] Move the Limelight read off the loop** onto its own reader thread
   (lyceum's `LimelightYoloReader` pattern). `SensorControl`'s `limelight.getLatestResult()` is a
   prime candidate — vision latency is the kind of variable cost that drags the *subsystem* loop's Hz
   down. Publish results via a `volatile`; the loop consumes the latest frame without blocking.
5. **[optional, feel] Static-friction clamp** on drive output (lyceum `clampPower`, `< 0.05 → 0`) to
   stop sub-threshold motor buzz. Behavior tweak, not a Hz change.

Items **not** worth adopting (litbot already equal/better): epsilon write-gating, run-mode/PIDF
caching, current-read throttling, single batched motor write.
