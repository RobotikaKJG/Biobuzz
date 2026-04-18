package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

public class OuttakeConstants {
    public static final double maxTurretAngle = 50.0;

    public static final double kpTurret = 0.015; // tune this
    public static final double turretServoMaxSpeed = 1;
    public static final double turretTolerance = 1.0;

    public static final double outtakeServoMaxPosClose = 0.6;
    public static final double outtakeServoMinPos = 0.62;
    public static final double outtakeServoMaxPos = 1;


    public static final double transferServoMaxPos = 0.67;
    public static final double transferServoMinPos = 0.167;

    public static double maxDistance = 2.5;
    public static double minDistance = 1.37;

    public static double stopTransferAfter = 1;
    public static double reverseFeederAfter = 0.1;
    public static double deactivateAfter = 0.3;
    public static double stopFeederWait = 0;

    public static double resetWait = 100.0;

    public static double outtakeSpeedCloseClose = 0.62;
    public static double outtakeSpeedFar = 0.89;
    public static double outtakeSpeedCloseFar = 0.73;

    // units in MM
    public static double redTargetX = 1830;
    public static double targetY = 1830;
    public static double blueTargetX = -redTargetX;

    public static double farShootingThreshold = 2300;
    public static double targetSpeedThreshold = 0.02;
}