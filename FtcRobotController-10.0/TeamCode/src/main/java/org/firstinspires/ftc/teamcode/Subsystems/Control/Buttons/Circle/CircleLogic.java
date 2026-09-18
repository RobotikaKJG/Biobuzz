package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

/**
 * Called by ButtonControl once on the Circle rising edge, not continuously while held.
 * Decide which command is allowed here, pass it to CircleControl, then clear the one-shot command.
 * Check mechanism state here when a command has an interlock; do not access hardware directly.
 */
public class CircleLogic {
    private final CircleControl control = new CircleControl();

    public void update() {
        // Set a new CircleStates command here when this button is assigned next season.
        control.update();
        ButtonStates.setCircleState(CircleStates.idle);
    }
}
