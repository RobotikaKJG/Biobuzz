package org.firstinspires.ftc.teamcode.Subsystems.Intake;

public class IntakeConstants {
    public static double stopFeederAfter = 0.1;

    public static double lockServoLockedPos = 0.3;
    public static double lockServoUnlockedPos = 0.044;

    public static double lockServoMinPos = Math.min(lockServoLockedPos, lockServoUnlockedPos);
    public static double lockServoMaxPos = Math.max(lockServoLockedPos, lockServoUnlockedPos);

    public static double checkAgainAfter = 0.1;
}
