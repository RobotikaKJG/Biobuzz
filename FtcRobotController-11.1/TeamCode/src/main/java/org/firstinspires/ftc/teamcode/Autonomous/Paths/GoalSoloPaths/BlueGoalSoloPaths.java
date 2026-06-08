package org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalSoloPaths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalSoloPaths implements GoalSoloPaths {
    Follower follower;
    private PathChain startPos_shootPos;
    private PathChain shootPos_takeFirstPos;
    private PathChain takeFirstPos_shootPos;
    private PathChain shootPos_openGatePos;
    private PathChain openGatePos_takeGatePos;
    private PathChain takeGatePos_shootPos;
    private PathChain takeGatePos_shootPos_last;
    private PathChain shootPos_takeSecondPos_shootPos;

    private final Pose pt_startPose = new Pose(17.6, 118.9, Math.toRadians(144));
    private final Pose pt_shootPose = new Pose(48.4, 81.9, Math.toRadians(180));
    private final Pose pt_takeFirstPose = new Pose(8.5, 58);
    private final Pose cp_takeFirstPose = new Pose(49.3, 54.8);
    private final Pose pt_openGatePose = new Pose(11.2, 59.9);
    private final Pose cp_openGatePose = new Pose(43.8, 58.9);
    private final Pose cp_takeGatePose = new Pose(14.2, 57.5);
    private final Pose pt_takeGatePose = new Pose(11.5, 53);
    private final Pose cp_takeSecondPose = new Pose(-20, 81.9);

    private int takeGateHeading = 145;

    public BlueGoalSoloPaths(Follower follower) {
        this.follower = follower;
        buildPaths();
    }

    private void buildPaths() {
        startPos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_startPose, pt_shootPose))
                .setLinearHeadingInterpolation(pt_startPose.getHeading(), pt_shootPose.getHeading())
                .build();

        shootPos_takeFirstPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_takeFirstPose, pt_takeFirstPose))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        takeFirstPos_shootPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_takeFirstPose, cp_takeFirstPose, pt_shootPose))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(takeGateHeading))
                .build();

        shootPos_openGatePos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_openGatePose, pt_openGatePose))
                .setConstantHeadingInterpolation(Math.toRadians(takeGateHeading))
                .build();

        openGatePos_takeGatePos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_openGatePose, cp_takeGatePose, pt_takeGatePose))
                .setConstantHeadingInterpolation(Math.toRadians(takeGateHeading))
                .build();

        takeGatePos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_takeGatePose, pt_shootPose))
                .setConstantHeadingInterpolation(Math.toRadians(takeGateHeading))
                .build();

        takeGatePos_shootPos_last = follower.pathBuilder()
                .addPath(new BezierLine(pt_takeGatePose, pt_shootPose))
                .setLinearHeadingInterpolation(Math.toRadians(takeGateHeading), Math.toRadians(180))
                .build();

        shootPos_takeSecondPos_shootPos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_takeSecondPose, pt_shootPose))
                .setConstantHeadingInterpolation(Math.toRadians(takeGateHeading))
                .build();
    }

    public PathChain startPos_shootPos() {
        return startPos_shootPos;
    }

    public PathChain shootPos_takeFirstPos() {
        return shootPos_takeFirstPos;
    }

    public PathChain takeFirstPos_shootPos() {
        return takeFirstPos_shootPos;
    }

    public PathChain shootPos_openGatePos() {
        return shootPos_openGatePos;
    }

    public PathChain openGatePos_takeGatePos() {
        return openGatePos_takeGatePos;
    }

    public PathChain takeGatePos_shootPos() {
        return takeGatePos_shootPos;
    }

    public PathChain takeGatePos_shootPos_last() {
        return takeGatePos_shootPos_last;
    }

    public PathChain shootPos_takeSecondPos_shootPos() {
        return shootPos_takeSecondPos_shootPos;
    }
}