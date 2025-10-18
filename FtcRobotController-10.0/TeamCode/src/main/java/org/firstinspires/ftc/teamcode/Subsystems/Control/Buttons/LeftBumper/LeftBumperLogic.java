package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftBumperLogic {
    private final LeftBumperControl leftBumperControl = new LeftBumperControl();

    public void update() {
        if(shoot()) return;
        return;
    }

    private boolean shoot() {
        if(OuttakeStates.getAutoCycleShootState() !=  AutoCycleShootStates.idle) return false;
        ButtonStates.setLeftBumperState(LeftBumperStates.shoot);
        completeAction();
        return true;
    }

    private void completeAction(){
        leftBumperControl.update();
        ButtonStates.setLeftBumperState(LeftBumperStates.idle);
    }
}
