package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedTrajectories implements Trajectories{

    SampleMecanumDrive drive;
    TrajectorySequence moveToShoot;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(45, 45,Math.toRadians(45));

    public RedTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShoot = drive.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(15,15))
                .waitSeconds(3)
                .build();

        park = drive.trajectorySequenceBuilder(moveToShoot.end())
                .turn(Math.toRadians(45))
                .lineTo(new Vector2d(15, -10))
                .build();

    }

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