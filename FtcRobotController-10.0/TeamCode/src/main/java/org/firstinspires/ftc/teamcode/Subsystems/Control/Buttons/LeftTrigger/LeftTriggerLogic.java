package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the LeftTrigger rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to LeftTriggerControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class LeftTriggerLogic {
    private final LeftTriggerControl control = new LeftTriggerControl();

    public void update() {
        // Set a new LeftTriggerStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setLeftTriggerState(LeftTriggerStates.idle);
    }
}
