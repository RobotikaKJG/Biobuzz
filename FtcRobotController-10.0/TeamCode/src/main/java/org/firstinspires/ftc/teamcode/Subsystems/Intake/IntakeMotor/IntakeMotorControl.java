package org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

/**
 * Hardware-facing extension point called every loop by IntakeControl.
 * Dependencies injects the shared MotorControl; this class must not construct another hardware owner.
 * Add calibrated hardware commands for each state after configuring the new device in HardwareInterface.
 * No device is mapped or moved by this placeholder. Once a motor is added, idle must command zero.
 */
public class IntakeMotorControl {
    private final MotorControl motorControl;

    public IntakeMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        switch (IntakeStates.getMotorState()) {
            case idle:
                // No mechanism fitted. Implement the resting behavior when adding this device.
                break;
        }
    }
}
