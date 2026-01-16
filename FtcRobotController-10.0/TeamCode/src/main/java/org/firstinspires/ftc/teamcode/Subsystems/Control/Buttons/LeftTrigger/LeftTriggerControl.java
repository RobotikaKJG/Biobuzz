package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseStates;
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
                OuttakeStates.setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates.cycle);
                break;
            case forwardFar:
                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                OuttakeStates.setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates.idle);
                break;
            case forwardClose:
                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                OuttakeStates.setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates.idle);
                break;
        }
    }
}