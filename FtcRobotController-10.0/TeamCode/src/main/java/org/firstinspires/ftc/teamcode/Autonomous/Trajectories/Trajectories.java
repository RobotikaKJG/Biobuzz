package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

/**
 * Alliance-specific path contract used by GoalAuton. Distances are inches, headings are radians.
 * Add named TrajectorySequence methods here and implement them in both alliance classes.
 * Keep path geometry here and decisions about when to follow a path in GoalAuton.
 */
public interface Trajectories {
    Pose2d getStartPose();
}
