package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoFeederIntakeControl {
    private AutoFeederIntakeStates prevAutoFeederIntakeState = AutoFeederIntakeStates.idle;

    public void update() {
        if(IntakeStates.getAutoFeederIntakeState() != prevAutoFeederIntakeState) {
            updateStates();
            prevAutoFeederIntakeState = IntakeStates.getAutoFeederIntakeState();
        }
    }

    public void updateStates() {
        switch (IntakeStates.getAutoFeederIntakeState()) {
            case stopIntake:
                IntakeStates.setMotorState(IntakeMotorStates.idle);
                OuttakeStates.setFeederMotorState(FeederMotorStates.backward);
                OuttakeStates.setMotorState(OuttakeMotorStates.backward);
                break;
            case stopFeeder:
                OuttakeStates.setFeederMotorState(FeederMotorStates.idle);
                OuttakeStates.setMotorState(OuttakeMotorStates.idle);
                break;
            case idle:
                break;
        }
    }
}
