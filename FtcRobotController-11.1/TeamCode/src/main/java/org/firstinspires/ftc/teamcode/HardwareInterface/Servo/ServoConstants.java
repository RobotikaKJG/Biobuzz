package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;

public class ServoConstants {
    // Servo indexes
    public static final int lockServo = 0;
    public static final int turretServo1 = 1;
    public static final int turretServo2 = 2;
    public static final int turretServo3 = 3;

    // CR Servo indexes
    public static final int turretServo = 0;


    // Analog indexes
    public static final int turretAnalog = 0;


    public static final double[] servoMinPos =
            {
                    IntakeConstants.lockServoMinPos
            };
    public static final double[] servoMaxPos =
            {
                    IntakeConstants.lockServoMaxPos
            };
}
