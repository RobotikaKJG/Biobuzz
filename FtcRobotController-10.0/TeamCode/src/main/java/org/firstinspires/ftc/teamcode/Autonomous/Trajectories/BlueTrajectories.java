package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;

/**
 * Blue alliance path extension point, selected at START by GoalAuton.
 * The origin is a placeholder, not a competition starting tile. Measure a new start pose first.
 * Add a SampleMecanumDrive constructor parameter when adding trajectory builder methods.
 */
public class BlueTrajectories implements Trajectories {
    @Override
    public Pose2d getStartPose() { return new Pose2d(0, 0, 0); }
}
