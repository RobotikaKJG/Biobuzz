package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadUp;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the DpadUp rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to DpadUpControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class DpadUpLogic {
    private final DpadUpControl control = new DpadUpControl();

    public void update() {
        // Set a new DpadUpStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setDpadUpState(DpadUpStates.idle);
    }
}
