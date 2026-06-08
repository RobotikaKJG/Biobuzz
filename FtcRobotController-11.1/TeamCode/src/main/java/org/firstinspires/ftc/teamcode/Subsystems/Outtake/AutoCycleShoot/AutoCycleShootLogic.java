package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootLogic {
    private double currentWait = 0;
    private double feedStartSec = 0;
    private double clearSinceSec = -1; // when the ball queue first went (and stayed) empty
    private boolean wasIfCalled = false;
    private SensorControl sensorControl;
    private MotorControl motorControl;

    public AutoCycleShootLogic(SensorControl sensorControl, MotorControl motorControl) {
        this.sensorControl = sensorControl;
        this.motorControl = motorControl;
    }

    public void update() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case recalibrate:
                recalibrate();
                break;
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

    private void recalibrate() {
        if (sensorControl.resetLocalizerWithLimelight()) {
            OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
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
        // TeleOp auto-finish: feed until the ball queue (both sensors — including the
        // mid/back one that fills first when intaking) has been empty CONTINUOUSLY for
        // shootClearHoldSec. The "held" requirement is critical: while feeding, a ball
        // is briefly in transit BETWEEN the two sensors and both momentarily read empty;
        // without the hold that ended the shot early and only 2 of 3 balls fired. The
        // hold also gives the last ball time to launch before the gate closes.
        // Autonomous scripts its own stop, so leave that path unchanged.
        if (GlobalVariables.isAutonomous) return;
        double now = getSeconds();
        double elapsed = now - feedStartSec;
        boolean queueEmpty = !sensorControl.isMidBall() && !sensorControl.isFrontBall();
        if (queueEmpty && elapsed >= OuttakeConstants.shootFeedMinSec) {
            if (clearSinceSec < 0) clearSinceSec = now;   // queue just went empty
        } else {
            clearSinceSec = -1;                            // ball present / in transit -> reset
        }
        boolean queueEmptyHeld = clearSinceSec >= 0
                && (now - clearSinceSec) >= OuttakeConstants.shootClearHoldSec;
        if (queueEmptyHeld || elapsed >= OuttakeConstants.shootFeedMaxSec) {
            OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
        }
    }

    private void startFeed() {
        feedStartSec = getSeconds();
        clearSinceSec = -1;
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
