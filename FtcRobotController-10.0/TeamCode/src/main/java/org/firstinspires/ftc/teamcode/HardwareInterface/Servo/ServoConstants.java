package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoConstants {
    // CR Servo indexes
    public static final int transferCRServo = 0;

    // Servo indexes
    public static final int outtakeServo = 0;
    public static final int transferServo = 1;

    // Analog indexes
    public static final int transferAnalog = 0;


    public static final double[] servoMinPos =
            {
                    OuttakeConstants.outtakeServoMinPos,
                    OuttakeConstants.transferServoMinPos
            };
    public static final double[] servoMaxPos =
            {
                    OuttakeConstants.outtakeServoMaxPosFar,
                    OuttakeConstants.transferServoMaxPos
            };
}
