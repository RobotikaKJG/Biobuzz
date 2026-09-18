package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the RightBumper rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to RightBumperControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class RightBumperLogic {
    private final RightBumperControl control = new RightBumperControl();

    public void update() {
        // Set a new RightBumperStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setRightBumperState(RightBumperStates.idle);
    }
}
