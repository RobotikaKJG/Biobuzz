package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class RightTriggerLogic {
    private final RightTriggerControl rightTriggerControl = new RightTriggerControl();

    public void update() {
        takeSpecimen();
    }

    private void completeAction(){
        rightTriggerControl.update();
        ButtonStates.setRightTriggerState(RightTriggerStates.idle);
    }

    private void takeSpecimen() {
        ButtonStates.setRightTriggerState(RightTriggerStates.aim);
        completeAction();
    }
}
