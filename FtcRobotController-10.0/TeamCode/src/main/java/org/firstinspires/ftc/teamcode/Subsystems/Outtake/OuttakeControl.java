package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor.TransferMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo.TransferServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeServoControl outtakeServoControl;
    private final OuttakeMotorControl outtakeMotorControl;
    private final TransferServoControl transferServoControl;
    private final TransferMotorControl transferMotorControl;
    private final AutoCycleShootControl autoCycleShootControl = new AutoCycleShootControl();
    private final AutoCycleShootLogic autoCycleShootLogic = new AutoCycleShootLogic();

    public OuttakeControl(OuttakeServoControl outtakeServoControl, OuttakeMotorControl outtakeMotorControl, TransferServoControl transferServoControl, TransferMotorControl transferMotorControl) {
        this.outtakeServoControl = outtakeServoControl;
        this.outtakeMotorControl = outtakeMotorControl;
        this.transferServoControl = transferServoControl;
        this.transferMotorControl = transferMotorControl;
    }

    public void update() {
        outtakeServoControl.update();
        outtakeMotorControl.update();
        transferServoControl.update();
        transferMotorControl.update();
        autoCycleShootControl.update();
        autoCycleShootLogic.update();

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