package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.DriveConstants;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedGoalTrajectories implements GoalTrajectories {

    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeSecondBalls;
    TrajectorySequence takeSecondBalls;
    TrajectorySequence goToRelease;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToTakeThirdBalls;
    TrajectorySequence takeThirdBalls;
    TrajectorySequence moveToShootThird;
    TrajectorySequence goToTakeFourthBalls;
    TrajectorySequence takeFourthBalls;
    TrajectorySequence moveToShootFourth;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(60, -55,Math.toRadians(306));
    private final Pose2d shootPose = new Pose2d(23, -12, Math.toRadians(315));

    public RedGoalTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose, 100)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(moveToShootFirst.end(), 90)
                .lineToLinearHeading(new Pose2d(25, -0, Math.toRadians(270)))
                .build();

        takeSecondBalls = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end(), 18)
                .lineTo(new Vector2d(25, -60))
                .build();

        goToRelease = drive.trajectorySequenceBuilder(takeSecondBalls.end(), 100)
                .lineTo(new Vector2d(16, -40))
                .lineTo(new Vector2d(16, -65))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(goToRelease.end(), 100)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeThirdBalls = drive.trajectorySequenceBuilder(moveToShootSecond.end(), 90)
                .lineToLinearHeading(new Pose2d(0, -10, Math.toRadians(270)))
                .build();

        takeThirdBalls = drive.trajectorySequenceBuilder(goToTakeThirdBalls.end(), 18)
                .lineTo(new Vector2d(0, -60))
                .build();

        moveToShootThird = drive.trajectorySequenceBuilder(takeThirdBalls.end(), 100)
                .lineToLinearHeading(shootPose)
                .build();

        goToTakeFourthBalls = drive.trajectorySequenceBuilder(moveToShootThird.end(), 90)
                .lineToLinearHeading(new Pose2d(-23, -10, Math.toRadians(270)))
                .build();

        takeFourthBalls = drive.trajectorySequenceBuilder(goToTakeFourthBalls.end(), 18)
                .lineTo(new Vector2d(-23, -60))
                .build();

        moveToShootFourth = drive.trajectorySequenceBuilder(takeFourthBalls.end(), 100)
                .lineToLinearHeading(shootPose)
                .build();

        park = drive.trajectorySequenceBuilder(moveToShootFourth.end(), 100)
                .lineTo(new Vector2d(10, -20))
                .build();
    }

    public TrajectorySequence moveToShootFirst() {
        return moveToShootFirst;
    }

    public TrajectorySequence goToTakeSecondBalls() {
        return goToTakeSecondBalls;
    }

    public TrajectorySequence takeSecondBalls() {
        return takeSecondBalls;
    }

    public TrajectorySequence goToRelease() {
        return goToRelease;
    }

    public TrajectorySequence getGoToRelease() {
        return goToRelease;
    }

    public TrajectorySequence moveToShootSecond() {
        return moveToShootSecond;
    }

    public TrajectorySequence goToTakeThirdBalls() {
        return goToTakeThirdBalls;
    }

    public TrajectorySequence takeThirdBalls() {
        return takeThirdBalls;
    }

    public TrajectorySequence moveToShootThird() {
        return moveToShootThird;
    }

    public TrajectorySequence goToTakeFourthBalls() {
        return goToTakeFourthBalls;
    }

    public TrajectorySequence takeFourthBalls() {
        return takeFourthBalls;
    }

    public TrajectorySequence moveToShootFourth() {
        return moveToShootFourth;
    }

    public TrajectorySequence park() {
        return park;
    }

    public Pose2d getStartPose() {
        return startPose;
    }

}