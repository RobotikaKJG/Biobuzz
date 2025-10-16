package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class SquareControl {
    public void update() {
        switch (ButtonStates.getSquareState()) {
            case runOuttake:
//                OuttakeStates.setMotorState(OuttakeMotorStates.forwardStart);
                IntakeStates.setTransferCRServoState(TransferCRServoStates.turn);
                break;
            case stopOuttake:
//                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}
