package org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPaths implements GoalPaths {
    Follower follower;
    private PathChain startPos_shootPos;
    private PathChain shootPos_takeFirstPos;
    private PathChain takeFirstPos_shootPos;

    private final Pose pt_startPose = new Pose(23.1, 119.2, Math.toRadians(143.8));
    private final Pose pt_shootPose = new Pose(53, 84, Math.toRadians(180));

    private final Pose pt_takeFirstPose = new Pose(19.6, 54.65);
    private final Pose cp_takeFirstPose = new Pose(34.2, 54.5);
    private final Pose cp_takeFirstPoseBack = new Pose(48.3, 81.27);

    public BlueGoalPaths(Follower follower) {
        this.follower = follower;
        buildPaths();
    }

    private void buildPaths() {
        startPos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_startPose, pt_shootPose))
                .setLinearHeadingInterpolation(pt_startPose.getHeading(), pt_shootPose.getHeading())
                .setBrakingStart(5)
                .build();

        shootPos_takeFirstPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_takeFirstPose, pt_takeFirstPose))
                .setTangentHeadingInterpolation()
                .build();

        takeFirstPos_shootPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_takeFirstPose, cp_takeFirstPose, cp_takeFirstPoseBack, pt_shootPose))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();
    }

    public PathChain startPos_shootPos() {
        return startPos_shootPos;
    }

    @Override
    public Pose getStartPose() {
        return pt_startPose;
    }

    public PathChain shootPos_takeFirstPos() {
        return shootPos_takeFirstPos;
    }

    public PathChain takeFirstPos_shootPos() {
        return takeFirstPos_shootPos;
    }
}