package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeMovement.AutoIntakeMovementStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class CircleControl {
    public void update() {
        switch (ButtonStates.getCircleState()) {
            case intakeOn:
                IntakeStates.setAutoIntakeMovementState(AutoIntakeMovementStates.activate);
                break;
            case intakeOff:
                IntakeStates.setAutoIntakeMovementState(AutoIntakeMovementStates.idle);
                break;
            case idle:
                break;
        }
    }
}
