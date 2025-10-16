package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class SquareLogic {
    private final SquareControl squareControl = new SquareControl();

    public void update() {
        if (runOuttake()) return;
        stopOuttake();

    }

    private void completeAction(){
        squareControl.update();
        ButtonStates.setSquareState(SquareStates.idle);
    }

    private boolean runOuttake() {
        if(OuttakeStates.getMotorState() == OuttakeMotorStates.forwardFull || OuttakeStates.getMotorState() == OuttakeMotorStates.forwardStart) return false;
        ButtonStates.setSquareState(SquareStates.runOuttake);
        completeAction();
        return true;
    }

    private void stopOuttake() {
        System.out.println("Outtake stop");
        ButtonStates.setSquareState(SquareStates.stopOuttake);
        completeAction();
    }
}
