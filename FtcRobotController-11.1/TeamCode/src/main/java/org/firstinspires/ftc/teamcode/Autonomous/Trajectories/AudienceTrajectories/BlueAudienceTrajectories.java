package org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class BlueAudienceTrajectories implements AudienceTrajectories {
    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeFirst;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToTakeBalls;
    TrajectorySequence moveToShoot;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(64, -10,Math.toRadians(-90));
    private final Pose2d shootPose = new Pose2d(62, -10, Math.toRadians(-90));

    public BlueAudienceTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 5)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeFirst = drive.trajectorySequenceBuilder(shootPose, 70)
                .lineToSplineHeading(new Pose2d(46, -15, Math.toRadians(90)))
                .splineToSplineHeading(new Pose2d(34, -25, Math.toRadians(90)), Math.toRadians(-90))
                .splineToSplineHeading(new Pose2d(34, -51, Math.toRadians(90)), Math.toRadians(-90))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(goToTakeFirst.end(), 50)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeBalls = drive.trajectorySequenceBuilder(shootPose, 100)
                .splineToSplineHeading(new Pose2d(42, -61, Math.toRadians(-190)), Math.toRadians(0))
                .waitSeconds(0.1)
                .lineToSplineHeading(new Pose2d(65, -65, Math.toRadians(180)))
                .build();

        moveToShoot = drive.trajectorySequenceBuilder(goToTakeBalls.end(), 100)
                .lineToLinearHeading(shootPose)
                .build();

        park = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineTo(new Vector2d(60, -40))
                .build();
    }

    @Override
    public TrajectorySequence moveToShootFirst() {
        return moveToShootFirst;
    }

    @Override
    public TrajectorySequence goToTakeFirst() { return goToTakeFirst; }


    @Override
    public TrajectorySequence moveToShootSecond() {
        return moveToShootSecond;
    }

    @Override
    public TrajectorySequence goToTakeBalls() {
        return goToTakeBalls;
    }


    @Override
    public TrajectorySequence moveToShoot() {
        return moveToShoot;
    }

    public TrajectorySequence park() {
        return park;
    }

    public Pose2d getStartPose() {
        return startPose;
    }
}
