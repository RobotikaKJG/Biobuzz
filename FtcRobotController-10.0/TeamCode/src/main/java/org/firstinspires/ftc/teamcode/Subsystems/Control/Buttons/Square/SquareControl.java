package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class SquareControl {
    public void update() {
        switch (ButtonStates.getSquareState()) {
            case turnIntake:
                break;
            case idle:
                break;
        }
    }
}
