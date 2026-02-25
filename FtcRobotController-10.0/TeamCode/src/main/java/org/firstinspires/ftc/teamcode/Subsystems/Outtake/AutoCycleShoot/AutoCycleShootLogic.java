package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootLogic {
    private double currentWait = 0;
    private boolean wasIfCalled = false;
    private SensorControl sensorControl;
    private MotorControl motorControl;

    public AutoCycleShootLogic(SensorControl sensorControl, MotorControl motorControl) {
        this.sensorControl = sensorControl;
        this.motorControl = motorControl;
    }

    public void update() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case activate:
                activate();
                break;
            case turnTransfer:
                turnTransfer();
                break;
            case stopTransfer:
                stopTransfer();
                break;
            case turnFeederBack:
                turnFeederBack();
                break;
            case deactivate:
                deactivate();
                break;
            case idle:
                break;
        }
    }

    private void activate() {
        if(!wasIfCalled) {
            wasIfCalled = true;
            addWaitTime(0.2);
        }
        if(currentWait > getSeconds()) return;
        wasIfCalled = false;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
    }

    private void turnTransfer() {
    }

    private void stopTransfer() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnFeederBack);
        addWaitTime(OuttakeConstants.deactivateAfter);
    }

    private void turnFeederBack() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.deactivate);
    }

    private void deactivate() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}