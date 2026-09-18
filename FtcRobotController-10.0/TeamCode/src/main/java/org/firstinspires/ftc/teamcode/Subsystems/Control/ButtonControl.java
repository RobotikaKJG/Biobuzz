package org.firstinspires.ftc.teamcode.Subsystems.Control;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle.CircleLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadUp.DpadUpLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper.LeftBumperLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger.LeftTriggerLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper.RightBumperLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightTrigger.RightTriggerLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square.SquareLogic;

/**
 * Routes gamepad edges to per-button Logic classes. All mechanism bindings are intentionally idle.
 * The default instance uses gamepad 1. Dependencies also exposes a gamepad 2 factory for an operator.
 * Use rising edges for toggles; add falling edges or held-input handling explicitly when required.
 */
public class ButtonControl {
    private final EdgeDetection edgeDetection;
    private final CircleLogic circleLogic = new CircleLogic();
    private final DpadUpLogic dpadUpLogic = new DpadUpLogic();
    private final LeftBumperLogic leftBumperLogic = new LeftBumperLogic();
    private final LeftTriggerLogic leftTriggerLogic = new LeftTriggerLogic();
    private final RightBumperLogic rightBumperLogic = new RightBumperLogic();
    private final RightTriggerLogic rightTriggerLogic = new RightTriggerLogic();
    private final SquareLogic squareLogic = new SquareLogic();

    public ButtonControl(EdgeDetection edgeDetection, SensorControl sensorControl) {
        this.edgeDetection = edgeDetection;
        // Pass sensorControl to a button Logic class only if its decision needs a measurement.
    }

    public void update() {
        if (edgeDetection.rising(GamepadIndexValues.circle)) circleLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.dpadUp)) dpadUpLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.leftBumper)) leftBumperLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.leftTrigger)) leftTriggerLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.rightBumper)) rightBumperLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.rightTrigger)) rightTriggerLogic.update();
        if (edgeDetection.rising(GamepadIndexValues.square)) squareLogic.update();
    }
}
