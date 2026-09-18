package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Hardware-facing extension point called every loop by OuttakeControl.
 * Dependencies injects the shared ServoControl; this class must not construct another hardware owner.
 * Add calibrated hardware commands for each state after configuring the new device in HardwareInterface.
 * No device is mapped or moved by this placeholder. Once a motor is added, idle must command zero.
 */
public class TransferServoControl {
    private final ServoControl servoControl;

    public TransferServoControl(ServoControl servoControl) {
        this.servoControl = servoControl;
    }

    public void update() {
        switch (OuttakeStates.getTransferServoState()) {
            case idle:
                // No mechanism fitted. Implement the resting behavior when adding this device.
                break;
        }
    }
}
