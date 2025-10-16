package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeServoControl outtakeServoControl;
    private final OuttakeMotorControl outtakeMotorControl;

    public OuttakeControl(OuttakeServoControl outtakeServoControl, OuttakeMotorControl outtakeMotorControl) {
        this.outtakeServoControl = outtakeServoControl;
        this.outtakeMotorControl = outtakeMotorControl;
    }

    public void update() {
        outtakeServoControl.update();
        outtakeMotorControl.update();

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