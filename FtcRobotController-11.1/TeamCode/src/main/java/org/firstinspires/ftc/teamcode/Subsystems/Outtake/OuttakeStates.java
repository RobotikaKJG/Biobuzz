package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoResetPos.AutoResetPosStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor.TurretMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeMotorStates outtakeMotorState = OuttakeMotorStates.idle;
    private static AutoCycleShootStates autoCycleShootStates = AutoCycleShootStates.idle;
    private static TurretMotorStates turretServoState = TurretMotorStates.idle;
    private static AutoOuttakeFarCloseStates autoOuttakeFarCloseState = AutoOuttakeFarCloseStates.idle;
    private static AutoResetPosStates autoResetPosState = AutoResetPosStates.idle;
    private static boolean turretTrackingEnabled = true;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeMotorState = OuttakeMotorStates.idle;
        autoCycleShootStates = AutoCycleShootStates.idle;
        turretServoState = TurretMotorStates.idle;
        autoOuttakeFarCloseState = AutoOuttakeFarCloseStates.idle;
        autoResetPosState = AutoResetPosStates.idle;
        turretTrackingEnabled = true;
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

    public static TurretMotorStates getTurretServoState() {
        return turretServoState;
    }

    public static void setTurretServoState(TurretMotorStates state) {
        turretServoState = state;
    }

    public static void setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates state) {
        autoOuttakeFarCloseState = state;
    }

    public static AutoOuttakeFarCloseStates getAutoOuttakeFarCloseState() {
        return autoOuttakeFarCloseState;
    }

    public static void setAutoResetPosState(AutoResetPosStates state) {
        autoResetPosState = state;
    }

    public static AutoResetPosStates getAutoResetPosState() {
        return autoResetPosState;
    }

    public static boolean isTurretTrackingEnabled() {
        return turretTrackingEnabled;
    }

    public static void toggleTurretTracking() {
        turretTrackingEnabled = !turretTrackingEnabled;
    }
}