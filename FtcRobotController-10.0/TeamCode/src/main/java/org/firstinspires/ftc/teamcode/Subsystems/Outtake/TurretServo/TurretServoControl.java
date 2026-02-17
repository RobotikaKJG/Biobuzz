package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;

public class TurretServoControl {

    private final ServoControl servoControl;
    private final SensorControl sensorControl;

    private static final double maxTurretAngle = 85.0;
    private static final double kP = 0.01;
    private static final double maxSpeed = 1.0;
    private static final double minSpeed = 0.05;
    private static final double tolerance = 0.2;
    private static final double gearRatio = 40.0 / 120.0;

    private double lastAnalog = 0.0;
    private int rotationCount = 0;
    private double turretAngleDeg = 0.0;
    private double analogZero = 0.0;
    private double servoAngleZeroDeg = 0.0;

    /* ================= CONSTRUCTOR ================= */

    public TurretServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;

        analogZero = servoControl.getCRSPos(ServoConstants.turretAnalog);
        lastAnalog = analogZero;
        rotationCount = 0;

        servoAngleZeroDeg = analogZero * 360.0;
        turretAngleDeg = 0.0;
    }

    /* ================= PUBLIC UPDATE ================= */

    public void update() {
        updateTurretAngle();
        updateTurretControl();
    }

    /* ================= ANGLE TRACKING ================= */

    private void updateTurretAngle() {
        double analog = servoControl.getCRSPos(ServoConstants.turretAnalog);
        double delta = analog - lastAnalog;

        if (delta > 0.5) {
            rotationCount--;
        } else if (delta < -0.5) {
            rotationCount++;
        }

        lastAnalog = analog;

        double servoAngleDeg =
                rotationCount * 360.0 + analog * 360.0;

        turretAngleDeg =
                (servoAngleDeg - servoAngleZeroDeg) * gearRatio;
    }

    /* ================= CONTROL ================= */
    private void updateTurretControl() {
        // Get the desired turret angle from the sensor control (using the pinoint odometers)
        double desiredTurretAngleDeg = sensorControl.getTurretTargetAngleDegrees();

        // Clamp the desired angle to turret's physical range
        if (desiredTurretAngleDeg > maxTurretAngle) {
            desiredTurretAngleDeg = maxTurretAngle;
        } else if (desiredTurretAngleDeg < -maxTurretAngle) {
            desiredTurretAngleDeg = -maxTurretAngle;
        }

        // Calculate error (in degrees) between current and desired angle
        double angleError = desiredTurretAngleDeg - turretAngleDeg;

        // Reverse the direction of the servo by negating angleError
        angleError = -angleError;

        // If the angle error is within the tolerance, stop the servo
        if (Math.abs(angleError) <= tolerance) {
            servoControl.setServoSpeed(ServoConstants.turretServo, 0.0);
            return;
        }

        // Simple proportional control for servo power
        double servoPower = kP * angleError;

        // Clamp servo power to maximum speed
        if (servoPower > maxSpeed) {
            servoPower = maxSpeed;
        } else if (servoPower < -maxSpeed) {
            servoPower = -maxSpeed;
        }

        // Add minimum servo speed so the servo doesn't stop when power is nonzero but small
        if (servoPower > 0 && servoPower < minSpeed) {
            servoPower = minSpeed;
        } else if (servoPower < 0 && servoPower > -minSpeed) {
            servoPower = -minSpeed;
        }

        // Send command to the turret servo (continuous rotation servo assumed)
        servoControl.setServoSpeed(ServoConstants.turretServo, servoPower);
    }

    /* ================= GETTERS ================= */

    public double getTurretAngleDeg() {
        return turretAngleDeg;
    }
}
