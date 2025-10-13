package org.firstinspires.ftc.teamcode.HardwareInterface.Motor;

public class MotorConstants {
    public static final int frontLeft = 0;
    public static final int backLeft = 1;
    public static final int frontRight = 2;
    public static final int backRight = 3;
    public static final int outtake = 4;
    public static final int[][] motorConfig = {
            //separate motors
            {frontLeft},
            {backLeft},
            {frontRight},
            {backRight},
            {outtake},
            //various combinations
            {frontLeft, backLeft, frontRight, backRight},
            {frontLeft, backLeft},
            {frontRight, backRight},
            {frontLeft, backRight},
            {backLeft, frontRight},
            {frontLeft, backLeft, frontRight, backRight, outtake},
            {frontLeft, backLeft, frontRight, backRight, outtake}
    };
    // motorConfig combined value names
    public static final int allDrive = 5;
    public static final int leftDrive = 6;
    public static final int rightDrive = 7;
    public static final int frontLeftBackRight = 8;
    public static final int frontRightBackLeft = 9;
    public static final int all = 10;
    public static final int notSlide = 11;
}
