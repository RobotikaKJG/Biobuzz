package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Translates a RightTriggerLogic command into requested IntakeStates/OuttakeStates (or a new subsystem).
 * Subsystem controllers apply those requests later in the same IterativeController loop.
 * This separation lets autonomous request the same mechanism states without simulating buttons.
 */
public class RightTriggerControl {
    public void update() {
        switch (ButtonStates.getRightTriggerState()) {
            case idle:
                // Unassigned in the starter. Add cases that set subsystem states here.
                break;
        }
    }
}
