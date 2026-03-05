package org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor;

import com.qualcomm.hardware.dfrobot.HuskyLens;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor.TurretMotorStates;

public class IntakeMotorControl {
    private final MotorControl motorControl;
    private IntakeMotorStates prevMotorStates = IntakeMotorStates.idle;

    public IntakeMotorControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        if(IntakeStates.getMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = IntakeStates.getMotorState();
        }

    }

    public void updateStates() {
        switch (IntakeStates.getMotorState()) {
            case forward:
                motorControl.setMotorSpeed(MotorConstants.intake, 1.0);
                if (!GlobalVariables.isAutonomous) {
                    IntakeStates.setLockServoState(LockServoStates.lock);
                    OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
                }
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.intake, -1.0);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.intake, 0);
                break;
        }

    }
}
