package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeServoControl {
    private OuttakeServoStates prevOuttakeServoState;
    private ServoControl servoControl;
    private SensorControl sensorControl;
    private double max = OuttakeConstants.outtakeServoMaxPosClose;
    private double min = OuttakeConstants.outtakeServoMinPos;
    private double maxD = OuttakeConstants.maxDistance;
    private double minD = OuttakeConstants.minDistance;
    private double currentWait = 0;
    private boolean wasIfCalled = false;
    double servoPos = min;
    double distance = minD;

    public OuttakeServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if (prevOuttakeServoState != OuttakeStates.getOuttakeServoState()) {
            updateStates();
            prevOuttakeServoState = OuttakeStates.getOuttakeServoState();
        }
        else if (OuttakeStates.getOuttakeServoState() == OuttakeServoStates.setPosAuto) {
            updateStates();
        }
    }

    private void updateStates() {
        switch (OuttakeStates.getOuttakeServoState()) {
            case setPosAuto:
                setPosAuto();
                break;
            case setPosFar:
                setPosFar();
                break;
            case idle:
                break;
        }
    }

    private void setPosAuto() {
        System.out.println("Black1");

        if(!wasIfCalled)
        {
            addWaitTime(0.1);
            wasIfCalled = true;
        }

        if(currentWait > getSeconds()) return;

        distance = sensorControl.getTagDistance();
        if (distance <= 0) return;
        servoPos = Math.min(max, Math.max(min, min + (distance - minD) * (max - min) / (maxD - minD)));

        servoControl.setServoPos(ServoConstants.outtakeServo, servoPos);

        wasIfCalled = false;
    }

    private void setPosFar() {
        servoControl.setServoPos(ServoConstants.outtakeServo, OuttakeConstants.outtakeServoMaxPosFar);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
