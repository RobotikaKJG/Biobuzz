package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurnServo.TurnServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final TurnServoControl turnServoControl;

    public OuttakeControl(TurnServoControl turnServoControl) {
        this.turnServoControl = turnServoControl;
    }

    public void update() {
        turnServoControl.update();

        updateOuttakeState();
    }

    private void updateOuttakeState(){
        if(outtakeActive())
            OuttakeStates.setOuttakeState(SubsystemState.Run);
        else
            OuttakeStates.setOuttakeState(SubsystemState.Idle);
    }

    private boolean outtakeActive() {
        return false;
    }
}