package org.firstinspires.ftc.teamcode.Main;

import org.firstinspires.ftc.teamcode.Autonomous.AutonomousMode;

/**
 * Small shared match context. FTC can run several OpModes in one process, so every entry point
 * calls reset() rather than inheriting static flags from the previous match.
 * Put mechanism states in their own States class, not in this global context.
 */
public final class GlobalVariables {
    private GlobalVariables() { }
    public static Alliance alliance = Alliance.Red;
    public static AutonomousMode autonomousMode = AutonomousMode.IDLE;
    public static boolean isAutonomous;
    public static boolean slowMode;

    public static void reset(Alliance selectedAlliance, boolean autonomous) {
        alliance = selectedAlliance;
        autonomousMode = AutonomousMode.IDLE;
        isAutonomous = autonomous;
        slowMode = false;
    }
}
