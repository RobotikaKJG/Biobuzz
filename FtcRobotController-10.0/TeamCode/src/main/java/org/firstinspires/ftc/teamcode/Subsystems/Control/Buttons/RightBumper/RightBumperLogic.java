package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class RightBumperLogic {
    private final RightBumperControl rightBumperControl = new RightBumperControl();

    public void update() {
        if (aimAuto()) return;
        aimFar();
    }

    private void completeAction(){
        rightBumperControl.update();
        ButtonStates.setRightBumperState(RightBumperStates.idle);
    }

    private boolean aimAuto() {
        if (OuttakeStates.getOuttakeServoState() == OuttakeServoStates.adjust) return false;
        ButtonStates.setRightBumperState(RightBumperStates.aimAuto);
        completeAction();
        return true;
    }

    private void aimFar() {
        ButtonStates.setRightBumperState(RightBumperStates.aimFar);
        completeAction();
    }
}
