package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
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
            case turnBack:
                turnBack();
                break;
            case turnTransfer:
                turnTransfer();
                break;
            case stop:
                stopTransfer();
                break;
            case turnTransferBack:
                turnTransferBack();
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
            if (isNoBallSeen()) {
                OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnBack);
                addWaitTime(OuttakeConstants.oneBallWait);
            }
            else {
                wasIfCalled = true;
                addWaitTime(OuttakeConstants.servoOpenWait);
            }
        }
        if(currentWait > getSeconds()) return;
        wasIfCalled = false;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
    }

    private void turnBack() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
    }

    private void turnTransfer() {
    }

    private void stopTransfer() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransferBack);
        if (!GlobalVariables.isAutonomous) {
            addWaitTime(OuttakeConstants.deactivateAfter);
        }
    }

    private void turnTransferBack() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.deactivate);
    }

    private void deactivate() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
    }

    private boolean isNoBallSeen() {
        if (sensorControl.getFrontColorSensorDistance(DistanceUnit.INCH) < sensorControl.ballDistanceIn && sensorControl.getMidColorSensorDistance(DistanceUnit.INCH) < sensorControl.ballDistanceIn) {
            return true;
        }
        return false;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
