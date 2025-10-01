package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoClose;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Latch.LatchStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Motor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Pivot.PivotStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.Slides.ArmSlideStates;

public class AutoCloseControl {
//    lemme try

    private AutoCloseStates prevIntakeAutoCloseState = AutoCloseStates.idle;
    private final Gamepad gamepad1;

    public AutoCloseControl(Gamepad gamepad1) {
        this.gamepad1 = gamepad1;
    }

    public void update() {
        if(IntakeStates.getAutoCloseState() != prevIntakeAutoCloseState) {
            updateStates();
            prevIntakeAutoCloseState = IntakeStates.getAutoCloseState();
        }
    }

    public void updateStates() {
        switch (IntakeStates.getAutoCloseState()) {
            case checkColor:
                checkColor();
                break;
            case securedGoodSample:
                securedGoodSample();
                break;
            case ejectExtraSamples:
                ejectExtraSamples();
                break;
            case waitForRetractConfirmation:
                waitForRetractConfirmation();
                break;
            case waitToRetract:
                IntakeStates.setArmSlideState(ArmSlideStates.close);
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                break;
            case pivot:
                IntakeStates.setPivotState(PivotStates.up);
                break;
            case openLatch:
                IntakeStates.setLatchState(LatchStates.open);
                break;

            case release:
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                break;
            case pivotDown:
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                IntakeStates.setLatchState(LatchStates.closed);
                IntakeStates.setPivotState(PivotStates.down);
                break;
            case idle:
                break;
        }
    }




    private void checkColor() {
        IntakeStates.setLatchState(LatchStates.closed);
    }

    private void securedGoodSample() {
        gamepad1.rumble(200);
        IntakeStates.setLatchState(LatchStates.closed);
        IntakeStates.setPivotState(PivotStates.upSlightly);
    }

    private void ejectExtraSamples() {
        if(!GlobalVariables.isAutonomous)
            IntakeStates.setMotorState(IntakeMotorStates.extraSamples);
    }

    private void waitForRetractConfirmation() {
        IntakeStates.setMotorState(IntakeMotorStates.idle);
    }





}