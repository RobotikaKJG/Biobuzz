package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class RightTriggerControl {
    public void update() {
        switch (ButtonStates.getRightTriggerState()) {
            case forward:
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                OuttakeStates.setFeederMotorState(FeederMotorStates.forward);
                break;
            case stop:
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                OuttakeStates.setFeederMotorState(FeederMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}
