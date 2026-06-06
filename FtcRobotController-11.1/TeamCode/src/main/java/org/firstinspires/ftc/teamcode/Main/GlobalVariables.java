package org.firstinspires.ftc.teamcode.Main;


import org.firstinspires.ftc.teamcode.Autonomous.AutonomousMode;

public class GlobalVariables {
    public static volatile boolean far = false; // toggled on control loop, read by turret loop
    public static Alliance alliance;
    public static AutonomousMode autonomousMode;
    public static boolean wasAutonomous;
    public static boolean isAutonomous;
    public static volatile boolean slowMode = false; // read by the drive loop
    public static boolean subCycles;
    public static boolean hang = false;
    public static double lastTurretAngle = 0;
    public static double outtakeTargetSpeed = 0;
    public static boolean secondRelease;
}