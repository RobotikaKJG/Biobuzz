package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
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
        } else if (OuttakeStates.getMotorState() == OuttakeMotorStates.forwardClose || OuttakeStates.getMotorState() == OuttakeMotorStates.forwardFar) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getMotorState()) {
            case forwardStart:
//                motorControl.setMotorRPM(MotorConstants.outtake1, OuttakeConstants.outtakeVelStart);
//                motorControl.setMotorRPM(MotorConstants.outtake2, OuttakeConstants.outtakeVelStart);
//                if (motorControl.getMotorVelocity(MotorConstants.outtake1) > OuttakeConstants.outtakeVelStart - 150)
//                    OuttakeStates.setAutoOuttakeFarCloseState(AutoOuttakeFarCloseStates.cycle);
                break;
            case forwardFar:
                motorControl.setMotorRPM(MotorConstants.outtake1, OuttakeConstants.outtakeVelFar);
                motorControl.setMotorRPM(MotorConstants.outtake2, OuttakeConstants.outtakeVelFar);
                break;
            case forwardClose:
                motorControl.setMotorRPM(MotorConstants.outtake1, OuttakeConstants.outtakeVelClose);
                motorControl.setMotorRPM(MotorConstants.outtake2, OuttakeConstants.outtakeVelClose);
                break;
            case backward:
                motorControl.setMotorRPM(MotorConstants.outtake1, -OuttakeConstants.outtakeVelStart);
                motorControl.setMotorRPM(MotorConstants.outtake2, -OuttakeConstants.outtakeVelStart);
                break;
            case idle:
                motorControl.setMotorRPM(MotorConstants.outtake1, 0);
                break;
        }
    }
}