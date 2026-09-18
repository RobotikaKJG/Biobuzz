package org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

/**
 * Hardware-facing extension point called every loop by IntakeControl.
 * Dependencies injects the shared ServoControl; this class must not construct another hardware owner.
 * Add calibrated hardware commands for each state after configuring the new device in HardwareInterface.
 * No device is mapped or moved by this placeholder. Once a motor is added, idle must command zero.
 */
public class TransferCRServoControl {
    private final ServoControl servoControl;

    public TransferCRServoControl(ServoControl servoControl) {
        this.servoControl = servoControl;
    }

    public void update() {
        switch (IntakeStates.getTransferCRServoState()) {
            case idle:
                // No mechanism fitted. Implement the resting behavior when adding this device.
                break;
        }
    }
}
