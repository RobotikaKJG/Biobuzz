package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Body of the CONTROL loop (runs on {@link ControlThread}). Handles every
 * non-drive, non-turret subsystem: gamepad edge detection, LED, buttons, intake,
 * outtake, and the single batched {@code setMotors(notDrive)} write (motors 4..7).
 *
 * The drivebase runs on the main OpMode thread (DrivebaseController, motors 0..3);
 * the turret + localizer run on {@link TurretThread}. This class therefore no
 * longer touches the drive motors, the turret servos, or the localizer.
 */
public class IterativeController {
    private final MotorControl motorControl;
    private final Gamepad gamepad1;
    private final Gamepad currentGamepad1 = new Gamepad();
    private final Gamepad prevGamepad1 = new Gamepad();
    private final EdgeDetection edgeDetection;
    private final ButtonControl buttonControl;
    private final OuttakeControl outtakeControl;
    private final IntakeControl intakeControl;
    private final SensorControl sensorControl;
    private final RevBlinkinLedDriver led;
    private RevBlinkinLedDriver.BlinkinPattern lastLedPattern = null;
    private boolean isLimelightRecalibrating = false;

    public IterativeController(Dependencies dependencies) {
        gamepad1 = dependencies.gamepad1;
        edgeDetection = dependencies.edgeDetection;
        motorControl = dependencies.motorControl;
        currentGamepad1.copy(this.gamepad1);
        prevGamepad1.copy(currentGamepad1);
        buttonControl = dependencies.createSubsystemControl();
        outtakeControl = dependencies.createOuttakeControl();
        intakeControl = dependencies.createIntakeControl();
        sensorControl = dependencies.sensorControl;

        led = dependencies.hardwareMap.get(RevBlinkinLedDriver.class, "led");

        sensorControl.initLimelight(0);

        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
    }

    public void TeleOp() {
        updateCommonValues();

        if (edgeDetection.rising(GamepadIndexValues.rightStickButton))
            GlobalVariables.far = !GlobalVariables.far;

        if (edgeDetection.rising(GamepadIndexValues.dpadLeft)) {
            isLimelightRecalibrating = true;
        }

        if (isLimelightRecalibrating) {
            if (sensorControl.resetLocalizerWithLimelight()) {
                isLimelightRecalibrating = false;
            }
        }

        RevBlinkinLedDriver.BlinkinPattern ledPattern = isLimelightRecalibrating
                ? RevBlinkinLedDriver.BlinkinPattern.VIOLET
                : (GlobalVariables.far
                ? RevBlinkinLedDriver.BlinkinPattern.SKY_BLUE
                : RevBlinkinLedDriver.BlinkinPattern.HOT_PINK);

        if (ledPattern != lastLedPattern) {
            led.setPattern(ledPattern);
            lastLedPattern = ledPattern;
        }

        buttonControl.update();
        intakeControl.update();
        outtakeControl.update();

        // Drive motors (0..3) are written by the drive loop; write everything else here.
        motorControl.setMotors(MotorConstants.notDrive);
    }

    private void updateCommonValues() {
        prevGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);
        edgeDetection.refreshGamepadIndex(currentGamepad1, prevGamepad1);
        // localizer.update() + heading reset moved to TurretThread (owns the localizer).
    }
}
