package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.Drivebase;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor.TransferMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo.TransferServoControl;

/**
 * Composition root: maps shared hardware once and wires it into the robot's controllers.
 * OpModes supply SDK objects here; subsystem code receives only the adapters it needs.
 * Factories create controllers, not new hardware adapters. Build one controller tree per OpMode.
 * Default hardware is just the four drive motors; mechanism controllers are idle extension points.
 */
public class Dependencies {
    public final HardwareMap hardwareMap;
    public final Gamepad gamepad1;
    public final Gamepad gamepad2;
    public final Telemetry telemetry;
    public final MotorControl motorControl;
    public final SensorControl sensorControl;
    public final ServoControl servoControl;
    public final EdgeDetection edgeDetection = new EdgeDetection();
    public final EdgeDetection gamepad2EdgeDetection = new EdgeDetection();

    public Dependencies(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {

        this.hardwareMap = hardwareMap;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.telemetry = telemetry;
        motorControl = new MotorControl(hardwareMap);
        sensorControl = new SensorControl(hardwareMap, edgeDetection);
        servoControl = new ServoControl(hardwareMap);
        // Opt in only after configuring and calibrating pinpointIMU:
        // sensorControl.initPinpoint();
    }

    public Drivebase createDrivebase() {
        return new Drivebase(gamepad1,gamepad2, motorControl, sensorControl);
    }

    public DrivebaseController createDrivebaseController() {
        return new DrivebaseController(createDrivebase(), edgeDetection);
    }

    ButtonControl createSubsystemControl() {
        return new ButtonControl(edgeDetection, sensorControl);
    }

    ButtonControl createSubsystemControl2() {
        return new ButtonControl(gamepad2EdgeDetection, sensorControl);
    }

    public OuttakeControl createOuttakeControl() {
        return new OuttakeControl(createTurnServoControl(), createOuttakeMotorControl(), createTransferServoControl(), createTransferMotorControl());
    }

    private OuttakeServoControl createTurnServoControl() {
        return new OuttakeServoControl(servoControl, sensorControl);
    }

    private OuttakeMotorControl createOuttakeMotorControl() {
        return new OuttakeMotorControl(motorControl);
    }

    public IntakeControl createIntakeControl() {
        return new IntakeControl(createIntakeMotorControl(), createTransferCRServoControl());
    }

    private IntakeMotorControl createIntakeMotorControl() {
        return new IntakeMotorControl(motorControl);
    }

    private TransferCRServoControl createTransferCRServoControl() {
        return new TransferCRServoControl(servoControl);
    }

    private TransferServoControl createTransferServoControl() {
        return new TransferServoControl(servoControl);
    }

    private TransferMotorControl createTransferMotorControl() {
        return new TransferMotorControl(motorControl);
    }
    /** Called in the OpMode's finally block, including when STOP arrives before START. */
    public void stop() {
        try {
            motorControl.resetMotors();
        } finally {
            try {
                servoControl.stop();
            } finally {
                sensorControl.stop();
            }
        }
    }
}
