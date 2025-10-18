package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo.TransferServoStates;

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
                IntakeStates.setTransferCRServoState(TransferCRServoStates.turnOuttake);
                OuttakeStates.setTransferMotorState(TransferMotorStates.forward);
                break;
            case turnTransfer:
                break;
            case ballToOuttake:
                OuttakeStates.setTransferServoState(TransferServoStates.up);
                break;
            case servoDown:
                OuttakeStates.setTransferServoState(TransferServoStates.down);
                break;
            case idle:
                OuttakeStates.setTransferMotorState(TransferMotorStates.idle);
                break;
        }
    }
}
