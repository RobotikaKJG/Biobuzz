package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftTriggerControl {

    public LeftTriggerControl() {
    }

    public void update() {
        switch(ButtonStates.getLeftTriggerState()){
            case toggleOuttake:
                toggleOuttake();
                break;
            case idle:
                break;
        }
    }

    private void toggleOuttake() {
        switch (OuttakeStates.getMotorState()){
            case idle:
                OuttakeStates.setMotorState(OuttakeMotorStates.forwardStart);
                break;
            case forwardStart:
            case forwardFull:
                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                break;
        }
    }
}