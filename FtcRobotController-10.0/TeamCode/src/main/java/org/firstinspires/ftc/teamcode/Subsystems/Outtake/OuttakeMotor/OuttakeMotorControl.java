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
        } else if (OuttakeStates.getMotorState() == OuttakeMotorStates.forwardStart || OuttakeStates.getMotorState() == OuttakeMotorStates.forwardFull) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getMotorState()) {
            case forwardStart:
                motorControl.setMotorRPM(MotorConstants.outtake1, 1400);
                motorControl.setMotorRPM(MotorConstants.outtake2, 1400);
                if (motorControl.getMotorVelocity(MotorConstants.outtake1) > 1300)
                    OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
                break;
            case forwardFull:
                motorControl.setMotorRPM(MotorConstants.outtake1, 2650);
                motorControl.setMotorRPM(MotorConstants.outtake2, 2650);
                break;
            case backward:
                motorControl.setMotorRPM(MotorConstants.outtake1, -1400);
                motorControl.setMotorRPM(MotorConstants.outtake2, -1400);
                break;
            case idle:
                motorControl.setMotorRPM(MotorConstants.outtake1, 0);
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