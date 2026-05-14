package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootControl {
    private final MotorControl motorControl;
    private AutoCycleShootStates prevAutoCycleShootState = AutoCycleShootStates.idle;

    private static final double FAR_MIN_OUTTAKE_VELOCITY = 2080.0;

    public AutoCycleShootControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        if(OuttakeStates.getAutoCycleShootState() != prevAutoCycleShootState) {
            updateStates();
            prevAutoCycleShootState = OuttakeStates.getAutoCycleShootState();
        }
        else if (OuttakeStates.getAutoCycleShootState() == AutoCycleShootStates.turnTransfer) {
            updateStates();
        }
    }

    public void updateStates() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case activate:
                IntakeStates.setLockServoState(LockServoStates.unlock);
                break;
            case turnBack:

                break;
            case turnTransfer:
                if (GlobalVariables.far) {
                    if (!GlobalVariables.isAutonomous) {
//                        if (motorControl.getMotorVelocity(MotorConstants.outtake1) > FAR_MIN_OUTTAKE_VELOCITY && motorControl.getMotorVelocity(MotorConstants.outtake1) < FAR_MIN_OUTTAKE_VELOCITY) {
//                            IntakeStates.setMotorState(IntakeMotorStates.forward);
//                        } else {
//                            IntakeStates.setMotorState(IntakeMotorStates.idle);
//                        }
                        IntakeStates.setIntakeMotorState(IntakeMotorStates.forward);
                        IntakeStates.setTransferMotorState(TransferMotorStates.forward);
                    }
                    else {
                        if (motorControl.getMotorVelocity(MotorConstants.outtake1) > FAR_MIN_OUTTAKE_VELOCITY-200 && motorControl.getMotorVelocity(MotorConstants.outtake1) < FAR_MIN_OUTTAKE_VELOCITY) {
                            IntakeStates.setIntakeMotorState(IntakeMotorStates.forward);
                            IntakeStates.setTransferMotorState(TransferMotorStates.forward);
                        } else {
                            IntakeStates.setIntakeMotorState(IntakeMotorStates.idle);
                            IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                        }
                    }
                } else {
                    IntakeStates.setIntakeMotorState(IntakeMotorStates.forward);
                    OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
                }
                break;
            case stop:
                break;
            case turnTransferBack:
                IntakeStates.setTransferMotorState(TransferMotorStates.backward);
                break;
            case deactivate:
                IntakeStates.setIntakeMotorState(IntakeMotorStates.idle);
                IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                IntakeStates.setLockServoState(LockServoStates.lock);
                break;
            case idle:
                break;
        }
    }
}