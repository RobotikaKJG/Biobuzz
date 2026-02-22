package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.Drivebase;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoResetPos.AutoResetPosControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoControl;

public class Dependencies {
    public final HardwareMap hardwareMap;
    public final Gamepad gamepad1;
    public final Gamepad gamepad2;
    public final Telemetry telemetry;
    public final StandardTrackingWheelLocalizer localizer;
    public MotorControl motorControl;
    public SensorControl sensorControl;
    public ServoControl servoControl;
    public TurretServoControl turretServoControl;
    public EdgeDetection edgeDetection = new EdgeDetection();
    public EdgeDetection gamepad2EdgeDetection = new EdgeDetection();

    public Dependencies(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {

        this.hardwareMap = hardwareMap;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.telemetry = telemetry;
        localizer = new StandardTrackingWheelLocalizer(hardwareMap);
        motorControl = new MotorControl(hardwareMap);
        sensorControl = new SensorControl(hardwareMap, edgeDetection, localizer);
        servoControl = new ServoControl(hardwareMap);
        turretServoControl = new TurretServoControl(servoControl, sensorControl);
    }

    public Drivebase createDrivebase() {
        return new Drivebase(gamepad1,gamepad2, motorControl, sensorControl);
    }

    public DrivebaseController createDrivebaseController() {
        return new DrivebaseController(createDrivebase(), edgeDetection);
    }

    ButtonControl createSubsystemControl() {
        return new ButtonControl(edgeDetection, sensorControl, motorControl);
    }

    ButtonControl createSubsystemControl2() {
        return new ButtonControl(gamepad2EdgeDetection, sensorControl, motorControl);
    }

    public OuttakeControl createOuttakeControl() {
        return new OuttakeControl(createOuttakeMotorControl(), createAutoCycleShootLogic(), createFeederMotorControl(), createTurretServoControl(), createAutoOuttakeFarCloseControl(), createAutoResetPosControl());
    }

    private OuttakeMotorControl createOuttakeMotorControl() {
        return new OuttakeMotorControl(motorControl, sensorControl);
    }

    public IntakeControl createIntakeControl() {
        return new IntakeControl(createIntakeMotorControl(), createLockServoControl());
    }

    private IntakeMotorControl createIntakeMotorControl() {
        return new IntakeMotorControl(motorControl);
    }

    private FeederMotorControl createFeederMotorControl() {
        return new FeederMotorControl(motorControl);
    }

    private TurretServoControl createTurretServoControl() {
        return turretServoControl;
    }

    private AutoCycleShootLogic createAutoCycleShootLogic() {
        return new AutoCycleShootLogic(sensorControl, motorControl);
    }

    private AutoOuttakeFarCloseControl createAutoOuttakeFarCloseControl() {
        return new AutoOuttakeFarCloseControl(sensorControl);
    }

    private LockServoControl createLockServoControl() {
        return new LockServoControl(servoControl);
    }

    private AutoResetPosControl createAutoResetPosControl() {
        return new AutoResetPosControl(sensorControl);
    }
}
