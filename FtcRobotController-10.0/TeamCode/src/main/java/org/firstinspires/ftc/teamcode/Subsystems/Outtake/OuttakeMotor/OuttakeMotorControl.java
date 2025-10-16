package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeMotorControl {
    private final MotorControl motorControl;
    private OuttakeMotorStates prevMotorStates = OuttakeMotorStates.idle;
    private double currentWait = 0;

    public OuttakeMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;

    }

    public void update() {
        if(OuttakeStates.getMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = OuttakeStates.getMotorState();
        } else if (OuttakeStates.getMotorState() == OuttakeMotorStates.forwardFull) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getMotorState()) {
            case forwardStart:
                motorControl.setMotorSpeed(MotorConstants.outtake, -0.5);
                addWaitTime(1);
                OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
                break;
            case forwardFull:
                if(currentWait > getSeconds()) return;
                motorControl.setMotorSpeed(MotorConstants.outtake, -1);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.outtake, 1.0);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.outtake, 0);
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