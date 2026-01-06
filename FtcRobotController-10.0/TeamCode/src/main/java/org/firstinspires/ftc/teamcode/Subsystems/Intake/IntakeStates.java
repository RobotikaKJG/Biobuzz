package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake.AutoFeederIntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake.AutoFeederIntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeStates {
    private static SubsystemState intakeState = SubsystemState.Idle;
    private static IntakeMotorStates intakeMotorState = IntakeMotorStates.idle;
    private static AutoFeederIntakeStates autoFeederIntakeState = AutoFeederIntakeStates.idle;

    public static void setInitialStates() {
        intakeState = SubsystemState.Idle;
        intakeMotorState = IntakeMotorStates.idle;
        autoFeederIntakeState = AutoFeederIntakeStates.idle;
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

    public static AutoFeederIntakeStates getAutoFeederIntakeState() {
        return autoFeederIntakeState;
    }

    public static void setAutoFeederIntakeState(AutoFeederIntakeStates state) {
        autoFeederIntakeState = state;
    }
}