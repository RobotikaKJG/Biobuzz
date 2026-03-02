package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface GoalTrajectories {

    TrajectorySequence moveToShootFirst();

    TrajectorySequence goToTakeSecondBalls();
    TrajectorySequence moveToShootSecond();

    TrajectorySequence goToReleaseBalls();
    TrajectorySequence goToTakeBalls();
    TrajectorySequence moveToShootBalls();

    TrajectorySequence goToTakeFifthBalls();
    TrajectorySequence moveToShootFifth();

    TrajectorySequence park();

    Pose2d getStartPose();
}