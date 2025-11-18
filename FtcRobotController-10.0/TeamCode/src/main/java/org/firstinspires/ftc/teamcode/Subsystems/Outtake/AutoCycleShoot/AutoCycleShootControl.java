package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;

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
                break;
            case turnTransfer:
                IntakeStates.setTransferMotorState(TransferMotorStates.forward);
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                break;
            case turnFeeder:
                OuttakeStates.setFeederMotorState(FeederMotorStates.forward);
                break;
            case stopTransfer:
                IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                break;
            case turnFeederBack:
                OuttakeStates.setFeederMotorState(FeederMotorStates.backward);
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                break;
            case deactivate:
                OuttakeStates.setFeederMotorState(FeederMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}