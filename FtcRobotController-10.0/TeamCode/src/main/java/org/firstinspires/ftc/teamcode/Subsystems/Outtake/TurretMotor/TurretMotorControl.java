package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;

public class TurretMotorControl {

    private final MotorControl motorControl;
    private final SensorControl sensorControl;

    private static final double turretLimit = 90.0;
    private boolean rightLimitWasPressed = false;
    private static final double limitSwitchRightAngle = -90.0;
    private static final double kP = 0.02;
    private static final double maxSpeed = 0.9;
    private static final double minSpeed = 0.07;
    private static final double tolerance = 0.2;    // degrees
    private static final double gearRatio = 120.0 / 74.0;
    private static final double ticksPerDegree = (1425.5 * gearRatio) / 360;
    private double desiredAngle = 0;

    private double turretAngleDeg = 0.0;
    private double targetAngleDeg = 0.0;

    public TurretMotorControl(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;

        motorControl.setMotorMode(MotorConstants.turret, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update() {
//        if (GlobalVariables.isAutonomous) {
//            desiredAngle = -45.0;
//        }
//        else {
//            desiredAngle = sensorControl.getTurretTargetAngleDegrees();
//        }

//        desiredAngle = sensorControl.getTurretTargetAngleDegrees();
//
//        targetAngleDeg = clamp(desiredAngle, -turretLimit, turretLimit);
//
//        turretAngleDeg = getCurrentTurretAngleDeg();
//        boolean rightLimitPressed = sensorControl.isLimitSwitchPressed();
//
//        if (rightLimitPressed && !rightLimitWasPressed) {
//            motorControl.resetMotorEncoders(MotorConstants.turret);
//            rightLimitWasPressed = true;
//        }
//
//        if (rightLimitWasPressed && !rightLimitPressed) {
//            rightLimitWasPressed = false;
//        }
//
//        turretAngleDeg = getCurrentTurretAngleDeg() + limitSwitchRightAngle;
//
//        double error = targetAngleDeg - turretAngleDeg;
//
//        if (Math.abs(error) < tolerance) {
//            motorControl.setMotorSpeed(MotorConstants.turret, 0);
//            motorControl.setMotors(MotorConstants.turret);
//            return;
//        }
//
//        double power = error * kP;
//
//        if (Math.abs(power) < minSpeed) {
//            power = Math.signum(power) * minSpeed;
//        }
//
//        power = clamp(power, -maxSpeed, maxSpeed);
//
//        if ((turretAngleDeg >= turretLimit && power > 0) ||
//                (turretAngleDeg <= -turretLimit && power < 0)) {
//            power = 0;
//        }
//
//        if (turretAngleDeg <= -90 && !rightLimitPressed) {
//            power = -0.2;
//        }
//
//        if (rightLimitPressed && power < 0) {
//            power = 0;
//        }
//
//        motorControl.setMotorSpeed(MotorConstants.turret, power);
//        motorControl.setMotors(MotorConstants.turret);
    }

    private double getCurrentTurretAngleDeg() {
        int ticks = motorControl.getMotorPosition(MotorConstants.turret);
        return ticks / ticksPerDegree;
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