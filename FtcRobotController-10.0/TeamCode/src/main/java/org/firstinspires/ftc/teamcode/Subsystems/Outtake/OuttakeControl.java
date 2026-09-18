package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferMotor.TransferMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo.TransferServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

/**
 * Aggregates the outtake child controllers created by Dependencies.
 * Called once per iteration in TeleOp and autonomous, after intentions have been selected.
 * Retained folder/class names are extension points; the starter has no outtake hardware.
 */
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
        // Select sequence transitions before applying child states in this same loop.
        autoCycleShootLogic.update();
        autoCycleShootControl.update();
        outtakeServoControl.update();
        outtakeMotorControl.update();
        transferServoControl.update();
        transferMotorControl.update();

        updateOuttakeState();
    }

    private void updateOuttakeState(){
        if(outtakeActive())
            OuttakeStates.setOuttakeState(SubsystemState.Run);
        else
            OuttakeStates.setOuttakeState(SubsystemState.Idle);
    }

    private boolean outtakeActive() {
        // Extend this predicate when introducing non-idle child states.
        return false;
    }
}