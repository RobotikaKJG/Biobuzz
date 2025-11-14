package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeMotorStates outtakeMotorState = OuttakeMotorStates.idle;
    private static AutoCycleShootStates autoCycleShootStates = AutoCycleShootStates.idle;
    private static TransferMotorStates transferMotorState = TransferMotorStates.idle;
    private static FeederMotorStates feederMotorState = FeederMotorStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeMotorState = OuttakeMotorStates.idle;
        autoCycleShootStates = AutoCycleShootStates.idle;
        transferMotorState = TransferMotorStates.idle;
        feederMotorState = FeederMotorStates.idle;
    }

    public static SubsystemState getOuttakeState() {
        return outtakeState;
    }

    public static void setOuttakeState(SubsystemState state) {
        outtakeState = state;
    }

    public static OuttakeMotorStates getMotorState() {
        return outtakeMotorState;
    }

    public static void setMotorState(OuttakeMotorStates state) {outtakeMotorState = state;}

    public static AutoCycleShootStates getAutoCycleShootState() {
        return autoCycleShootStates;
    }

    public static void setAutoCycleShootState(AutoCycleShootStates state) {autoCycleShootStates = state;}

    public static FeederMotorStates getFeederMotorState() {
        return feederMotorState;
    }

    public static void setFeederMotorState(FeederMotorStates state) {feederMotorState = state;}

}