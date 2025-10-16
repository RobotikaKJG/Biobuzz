package org.firstinspires.ftc.teamcode.Subsystems.Control;

import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper.RightBumperStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger.RightTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square.SquareStates;

public class ButtonStates {
    private static RightTriggerStates rightTriggerState = RightTriggerStates.idle;
    private static SquareStates squareStates = SquareStates.idle;
    private static RightBumperStates rightBumperState = RightBumperStates.idle;

    public static void setInitialStates(){
        rightTriggerState = RightTriggerStates.idle;
        squareStates = SquareStates.idle;
        rightBumperState = RightBumperStates.idle;
    }

    public static RightTriggerStates getRightTriggerState() {
        return rightTriggerState;
    }

    public static void setRightTriggerState(RightTriggerStates state) {
        ButtonStates.rightTriggerState = state;
    }

    public static SquareStates getSquareState() {
        return squareStates;
    }

    public static void setSquareState(SquareStates state) {
        ButtonStates.squareStates = state;
    }

    public static RightBumperStates getRightBumperState() {
        return rightBumperState;
    }

    public static void setRightBumperState(RightBumperStates state) {
        ButtonStates.rightBumperState = state;
    }
}
