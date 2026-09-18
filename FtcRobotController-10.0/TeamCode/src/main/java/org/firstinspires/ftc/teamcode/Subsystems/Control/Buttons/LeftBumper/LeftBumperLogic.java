package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the LeftBumper rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to LeftBumperControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class LeftBumperLogic {
    private final LeftBumperControl control = new LeftBumperControl();

    public void update() {
        // Set a new LeftBumperStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setLeftBumperState(LeftBumperStates.idle);
    }
}
