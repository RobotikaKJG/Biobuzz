package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeMotorControl outtakeMotorControl;
    private final AutoCycleShootControl autoCycleShootControl = new AutoCycleShootControl();
    private final AutoCycleShootLogic autoCycleShootLogic = new AutoCycleShootLogic();
    private final FeederMotorControl feederMotorControl;

    public OuttakeControl(OuttakeMotorControl outtakeMotorControl, FeederMotorControl feederMotorControl) {
        this.outtakeMotorControl = outtakeMotorControl;
        this.feederMotorControl = feederMotorControl;
    }

    public void update() {
        outtakeMotorControl.update();
        autoCycleShootControl.update();
        autoCycleShootLogic.update();
        feederMotorControl.update();

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