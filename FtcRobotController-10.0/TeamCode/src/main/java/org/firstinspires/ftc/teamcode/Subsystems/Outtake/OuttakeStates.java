package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeMotorStates outtakeMotorState = OuttakeMotorStates.idle;
    private static AutoCycleShootStates autoCycleShootStates = AutoCycleShootStates.idle;
    private static FeederMotorStates feederMotorState = FeederMotorStates.idle;
    private static TurretServoStates turretServoState = TurretServoStates.idle;
    private static OuttakeServoStates outtakeServoState = OuttakeServoStates.idle;
    private static AutoOuttakeFarCloseStates autoOuttakeFarCloseState = AutoOuttakeFarCloseStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeMotorState = OuttakeMotorStates.idle;
        autoCycleShootStates = AutoCycleShootStates.idle;
        feederMotorState = FeederMotorStates.idle;
        turretServoState = TurretServoStates.idle;
        outtakeServoState = OuttakeServoStates.idle;
        autoOuttakeFarCloseState = AutoOuttakeFarCloseStates.idle;
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

    public static TurretServoStates getTurretServoState() {
        return turretServoState;
    }

    public static void setTurretServoState(TurretServoStates state) {
        turretServoState = state;
    }

    public static OuttakeServoStates getOuttakeServoState() {
        return outtakeServoState;
    }

    public static void setOuttakeServoState(OuttakeServoStates state) {
        outtakeServoState = state;
    }

    public static void setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates state) {
        autoOuttakeFarCloseState = state;
    }

    public static AutoOuttakeFarCloseStates getAutoOuttakeFarCloseState() {
        return autoOuttakeFarCloseState;
    }
}