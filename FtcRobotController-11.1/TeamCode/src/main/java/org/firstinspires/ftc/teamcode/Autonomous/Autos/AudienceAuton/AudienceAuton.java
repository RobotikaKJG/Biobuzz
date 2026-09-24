package org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.AudiencePaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.BlueAudiencePaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.RedAudiencePaths;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AudienceAuton implements Auton {
    private SensorControl sensorControl;
    private Follower follower;
    private AudiencePaths paths;
    private AudienceAutonState audienceAutonState = AudienceAutonState.drive_startPos_shootPos;
    private double currentWait = 0;

    public AudienceAuton(Follower follower, SensorControl sensorControl) {
        this.follower = follower;
        this.sensorControl = sensorControl;
    }

    @Override
    public void start() {
        setTrajectorySide();
        follower.setStartingPose(paths.getPt_startPose());
        follower.followPath(paths.startPos_shootPos(), true);
        OuttakeStates.setOuttakeMotorState(OuttakeMotorStates.forwardClose);
        audienceAutonState = AudienceAutonState.drive_startPos_shootPos;
        addWaitTime(AutonomousConstants.shooterToMaxSpeed + 0.8);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                paths = new RedAudiencePaths(follower);
                break;
            case Blue:
                paths = new BlueAudiencePaths(follower);
                break;
        }
    }

    @Override
    public void run() {
        switch (audienceAutonState)
        {
            case drive_startPos_shootPos:
                drive_StartPos_ShootPos();
                break;
            case shoot_preload:
                shoot_preload();
                break;
            case drive_shootPos_takeThreePos:
                drive_shootPos_takeThreePos();
                break;
            case drive_takeThreePos_shootPos:
                drive_takeThreePos_shootPos();
                break;
            case shoot_first:
                break;
            case stop:
                stop();
                break;
            case idle:
                break;
        }
    }

//AUTONOTE FILL IN THE LOGIC

    private void drive_StartPos_ShootPos() {
        if(follower.isBusy() || getSeconds() < currentWait) return;
        audienceAutonState = AudienceAutonState.shoot_preload;
        addWaitTime(AutonomousConstants.shootTime + 0.6);
    }

    private void shoot_preload() {
        if(getSeconds() < currentWait) return;
        IntakeStates.setLockServoState(LockServoStates.lock);
        follower.followPath(paths.shootPos_takeThreePos(), 0.8, false);
        audienceAutonState = AudienceAutonState.drive_shootPos_takeThreePos;
        addWaitTime(0.75);
    }

    private void drive_shootPos_takeThreePos() {
        if (getSeconds() < currentWait) return;
        follower.followPath(paths.takeThreePos_shootPos(), false);
        audienceAutonState = AudienceAutonState.drive_takeThreePos_shootPos;
    }

    private void drive_takeThreePos_shootPos() {
        if (follower.isBusy()) return;
        audienceAutonState = AudienceAutonState.shoot_first;
        addWaitTime(AutonomousConstants.shootTime + 0.6);
    }

    private void stop() {
        if(follower.isBusy()) return;
        audienceAutonState = AudienceAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}