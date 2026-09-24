package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.BlueGoalPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.GoalPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.RedGoalPaths;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton {
    private Follower follower;
    private GoalPaths paths;
    private GoalAutonState goalAutonState = GoalAutonState.drive_startPos_shootPos;
    private double currentWait = 0;
    private boolean wasIfCalled = false;

    public GoalAuton(Follower follower) {
        this.follower = follower;
    }

    @Override
    public void start() {
        setTrajectorySide();
        follower.setStartingPose(paths.getStartPose());
        follower.followPath(paths.startPos_shootPos(), 0.9, false);
        OuttakeStates.setOuttakeMotorState(OuttakeMotorStates.forwardClose);
        IntakeStates.setLockServoState(LockServoStates.unlock);
        goalAutonState = GoalAutonState.drive_startPos_shootPos;
        addWaitTime(AutonomousConstants.shooterToMaxSpeed);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                paths = new RedGoalPaths(follower);
                break;
            case Blue:
                paths = new BlueGoalPaths(follower);
                break;
        }
    }

    @Override
    public void run() {
        switch (goalAutonState)
        {
            case drive_startPos_shootPos:
                drive_StartPos_ShootPos();
                break;
            case shoot_preload:
                shoot_preload();
                break;
            case drive_shootPos_takeFirstPos:
                drive_shootPos_takeFirstPos();
                break;
            case drive_takeFirstPos_shootPos:
                drive_takeFirstPos_shootPos();
                break;
            case shoot_first:
                break;
            case stop:
                break;
            case idle:
                break;
        }
    }

//AUTONOTE FILL IN THE LOGIC

    private void drive_StartPos_ShootPos() {
        if(getSeconds() < currentWait) return;
        if (follower.isBusy()) return;
        goalAutonState = GoalAutonState.shoot_preload;
//        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot_preload() {
//        if(getSeconds() < currentWait) return;
        OuttakeStates.setOuttakeMotorState(OuttakeMotorStates.forwardClose);
        IntakeStates.setLockServoState(LockServoStates.lock);
        follower.followPath(paths.shootPos_takeFirstPos(), false);
        goalAutonState = GoalAutonState.drive_shootPos_takeFirstPos;
    }

    private void drive_shootPos_takeFirstPos() {
        if (follower.isBusy()) return;
        follower.followPath(paths.takeFirstPos_shootPos());
        goalAutonState = GoalAutonState.drive_takeFirstPos_shootPos;
    }

    private void drive_takeFirstPos_shootPos() {
        if (follower.isBusy()) return;
        goalAutonState = GoalAutonState.shoot_first;
        addWaitTime(0.2);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}