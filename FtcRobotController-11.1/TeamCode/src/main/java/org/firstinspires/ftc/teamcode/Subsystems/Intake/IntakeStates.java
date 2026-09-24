package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeStates {
    private static SubsystemState intakeState = SubsystemState.Idle;
    private static IntakeMotorStates intakeMotorState = IntakeMotorStates.idle;
    private static LockServoStates lockServoState = LockServoStates.idle;

    public static void setInitialStates() {
        intakeState = SubsystemState.Idle;
        intakeMotorState = IntakeMotorStates.idle;
        lockServoState = LockServoStates.lock;
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

    public static LockServoStates getLockServoState() {
        return lockServoState;
    }

    public static void setLockServoState(LockServoStates state) {
        lockServoState = state;
    }
}