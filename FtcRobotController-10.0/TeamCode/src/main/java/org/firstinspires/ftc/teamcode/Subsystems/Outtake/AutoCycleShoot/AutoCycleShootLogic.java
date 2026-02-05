package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
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
            case turnFeeder:
                turnFeeder();
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
            if (GlobalVariables.isAutonomous) {
                addWaitTime(0.2);
            }
        }
        if(currentWait > getSeconds()) return;
        wasIfCalled = false;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
    }

    private void turnTransfer() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnFeeder);
//        addWaitTime(OuttakeConstants.stopTransferAfter);
    }

    private void turnFeeder() {
//        if(currentWait > getSeconds() || (motorControl.getMotorVelocity(MotorConstants.outtake1)<OuttakeConstants.outtakeVelFar-200 && motorControl.getMotorVelocity(MotorConstants.outtake1)>OuttakeConstants.outtakeVelClose+300)) return;
//        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
//        addWaitTime(OuttakeConstants.reverseFeederAfter);
    }

    private void stopTransfer() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnFeederBack);
        addWaitTime(OuttakeConstants.stopFeederAfter);
    }

    private void turnFeederBack() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.deactivate);
        addWaitTime(OuttakeConstants.stopFeederWait);
    }

    private void deactivate() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}