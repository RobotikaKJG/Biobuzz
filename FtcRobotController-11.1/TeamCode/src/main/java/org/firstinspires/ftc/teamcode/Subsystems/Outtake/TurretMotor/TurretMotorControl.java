package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TurretMotorControl {

    private final MotorControl motorControl;
    private final SensorControl sensorControl;

    private static final double turretLimit = 45.0;
    // private boolean rightLimitWasPressed = false; // [LimitSwitch Disabled]
    // private static final double limitSwitchRightAngle = -90.0; // [LimitSwitch Disabled]
    private static final double kP = 0.02;
    private static final double maxSpeed = 0.9;
    private static final double minSpeed = 0.07;
    private static final double tolerance = 0.2;    // degrees
    private static final double gearRatio = 120.0 / 74.0;
    private static final double ticksPerDegree = (480 * gearRatio) / 360;

    private double turretAngleDeg = 0.0;
    private double targetAngleDeg = 0.0;

    public TurretMotorControl(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;

        // Reset encoder at start so current position is 0 degrees
        motorControl.resetMotorEncoders(MotorConstants.turret);
        motorControl.setMotorMode(MotorConstants.turret, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update() {
        if (!OuttakeStates.isTurretTrackingEnabled()) {
            motorControl.setMotorSpeed(MotorConstants.turret, 0);
            return;
        }

        targetAngleDeg = clamp(sensorControl.getTurretTargetAngleDegrees(), -turretLimit, turretLimit);

        /* [LimitSwitch Logic Disabled]
        boolean rightLimitPressed = sensorControl.isLimitPressed();

        if (rightLimitPressed && !rightLimitWasPressed) {
            motorControl.resetMotorEncoders(MotorConstants.turret);
            motorControl.setMotorMode(MotorConstants.turret, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightLimitWasPressed = true;
        } else if (!rightLimitPressed) {
            rightLimitWasPressed = false;
        }
        */

        // Read encoder. Offset removed as we are assuming startup position is 0.
        turretAngleDeg = getCurrentTurretAngleDeg();

        double error = targetAngleDeg - turretAngleDeg;

        if (Math.abs(error) < tolerance) {
            motorControl.setMotorSpeed(MotorConstants.turret, 0);
            return;
        }

        double power = clamp(error * kP, -maxSpeed, maxSpeed);

        if (Math.abs(power) < minSpeed) {
            power = Math.signum(power) * minSpeed;
        }

        // Software Bounds: Stop power if we hit limits
        if ((turretAngleDeg >= turretLimit && power > 0) ||
                (turretAngleDeg <= -turretLimit && power < 0)) {
            power = 0;
        }

        /* [LimitSwitch Safety Logic Disabled]
        if (turretAngleDeg <= -90 && !rightLimitPressed) {
            power = -0.2;
        }

        if (rightLimitPressed && power < 0) {
            power = 0;
        }
        */

        motorControl.setMotorSpeed(MotorConstants.turret, power);
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