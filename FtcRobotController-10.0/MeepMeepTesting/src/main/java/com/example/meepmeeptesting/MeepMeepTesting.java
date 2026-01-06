package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(70, 50, 3, Math.toRadians(180), 11)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(-45, 45, Math.toRadians(54)))
//                        .lineToLinearHeading(new Pose2d(-3.069, -30.69, Math.toRadians(90)))
//                        .waitSeconds(0.3)
//                        .lineToLinearHeading(new Pose2d(30, -40, Math.toRadians(41)))
//                        .waitSeconds(0.6)
//                        .turn(Math.toRadians(-90))
//                        .waitSeconds(0.2)
//                        .lineToLinearHeading(new Pose2d(40, -40, Math.toRadians(43)))
//                        .waitSeconds(0.6)
//                        .turn(Math.toRadians(-100))
//                        .waitSeconds(0.2)
//                        .lineToLinearHeading(new Pose2d(50, -40, Math.toRadians(43)))
//                        .waitSeconds(0.6)
//                        .lineToLinearHeading(new Pose2d(50, -50, Math.toRadians(100)))
//                        .waitSeconds(0.5)
//                        .lineToLinearHeading(new Pose2d(45, -55, Math.toRadians(90)))
//                        .splineToConstantHeading(new Vector2d(45, -60), Math.toRadians(0))
//                        .splineToConstantHeading(new Vector2d(-1.5, -30.69), Math.toRadians(90))
//
//                        .lineToLinearHeading(new Pose2d(45, -55, Math.toRadians(90)))
//                        .splineToConstantHeading(new Vector2d(45, -60), Math.toRadians(90))
//                        .lineToConstantHeading(new Vector2d(0, -30.69))
//
//                        .lineToConstantHeading(new Vector2d(40, -60))
//                        .lineToConstantHeading(new Vector2d(45, -60))
//                        .splineToConstantHeading(new Vector2d(1.5, -30.69), Math.toRadians(90))
//
//                        .lineToConstantHeading(new Vector2d(40, -60))
//                        .lineToConstantHeading(new Vector2d(45, -60))
//                        .splineToConstantHeading(new Vector2d(3, -30.69), Math.toRadians(90))
                        .lineTo(new Vector2d(-15,15))
                        .turn(Math.toRadians(-45))
                        .lineTo(new Vector2d(-15, -10))

//                        .lineToLinearHeading(new Pose2d(36,-55,Math.toRadians(90)))
//                        .splineToLinearHeading(new Pose2d(36, -63, Math.toRadians(90)), Math.toRadians(-90))

                        .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTOTHEDEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
