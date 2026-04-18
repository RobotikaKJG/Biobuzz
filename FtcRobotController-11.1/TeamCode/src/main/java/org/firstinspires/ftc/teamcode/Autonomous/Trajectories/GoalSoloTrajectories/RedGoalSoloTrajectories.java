package org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedGoalSoloTrajectories implements GoalSoloTrajectories {

    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeSecondBalls;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToTakeThirdBalls;
    TrajectorySequence goToTakeThirdBallsRelease;
    TrajectorySequence moveToShootThird;
    TrajectorySequence goToTakeFourthBalls;
    TrajectorySequence moveToShootFourth;
    TrajectorySequence goToTakeFifthBalls;
    TrajectorySequence moveToShootFifth;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(-65, 40, Math.toRadians(90));
    private final Vector2d shootPos = new Vector2d(-25, 20);
    private final double shootAngle = Math.toRadians(180);
    private final Pose2d shootPose = new Pose2d(shootPos, shootAngle);

    public RedGoalSoloTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 100)
                .setReversed(false)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(shootPose, 60)
                .setReversed(true)
                .lineTo(new Vector2d(-10, 20))
                .splineTo(new Vector2d(10, 25), Math.toRadians(90))
                .splineTo(new Vector2d(10, 63), Math.toRadians(90))
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(5, 50), Math.toRadians(180))
                .splineToConstantHeading(new Vector2d(0, 55), Math.toRadians(90))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeThirdBalls = drive.trajectorySequenceBuilder(moveToShootSecond.end(), 70)
                .setReversed(true)
//                .lineTo(new Vector2d(-7, 20))
                .splineTo(new Vector2d(-15, 20), Math.toRadians(90))
                .splineTo(new Vector2d(-15, 54), Math.toRadians(90))
                .build();

        goToTakeThirdBallsRelease = drive.trajectorySequenceBuilder(moveToShootSecond.end(), 70)
                .setReversed(true)
                .splineTo(new Vector2d(-15, 20), Math.toRadians(90))
                .splineTo(new Vector2d(-15, 54), Math.toRadians(90))
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-5, 50), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(0, 55), Math.toRadians(90))
                .build();

        moveToShootThird = drive.trajectorySequenceBuilder(goToTakeThirdBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeFourthBalls = drive.trajectorySequenceBuilder(moveToShootThird.end(), 70)
                .setReversed(true)
                .lineTo(new Vector2d(20, 20))
                .splineTo(new Vector2d(31, 25), Math.toRadians(90))
                .splineTo(new Vector2d(31, 63), Math.toRadians(90))
                .build();

        moveToShootFourth = drive.trajectorySequenceBuilder(goToTakeFourthBalls.end(), 100)
                .splineTo(shootPos, shootAngle)
                .build();

        goToTakeFifthBalls = drive.trajectorySequenceBuilder(shootPose, 20)
                .setReversed(true)
                .splineTo(new Vector2d(60, 60), Math.toRadians(180))
                .build();

        moveToShootFifth = drive.trajectorySequenceBuilder(goToTakeFifthBalls.end(), 20)
                .setReversed(false)
                .splineTo(shootPos, shootAngle)
                .build();

        park = drive.trajectorySequenceBuilder(shootPose, 100)
                .lineTo(new Vector2d(60, 40))
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

    public TrajectorySequence goToTakeThirdBallsRelease() {
        return goToTakeThirdBallsRelease;
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