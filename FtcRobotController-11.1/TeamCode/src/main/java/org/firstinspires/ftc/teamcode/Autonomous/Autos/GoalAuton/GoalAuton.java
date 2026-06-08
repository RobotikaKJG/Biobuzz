package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.BlueGoalPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.GoalPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths.RedGoalPaths;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton {
    private Follower follower;
    private GoalPaths paths;
    private GoalAutonState goalAutonState = GoalAutonState.drive_startPos_shootPos;
    private double currentWait = 0;

    public GoalAuton(Follower follower) {
        this.follower = follower;
    }

    @Override
    public void start() {
        GlobalVariables.far = false;
        setTrajectorySide();
        follower.followPath(paths.drive_startPos_shootPos(), true);
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        goalAutonState = GoalAutonState.drive_startPos_shootPos;
        addWaitTime(3.5);
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
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shoot_preload;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void stop() {
        if(follower.isBusy()) return;
        goalAutonState = GoalAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}