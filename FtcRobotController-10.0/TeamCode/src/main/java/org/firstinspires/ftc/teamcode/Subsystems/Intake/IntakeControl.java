package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

/**
 * Aggregates the intake child controllers created by Dependencies.
 * Called once per iteration in TeleOp and autonomous, after intentions have been selected.
 * Retained folder/class names are extension points; the starter has no intake hardware.
 */
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
        // Extend this predicate when introducing non-idle child states.
        return false;
    }
}
