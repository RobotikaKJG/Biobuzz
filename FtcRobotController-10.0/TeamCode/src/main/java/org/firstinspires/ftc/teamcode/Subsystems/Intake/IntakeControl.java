package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeControl {
    private final IntakeMotorControl intakeMotorControl;
    private final TransferCRServoControl transferCRServoControl;

    public IntakeControl(IntakeMotorControl intakeMotorControl, TransferCRServoControl transferCRServoControl) {
        this.intakeMotorControl = intakeMotorControl;
        this.transferCRServoControl = transferCRServoControl;
    }

    public void update() {
        intakeMotorControl.update();
        transferCRServoControl.update();

        updateIntakeState();
    }

    private void updateIntakeState(){
        if(intakeActive())
            IntakeStates.setIntakeState(SubsystemState.Run);
        else
            IntakeStates.setIntakeState(SubsystemState.Idle);
    }

    private boolean intakeActive() {
        return false;
    }
}
