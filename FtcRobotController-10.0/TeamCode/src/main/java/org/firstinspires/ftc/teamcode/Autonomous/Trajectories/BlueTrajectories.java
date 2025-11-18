package org.firstinspires.ftc.teamcode.Autonomous.Trajectories;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.trajectorysequence.TrajectorySequence;

public class BlueTrajectories implements Trajectories{

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

    private final Pose2d startPose = new Pose2d(-45, 45,Math.toRadians(126));

    public BlueTrajectories(SampleMecanumDrive drive) {
        this.drive = drive;
        fillVariables();
    }

    private void fillVariables() {
        moveToShootFirst = drive.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(-15,15))
                .build();

        goToTakeSecondBalls = drive.trajectorySequenceBuilder(moveToShootFirst.end())
                .lineToLinearHeading(new Pose2d(-10, 11, Math.toRadians(90)))
                .build();

        takeSecondBalls = drive.trajectorySequenceBuilder(goToTakeSecondBalls.end())
                .lineTo(new Vector2d(-50, 11))
                .build();

        moveToShootSecond = drive.trajectorySequenceBuilder(takeSecondBalls.end())
                .lineToLinearHeading(new Pose2d(-15, 15, Math.toRadians(54)))
                .build();

        goToTakeThirdBalls = drive.trajectorySequenceBuilder(moveToShootSecond.end())
                .lineToLinearHeading(new Pose2d(-10, -10, Math.toRadians(90)))
                .build();

        takeThirdBalls = drive.trajectorySequenceBuilder(goToTakeThirdBalls.end())
                .lineTo(new Vector2d(-50, -10))
                .build();

        moveToShootThird = drive.trajectorySequenceBuilder(takeThirdBalls.end())
                .lineToLinearHeading(new Pose2d(-15, 15, Math.toRadians(54)))
                .build();

        goToTakeFourthBalls = drive.trajectorySequenceBuilder(moveToShootThird.end())
                .lineToLinearHeading(new Pose2d(-10, -27, Math.toRadians(90)))
                .build();

        takeFourthBalls = drive.trajectorySequenceBuilder(goToTakeFourthBalls.end())
                .lineTo(new Vector2d(-50, -27))
                .build();

        moveToShootFourth = drive.trajectorySequenceBuilder(takeFourthBalls.end())
                .lineToLinearHeading(new Pose2d(-15, 15, Math.toRadians(54)))
                .build();

        park = drive.trajectorySequenceBuilder(moveToShootFourth.end())
                .lineTo(new Vector2d(-10, 44))
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