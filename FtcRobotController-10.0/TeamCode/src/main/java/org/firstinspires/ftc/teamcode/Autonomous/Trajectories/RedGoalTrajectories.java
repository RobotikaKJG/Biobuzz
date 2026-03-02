package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedGoalTrajectories implements GoalTrajectories {

    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeSecondBalls;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToReleaseBalls;
    TrajectorySequence goToTakeBalls;
    TrajectorySequence moveToShootBalls;

    TrajectorySequence goToTakeFifthBalls;
    TrajectorySequence moveToShootFifth;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(-65, 40, Math.toRadians(90));
    private final Vector2d shootPos = new Vector2d(-25, 20);
    private final double shootAngle = Math.toRadians(178);
    private final Pose2d shootPose = new Pose2d(shootPos, shootAngle);

    public RedGoalTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 100)
                .setReversed(false)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(shootPose, 70)
                .setReversed(true)
                .lineTo(new Vector2d(0, 20))
                .splineTo(new Vector2d(12, 25), Math.toRadians(90))
                .lineToLinearHeading(new Pose2d(12, 65, Math.toRadians(-90)))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToReleaseBalls = drive.trajectorySequenceBuilder(shootPose, 100)
                .setReversed(true)
                .splineToLinearHeading(new Pose2d(-5, 55, Math.toRadians(-100)), Math.toRadians(180))
                .build();

        goToTakeBalls = drive.trajectorySequenceBuilder(goToReleaseBalls.end(), 100)
                .setReversed(false)
                .splineToLinearHeading(new Pose2d(15, 67, Math.toRadians(-55)), Math.toRadians(180))
                .build();

        moveToShootBalls =  drive.trajectorySequenceBuilder(goToReleaseBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeFifthBalls = drive.trajectorySequenceBuilder(shootPose, 20)
                .setReversed(true)
                .splineTo(new Vector2d(-12, 55), Math.toRadians(90))
                .build();

        moveToShootFifth = drive.trajectorySequenceBuilder(goToTakeFifthBalls.end(), 20)
                .setReversed(false)
                .splineTo(shootPos, shootAngle)
                .build();

        park = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineTo(new Vector2d(-50, 15))
                .build();
    }

    public TrajectorySequence moveToShootFirst() {
        return moveToShootFirst;
    }

    public TrajectorySequence goToTakeSecondBalls() {
        return goToTakeSecondBalls;
    }

    public TrajectorySequence moveToShootSecond() {
        return moveToShootSecond;
    }

    public TrajectorySequence goToReleaseBalls() {
        return goToReleaseBalls;
    }

    public TrajectorySequence goToTakeBalls() {
        return goToTakeBalls;
    }

    public TrajectorySequence moveToShootBalls() {
        return moveToShootBalls;
    }

    public TrajectorySequence goToTakeFifthBalls() {
        return goToTakeFifthBalls;
    }

    public TrajectorySequence moveToShootFifth() {
        return moveToShootFifth;
    }

    public TrajectorySequence park() {
        return park;
    }

    public Pose2d getStartPose() {
        return startPose;
    }
}