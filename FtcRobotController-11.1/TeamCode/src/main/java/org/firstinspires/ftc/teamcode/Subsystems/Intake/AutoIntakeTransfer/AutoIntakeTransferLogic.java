package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer;

import com.acmerobotics.dashboard.message.redux.ReceiveGamepadState;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.InfraRedSensors;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class AutoIntakeTransferLogic {
    private static final String TAG = "Intake"; // RobotLog tag for the loading-sequence trace
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
            RobotLog.ii(TAG, "activate: MID ball (mid=%.1f) -> checkAgainMid", currentDistanceInchesMid);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainMid);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainMid() {
        updateMid();
        if (currentWait > getSeconds()) return;
        if (isMidBall()) {
            RobotLog.ii(TAG, "checkAgainMid: confirmed (mid=%.1f) -> stopTransfer (transfer off)", currentDistanceInchesMid);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        }
        else {
            RobotLog.ii(TAG, "checkAgainMid: lost (mid=%.1f) -> activate", currentDistanceInchesMid);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
        }
    }

    private void stopTransfer() {
        updateFront();
        if (GlobalVariables.isAutonomous) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainFront);
            return;
        }
        if (isFrontBall()) {
            RobotLog.ii(TAG, "stopTransfer: FRONT ball (front=%.1f) -> checkAgainFront", currentDistanceInchesFront);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.checkAgainFront);
            addWaitTime(IntakeConstants.checkAgainAfter);
        }
    }

    private void checkAgainFront() {
        updateFront();
        if (currentWait > getSeconds()) return;
        if (isFrontBall()) {
            RobotLog.ii(TAG, "checkAgainFront: confirmed (front=%.1f) -> stop (3 balls loaded)", currentDistanceInchesFront);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stop);
            gamepad1.rumble(300);
        }
        else {
            RobotLog.ii(TAG, "checkAgainFront: lost (front=%.1f) -> stopTransfer", currentDistanceInchesFront);
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stopTransfer);
        }
    }

    private void stop() {
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
    }

    private boolean isMidBall() {
        return currentDistanceInchesMid < sensorControl.ballDistanceIn || sensorControl.isInfraRedObstructed(InfraRedSensors.infraMid);
    }

    private boolean isFrontBall() {
        return currentDistanceInchesFront < sensorControl.ballDistanceIn || sensorControl.isInfraRedObstructed(InfraRedSensors.infraFront);
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
