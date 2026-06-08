package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TurretServoControl {

    private final ServoControl servoControl;
    private final SensorControl sensorControl;

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

        double target = sensorControl.getTurretTargetAngleDegrees();
        if (!isFinite(target)) {
            // Pose/velocity glitch produced a bad angle. Hold the last good aim rather
            // than writing an illegal (NaN) servo position. Root cause is guarded in the
            // Pinpoint driver; this is defense-in-depth so the turret never errors.
            return;
        }

        targetAngleDeg = clamp(target, OuttakeConstants.turretLimitRight, OuttakeConstants.turretLimitLeft);
        turretAngleDeg = getCurrentTurretAngleDeg();

        double pos = clamp((targetAngleDeg * OuttakeConstants.turretGearRatio) / OuttakeConstants.turretServoTravel + 0.5, 0.01, 0.99);
        if (!isFinite(pos)) {
            return; // never write NaN to the servo
        }
        targetServoPos = pos;
        servoControl.setTurretServosPos(targetServoPos);
    }

    private static boolean isFinite(double v) {
        return !Double.isNaN(v) && !Double.isInfinite(v);
    }

    private double getCurrentTurretAngleDeg() {
        double pos = servoControl.getServoPos(ServoConstants.turretServo1);
        return ((pos - 0.5) * OuttakeConstants.turretServoTravel) / OuttakeConstants.turretGearRatio;
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