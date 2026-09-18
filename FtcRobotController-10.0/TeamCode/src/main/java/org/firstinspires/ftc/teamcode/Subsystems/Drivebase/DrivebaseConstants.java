package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;

/** Speed limits read each loop. A future button mapping may toggle GlobalVariables.slowMode. */
public final class DrivebaseConstants {
    private DrivebaseConstants() { }
    public static final double NORMAL_SPEED = 1.0;
    public static final double SLOW_SPEED = 0.5;
    public static double getDriveSpeed() {
        return GlobalVariables.slowMode ? SLOW_SPEED : NORMAL_SPEED;
    }
}
