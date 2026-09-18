package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo.TransferServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

/**
 * Requested outtake states shared by button commands and autonomous routines.
 * OuttakeControl and its child controllers read these each loop. Reset at each OpMode start.
 * Add state values to the child enums and implement their behavior in the matching Control classes.
 */
public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeServoStates outtakeServoState = OuttakeServoStates.idle;
    private static OuttakeMotorStates outtakeMotorState = OuttakeMotorStates.idle;
    private static TransferServoStates transferServoState = TransferServoStates.idle;
    private static AutoCycleShootStates autoCycleShootStates = AutoCycleShootStates.idle;
    private static TransferMotorStates transferMotorState = TransferMotorStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeServoState = OuttakeServoStates.idle;
        outtakeMotorState = OuttakeMotorStates.idle;
        transferServoState = TransferServoStates.idle;
        autoCycleShootStates = AutoCycleShootStates.idle;
        transferMotorState = TransferMotorStates.idle;
    }

    public static SubsystemState getOuttakeState() {
        return outtakeState;
    }

    public static void setOuttakeState(SubsystemState state) {
        outtakeState = state;
    }

    public static OuttakeServoStates getOuttakeServoState() {
        return outtakeServoState;
    }

    public static void setOuttakeServoState(OuttakeServoStates state) {outtakeServoState = state;}

    public static OuttakeMotorStates getMotorState() {
        return outtakeMotorState;
    }

    public static void setMotorState(OuttakeMotorStates state) {outtakeMotorState = state;}

    public static TransferServoStates getTransferServoState() {
        return transferServoState;
    }

    public static void setTransferServoState(TransferServoStates state) {
        transferServoState = state;
    }

    public static AutoCycleShootStates getAutoCycleShootState() {
        return autoCycleShootStates;
    }

    public static void setAutoCycleShootState(AutoCycleShootStates state) {autoCycleShootStates = state;}

    public static TransferMotorStates getTransferMotorState() {
        return transferMotorState;
    }

    public static void setTransferMotorState(TransferMotorStates state) {transferMotorState = state;}

}
