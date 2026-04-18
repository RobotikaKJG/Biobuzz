package org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface AudienceTrajectories {
    TrajectorySequence moveToShootFirst();
    TrajectorySequence goToTakeFirst();
    TrajectorySequence moveToShootSecond();
    TrajectorySequence goToTakeBalls();
    TrajectorySequence moveToShoot();

    TrajectorySequence park();

    Pose2d getStartPose();
}
