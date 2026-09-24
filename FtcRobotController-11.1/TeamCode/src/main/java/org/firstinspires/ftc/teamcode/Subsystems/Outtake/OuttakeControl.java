package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeMotorControl outtakeMotorControl;

    // NOTE: the turret is intentionally NOT updated here. In TeleOp it runs on the
    // dedicated TurretThread; in Autonomous it is updated by AutonomousControl.
    public OuttakeControl(OuttakeMotorControl outtakeMotorControl) {
        this.outtakeMotorControl = outtakeMotorControl;
    }

    public void update() {
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