# Optional Road Runner support

The existing Road Runner 0.5.6 structure is retained. The starter's autonomous does not construct it
unless `AutonomousConstants.USE_ROAD_RUNNER` is enabled. All tuning OpModes in `opmode/` are disabled
until explicitly needed; remove `@Disabled` from the individual tool after checking its setup.

## Relationships

- `SampleMecanumDrive` maps the four names from `MotorConstants` and the optional `pinpointIMU`.
  It owns drive power during trajectory following and uses `TwoWheelTrackingLocalizer` by default.
- `DriveConstants` supplies dimensions, feedforward and trajectory constraints. Values are retained
  chassis calibration, not measured defaults for a new robot. Check motor specifications, wheel size,
  track width, maximum speeds and gains when the chassis changes.
- `TwoWheelTrackingLocalizer` reads raw X/Y encoder positions and heading from Pinpoint. It requires
  no intake motor or REV motor encoder mapping. Verify tick scale, wheel offsets and signs on the robot.
  It preserves the existing negative Pinpoint heading/encoder convention; heading velocity uses the
  same sign. Pinpoint's reported linear velocity is in mm/s rather than tracking-wheel ticks/s,
  so optional wheel-velocity feedback is omitted instead of applying a ticks conversion to it.
- `StandardTrackingWheelLocalizer` is an alternative three-wheel example. It currently names drive
  motor ports; do not assume those readings are independent dead wheels. Configure actual encoders,
  mounting offsets and directions before selecting this localizer.
- `SampleTankDrive` is an unused alternative for a tank robot and requires its own hardware mapping
  and `imu`. It is not the starter's mecanum configuration.
- `trajectorysequence/` builds and runs paths, turns and waits; `sequencesegment/` describes each step.
  `FTCDashboard/` supplies visualization, log files, encoder utilities and tuning helpers.

## Bring-up order

1. Check the existing drive directions and motor configuration first.
2. Configure `pinpointIMU`; verify pod type, encoder resolution, offsets and directions with the
   methods on `Main/GoBildaPinpointDriver`. This template does not guess the new mounting setup.
3. Check localization scale, axes and heading with the appropriate diagnostic before following paths.
   The Pinpoint pose helper uses millimeters/radians; Road Runner uses inches/radians.
4. Tune drive constraints, feedforward and follower gains for the actual chassis.
5. Define real start poses and paths under `Autonomous/Trajectories`, then add an explicit routine.

Use async following in competition routines and call `drive.update()` from the STOP-aware OpMode loop.
The retained blocking convenience methods and tuning tools are reference utilities; they are not
called by the starter autonomous. Do not run `IterativeController.TeleOp()` or flush `allDrive`
through `MotorControl` while Road Runner owns the drive.
