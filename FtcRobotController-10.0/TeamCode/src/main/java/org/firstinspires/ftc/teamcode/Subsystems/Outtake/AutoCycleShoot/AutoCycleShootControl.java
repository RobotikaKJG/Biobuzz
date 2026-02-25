package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootControl {
    private AutoCycleShootStates prevAutoCycleShootState = AutoCycleShootStates.idle;

    public void update() {
        if(OuttakeStates.getAutoCycleShootState() != prevAutoCycleShootState) {
            updateStates();
            prevAutoCycleShootState = OuttakeStates.getAutoCycleShootState();
        }
    }

    public void updateStates() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case activate:
                IntakeStates.setLockServoState(LockServoStates.unlock);
                break;
            case turnTransfer:
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                break;
            case stopTransfer:
                break;
            case turnFeederBack:
                IntakeStates.setMotorState(IntakeMotorStates.backward);
                break;
            case deactivate:
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                IntakeStates.setLockServoState(LockServoStates.lock);
                break;
            case idle:
                break;
        }
    }
}