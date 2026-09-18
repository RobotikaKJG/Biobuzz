package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Translates a CircleLogic command into requested IntakeStates/OuttakeStates (or a new subsystem).
 * Subsystem controllers apply those requests later in the same IterativeController loop.
 * This separation lets autonomous request the same mechanism states without simulating buttons.
 */
public class CircleControl {
    public void update() {
        switch (ButtonStates.getCircleState()) {
            case idle:
                // Unassigned in the starter. Add cases that set subsystem states here.
                break;
        }
    }
}
