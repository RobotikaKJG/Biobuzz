# BioBuzz — FTC Robot Code

Welcome to **BioBuzz’s main robot codebase for the current FTC season**.

This repository contains our driver controls, hardware interfaces, subsystem controllers, autonomous framework, and testing tools.

## Getting Started

1. Clone the repository:

   ```sh
   git clone https://github.com/RobotikaKJG/Biobuzz.git
   ```

2. Open `Biobuzz/FtcRobotController-10.0` in Android Studio.
3. Configure the Android SDK and select **JDK 17** for Gradle.
4. Allow Gradle to synchronize.
5. Configure the robot’s four drive motors using the names above.
6. Build and deploy the `TeamCode` application to the Robot Controller.

## Build and Test

From `FtcRobotController-10.0/`, run:

```sh
bash gradlew :TeamCode:testDebugUnitTest :TeamCode:assembleDebug :MeepMeepTesting:compileJava
```

On Windows, use `gradlew.bat` with the same task names.

The Android APK is generated in:

```text
TeamCode/build/outputs/apk/debug/
```

Unit tests check controller wiring and software behavior using mocked hardware. Physical operation and calibration must also be tested on the robot.

## Development Guidelines

- Keep hardware mapping in `HardwareInterface`.
- Keep mechanism behavior in `Subsystems`.
- Connect shared components through `Dependencies`.
- Keep autonomous sequencing in `Autonomous`.
- Use short, non-blocking controller updates.
- Stop motors and continuous-rotation servos when an OpMode exits.
- Document hardware names, controls, calibration values, and important class relationships.
- Test new mechanisms individually before adding them to coordinated routines.

See the [TeamCode guide](FtcRobotController-10.0/TeamCode/README.md) for detailed instructions.

## Dependencies

- FTC SDK **10.0.0**
- Road Runner **0.5.6**
- FTC Dashboard
- MeepMeep
