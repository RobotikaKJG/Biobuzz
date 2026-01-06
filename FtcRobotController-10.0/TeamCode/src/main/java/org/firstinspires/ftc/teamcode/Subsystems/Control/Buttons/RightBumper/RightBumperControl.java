package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class RightBumperControl {
    public void update() {
        switch (ButtonStates.getRightBumperState()) {
            case aimAuto:
                OuttakeStates.setOuttakeServoState(OuttakeServoStates.adjust);
                break;
            case aimFar:
                OuttakeStates.setOuttakeServoState(OuttakeServoStates.idle);
                break;
            case idle:
                break;
        }
    }
}
