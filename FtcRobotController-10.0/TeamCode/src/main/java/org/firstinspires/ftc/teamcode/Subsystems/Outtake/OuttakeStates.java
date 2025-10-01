package org.firstinspires.ftc.teamcode.Subsystems.Outtake;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurnServo.TurnServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeStates {
    private static SubsystemState outtakeState = SubsystemState.Idle;
    private static TurnServoStates turnServoState = TurnServoStates.idle;

    public static void setInitialStates() {
        outtakeState = SubsystemState.Idle;
        turnServoState = TurnServoStates.idle;
    }

    public static SubsystemState getOuttakeState() {
        return outtakeState;
    }

    public static void setOuttakeState(SubsystemState state) {
        outtakeState = state;
    }

    public static TurnServoStates getTurnServoState() {
        return turnServoState;
    }

    public static void setTurnServoState(TurnServoStates state) {turnServoState = state;}

}
