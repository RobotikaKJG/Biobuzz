package org.firstinspires.ftc.teamcode.HardwareInterface.Motor;

/**
 * Motor array slots and groups used by MotorControl. A group contains motor slots, not other groups.
 * Add new mechanism slots, hardware names and groups here together, then map them in MotorControl.
 * Keep the four drive slots in this order: Drivebase uses the diagonal groups for mecanum mixing.
 */
public final class MotorConstants {
    private MotorConstants() { }

    public static final int frontLeft = 0;
    public static final int backLeft = 1;
    public static final int frontRight = 2;
    public static final int backRight = 3;
    public static final String FRONT_LEFT_NAME = "frontLeftMotor";
    public static final String BACK_LEFT_NAME = "backLeftMotor";
    public static final String FRONT_RIGHT_NAME = "frontRightMotor";
    public static final String BACK_RIGHT_NAME = "backRightMotor";
    public static final int MOTOR_COUNT = 4;

    public static final int allDrive = 4;
    public static final int leftDrive = 5;
    public static final int rightDrive = 6;
    public static final int frontLeftBackRight = 7;
    public static final int frontRightBackLeft = 8;
    public static final int all = 9;
    public static final int notDrive = 10;
    public static final int[][] motorConfig = {
        {frontLeft}, {backLeft}, {frontRight}, {backRight},
        {frontLeft, backLeft, frontRight, backRight},
        {frontLeft, backLeft}, {frontRight, backRight},
        {frontLeft, backRight}, {frontRight, backLeft},
        {frontLeft, backLeft, frontRight, backRight},
        {} // Populate when adding mechanisms. Autonomous must not overwrite Road Runner drive output.
    };
}
