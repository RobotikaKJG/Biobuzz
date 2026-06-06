package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

public class OuttakeConstants {
    public static double turretServo1Mult = 1.0;
    public static double turretServo2Mult = 0.998;
    public static double turretServo3Mult = 1.0;

    public static double turretServoMax = 0.78;
    public static double turretServoMin = 0.0;

    public static double turretServo1Max = turretServoMax * turretServo1Mult;
    public static double turretServo2Max = turretServoMax * turretServo2Mult;
    public static double turretServo3Max = turretServoMax * turretServo3Mult;

    public static double turretServo1Min = turretServoMin * turretServo1Mult;
    public static double turretServo2Min = turretServoMin * turretServo2Mult;
    public static double turretServo3Min = turretServoMin * turretServo3Mult;

    public static double maxDistance = 98.43;
    public static double minDistance = 53.94;

    public static double oneBallWait = 0.1;
    public static double servoOpenWait = 0.1;
    public static double deactivateAfter = 0.3;

    // TeleOp auto-finish for a shot: keep feeding until both ball sensors read empty
    // (with a short min so a stale reading can't end it instantly), or until the max
    // feed time elapses as a safety fallback — then stop and close the gate.
    public static double shootFeedMinSec = 0.15;
    public static double shootFeedMaxSec = 2.0;

    public static double resetWait = 100.0;

    public static double outtakeSpeedCloseClose = 0.62;
    public static double outtakeSpeedFar = 0.89;
    public static double outtakeSpeedCloseFar = 0.78;

    public static double farShootingThreshold = 2300;
    public static double targetSpeedThreshold = 0.02;

}