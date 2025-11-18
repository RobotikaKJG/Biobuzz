package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface Trajectories {

    // Abstract methods for each trajectory sequence to be implemented by subclasses
    TrajectorySequence moveToShootFirst();

    TrajectorySequence goToTakeSecondBalls();
    TrajectorySequence takeSecondBalls();
    TrajectorySequence moveToShootSecond();

    TrajectorySequence goToTakeThirdBalls();
    TrajectorySequence takeThirdBalls();
    TrajectorySequence moveToShootThird();

    TrajectorySequence goToTakeFourthBalls();
    TrajectorySequence takeFourthBalls();
    TrajectorySequence moveToShootFourth();

    TrajectorySequence park();

    Pose2d getStartPose();
}