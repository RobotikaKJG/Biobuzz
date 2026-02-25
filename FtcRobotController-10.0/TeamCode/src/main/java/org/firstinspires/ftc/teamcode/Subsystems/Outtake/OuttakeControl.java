package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoResetPos.AutoResetPosControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretMotor.TurretMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeMotorControl outtakeMotorControl;
    private final AutoCycleShootControl autoCycleShootControl = new AutoCycleShootControl();
    private final AutoCycleShootLogic autoCycleShootLogic;
    private final TurretMotorControl turretServoControl;
    private final AutoOuttakeFarCloseControl autoOuttakeFarCloseControl;
    private final AutoResetPosControl autoResetPosControl;

    public OuttakeControl(OuttakeMotorControl outtakeMotorControl, AutoCycleShootLogic autoCycleShootLogic, TurretMotorControl turretServoControl, AutoOuttakeFarCloseControl autoOuttakeFarCloseControl, AutoResetPosControl autoResetPosControl) {
        this.outtakeMotorControl = outtakeMotorControl;
        this.autoCycleShootLogic = autoCycleShootLogic;
        this.turretServoControl = turretServoControl;
        this.autoOuttakeFarCloseControl = autoOuttakeFarCloseControl;
        this.autoResetPosControl = autoResetPosControl;
    }

    public void update() {
        outtakeMotorControl.update();
        autoCycleShootControl.update();
        autoCycleShootLogic.update();
        turretServoControl.update();
        autoOuttakeFarCloseControl.update();
        autoResetPosControl.update();

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