package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedAudienceTrajectories implements AudienceTrajectories {
    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeFirst;
    TrajectorySequence takeFirst;
    TrajectorySequence moveToShoot;
    TrajectorySequence goToTakeBalls;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(-60, -15,Math.toRadians(360));
    private final Pose2d shootPose = new Pose2d(-50, -20, Math.toRadians(360));
    private final Pose2d takePose = new Pose2d(-10, -40, Math.toRadians(271));

    public RedAudienceTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 100)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeFirst = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineToLinearHeading(new Pose2d(-25, -40, Math.toRadians(270)))
                .build();

        takeFirst = drive.trajectorySequenceBuilder(goToTakeFirst.end(), 40)
                .lineToLinearHeading(new Pose2d(-25, -70, Math.toRadians(270)))
                .build();

        moveToShoot = drive.trajectorySequenceBuilder(takeFirst.end(), 50)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeBalls = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineToLinearHeading(takePose)
                .build();

        park = drive.trajectorySequenceBuilder(moveToShoot.end(), 100)
                .lineTo(new Vector2d(-55, -40))
                .build();
    }

    @Override
    public TrajectorySequence moveToShootFirst() {
        return moveToShootFirst;
    }

    @Override
    public TrajectorySequence goToTakeFirst() { return goToTakeFirst; }

    @Override
    public TrajectorySequence takeFirst() { return takeFirst; }

    @Override
    public TrajectorySequence moveToShoot() {
        return moveToShoot;
    }

    @Override
    public TrajectorySequence goToTakeBalls() {
        return goToTakeBalls;
    }

    public TrajectorySequence park() {
        return park;
    }

    public Pose2d getStartPose() {
        return startPose;
    }
}
