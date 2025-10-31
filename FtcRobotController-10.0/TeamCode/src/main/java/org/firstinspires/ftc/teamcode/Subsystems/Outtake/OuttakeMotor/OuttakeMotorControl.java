package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeMotorControl {
    private final MotorControl motorControl;
    private OuttakeMotorStates prevMotorStates = OuttakeMotorStates.idle;
    private double currentWait = 0;
    private double power = -0.7;

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
            case forwardStart: // here both do the same but if speed is not close enough can be used elsewhere
            case forwardFull:
                motorSpeedControl();
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.outtake, 0.9);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.outtake, 0);
                break;
        }

    }

    private void motorSpeedControl() {
        double currentVelocity = motorControl.getMotorVelocity(MotorConstants.outtake);
        double error = OuttakeConstants.targetVelocity + currentVelocity;
        if(error < OuttakeConstants.targetVelocity/3.0){
            if (error > 50)
                power = power - 0.002;
            else if (error < -50)
                power = power + 0.003;
            power = Math.max(-1, Math.min(1, power));
            System.out.println("Power: " + power);
        }
        motorControl.setMotorSpeed(MotorConstants.outtake, power);
        if(Math.abs(error) > 150)
            OuttakeStates.setMotorState(OuttakeMotorStates.forwardStart);
        else
            OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}