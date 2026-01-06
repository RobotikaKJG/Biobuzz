package org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class FeederMotorControl {
    private final MotorControl motorControl;
    private FeederMotorStates prevFeederMotorStates = FeederMotorStates.idle;

    public FeederMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;

    }

    public void update() {
        if(OuttakeStates.getFeederMotorState() != prevFeederMotorStates) {
            updateStates();
            prevFeederMotorStates = OuttakeStates.getFeederMotorState();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getFeederMotorState()) {
            case forward:
                motorControl.setMotorSpeed(MotorConstants.feeder, -1);
                break;
            case intake:
                motorControl.setMotorSpeed(MotorConstants.feeder, -0.5);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.feeder, 1.0);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.feeder, 0);
                break;
        }

    }
}