package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedAudienceTrajectories implements AudienceTrajectories {
    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence moveToShoot;
    TrajectorySequence goToTakeBalls;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(-60, -15,Math.toRadians(360));
    private final Pose2d shootPose = new Pose2d(-55, -20, Math.toRadians(333.5));
    private final Pose2d takePose = new Pose2d(-55, -65, Math.toRadians(271));

    public RedAudienceTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(shootPose)
                .build();

        moveToShoot = drive.trajectorySequenceBuilder(takePose)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeBalls = drive.trajectorySequenceBuilder(shootPose)
                .lineToLinearHeading(takePose)
                .build();

        park = drive.trajectorySequenceBuilder(moveToShoot.end())
                .lineTo(new Vector2d(-55, -40))
                .build();
    }

    @Override
    public TrajectorySequence moveToShootFirst() {
        return moveToShootFirst;
    }

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
