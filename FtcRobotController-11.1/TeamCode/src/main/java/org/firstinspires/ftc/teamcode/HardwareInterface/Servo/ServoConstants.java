package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoConstants {
    // Servo indexes
    public static final int lockServo = 0;
    public static final int turretServo1 = 1;
    public static final int turretServo2 = 2;
    public static final int turretServo3 = 3;

    // CR Servo indexes


    // Analog indexes
    public static final int turretAnalog = 0;


    public static final double[] servoMinPos =
            {
                    IntakeConstants.lockServoMinPos,
                    0.0,
                    0.0,
                    0.0
            };
    public static final double[] servoMaxPos =
            {
                    IntakeConstants.lockServoMaxPos,
                    OuttakeConstants.turretServo1Mult,
                    OuttakeConstants.turretServo2Mult,
                    OuttakeConstants.turretServo3Mult
            };
}
