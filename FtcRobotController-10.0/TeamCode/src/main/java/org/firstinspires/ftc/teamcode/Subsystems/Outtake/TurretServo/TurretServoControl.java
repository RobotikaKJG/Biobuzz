package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;

public class TurretServoControl {

    private final ServoControl servo;
    private final SensorControl sensor;

    /* ================= CONFIG ================= */

    // Maximum allowed rotation from zero (degrees)
    private static final double MAX_TURRET_ANGLE_DEG = 85.0;

    // Limelight proportional gain
    private static final double KP = 0.01;

    // Max CR servo speed
    private static final double MAX_SERVO_SPEED = 0.5;

    // Deadband in degrees
    private static final double TARGET_TOLERANCE_DEG = 1.0;

    private static final double GEAR_RATIO = 55.0 / 230.0; // turret / servo

    /* ================= STATE ================= */

    private double lastAnalog = 0.0;
    private int rotationCount = 0;

    // Continuous turret angle (0° = start of run)
    private double turretAngleDeg = 0.0;

    private double analogZero = 0.0;
    private double servoAngleZeroDeg = 0.0;

    private static final double AIM_OFFSET_DEG = 2.0;

    /* ================= CONSTRUCTOR ================= */

    public TurretServoControl(ServoControl servo, SensorControl sensor) {
        this.servo = servo;
        this.sensor = sensor;

        // Zero turret at startup
        analogZero = servo.getCRSPos(ServoConstants.turretAnalog);
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
        double analog = servo.getCRSPos(ServoConstants.turretAnalog); // 0–1
        double delta = analog - lastAnalog;

        if (delta > 0.5) {
            rotationCount--;
        } else if (delta < -0.5) {
            rotationCount++;
        }

        lastAnalog = analog;

        // Continuous SERVO angle
        double servoAngleDeg =
                rotationCount * 360.0 + analog * 360.0;

        // Subtract startup angle, THEN apply gear ratio
        turretAngleDeg =
                (servoAngleDeg - servoAngleZeroDeg) * (55.0 / 230.0);
    }


    /* ================= CONTROL ================= */

    private void updateTurretControl() {
        double tx = sensor.getDisToCenter() + AIM_OFFSET_DEG;

        // No target
        if (Double.isNaN(tx)) {
            servo.setServoSpeed(ServoConstants.turretServo, 0);
            return;
        }

        // Deadband
        if (Math.abs(tx) < TARGET_TOLERANCE_DEG) {
            servo.setServoSpeed(ServoConstants.turretServo, 0);
            return;
        }

        // Proportional control
        double power = tx * KP;

        // Clamp speed
        power = Math.max(-MAX_SERVO_SPEED, Math.min(MAX_SERVO_SPEED, power));

        // Enforce limits
        if ((turretAngleDeg <= -MAX_TURRET_ANGLE_DEG && power > 0) ||
                (turretAngleDeg >=  MAX_TURRET_ANGLE_DEG && power < 0)) {
            power = 0;
        }

        servo.setServoSpeed(ServoConstants.turretServo, power);
    }

    /* ================= GETTERS ================= */

    public double getTurretAngleDeg() {
        return turretAngleDeg;
    }

    public boolean atLeftLimit() {
        return turretAngleDeg <= -MAX_TURRET_ANGLE_DEG;
    }

    public boolean atRightLimit() {
        return turretAngleDeg >= MAX_TURRET_ANGLE_DEG;
    }
}
