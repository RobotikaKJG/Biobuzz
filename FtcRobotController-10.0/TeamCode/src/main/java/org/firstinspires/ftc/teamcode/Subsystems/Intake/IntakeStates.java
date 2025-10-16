package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeStates {
    private static SubsystemState intakeState = SubsystemState.Idle;
    private static IntakeMotorStates intakeMotorState = IntakeMotorStates.idle;
    private static TransferCRServoStates transferCRServoState = TransferCRServoStates.idle;

    public static void setInitialStates() {
        intakeState = SubsystemState.Idle;
        intakeMotorState = IntakeMotorStates.idle;
        transferCRServoState = TransferCRServoStates.idle;
    }

    public static SubsystemState getIntakeState() {
        return intakeState;
    }

    public static void setIntakeState(SubsystemState state) {
        intakeState = state;
    }

    public static IntakeMotorStates getIntakeMotorState() {
        return intakeMotorState;
    }

    public static void setIntakeMotorState(IntakeMotorStates state) {
        intakeMotorState = state;
    }

    public static TransferCRServoStates getTransferCRServoState() {
        return transferCRServoState;
    }

    public static void setTransferCRServoState(TransferCRServoStates state) {
        transferCRServoState = state;
    }
}
