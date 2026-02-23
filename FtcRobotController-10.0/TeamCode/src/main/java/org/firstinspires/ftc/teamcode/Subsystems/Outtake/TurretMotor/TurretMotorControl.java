package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;

public class TurretMotorControl {

    private final MotorControl motorControl;
    private final SensorControl sensorControl;

    private static final double maxTurretAngle = 85.0;
    private static final double kP = 0.01;
    private static final double maxSpeed = 1.0;
    private static final double minSpeed = 0.05;
    private static final double tolerance = 0.2;
    private static final double gearRatio = 40.0 / 120.0;
    private double turretAngleDeg = 0.0;

    public TurretMotorControl(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;
    }

    public void update() {

    }

    /* ================= GETTERS ================= */

    public double getTurretAngleDeg() {
        return turretAngleDeg;
    }
}
