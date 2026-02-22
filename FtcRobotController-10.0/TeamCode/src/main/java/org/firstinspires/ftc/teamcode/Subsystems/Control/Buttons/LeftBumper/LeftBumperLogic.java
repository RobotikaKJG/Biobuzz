package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftBumperLogic {
    private final LeftBumperControl leftBumperControl = new LeftBumperControl();
    private MotorControl motorControl;

    public LeftBumperLogic(MotorControl motorControl) {
        this.motorControl = motorControl;
    }


    public void update() {
        if(shoot()) return;
        stop();
        return;
    }

    private boolean shoot() {
        if(OuttakeStates.getAutoCycleShootState() !=  AutoCycleShootStates.idle || OuttakeStates.getMotorState() == OuttakeMotorStates.idle) return false;
//        if(!isCorrectSpeed()) return false;
        ButtonStates.setLeftBumperState(LeftBumperStates.shoot);
        completeAction();
        return true;
    }

    private boolean isCorrectSpeed() {
        double currentSpeed = motorControl.getMotorVelocity(MotorConstants.outtake1);
        double speedDelta = Math.abs(currentSpeed - GlobalVariables.outtakeTargetSpeed);
        return speedDelta < OuttakeConstants.targetSpeedThreshold;
    }

    private void stop() {
        ButtonStates.setLeftBumperState(LeftBumperStates.stop);
        completeAction();
    }

    private void completeAction(){
        leftBumperControl.update();
        ButtonStates.setLeftBumperState(LeftBumperStates.idle);
    }
}
