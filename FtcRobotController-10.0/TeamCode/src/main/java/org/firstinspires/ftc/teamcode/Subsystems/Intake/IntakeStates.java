package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeStates {
    private static SubsystemState intakeState = SubsystemState.Idle;
    private static IntakeMotorStates intakeMotorState = IntakeMotorStates.idle;
    private static TransferMotorStates transferMotorState = TransferMotorStates.idle;

    public static void setInitialStates() {
        intakeState = SubsystemState.Idle;
        intakeMotorState = IntakeMotorStates.idle;
        transferMotorState = TransferMotorStates.idle;
    }

    public static SubsystemState getIntakeState() {
        return intakeState;
    }

    public static void setIntakeState(SubsystemState state) {
        intakeState = state;
    }

    public static IntakeMotorStates getMotorState() {
        return intakeMotorState;
    }

    public static void setMotorState(IntakeMotorStates state) {
        intakeMotorState = state;
    }

    public static TransferMotorStates getTransferMotorState() {
        return transferMotorState;
    }

    public static void setTransferMotorState(TransferMotorStates state) {
        transferMotorState = state;
    }
}