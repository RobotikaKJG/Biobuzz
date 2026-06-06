package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

/**
 * Ball-loading state machine. Reads ONLY the mid (back) sensor until a ball is
 * confirmed there, then ONLY the front sensor until a ball is confirmed there
 * (= 3 balls loaded) and stops. The sensor reads are centralized, throttled and
 * shared in {@link SensorControl}, so this never does a redundant I2C round-trip
 * (and "no ball" defaults to infinity, not 0, before the first real read).
 */
public class AutoIntakeTransferLogic {
    private double currentWait = 0;
    private final SensorControl sensorControl;
    private final Gamepad gamepad1;

    public AutoIntakeTransferLogic(SensorControl sensorControl, Gamepad gamepad1) {
        this.sensorControl = sensorControl;
        this.gamepad1 = gamepad1;
    }

    public void update() {
        switch (IntakeStates.getAutoIntakeTransferState()) {
            case activate:
                activate();
                break;
            case checkAgainMid:
                checkAgainMid();
                break;
            case stopTransfer:
                stopTransfer();
                break;
            case checkAgainFront:
                checkAgainFront();
                break;
            case stop:
                stop();
                break;
            case idle:
                break;
        }
    }

    private void activate() {
        if (sensorControl.isMidBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainMid);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainMid() {
        if (currentWait > getSeconds()) return;
        if (sensorControl.isMidBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        } else {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
        }
    }

    private void stopTransfer() {
        if (sensorControl.isFrontBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainFront);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainFront() {
        if (currentWait > getSeconds()) return;
        if (sensorControl.isFrontBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stop);
        } else {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        }
    }

    private void stop() {
        gamepad1.rumble(300);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
