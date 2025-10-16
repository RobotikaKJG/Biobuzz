package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

public class RightBumperLogic {
    private final RightBumperControl rightBumperControl = new RightBumperControl();

    public void update() {
        aim();
    }

    private void completeAction(){
        rightBumperControl.update();
        ButtonStates.setRightBumperState(RightBumperStates.idle);
    }

    private void aim() {
        ButtonStates.setRightBumperState(RightBumperStates.aim);
        completeAction();
    }
}
