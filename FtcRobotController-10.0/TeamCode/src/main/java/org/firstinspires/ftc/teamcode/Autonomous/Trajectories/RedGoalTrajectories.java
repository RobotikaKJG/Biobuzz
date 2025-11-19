package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class RedGoalTrajectories implements GoalTrajectories {

    SampleMecanumDrive drive;
    TrajectorySequence moveToShootFirst;
    TrajectorySequence goToTakeSecondBalls;
    TrajectorySequence takeSecondBalls;
    TrajectorySequence moveToShootSecond;
    TrajectorySequence goToTakeThirdBalls;
    TrajectorySequence takeThirdBalls;
    TrajectorySequence moveToShootThird;
    TrajectorySequence goToTakeFourthBalls;
    TrajectorySequence takeFourthBalls;
    TrajectorySequence moveToShootFourth;
    TrajectorySequence park;

    private final Pose2d startPose = new Pose2d(60, -55,Math.toRadians(306));
    private final Vector2d shootPose = new Vector2d(30, -25);

    public RedGoalTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose)
                .lineTo(shootPose)
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(moveToShootFirst.end())
                .lineToLinearHeading(new Pose2d(17, -20, Math.toRadians(270)))
                .build();

        takeSecondBalls = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end())
                .lineTo(new Vector2d(17, -50))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(takeSecondBalls.end())
                .lineToLinearHeading(new Pose2d(shootPose, Math.toRadians(306)))
                .build();

        goToTakeThirdBalls = drive.trajectorySequenceBuilder(moveToShootSecond.end())
                .lineToLinearHeading(new Pose2d(-8, -20, Math.toRadians(270)))
                .build();

        takeThirdBalls = drive.trajectorySequenceBuilder(goToTakeThirdBalls.end())
                .lineTo(new Vector2d(-8, -50))
                .build();

        moveToShootThird = drive.trajectorySequenceBuilder(takeThirdBalls.end())
                .lineToLinearHeading(new Pose2d(shootPose, Math.toRadians(306)))
                .build();

        goToTakeFourthBalls = drive.trajectorySequenceBuilder(moveToShootThird.end())
                .lineToLinearHeading(new Pose2d(-32, -20, Math.toRadians(270)))
                .build();

        takeFourthBalls = drive.trajectorySequenceBuilder(goToTakeFourthBalls.end())
                .lineTo(new Vector2d(-32, -50))
                .build();

        moveToShootFourth = drive.trajectorySequenceBuilder(takeFourthBalls.end())
                .lineToLinearHeading(new Pose2d(shootPose, Math.toRadians(306)))
                .build();

        park = drive.trajectorySequenceBuilder(moveToShootFourth.end())
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