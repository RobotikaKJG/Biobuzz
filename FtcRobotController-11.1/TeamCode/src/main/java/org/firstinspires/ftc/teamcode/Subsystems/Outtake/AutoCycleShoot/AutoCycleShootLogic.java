package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootLogic {
    private double currentWait = 0;
    private double feedStartSec = 0;
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
        startFeed();
    }

    private void turnBack() {
        if(currentWait > getSeconds()) return;
        startFeed();
    }

    private void turnTransfer() {
        // TeleOp auto-finish: feed until the balls are gone (both sensors empty —
        // including the mid/back one that fills first when intaking), or until the
        // max feed time, then stop -> which closes the gate. Autonomous scripts its
        // own stop, so leave that path unchanged.
        // (To be less strict, relax to: boolean ballsGone = !sensorControl.isMidBall();)
        if (GlobalVariables.isAutonomous) return;
        double elapsed = getSeconds() - feedStartSec;
        boolean ballsGone = !sensorControl.isMidBall() && !sensorControl.isFrontBall();
        if ((elapsed >= OuttakeConstants.shootFeedMinSec && ballsGone)
                || elapsed >= OuttakeConstants.shootFeedMaxSec) {
            OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
        }
    }

    private void startFeed() {
        feedStartSec = getSeconds();
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
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
        // Shared, centrally-throttled reads (see SensorControl). Preserves the exact
        // original semantics: true only when BOTH front and mid currently see a ball.
        return sensorControl.isFrontBall() && sensorControl.isMidBall();
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
