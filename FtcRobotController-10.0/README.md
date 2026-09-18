# BioBuzz — FTC Robot Code

This is **BioBuzz's main robot codebase for the current FTC season**. It contains our robot's
driver controls, hardware interfaces, subsystem controllers, autonomous framework, and testing tools.

Development starts from our existing drivebase and controller → subsystem → hardware architecture.
We will build this season's mechanisms, driver actions, and autonomous routines within that structure
as the robot develops.

**Start with [the TeamCode guide](TeamCode/README.md)** for the class relationships, loop order,
controls, hardware configuration, and steps for adding mechanisms and autonomous routines.

## Current development status

The drivebase is implemented. Mechanism controllers and autonomous routines are currently idle
extension points, ready for this season's robot-specific behavior.

- `GeneralRedTeleOp` and `GeneralBlueTeleOp`: gamepad 1 mecanum drive with four motors only.
- `GeneralAutonomous`: non-blocking alliance selection, then an idle routine with no motion.
- Bench-test templates and Road Runner tuning OpModes: retained but marked `@Disabled`.
- `MeepMeepTesting`: a desktop preview with an idle sequence and no previous field path.

The required configuration names are **`frontLeftMotor`**, **`backLeftMotor`**,
**`frontRightMotor`**, and **`backRightMotor`**. No mechanism, camera or odometry device is
required by the three main OpModes. Motor directions and joystick mixing use our existing chassis
configuration; verify them on the physical robot before driving.

## Project layout

| Location | Purpose |
| --- | --- |
| `TeamCode/` | BioBuzz robot entry points, shared hardware adapters, subsystem controllers and autonomous framework |
| `FtcRobotController/` | FTC Android application and upstream SDK examples; normally leave these intact |
| `MeepMeepTesting/` | Desktop trajectory sandbox; does not deploy to the robot |
| `MeepMeep/` | Retained unused module scaffold; not included in `settings.gradle` |
| `gradle/`, root build files | Existing Android/Java build infrastructure |
| `doc/SDK_README.md` | Original SDK README, links and release history, preserved as upstream reference |

## Build and check

Use **JDK 17** for this repository's Gradle wrapper and desktop module. Configure the Android SDK
through Android Studio (`local.properties`) or `ANDROID_HOME`. Keep machine-specific paths out of Git.

```sh
bash gradlew :TeamCode:testDebugUnitTest :TeamCode:assembleDebug :MeepMeepTesting:compileJava
```

On Windows, use `gradlew.bat` with the same task names. The APK is produced under
`TeamCode/build/outputs/apk/debug/`. Unit tests use mocked hardware; they validate wiring, motor
mixing, input edges and lifecycle behavior, not physical calibration.

The codebase currently uses FTC SDK **10.0.0** and Road Runner **0.5.6**. SDK updates and this season's
field configuration should be handled explicitly as part of robot development.

## Developing the robot

Keep hardware mapping in `HardwareInterface`, mechanism behavior in `Subsystems`, and autonomous
sequencing in `Autonomous`. Use `Main/Dependencies` to connect those pieces to the OpMode controllers.

When adding a feature, document its controls, hardware names, and calibration values so another
BioBuzz team member can understand and work on it. See the [TeamCode guide](TeamCode/README.md)
for the update flow and step-by-step instructions for extending the robot.
