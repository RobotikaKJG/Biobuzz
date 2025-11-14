package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeControl {
    private final IntakeMotorControl intakeMotorControl;
    private final TransferMotorControl transferMotorControl;

    public IntakeControl(IntakeMotorControl intakeMotorControl, TransferMotorControl transferMotorControl) {
        this.intakeMotorControl = intakeMotorControl;
        this.transferMotorControl = transferMotorControl;
    }

    public void update() {
        intakeMotorControl.update();
        transferMotorControl.update();

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