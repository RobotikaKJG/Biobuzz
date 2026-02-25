package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;

public class TurretMotorControl {

    private final MotorControl motorControl;
    private final SensorControl sensorControl;

    private static final double turretLimit = 40.0;
    private static final double kP = 0.01;
    private static final double maxSpeed = 0.3;
    private static final double minSpeed = 0.1;
    private static final double tolerance = 0.5;    // degrees
    private static final double gearRatio = 287.0 / 30.0;
    private static final double ticksPerDegree = (145.6 * gearRatio) / 360;

    private double turretAngleDeg = 0.0;
    private double targetAngleDeg = 0.0;

    public TurretMotorControl(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;

        motorControl.setMotorMode(MotorConstants.turret, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update() {
        double desiredAngle = sensorControl.getTurretTargetAngleDegrees();

        targetAngleDeg = clamp(desiredAngle, -turretLimit, turretLimit);

        turretAngleDeg = getCurrentTurretAngleDeg();

        double error = angleErrorDeg(targetAngleDeg, turretAngleDeg);

        if (Math.abs(error) < tolerance) {
            motorControl.setMotorSpeed(MotorConstants.turret, 0);
            motorControl.setMotors(MotorConstants.turret);
            return;
        }

        double power = error * kP;

        if (Math.abs(power) < minSpeed) {
            power = Math.signum(power) * minSpeed;
        }

        power = clamp(power, -maxSpeed, maxSpeed);

        if ((turretAngleDeg >= turretLimit && power > 0) ||
                (turretAngleDeg <= -turretLimit && power < 0)) {
            power = 0;
        }

        motorControl.setMotorSpeed(MotorConstants.turret, power);
        motorControl.setMotors(MotorConstants.turret);
    }

    private double getCurrentTurretAngleDeg() {
        int ticks = motorControl.getMotorPosition(MotorConstants.turret);
        return ticks / ticksPerDegree;
    }

    private double angleErrorDeg(double target, double current) {
        double error = target - current;
        while (error > 180) error -= 360;
        while (error < -180) error += 360;
        return error;
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