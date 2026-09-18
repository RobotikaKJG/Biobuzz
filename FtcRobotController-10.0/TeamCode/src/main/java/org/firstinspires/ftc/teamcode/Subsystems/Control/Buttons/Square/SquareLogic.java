package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the Square rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to SquareControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class SquareLogic {
    private final SquareControl control = new SquareControl();

    public void update() {
        // Set a new SquareStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setSquareState(SquareStates.idle);
    }
}
