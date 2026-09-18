package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo.TransferCRServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

/**
 * Requested intake states shared by button commands and autonomous routines.
 * IntakeControl and its child controllers read these each loop. Reset at each OpMode start.
 * Add state values to the child enums and implement their behavior in the matching Control classes.
 */
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

    public static IntakeMotorStates getMotorState() {
        return intakeMotorState;
    }

    public static void setMotorState(IntakeMotorStates state) {
        intakeMotorState = state;
    }

    public static TransferCRServoStates getTransferCRServoState() {
        return transferCRServoState;
    }

    public static void setTransferCRServoState(TransferCRServoStates state) {
        transferCRServoState = state;
    }
}
