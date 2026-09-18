package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the RightTrigger rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to RightTriggerControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class RightTriggerLogic {
    private final RightTriggerControl control = new RightTriggerControl();

    public void update() {
        // Set a new RightTriggerStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setRightTriggerState(RightTriggerStates.idle);
    }
}
