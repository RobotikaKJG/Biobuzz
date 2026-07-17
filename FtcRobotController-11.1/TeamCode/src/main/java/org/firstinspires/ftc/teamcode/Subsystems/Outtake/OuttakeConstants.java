package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

public class OuttakeConstants {
    public static double turretServo1Mult = 1.0;
    public static double turretServo2Mult = 0.998;

    public static double turretServoMax = 0.9;
    public static double turretServoMin = 0.1;

    // These bound the same physical travel the turret has always had; because
    // turretDirection is -1, the RIGHT limit is the one near turretServoMax:
    // -100 -> pos 0.81. Anything past that presses the turret into its hard stop
    // and stalls it (the old limit-to-limit jam during fast drivetrain rotation).
    public static double turretLimitRight = -100.0;
    public static double turretLimitLeft = 120.0;
    public static double turretGearRatio = 1.0;
    public static double turretServoTravel = 323.0;

    // Sign of the angle -> servo-position mapping. -1 because increasing servo
    // position rotates the turret toward negative (right) angles; flip this if
    // the turret ever aims to the side opposite the target.
    public static double turretDirection = -1.0;

    // Max commanded servo-position change per second (1.0 = full 0-to-1 travel).
    // Caps how hard the turret slams toward a limit when the aim target jumps
    // (e.g. the +/-180 wrap while the drivetrain spins). Full-speed tracking
    // only needs ~0.7/s, so 2.0 leaves headroom and never slows normal aiming.
    public static double turretServoSlewPerSec = 2.0;

    // Hysteresis band (deg) around the dead-zone bisector used when the aim
    // target is behind the turret. While in the dead zone the turret holds the
    // angularly NEARER limit, re-evaluated each loop; the held limit only flips
    // once the target moves this far past the bisector, so noise near the
    // bisector can't make it chatter between the two limits.
    public static double turretDeadZoneHysteresisDeg = 20.0;

    public static double turretServo1Max = turretServoMax * turretServo1Mult;
    public static double turretServo2Max = turretServoMax * turretServo2Mult;

    public static double turretServo1Min = turretServoMin * turretServo1Mult;
    public static double turretServo2Min = turretServoMin * turretServo2Mult;

    public static double maxDistance = 98.43;
    public static double minDistance = 53.94;

    public static double oneBallWait = 0.15;
    public static double servoOpenWait = 0.15;
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

    public static double outtakeSpeedCloseClose = 1600;
    public static double outtakeSpeedFar = 2190;
    public static double outtakeSpeedClose = 1680;
    public static double outtakeSpeedCloseFar = 1800;

    public static double farShootingThreshold = 2300;
    public static double targetSpeedThreshold = 0.02;

}