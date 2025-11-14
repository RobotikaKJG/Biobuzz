package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;

public class RightTriggerControl {
    public void update() {
        switch (ButtonStates.getRightTriggerState()) {
            case forward:
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                IntakeStates.setTransferMotorState(TransferMotorStates.forward);
                break;
            case stop:
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}
