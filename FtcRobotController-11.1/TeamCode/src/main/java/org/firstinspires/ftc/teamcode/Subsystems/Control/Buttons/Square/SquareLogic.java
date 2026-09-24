package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

public class SquareLogic {
    private final SquareControl squareControl = new SquareControl();

    public void update() {
        resetPos();
    }

    private void completeAction(){
        squareControl.update();
        ButtonStates.setSquareState(SquareStates.idle);
    }

    private void resetPos() {
        ButtonStates.setSquareState(SquareStates.activate);
        completeAction();
    }
}
