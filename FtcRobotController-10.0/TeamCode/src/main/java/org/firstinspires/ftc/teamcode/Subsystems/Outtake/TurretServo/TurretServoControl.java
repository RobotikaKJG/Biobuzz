package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class TurretServoControl {
    private final ServoControl servoControl;
    private final SensorControl sensorControl;
    private TurretServoStates prevTurretServoState = TurretServoStates.idle;

    private final int servoGear = 55;
    private final int turretGear = 230;

    private double turretStartDeg = 0;
    private boolean turretZeroed = false;

    public TurretServoControl(ServoControl servoControl, SensorControl sensorControl) {

        this.servoControl = servoControl;
        this.sensorControl = sensorControl;

        initTurretZero();
    }


    public void update() {
        if (OuttakeStates.getTurretServoState() != prevTurretServoState) {
            updateStates();
            prevTurretServoState = OuttakeStates.getTurretServoState();
        }

        // continuous control in adjust mode
        if (OuttakeStates.getTurretServoState() == TurretServoStates.adjust) {
            updateStates();
        }
    }

    public void updateStates() {
        switch (OuttakeStates.getTurretServoState()) {
            case adjust:
                updateTurretWithLimelight();
                break;
            case idle:
                servoControl.setServoSpeed(ServoConstants.turretServo, 0);
                break;
        }

    }

    private void updateTurretWithLimelight() {
        initTurretZero();

        double currentDeg = servoControl.getCRSDegrees(ServoConstants.turretServo);
        double minDeg = turretStartDeg - OuttakeConstants.maxTurretAngle;
        double maxDeg = turretStartDeg + OuttakeConstants.maxTurretAngle;

        double tx = sensorControl.getDisToCenter(); // degrees

        // No valid target
        if (Double.isNaN(tx)) {
            servoControl.setServoSpeed(ServoConstants.turretServo, 0);
            return;
        }

        // Deadband
        if (Math.abs(tx) < OuttakeConstants.turretTolerance) {
            servoControl.setServoSpeed(ServoConstants.turretServo, 0);
            return;
        }

        // Proportional control
        double power = tx * OuttakeConstants.kpTurret;
        power = Math.max(-OuttakeConstants.turretServoMaxSpeed,
                Math.min(OuttakeConstants.turretServoMaxSpeed, power));

        // Enforce limits
        if ((currentDeg <= minDeg && power < 0) ||
                (currentDeg >= maxDeg && power > 0)) {
            power = 0;
        }

        servoControl.setServoSpeed(ServoConstants.turretServo, power);
    }

    private void initTurretZero() {
        if (!turretZeroed) {
            turretStartDeg = servoControl.getCRSDegrees(ServoConstants.turretServo);
            turretZeroed = true;
        }
    }
}
