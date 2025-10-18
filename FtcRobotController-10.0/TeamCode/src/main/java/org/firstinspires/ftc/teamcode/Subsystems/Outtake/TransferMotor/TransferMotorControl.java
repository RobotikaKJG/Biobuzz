package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TransferMotorControl {
    private final MotorControl motorControl;
    private TransferMotorStates prevMotorStates = TransferMotorStates.idle;
    private double currentWait = 0;

    public TransferMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;

    }

    public void update() {
        if(OuttakeStates.getTransferMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = OuttakeStates.getTransferMotorState();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getTransferMotorState()) {
            case forward:
                motorControl.setMotorSpeed(MotorConstants.transfer, -1);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.transfer, 1.0);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.transfer, 0);
                break;
        }

    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
