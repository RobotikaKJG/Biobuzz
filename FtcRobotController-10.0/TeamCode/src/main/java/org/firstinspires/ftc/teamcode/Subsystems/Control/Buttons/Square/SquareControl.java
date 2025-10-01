package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class SquareControl {
    public void update() {
        switch (ButtonStates.getSquareState()) {
            case takeSpecimen:
                break;
            case idle:
                break;
        }
    }
}
