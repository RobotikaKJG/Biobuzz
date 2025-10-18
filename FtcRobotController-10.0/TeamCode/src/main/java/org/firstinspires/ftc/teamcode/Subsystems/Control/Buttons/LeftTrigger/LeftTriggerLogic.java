package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftTrigger;

import android.widget.Button;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftTriggerLogic {
    private final LeftTriggerControl leftTriggerControl = new LeftTriggerControl();

    public LeftTriggerLogic() {
    }

    public void update() {
        if(runOuttake()) return;
        stopOuttake();
    }

    private void completeAction(){
        leftTriggerControl.update();
        ButtonStates.setLeftTriggerState(LeftTriggerStates.idle);
    }

    private boolean runOuttake() {
        if(OuttakeStates.getMotorState() != OuttakeMotorStates.idle) return false;
        ButtonStates.setLeftTriggerState(LeftTriggerStates.runOuttake);
        completeAction();
        return true;
    }

    private void stopOuttake() {
        ButtonStates.setLeftTriggerState(LeftTriggerStates.stopOuttake);
        completeAction();
    }
}