package org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class IntakeMotorControl {
    private final MotorControl motorControl;
    private IntakeMotorStates prevMotorStates = IntakeMotorStates.idle;

    public IntakeMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        if(IntakeStates.getMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = IntakeStates.getMotorState();
        }

    }

    public void updateStates() {
        switch (IntakeStates.getMotorState()) {
            case forward:
                motorControl.setMotorSpeed(MotorConstants.intake, 1.0);
                motorControl.setMotorSpeed(MotorConstants.transfer, 1.0);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.intake, -1.0);
                motorControl.setMotorSpeed(MotorConstants.transfer, -1.0);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.intake, 0);
                motorControl.setMotorSpeed(MotorConstants.transfer, 0);
                break;
        }

    }
}
