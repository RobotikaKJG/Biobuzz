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
            case forwardFull:
                motorControl.setMotorRPM(MotorConstants.outtake, -1000);
                break;
            case backward:
                motorControl.setMotorRPM(MotorConstants.outtake, 1300);
                break;
            case idle:
                motorControl.setMotorRPM(MotorConstants.outtake, 0);
                break;
        }

    }
}