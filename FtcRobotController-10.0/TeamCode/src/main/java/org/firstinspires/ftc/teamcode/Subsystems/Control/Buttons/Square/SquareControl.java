package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Square;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoStates;

public class SquareControl {
    public void update() {
        switch (ButtonStates.getSquareState()) {
            case turnTurret:
                OuttakeStates.setTurretServoState(TurretServoStates.adjust);
                break;
            case idle:
                break;
        }
    }
}
