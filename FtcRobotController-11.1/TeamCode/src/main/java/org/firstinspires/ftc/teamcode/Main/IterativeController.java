package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoControl;

public class IterativeController {
    private final MotorControl motorControl;
    private final Gamepad gamepad1;
    private final Gamepad currentGamepad1 = new Gamepad();
    private final Gamepad prevGamepad1 = new Gamepad();
    private final Gamepad gamepad2;
    private final Gamepad currentGamepad2 = new Gamepad();
    private final Gamepad prevGamepad2 = new Gamepad();
    private final EdgeDetection edgeDetection;
    private final EdgeDetection gamepad2EdgeDetection;
    private final DrivebaseController drivebaseController;
    private final StandardTrackingWheelLocalizer localizer;
    private final SampleMecanumDrive drive;
    private final ButtonControl buttonControl;
    private final ButtonControl subsystemControl2;
    private final OuttakeControl outtakeControl;
    private final IntakeControl intakeControl;
    private final SensorControl sensorControl;
    private final TurretServoControl turretServoControl;
    private final RevBlinkinLedDriver led;

    public IterativeController(Dependencies dependencies) {
        drivebaseController = dependencies.createDrivebaseController();
        gamepad1 = dependencies.gamepad1;
        gamepad2 = dependencies.gamepad2;
        edgeDetection = dependencies.edgeDetection;
        gamepad2EdgeDetection = dependencies.gamepad2EdgeDetection;
        motorControl = dependencies.motorControl;
        currentGamepad1.copy(this.gamepad1);
        prevGamepad1.copy(currentGamepad1);
        localizer = dependencies.localizer;
        drive = dependencies.drive;
        buttonControl = dependencies.createSubsystemControl();
        subsystemControl2 = dependencies.createSubsystemControl2();
        outtakeControl = dependencies.createOuttakeControl();
        intakeControl = dependencies.createIntakeControl();
        sensorControl = dependencies.sensorControl;
        turretServoControl = dependencies.turretServoControl;

        led = dependencies.hardwareMap.get(RevBlinkinLedDriver.class, "led");

        sensorControl.initLimelight(0);
        // Sync Pinpoint to Road Runner pose so turret angle uses same localization as autonomous
        sensorControl.setPositionFromRoadRunner(drive.getPoseEstimate());

        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
    }

    public void TeleOp() {
        updateCommonValues();

        if (edgeDetection.rising(GamepadIndexValues.rightStickButton))
            GlobalVariables.far = !GlobalVariables.far;

        led.setPattern(GlobalVariables.far
                ? RevBlinkinLedDriver.BlinkinPattern.SKY_BLUE
                : RevBlinkinLedDriver.BlinkinPattern.HOT_PINK);

        drivebaseController.updateState();
        buttonControl.update();

        intakeControl.update();
        outtakeControl.update();

        // Write all motor powers ONCE, after all subsystems have computed their values
        motorControl.setMotors(MotorConstants.all);
    }

    private void updateCommonValues() {
        prevGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);
        edgeDetection.refreshGamepadIndex(currentGamepad1, prevGamepad1);

        // Single Pinpoint I2C read for the entire loop
        sensorControl.updateLocalizer();

        // Continuous vision correction: blend Pinpoint position toward Limelight when tags visible
        // Only when turret is near center (Limelight is mounted on turret)
//        sensorControl.continuousVisionUpdate(turretServoControl.getTurretAngleDeg());

        // Use same TwoWheelTrackingLocalizer as autonomous so turret angle has tuned position
        drive.updatePoseOnly();
        sensorControl.setPositionFromRoadRunner(drive.getPoseEstimate());
    }

    private boolean gamepad1Active(){
        return currentGamepad1.square || currentGamepad1.triangle || currentGamepad1.dpad_up || currentGamepad1.dpad_down
                || !currentGamepad1.atRest() || currentGamepad1.left_bumper || currentGamepad1.left_trigger != 0
                || currentGamepad1.right_bumper || currentGamepad1.right_trigger != 0;
//        return true;
    }

    private boolean gamepad2Active(){
        return currentGamepad2.square || currentGamepad2.triangle || currentGamepad2.dpad_up || currentGamepad2.dpad_down
                || !currentGamepad2.atRest() || currentGamepad2.left_bumper || currentGamepad2.left_trigger != 0
                || currentGamepad2.right_bumper || currentGamepad2.right_trigger != 0;
//        return false;
    }
}
