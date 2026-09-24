package org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueAudiencePaths implements AudiencePaths {
    Follower follower;

    private PathChain startPos_shootPos;
    private PathChain shootPos_takeThreePos;
    private PathChain takeThreePos_shootPos;

    private final Pose pt_startPose = new Pose(57.5, 7, Math.toRadians(90));
    private final Pose pt_shootPose = new Pose(51.5, 9.53, Math.toRadians(180));

    private final Pose pt_takeThreePose = new Pose(25.94, 25.99);
    private final Pose cp_takeThreePose = new Pose(25.93, 6.7);

    public BlueAudiencePaths(Follower follower) {
        this.follower = follower;
        buildPaths();
    }

    private void buildPaths() {
        startPos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_startPose, pt_shootPose))
                .setLinearHeadingInterpolation(pt_startPose.getHeading(), pt_shootPose.getHeading())
                .build();

        shootPos_takeThreePos = follower.pathBuilder()
                .addPath(new BezierCurve(pt_shootPose, cp_takeThreePose, pt_takeThreePose))
                .setTangentHeadingInterpolation()
                .build();

        takeThreePos_shootPos = follower.pathBuilder()
                .addPath(new BezierLine(pt_takeThreePose, pt_shootPose))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();
    }

    @Override
    public Pose getPt_startPose() {
        return pt_startPose;
    }

    public PathChain startPos_shootPos() {
        return startPos_shootPos;
    }

    public PathChain shootPos_takeThreePos() {
        return shootPos_takeThreePos;
    }

    public PathChain takeThreePos_shootPos() {
        return takeThreePos_shootPos;
    }
}