package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeMovement.AutoIntakeMovementStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoStates;

public class CircleLogic {
    private final CircleControl circleControl = new CircleControl();

    public void update() {
        if (intakeOn()) return;
        intakeOff();
    }

    private void completeAction(){
        circleControl.update();
        ButtonStates.setCircleState(CircleStates.idle);
    }

    private boolean intakeOn() {
        if(IntakeStates.getAutoIntakeMovementState() == AutoIntakeMovementStates.activate) return false;
        ButtonStates.setCircleState(CircleStates.intakeOn);
        completeAction();
        return true;
    }

    private void intakeOff() {
        ButtonStates.setCircleState(CircleStates.intakeOff);
        completeAction();
    }
}
