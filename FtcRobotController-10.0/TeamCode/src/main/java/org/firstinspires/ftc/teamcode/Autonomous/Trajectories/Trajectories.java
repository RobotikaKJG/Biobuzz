package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public interface Trajectories {

    // Abstract methods for each trajectory sequence to be implemented by subclasses
    TrajectorySequence moveToShoot();
    TrajectorySequence park();
    Pose2d getStartPose();
}