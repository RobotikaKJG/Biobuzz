
package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.DpadUp;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class DpadUpControl {
    public void update() {
        switch (ButtonStates.getDpadUpState()) {
            case toggleMotor:
                toggleMotor();
                break;
            case idle:
                break;
        }
    }

    private void toggleMotor() {
        switch (IntakeStates.getMotorState()) {
            case forward:
                IntakeStates.setMotorState(IntakeMotorStates.backward);
                break;
            case backward:
                IntakeStates.setMotorState(IntakeMotorStates.forward);
                break;
            case idle:
                IntakeStates.setMotorState(IntakeMotorStates.backward);
                break;
        }
    }
}
