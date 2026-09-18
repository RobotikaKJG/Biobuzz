package org.firstinspires.ftc.teamcode.Subsystems;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;

/**
 * Small binding contract for controllers such as DrivebaseController. Implementations select
 * an input identifier; EdgeDetection determines whether that input was pressed this loop.
 */
public interface SubsystemTrigger {

    GamepadIndexValues getTrigger();
}
