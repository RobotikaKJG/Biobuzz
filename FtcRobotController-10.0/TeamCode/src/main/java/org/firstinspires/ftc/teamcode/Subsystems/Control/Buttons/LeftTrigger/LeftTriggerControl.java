package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftTriggerControl {

    public LeftTriggerControl() {
    }

    public void update() {
        switch(ButtonStates.getLeftTriggerState()){
            case runOuttake:
                OuttakeStates.setMotorState(OuttakeMotorStates.forwardStart);
                break;
            case stopOuttake:
                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}