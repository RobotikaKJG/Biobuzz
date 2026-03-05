package org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class BlueGoalSoloTrajectories implements GoalSoloTrajectories {

    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeSecondBalls;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToTakeThirdBalls;
    TrajectorySequence moveToShootThird;
    TrajectorySequence goToTakeFourthBalls;
    TrajectorySequence moveToShootFourth;
    TrajectorySequence goToTakeFifthBalls;
    TrajectorySequence moveToShootFifth;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(-65, -35, Math.toRadians(-90));
    private final Vector2d shootPos = new Vector2d(-25, -15);
    private final double shootAngle = Math.toRadians(180);
    private final Pose2d shootPose = new Pose2d(shootPos, shootAngle);

    public BlueGoalSoloTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 100)
                .setReversed(false)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(shootPose, 100)
                .setReversed(true)
                .lineTo(new Vector2d(-5, -15))
                .splineTo(new Vector2d(12, -55), Math.toRadians(-90))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeThirdBalls = drive.trajectorySequenceBuilder(moveToShootSecond.end(), 100)
                .setReversed(true)
                .splineTo(new Vector2d(-12, -40), Math.toRadians(-90))
                .build();

        moveToShootThird = drive.trajectorySequenceBuilder(goToTakeThirdBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeFourthBalls = drive.trajectorySequenceBuilder(moveToShootThird.end(), 100)
                .setReversed(true)
                .lineTo(new Vector2d(20, -15))
                .splineTo(new Vector2d(36, -55), Math.toRadians(-90))
                .build();

        goToTakeFifthBalls = drive.trajectorySequenceBuilder(shootPose, 20)
                .setReversed(true)
                .splineTo(new Vector2d(-12, -55), Math.toRadians(-90))
                .build();

        moveToShootFifth = drive.trajectorySequenceBuilder(goToTakeFifthBalls.end(), 20)
                .setReversed(false)
                .splineTo(shootPos, shootAngle)
                .build();

        park = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineTo(new Vector2d(-50, -15))
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

    public TrajectorySequence goToTakeThirdBalls() {
        return goToTakeThirdBalls;
    }

    public TrajectorySequence moveToShootThird() {
        return moveToShootThird;
    }

    public TrajectorySequence goToTakeFourthBalls() {
        return goToTakeFourthBalls;
    }

    public TrajectorySequence moveToShootFourth() {
        return moveToShootFourth;
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