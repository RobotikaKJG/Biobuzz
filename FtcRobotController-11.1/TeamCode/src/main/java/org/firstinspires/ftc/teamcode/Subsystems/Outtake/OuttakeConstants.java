package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

public class OuttakeConstants {
    public static double turretServo1Mult = 1.0;
    public static double turretServo2Mult = 0.998;
    public static double turretServo3Mult = 1.0;

    public static double turretServoMax = 0.8;
    public static double turretServoMin = 0.1;

    public static double turretServo1Max = turretServoMax * turretServo1Mult;
    public static double turretServo2Max = turretServoMax * turretServo2Mult;
    public static double turretServo3Max = turretServoMax * turretServo3Mult;

    public static double turretServo1Min = turretServoMin * turretServo1Mult;
    public static double turretServo2Min = turretServoMin * turretServo2Mult;
    public static double turretServo3Min = turretServoMin * turretServo3Mult;

    public static double maxDistance = 2.5;
    public static double minDistance = 1.37;

    public static double oneBallWait = 0.1;
    public static double servoOpenWait = 0.1;
    public static double deactivateAfter = 0.3;

    public static double resetWait = 100.0;

    public static double outtakeSpeedCloseClose = 0.62;
    public static double outtakeSpeedFar = 0.89;
    public static double outtakeSpeedCloseFar = 0.73;

    public static double farShootingThreshold = 2300;
    public static double targetSpeedThreshold = 0.02;

}