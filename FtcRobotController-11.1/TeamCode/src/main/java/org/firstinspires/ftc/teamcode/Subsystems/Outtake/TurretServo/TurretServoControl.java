package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TurretServoControl {

    private final ServoControl servoControl;
    private final SensorControl sensorControl;

    private static final double turretLimitRight = -135.0;
    private static final double turretLimitLeft = 100.0;
    private static final double gearRatio = 1;
    private static final double servoTravel = 323;

    private volatile double turretAngleDeg = 0.0;   // volatile: read by main thread for telemetry
    private volatile double targetAngleDeg = 0.0;   // volatile: read by main thread for telemetry
    private double targetServoPos = 0.0;

    public TurretServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if (!OuttakeStates.isTurretTrackingEnabled()) {
            return;
        }

        targetAngleDeg = clamp(sensorControl.getTurretTargetAngleDegrees(), turretLimitRight, turretLimitLeft);
        turretAngleDeg = getCurrentTurretAngleDeg();

        targetServoPos = (targetAngleDeg * gearRatio) / servoTravel + 0.5;
        targetServoPos = clamp(targetServoPos, 0.01, 0.99);

        servoControl.setTurretServosPos(targetServoPos);
    }

    private double getCurrentTurretAngleDeg() {
        double pos = servoControl.getServoPos(ServoConstants.turretServo1);
        return ((pos - 0.5) * servoTravel) / gearRatio;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public double getTurretAngleDeg() {
        return turretAngleDeg;
    }

    public double getTargetAngleDeg() {
        return targetAngleDeg;
    }
}