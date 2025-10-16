package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class RightTriggerLogic {
    private final RightTriggerControl rightTriggerControl = new RightTriggerControl();

    public void update() {

        if (runIntake()) return;
        stopIntake();

    }

    private void completeAction(){
        rightTriggerControl.update();
        ButtonStates.setRightTriggerState(RightTriggerStates.idle);
    }

    private boolean runIntake() {
        if(IntakeStates.getIntakeMotorState() != IntakeMotorStates.idle) return false;
        ButtonStates.setRightTriggerState(RightTriggerStates.forward);
        completeAction();
        return true;
    }

    private void stopIntake() {
        ButtonStates.setRightTriggerState(RightTriggerStates.stop);
        completeAction();
    }
}
