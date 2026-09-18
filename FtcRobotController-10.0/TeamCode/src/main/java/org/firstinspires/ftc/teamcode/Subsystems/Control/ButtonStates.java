package org.firstinspires.ftc.teamcode.Subsystems.Control;

import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle.CircleStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadUp.DpadUpStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper.LeftBumperStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger.LeftTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper.RightBumperStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger.RightTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square.SquareStates;

/**
 * One-shot command mailbox between each button's Logic and Control classes.
 * These static values survive OpMode changes: reset them when constructing a controller.
 * Long-running mechanism state belongs in IntakeStates/OuttakeStates, not here.
 */
public final class ButtonStates {
    private ButtonStates() { }
    private static CircleStates circleState = CircleStates.idle;
    private static DpadUpStates dpadUpState = DpadUpStates.idle;
    private static LeftBumperStates leftBumperState = LeftBumperStates.idle;
    private static LeftTriggerStates leftTriggerState = LeftTriggerStates.idle;
    private static RightBumperStates rightBumperState = RightBumperStates.idle;
    private static RightTriggerStates rightTriggerState = RightTriggerStates.idle;
    private static SquareStates squareState = SquareStates.idle;

    public static void setInitialStates() {
        circleState = CircleStates.idle;
        dpadUpState = DpadUpStates.idle;
        leftBumperState = LeftBumperStates.idle;
        leftTriggerState = LeftTriggerStates.idle;
        rightBumperState = RightBumperStates.idle;
        rightTriggerState = RightTriggerStates.idle;
        squareState = SquareStates.idle;
    }

    public static CircleStates getCircleState() { return circleState; }
    public static void setCircleState(CircleStates state) { circleState = state; }

    public static DpadUpStates getDpadUpState() { return dpadUpState; }
    public static void setDpadUpState(DpadUpStates state) { dpadUpState = state; }

    public static LeftBumperStates getLeftBumperState() { return leftBumperState; }
    public static void setLeftBumperState(LeftBumperStates state) { leftBumperState = state; }

    public static LeftTriggerStates getLeftTriggerState() { return leftTriggerState; }
    public static void setLeftTriggerState(LeftTriggerStates state) { leftTriggerState = state; }

    public static RightBumperStates getRightBumperState() { return rightBumperState; }
    public static void setRightBumperState(RightBumperStates state) { rightBumperState = state; }

    public static RightTriggerStates getRightTriggerState() { return rightTriggerState; }
    public static void setRightTriggerState(RightTriggerStates state) { rightTriggerState = state; }

    public static SquareStates getSquareState() { return squareState; }
    public static void setSquareState(SquareStates state) { squareState = state; }
}
