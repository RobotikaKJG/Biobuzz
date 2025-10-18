package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootLogic {
    private double currentWait = 0;

    public void update() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case activate:
                activate();
                break;
            case turnTransfer:
                turnTransfer();
                break;
            case ballToOuttake:
                ballToOuttake();
                break;
            case servoDown:
                servoDown();
                break;
            case idle:
                break;
        }
    }

    private void activate() {
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.turnTransfer);
    }

    private void turnTransfer() {
        if(IntakeStates.getTransferCRServoState() != TransferCRServoStates.turnedOuttake) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.ballToOuttake);
        addWaitTime(OuttakeConstants.ballTransferWait);
    }

    private void ballToOuttake() {
        if(currentWait > getSeconds()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.servoDown);
        addWaitTime(OuttakeConstants.ballTransferWait);
    }

    private void servoDown() {
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
