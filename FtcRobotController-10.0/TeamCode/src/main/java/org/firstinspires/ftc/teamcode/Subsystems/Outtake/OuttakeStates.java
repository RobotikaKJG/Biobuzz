package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static OuttakeServoStates outtakeServoState = OuttakeServoStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        outtakeServoState = OuttakeServoStates.idle;
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

}
