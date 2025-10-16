package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.RightBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class RightBumperControl {
    public void update() {
        switch (ButtonStates.getRightBumperState()) {
            case aim:
                OuttakeStates.setOuttakeServoState(OuttakeServoStates.setPosAuto);
                break;
            case idle:
                break;
        }
    }
}
