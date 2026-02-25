package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface AudienceTrajectories {
    TrajectorySequence moveToShootFirst();
    TrajectorySequence goToTakeFirst();
    TrajectorySequence takeFirst();
    TrajectorySequence moveToShootSecond();
    TrajectorySequence goToTakeBalls();
    TrajectorySequence takeBalls();
    TrajectorySequence moveToShoot();

    TrajectorySequence park();

    Pose2d getStartPose();
}
