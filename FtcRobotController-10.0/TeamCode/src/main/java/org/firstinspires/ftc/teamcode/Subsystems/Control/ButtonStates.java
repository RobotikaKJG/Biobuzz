package org.firstinspires.ftc.teamcode.Subsystems.Control;

//import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadDown.DpadDownStates;
//import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadLeft.DpadLeftStates;
//import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadRight.DpadRightStates;
//import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadUp.DpadUpStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper.LeftBumperStates;
//import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger.LeftTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger.LeftTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger.RightTriggerStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper.RightBumperStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square.SquareStates;

public class ButtonStates {
    private static LeftBumperStates leftBumperStates = LeftBumperStates.idle;
    private static LeftTriggerStates leftTriggerStates = LeftTriggerStates.idle;
    private static RightBumperStates rightBumperStates = RightBumperStates.idle;
    private static RightTriggerStates rightTriggerStates = RightTriggerStates.idle;
    private static SquareStates squareStates = SquareStates.idle;
//    private static DpadRightStates dpadRightStates = DpadRightStates.idle;
//    private static DpadDownStates dpadDownStates = DpadDownStates.idle;
//    private static DpadUpStates dpadUpStates = DpadUpStates.idle;
//    private static DpadLeftStates dpadLeftStates = DpadLeftStates.idle;

    public static void setInitialStates(){
        leftBumperStates = LeftBumperStates.idle;
        leftTriggerStates = LeftTriggerStates.idle;
        rightBumperStates = RightBumperStates.idle;
        rightTriggerStates = RightTriggerStates.idle;
        squareStates = SquareStates.idle;
//        dpadRightStates = DpadRightStates.idle;
//        dpadDownStates = DpadDownStates.idle;
//        dpadUpStates = DpadUpStates.idle;
//        dpadLeftStates = DpadLeftStates.idle;
    }

    public static LeftBumperStates getLeftBumperState() {
        return  leftBumperStates;
    }

    public static void setLeftBumperState(LeftBumperStates state) {
        ButtonStates.leftBumperStates = state;
    }

    public static LeftTriggerStates getLeftTriggerState() {
        return leftTriggerStates;
    }

    public static void setLeftTriggerState(LeftTriggerStates state) {
        ButtonStates.leftTriggerStates = state;
    }

    public static RightBumperStates getRightBumperState() {
        return rightBumperStates;
    }

    public static void setRightBumperState(RightBumperStates state) {
        ButtonStates.rightBumperStates = state;
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

//    public static DpadRightStates getDpadRightState() {
//        return dpadRightStates;
//    }

//    public static void setDpadRightState(DpadRightStates state) {
//        ButtonStates.dpadRightStates = state;
//    }

//    public static DpadDownStates getDpadDownState() {
//        return dpadDownStates;
//    }

//    public static void setDpadDownState(DpadDownStates state) {
//        ButtonStates.dpadDownStates = state;
//    }

//    public static DpadUpStates getDpadUpState() {
//        return dpadUpStates;
//    }

//    public static void setDpadUpState(DpadUpStates state) {
//        ButtonStates.dpadUpStates = state;
//    }

//    public static DpadLeftStates getDpadLeftState() {return dpadLeftStates;}
//
//    public static void setDpadLeftState(DpadLeftStates state) {ButtonStates.dpadLeftStates = state;}
}