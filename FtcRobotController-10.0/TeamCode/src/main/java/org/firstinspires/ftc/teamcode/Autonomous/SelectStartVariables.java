package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;

/**
 * Non-blocking pre-start menu. GeneralAutonomous calls update() until START or STOP.
 * Constructors must not wait for button presses; otherwise the Driver Station cannot stop init.
 */
public class SelectStartVariables {
    private final Gamepad gamepad;
    private final Telemetry telemetry;
    private final Gamepad current = new Gamepad();
    private final Gamepad previous = new Gamepad();
    private final EdgeDetection edges = new EdgeDetection();

    public SelectStartVariables(Gamepad gamepad, Telemetry telemetry) {
        this.gamepad = gamepad;
        this.telemetry = telemetry;
    }

    public void update() {
        previous.copy(current);
        current.copy(gamepad);
        edges.refreshGamepadIndex(current, previous);
        if (edges.rising(GamepadIndexValues.triangle)) GlobalVariables.alliance = Alliance.Red;
        if (edges.rising(GamepadIndexValues.square)) GlobalVariables.alliance = Alliance.Blue;
        telemetry.addLine("Triangle/Y: RED | Square/X: BLUE");
        telemetry.addData("Alliance", GlobalVariables.alliance);
        telemetry.addData("Routine", GlobalVariables.autonomousMode);
        telemetry.addLine("Starter autonomous stays idle. Add and select a routine before competition.");
    }
}
