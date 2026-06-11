package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

public class OuttakeConstants {
    public static double turretServo1Mult = 1.0;
    public static double turretServo2Mult = 0.998;
    public static double turretServo3Mult = 1.0;

    public static double turretServoMax = 0.78;
    public static double turretServoMin = 0.0;

    public static double turretLimitRight = -120.0;
    public static double turretLimitLeft = 100.0;
    public static double turretGearRatio = 1.0;
    public static double turretServoTravel = 323.0;

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

    // TeleOp auto-finish for a shot: feed until both ball sensors have read empty
    // CONTINUOUSLY for shootClearHoldSec (so the brief gaps while a ball is in transit
    // between the sensors don't end the shot early — that cut the 3rd ball — and the
    // last ball has time to launch), or until the max feed time as a safety fallback,
    // then stop and close the gate. Biased toward firing all 3 (over-feed a hair rather
    // than cut a ball): raise shootClearHoldSec if a ball is ever left unfired.
    public static double shootFeedMinSec = 0.15;    // ignore "empty" in the first moments
    public static double shootClearHoldSec = 0.5;   // queue must stay empty this long to stop
    public static double shootFeedMaxSec = 3.5;     // hard cap (jam / sensor failure)

    public static double resetWait = 100.0;

    public static double outtakeSpeedCloseClose = 0.62;
    public static double outtakeSpeedFar = 2270;
    public static double outtakeSpeedCloseFar = 0.78;

    public static double farShootingThreshold = 2300;
    public static double targetSpeedThreshold = 0.02;

}