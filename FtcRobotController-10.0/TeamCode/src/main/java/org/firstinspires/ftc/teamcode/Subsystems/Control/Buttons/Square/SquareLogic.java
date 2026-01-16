package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class SquareLogic {
    private final SquareControl squareControl = new SquareControl();

    public void update() {
        if (turnTurret()) return;
    }

    private void completeAction(){
        squareControl.update();
        ButtonStates.setSquareState(SquareStates.idle);
    }

    private boolean turnTurret() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return false;
        ButtonStates.setSquareState(SquareStates.turnTurret);
        completeAction();
        return true;
    }
}
