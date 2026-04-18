package org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.EnumMap;

public class EdgeDetection {

    private final GamepadIndex currentGamepad1Index = new GamepadIndex();
    private final GamepadIndex prevGamepad1Index = new GamepadIndex();
    private final EnumMap<GamepadIndexValues, Boolean> risingEdges = new EnumMap<>(GamepadIndexValues.class);
    private final EnumMap<GamepadIndexValues, Boolean> fallingEdges = new EnumMap<>(GamepadIndexValues.class);

    public void refreshGamepadIndex(Gamepad currentGamepad, Gamepad prevGamepad) {
        currentGamepad1Index.updateControls(currentGamepad);
        prevGamepad1Index.updateControls(prevGamepad);

        // Detect rising and falling edges
        for (GamepadIndexValues control : GamepadIndexValues.values()) {
            boolean currentState = currentGamepad1Index.getControl(control);
            boolean prevState = prevGamepad1Index.getControl(control);

            risingEdges.put(control, currentState && !prevState);
            fallingEdges.put(control, !currentState && prevState);
        }
    }

    public boolean rising(GamepadIndexValues control) {
        return Boolean.TRUE.equals(risingEdges.getOrDefault(control, false));
    }

    public boolean falling(GamepadIndexValues control) {
        return Boolean.TRUE.equals(fallingEdges.getOrDefault(control, false));
    }

    /**
     * @noinspection unused
     */
    public boolean isEdge(GamepadIndexValues control) {
        return rising(control) || falling(control);
    }
}