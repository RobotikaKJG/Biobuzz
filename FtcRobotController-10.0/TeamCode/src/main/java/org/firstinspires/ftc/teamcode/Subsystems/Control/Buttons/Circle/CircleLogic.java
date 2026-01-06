package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoStates;

public class CircleLogic {
    private final CircleControl circleControl = new CircleControl();

    public void update() {
        if (turretAdjust()) return;
        stopTurret();
    }

    private void completeAction(){
        circleControl.update();
        ButtonStates.setCircleState(CircleStates.idle);
    }

    private boolean turretAdjust() {
        if(OuttakeStates.getTurretServoState() != TurretServoStates.idle) return false;
        ButtonStates.setCircleState(CircleStates.turretAdjust);
        completeAction();
        return true;
    }

    private void stopTurret() {
        ButtonStates.setCircleState(CircleStates.idle);
        completeAction();
    }
}
