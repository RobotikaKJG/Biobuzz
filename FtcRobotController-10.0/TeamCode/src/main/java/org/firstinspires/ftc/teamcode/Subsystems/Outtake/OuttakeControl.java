package org.firstinspires.ftc.teamcode.Subsystems.Outtake;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootLogic;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo.OuttakeServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.SubsystemState;

public class OuttakeControl {
    private final OuttakeMotorControl outtakeMotorControl;
    private final AutoCycleShootControl autoCycleShootControl = new AutoCycleShootControl();
    private final AutoCycleShootLogic autoCycleShootLogic;
    private final FeederMotorControl feederMotorControl;
    private final TurretServoControl turretServoControl;
    private final OuttakeServoControl outtakeServoControl;

    public OuttakeControl(OuttakeMotorControl outtakeMotorControl, AutoCycleShootLogic autoCycleShootLogic, FeederMotorControl feederMotorControl, TurretServoControl turretServoControl, OuttakeServoControl outtakeServoControl) {
        this.outtakeMotorControl = outtakeMotorControl;
        this.autoCycleShootLogic = autoCycleShootLogic;
        this.feederMotorControl = feederMotorControl;
        this.turretServoControl = turretServoControl;
        this.outtakeServoControl = outtakeServoControl;
    }

    public void update() {
        outtakeMotorControl.update();
        autoCycleShootControl.update();
        autoCycleShootLogic.update();
        feederMotorControl.update();
        turretServoControl.update();
        outtakeServoControl.update();

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