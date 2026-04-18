package org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface GoalSoloTrajectories {

    TrajectorySequence moveToShootFirst();

    TrajectorySequence goToTakeSecondBalls();
    TrajectorySequence moveToShootSecond();

    TrajectorySequence goToTakeThirdBalls();
    TrajectorySequence goToTakeThirdBallsRelease();
    TrajectorySequence moveToShootThird();

    TrajectorySequence goToTakeFourthBalls();
    TrajectorySequence moveToShootFourth();

    TrajectorySequence goToTakeFifthBalls();
    TrajectorySequence moveToShootFifth();

    TrajectorySequence park();

    Pose2d getStartPose();
}