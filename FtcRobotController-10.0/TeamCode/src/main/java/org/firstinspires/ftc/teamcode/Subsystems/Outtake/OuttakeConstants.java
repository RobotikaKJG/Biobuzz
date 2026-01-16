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

    public static double maxDistance = 1.9;
    public static double minDistance = 1.14;

    public static double stopTransferAfter = 1;
    public static double reverseFeederAfter = 0.1;
    public static double stopFeederAfter = 0.1;
    public static double stopFeederWait = 0;

    public static int outtakeVelStart = 1400;
    public static int outtakeVelFar = 2100;
    public static int outtakeVelClose = 1700;
}