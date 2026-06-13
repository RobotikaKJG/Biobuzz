package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TurretServoControl {

    private final ServoControl servoControl;
    private final SensorControl sensorControl;

    private volatile double turretAngleDeg = 0.0;   // volatile: read by main thread for telemetry
    private volatile double targetAngleDeg = 0.0;   // volatile: read by main thread for telemetry
    private double targetServoPos = 0.0;

    // Limit held while the aim target is in the unreachable dead zone behind the
    // turret (NaN = target currently reachable). See resolveTargetWithinLimits().
    private double deadZoneLimitDeg = Double.NaN;

    // Slew-limiter state: last position actually commanded to the servos.
    private double lastCommandedPos = Double.NaN;
    private long lastUpdateNs = 0;

    public TurretServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if (!OuttakeStates.isTurretTrackingEnabled()) return;
        if (sensorControl.getLocalizerPose().getY() < -10 && !GlobalVariables.far) {
            return;
        }

        double target = sensorControl.getTurretTargetAngleDegrees();
        if (!isFinite(target)) {
            // Pose/velocity glitch produced a bad angle. Hold the last good aim rather
            // than writing an illegal (NaN) servo position. Root cause is guarded in the
            // Pinpoint driver; this is defense-in-depth so the turret never errors.
            return;
        }

        targetAngleDeg = resolveTargetWithinLimits(target);
        turretAngleDeg = getCurrentTurretAngleDeg();

        double pos = (targetAngleDeg * OuttakeConstants.turretGearRatio) / OuttakeConstants.turretServoTravel + 0.5;
        // Never command past the servos' usable travel — past turretServoMax the
        // turret presses into its hard stop and stalls (even if the angle limits
        // are someday re-widened, this clamp keeps the hardware safe).
        pos = clamp(pos, OuttakeConstants.turretServoMin, OuttakeConstants.turretServoMax);
        if (!isFinite(pos)) {
            return; // never write NaN to the servo
        }
        targetServoPos = slewLimit(pos);
        servoControl.setTurretServosPos(targetServoPos);
    }

    /**
     * Clamp the aim angle to the reachable window [turretLimitRight, turretLimitLeft].
     * When the target is in the dead zone behind the turret, park at the limit the
     * target left from and HOLD it until the target re-enters the window. Without
     * the hold, the ±180° wrap in the aim math flips the clamped target between
     * the two limits while the drivetrain spins, slamming the turret full-range
     * into its hard stops (the "stuck + jitter" failure).
     */
    private double resolveTargetWithinLimits(double targetDeg) {
        double left = OuttakeConstants.turretLimitLeft;
        double right = OuttakeConstants.turretLimitRight;
        if (targetDeg >= right && targetDeg <= left) {
            deadZoneLimitDeg = Double.NaN;
            return targetDeg;
        }
        if (Double.isNaN(deadZoneLimitDeg)) {
            // Just entered the dead zone: pick the angularly nearer limit once.
            double pastLeft = normalizeDegrees(targetDeg - left);
            double beforeRight = normalizeDegrees(right - targetDeg);
            deadZoneLimitDeg = (pastLeft <= beforeRight) ? left : right;
        }
        return deadZoneLimitDeg;
    }

    /**
     * Rate-limit the commanded servo position to turretServoSlewPerSec so a target
     * jump (dead-zone exit, pose correction) becomes a controlled sweep instead of
     * a full-speed slam of three ganged servos.
     */
    private double slewLimit(double desiredPos) {
        long now = System.nanoTime();
        if (Double.isNaN(lastCommandedPos)) {
            lastCommandedPos = desiredPos;
            lastUpdateNs = now;
            return desiredPos;
        }
        double dt = (now - lastUpdateNs) / 1e9;
        lastUpdateNs = now;
        dt = clamp(dt, 0.0, 0.05); // bound the step if the loop ever stalls
        double maxStep = OuttakeConstants.turretServoSlewPerSec * dt;
        lastCommandedPos += clamp(desiredPos - lastCommandedPos, -maxStep, maxStep);
        return lastCommandedPos;
    }

    private static double normalizeDegrees(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
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