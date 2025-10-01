package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoConstants {
    // Servo indexes
    public static final int outtakeArm = 0;
    public static final int outtakeClawTurn = 1;
    public static final int specimenClaw = 2;
    public static final int outtakeDown = 3;
    public static final int intake = 4;


    public static final double[] servoMinPos =
            {
                    OuttakeConstants.outtakeArmServoIdlePos,
                    OuttakeConstants.outtakeClawTurnServoMinPos,
                    OuttakeConstants.specimenClawServoMinPos,
                    OuttakeConstants.outtakeDownServoMinPos,
            };
    public static final double[] servoMaxPos =
            {
                    OuttakeConstants.outtakeArmServoMaxPos,
                    OuttakeConstants.outtakeClawTurnServoMaxPos,
                    OuttakeConstants.specimenClawServoMaxPos,
                    OuttakeConstants.outtakeDownServoMaxPos,
            };
}
