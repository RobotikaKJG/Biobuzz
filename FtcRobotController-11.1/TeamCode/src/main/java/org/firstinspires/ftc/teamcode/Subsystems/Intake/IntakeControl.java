package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeMovement.AutoIntakeMovementControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer.AutoIntakeTransferControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer.AutoIntakeTransferLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer.AutoIntakeTransferStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeControl {
    private final AutoIntakeMovementControl autoIntakeMovementControl;
    private final AutoIntakeTransferControl autoIntakeTransferControl;
    private final AutoIntakeTransferLogic autoIntakeTransferLogic;
    private final IntakeMotorControl intakeMotorControl;
    private final TransferMotorControl transferMotorControl;
    private final LockServoControl lockServoControl;

    public IntakeControl(AutoIntakeMovementControl autoIntakeMovementControl, AutoIntakeTransferControl autoIntakeTransferControl, AutoIntakeTransferLogic autoIntakeTransferLogic, IntakeMotorControl intakeMotorControl, TransferMotorControl transferMotorControl, LockServoControl lockServoControl) {
        this.autoIntakeMovementControl = autoIntakeMovementControl;
        this.autoIntakeTransferControl = autoIntakeTransferControl;
        this.autoIntakeTransferLogic = autoIntakeTransferLogic;
        this.intakeMotorControl = intakeMotorControl;
        this.transferMotorControl = transferMotorControl;
        this.lockServoControl = lockServoControl;
    }

    public void update() {
        autoIntakeMovementControl.update();
        autoIntakeTransferControl.update();
        autoIntakeTransferLogic.update();
        intakeMotorControl.update();
        transferMotorControl.update();
        lockServoControl.update();

        updateIntakeState();
    }

    private void updateIntakeState(){
        if(intakeActive())
            IntakeStates.setIntakeState(SubsystemState.Run);
        else
            IntakeStates.setIntakeState(SubsystemState.Idle);
    }

    private boolean intakeActive() {
        return IntakeStates.getIntakeMotorState() != IntakeMotorStates.idle;
    }
}