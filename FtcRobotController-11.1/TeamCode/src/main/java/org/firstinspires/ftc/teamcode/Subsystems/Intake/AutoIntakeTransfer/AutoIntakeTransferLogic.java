package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer;

import com.acmerobotics.dashboard.message.redux.ReceiveGamepadState;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class AutoIntakeTransferLogic {
    private double currentWait = 0;
    private boolean wasIfCalled = false;
    private SensorControl sensorControl;
    private Gamepad gamepad1;

    private double currentDistanceInchesMid;
    private double currentDistanceInchesFront;
    private long lastDistanceUpdateMs = 0;
    private static final long DISTANCE_UPDATE_INTERVAL_MS = 50; // Update every 50ms

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
        updateMid();
        if (isMidBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainMid);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainMid() {
        updateMid();
        if (currentWait > getSeconds()) return;
        if (isMidBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        }
        else {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
        }
    }

    private void stopTransfer() {
        updateFront();
        if (isFrontBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainFront);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainFront() {
        updateFront();
        if (currentWait > getSeconds()) return;
        if (isFrontBall()) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stop);
        }
        else {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        }
    }

    private void stop() {
        gamepad1.rumble(300);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
    }

    private boolean isMidBall() {
        return currentDistanceInchesMid < sensorControl.ballDistanceIn;
    }

    private boolean isFrontBall() {
        return currentDistanceInchesFront < sensorControl.ballDistanceIn;
    }

    private boolean isNoBallSeen() {
        return !isMidBall() && !isFrontBall();
    }

    private void updateFront() {
        if (System.currentTimeMillis() - lastDistanceUpdateMs < DISTANCE_UPDATE_INTERVAL_MS) return;
        currentDistanceInchesFront = sensorControl.getFrontColorSensorDistance(DistanceUnit.INCH);
        lastDistanceUpdateMs = System.currentTimeMillis();
    }
    private void updateMid() {
        if (System.currentTimeMillis() - lastDistanceUpdateMs < DISTANCE_UPDATE_INTERVAL_MS) return;
        currentDistanceInchesMid = sensorControl.getMidColorSensorDistance(DistanceUnit.INCH);
        lastDistanceUpdateMs = System.currentTimeMillis();
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
