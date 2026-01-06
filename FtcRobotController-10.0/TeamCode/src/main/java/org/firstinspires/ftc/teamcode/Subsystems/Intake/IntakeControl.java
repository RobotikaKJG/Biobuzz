package org.firstinspires.ftc.teamcode.Subsystems.Intake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake.AutoFeederIntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake.AutoFeederIntakeLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class IntakeControl {
    private final IntakeMotorControl intakeMotorControl;
    private final AutoFeederIntakeControl autoFeederIntakeControl;
    private final AutoFeederIntakeLogic autoFeederIntakeLogic;

    public IntakeControl(IntakeMotorControl intakeMotorControl, AutoFeederIntakeControl autoFeederIntakeControl, AutoFeederIntakeLogic autoFeederIntakeLogic) {
        this.intakeMotorControl = intakeMotorControl;
        this.autoFeederIntakeControl = autoFeederIntakeControl;
        this.autoFeederIntakeLogic = autoFeederIntakeLogic;
    }

    public void update() {
        intakeMotorControl.update();
        autoFeederIntakeControl.update();
        autoFeederIntakeLogic.update();

        updateIntakeState();
    }

    private void updateIntakeState(){
        if(intakeActive())
            IntakeStates.setIntakeState(SubsystemState.Run);
        else
            IntakeStates.setIntakeState(SubsystemState.Idle);
    }

    private boolean intakeActive() {
        return false;
    }
}