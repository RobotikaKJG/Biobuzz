package org.firstinspires.ftc.teamcode.Main;


import com.pedropathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousMode;

public class GlobalVariables {
    public static Alliance alliance;
    public static AutonomousMode autonomousMode;
    public static boolean wasAutonomous = false;
    public static boolean isAutonomous;
    public static volatile boolean slowMode = false; // read by the drive loop
    public static boolean subCycles;
    public static Pose lastPose = new Pose(0,0,0);
}