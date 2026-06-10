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
    private PathChain shootPos_openGatePosBreak;
    private PathChain openGatePosBreak_openGatePos;
    private PathChain openGatePos_shootPos;
    private PathChain shootPos_takeSecondPos_shootPos;
    private PathChain shootPos_takeThirdPos;
    private PathChain takeThirdPos_shootPosPark;

    private final Pose pt_startPose = new Pose(21.0, 119.3, Math.toRadians(143.8));
    private final Pose pt_shootPose = new Pose(48.4, 81.9, Math.toRadians(180));

    private final Pose pt_takeFirstPose = new Pose(17.6, 54.65);
    private final Pose cp_takeFirstPose = new Pose(27.2, 54.5);
    private final Pose cp_takeFirstPoseBack = new Pose(46.3, 81.27);

    private final Pose pt_openGatePose = new Pose(13.7, 58.75, Math.toRadians(150));
    private final Pose pt_openGatePoseBreak = new Pose(27.0, 58.75);
    private final Pose cp_openGatePose = new Pose(43.8, 58.9);

    private final Pose pt_takeSecondPose = new Pose(30.5, 81.9);
    private final Pose pt_shootPoseSecond = new Pose(48.4, 85.9, Math.toRadians(180));

    private final Pose pt_takeThirdPose = new Pose(24.4, 34.9);
    private final Pose cp_takeThirdPose = new Pose(24.9, 49.8);
    private final Pose cp_takeThirdPoseBack = new Pose(48.1, 87.5);

    private final Pose pt_shootPosePark = new Pose(59.6, 96.5, Math.toRadians(180));

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

        shootPos_openGatePosBreak = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_openGatePose, pt_openGatePoseBreak))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        openGatePosBreak_openGatePos = follower.pathBuilder()
                .addPath(new BezierLine(pt_openGatePoseBreak, pt_openGatePose))
                .setLinearHeadingInterpolation(Math.toRadians(180), pt_openGatePose.getHeading())
                .build();


        openGatePos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_openGatePoseBreak, pt_shootPose))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();

        shootPos_takeSecondPos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_shootPose, pt_takeSecondPose))
                .addPath(new BezierLine(pt_takeSecondPose, pt_shootPoseSecond))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        shootPos_takeThirdPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPoseSecond, cp_takeThirdPose, pt_takeThirdPose))
                .setTangentHeadingInterpolation()
                .build();

        takeThirdPos_shootPosPark = follower.pathBuilder()
                .addPath(new BezierCurve(pt_takeThirdPose, cp_takeThirdPose, cp_takeThirdPoseBack, pt_shootPosePark))
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

    public PathChain shootPos_openGatePosBreak() {
        return shootPos_openGatePosBreak;
    }

    public PathChain openGatePosBreak_openGatePos() {
        return openGatePosBreak_openGatePos;
    }

    public PathChain openGatePos_shootPos() {
        return openGatePos_shootPos;
    }

    public PathChain shootPos_takeSecondPos_shootPos() {
        return shootPos_takeSecondPos_shootPos;
    }

    public PathChain shootPos_takeThirdPos() {
        return shootPos_takeThirdPos;
    }

    public PathChain takeThirdPos_shootPosPark() {
        return takeThirdPos_shootPosPark;
    }
}