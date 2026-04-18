package org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories.AudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories.BlueAudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories.RedAudienceTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AudienceAuton implements Auton {
    private final SampleMecanumDrive drive;
    private AudienceTrajectories trajectories;
    private ServoControl servoControl;
    private SensorControl sensorControl;
    private AudienceAutonState audienceAutonState = AudienceAutonState.moveToShootFirst;
    private double currentWait = 0;
    private boolean wasIfCalled = false;

    public AudienceAuton(SampleMecanumDrive drive, SensorControl sensorControl) {
        this.drive = drive;
        this.sensorControl = sensorControl;
    }

    @Override
    public void start() {
        setTrajectorySide();
        GlobalVariables.far = true;
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        audienceAutonState = AudienceAutonState.moveToShootFirst;
        addWaitTime(AutonomousConstants.shooterToMaxSpeed);
    }


    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedAudienceTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueAudienceTrajectories(drive);
                break;
        }
    }

    @Override
    public void run() {
        switch (audienceAutonState)
        {
            case moveToShootFirst:
                moveToShootFirst();
                break;
            case shootFirst:
                shootFirst();
                break;
            case goToTakeFirst:
                goToTakeFirst();
                break;
            case moveToShootSecond:
                moveToShootSecond();
                break;
            case shootSecond:
                shootSecond();
                break;
            case goToTake:
                goToTake();
                break;
            case moveToShoot:
                moveToShoot();
                break;
            case shoot:
                shoot();
                break;
            case park:
                park();
            case idle:
                break;
        }
    }

    private void moveToShootFirst() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFirst;
        addWaitTime(AutonomousConstants.shootTimeFar - 2);
    }

    private void shootFirst() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        if (GlobalVariables.secondRelease) {
            drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
            addWaitTime(4);
        }
        else {
            drive.followTrajectorySequenceAsync(trajectories.goToTakeFirst());
        }
        audienceAutonState = AudienceAutonState.goToTakeFirst;
    }

    private void goToTakeFirst() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        drive.followTrajectorySequenceAsync(trajectories.moveToShootSecond());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.moveToShootSecond;
    }


    private void moveToShootSecond() {
        if(drive.isBusy()) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootSecond;
        addWaitTime(AutonomousConstants.shootTimeFar);
    }


    private void shootSecond() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        audienceAutonState = AudienceAutonState.goToTake;

        addWaitTime(4);
    }

    private void goToTake() {
        if (drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.moveToShoot;
        addWaitTime(4);
    }

    private void moveToShoot() {
        if (drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shoot;
        addWaitTime(AutonomousConstants.shootTimeFar+3.0);
    }

    private void shoot() {
        if (getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
        drive.followTrajectorySequenceAsync(trajectories.park());
        audienceAutonState = AudienceAutonState.park;
    }

    private void park() {
        if (drive.isBusy()) return;
        audienceAutonState = AudienceAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}
