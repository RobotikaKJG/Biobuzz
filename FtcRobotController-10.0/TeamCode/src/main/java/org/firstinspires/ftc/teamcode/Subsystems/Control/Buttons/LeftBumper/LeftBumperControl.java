package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.LeftBumper;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class LeftBumperControl {
    public void update() {
        switch (ButtonStates.getLeftBumperState()) {
            case shoot:
                OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
                break;
            case stop:
                OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
                break;
            case idle:
                break;
        }
    }
}
