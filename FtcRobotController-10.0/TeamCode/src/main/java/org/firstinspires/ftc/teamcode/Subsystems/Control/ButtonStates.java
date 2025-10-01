package org.firstinspires.ftc.teamcode.Subsystems.Control;

import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger.RightTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square.SquareStates;

public class ButtonStates {
    private static RightTriggerStates rightTriggerStates = RightTriggerStates.idle;
    private static SquareStates squareStates = SquareStates.idle;

    public static void setInitialStates(){
        rightTriggerStates = RightTriggerStates.idle;
        squareStates = SquareStates.idle;
    }

    public static RightTriggerStates getRightTriggerState() {
        return rightTriggerStates;
    }

    public static void setRightTriggerState(RightTriggerStates state) {
        ButtonStates.rightTriggerStates = state;
    }

    public static SquareStates getSquareState() {
        return squareStates;
    }

    public static void setSquareState(SquareStates state) {
        ButtonStates.squareStates = state;
    }
}
