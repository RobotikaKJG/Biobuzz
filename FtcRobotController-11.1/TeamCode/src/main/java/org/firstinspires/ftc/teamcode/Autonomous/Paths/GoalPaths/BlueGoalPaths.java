package org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPaths implements GoalPaths {

    Follower follower;

    private PathChain drive_startPos_shootPos;

    private final Pose pt_startPose = new Pose(0, 0, Math.toRadians(0));
    private final Pose pt_shootPose = new Pose(0, 0, Math.toRadians(0));

    public BlueGoalPaths(Follower follower) {
        this.follower = follower;
        buildPaths();
    }

    private void buildPaths() {
        drive_startPos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_startPose, pt_shootPose))
                .setLinearHeadingInterpolation(pt_startPose.getHeading(), pt_shootPose.getHeading())
                .build();
    }

    @Override
    public Pose getStartPose() {
        return pt_startPose;
    }

    public PathChain drive_startPos_shootPos() {
        return drive_startPos_shootPos;
    }
}