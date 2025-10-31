package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class CircleLogic {
    private final CircleControl circleControl = new CircleControl();

    public void update() {
        if (turnOuttake()) return;
    }

    private void completeAction(){
        circleControl.update();
        ButtonStates.setCircleState(CircleStates.idle);
    }

    private boolean turnOuttake() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return false;
        ButtonStates.setCircleState(CircleStates.turnOuttake);
        completeAction();
        return true;
    }
}
