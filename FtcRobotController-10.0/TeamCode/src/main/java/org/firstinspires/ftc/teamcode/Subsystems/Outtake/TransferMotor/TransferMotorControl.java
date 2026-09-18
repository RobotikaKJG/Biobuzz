package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Hardware-facing extension point called every loop by OuttakeControl.
 * Dependencies injects the shared MotorControl; this class must not construct another hardware owner.
 * Add calibrated hardware commands for each state after configuring the new device in HardwareInterface.
 * No device is mapped or moved by this placeholder. Once a motor is added, idle must command zero.
 */
public class TransferMotorControl {
    private final MotorControl motorControl;

    public TransferMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        switch (OuttakeStates.getTransferMotorState()) {
            case idle:
                // No mechanism fitted. Implement the resting behavior when adding this device.
                break;
        }
    }
}
