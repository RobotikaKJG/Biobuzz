package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeMovement;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;

public class AutoIntakeMovementControl {
    private SensorControl sensorControl;

    public AutoIntakeMovementControl(SensorControl sensorControl) {
        this.sensorControl = sensorControl;
    }

    public void update() {
        updateStates();
    }

    public void updateStates() {
        switch (IntakeStates.getAutoIntakeMovementState()) {
            case activate:
                autoSwitch();
                break;
            case idle:
                break;
        }
    }

    private void autoSwitch() {
        if (sensorControl.isDrivingForward()) {
            IntakeStates.setIntakeMotorState(IntakeMotorStates.forward);
            IntakeStates.setTransferMotorState(TransferMotorStates.forward);
        }
        else if (sensorControl.isDrivingBackward()){
            IntakeStates.setIntakeMotorState(IntakeMotorStates.idle);
            IntakeStates.setTransferMotorState(TransferMotorStates.idle);
        }
    }
}