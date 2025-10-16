package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeServoStates outtakeServoState = OuttakeServoStates.idle;
    private static OuttakeMotorStates outtakeMotorState = OuttakeMotorStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeServoState = OuttakeServoStates.idle;
        outtakeMotorState = OuttakeMotorStates.idle;
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

}
