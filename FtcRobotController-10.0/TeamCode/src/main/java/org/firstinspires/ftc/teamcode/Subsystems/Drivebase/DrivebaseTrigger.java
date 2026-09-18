package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemTrigger;

/**
 * Binding for drive-mode selection. Change this identifier to remap the button; the input adapter
 * and edge detector remain shared with the rest of the robot.
 */
public class DrivebaseTrigger implements SubsystemTrigger {

    private static final GamepadIndexValues trigger = GamepadIndexValues.share;
    @Override
    public GamepadIndexValues getTrigger() {
        return trigger;
    }
}
