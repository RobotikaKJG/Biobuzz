package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.Trajectories;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton{
    private final SampleMecanumDrive drive;
    private Trajectories trajectories;
    private GoalAutonState goalAutonState = GoalAutonState.moveToShoot;
    private double currentWait = 0;

    public GoalAuton(SampleMecanumDrive drive) {
        this.drive = drive;

        trajectories = new RedTrajectories(drive);
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        goalAutonState = GoalAutonState.moveToShoot;
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueTrajectories(drive);
                break;
        }
    }

    @Override
    public void run() {
        switch (goalAutonState)
        {
            case moveToShoot:
                moveToShoot();
                break;
            case shootFirst:
                shootFirst();
                break;
            case shootSecond:
                shootSecond();
                break;
            case shootThird:
                shootThird();
                break;
            case park:
                park();
                break;
            case stop:
                stop();
                break;
            case idle:
                break;
        }
    }

//AUTONOTE FILL IN THE LOGIC

    private void moveToShoot() {
        if(drive.isBusy()) return;
        //activate shooting
        goalAutonState = GoalAutonState.shootFirst;
    }

    private void shootFirst() {
//        if(OuttakeStates.getShootState() != ShootStates.idle()) return;
        //activate shooting
        goalAutonState = GoalAutonState.shootSecond;
    }

    private void shootSecond() {
//        if(OuttakeStates.getShootState() != ShootStates.idle()) return;
        //activate shooting
        goalAutonState = GoalAutonState.shootThird;
    }

    private void shootThird() {
//        if(OuttakeStates.getShootState() != ShootStates.idle()) return;
        //activate shooting
        goalAutonState = GoalAutonState.park;
    }

    private void park() {
        //        if(OuttakeStates.getShootState() != ShootStates.idle()) return;
        drive.followTrajectorySequenceAsync(trajectories.park());
        goalAutonState = GoalAutonState.stop;
    }

    private void stop() {
        if(drive.isBusy()) return;
        goalAutonState = GoalAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}