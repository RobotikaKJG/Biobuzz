package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(13.4, 17.6)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(70, 50, 50, Math.toRadians(180), 11)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(64, 10, Math.toRadians(90)))
                        .lineToLinearHeading(new Pose2d(63, 10, Math.toRadians(90)))
                        .waitSeconds(0.1)
                        .lineToSplineHeading(new Pose2d(46, 15, Math.toRadians(-90)))
                        .splineToSplineHeading(new Pose2d(36, 25, Math.toRadians(-90)), Math.toRadians(90))
                        .splineToSplineHeading(new Pose2d(36, 63, Math.toRadians(-90)), Math.toRadians(90))
                        .waitSeconds(0.1)
                        .lineToLinearHeading(new Pose2d(63, 10, Math.toRadians(90)))
                        .waitSeconds(0.1)
                        .splineToSplineHeading(new Pose2d(46, 64, Math.toRadians(180)), Math.toRadians(0))
                        .lineToSplineHeading(new Pose2d(60, 64, Math.toRadians(180)))
                        .lineToLinearHeading(new Pose2d(63, 10, Math.toRadians(90)))


//                        .waitSeconds(0.3)
//
//                        .setReversed(true)
//                        .lineTo(new Vector2d(0, 20))
//                        .splineTo(new Vector2d(12, 25), Math.toRadians(90))
//                        .splineTo(new Vector2d(12, 65), Math.toRadians(90))
//                        .waitSeconds(0.3)
//
//                        //move to shoot
//                        .setReversed(false)
//                        .splineTo(new Vector2d(-25, 20), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        // release + take path
//
//                        .setReversed(false)
////                        .splineToSplineHeading(new Pose2d(0, 25, Math.toRadians(90)), Math.toRadians(0))
//                        .lineToSplineHeading(new Pose2d(0, 25, Math.toRadians(-90)))
//                        .splineToSplineHeading(new Pose2d(7, 55, Math.toRadians(-30)), Math.toRadians(10))
//                        .splineToSplineHeading(new Pose2d(20, 65, Math.toRadians(-15)), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        .setReversed(false)
//                        .splineTo(new Vector2d(-25, 20), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        // release + take path
//
//                        .setReversed(true)
//                        .splineTo(new Vector2d(5, 55), Math.toRadians(0))
//                        .splineToLinearHeading(new Pose2d(15, 65, Math.toRadians(-55)), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        .setReversed(false)
//                        .splineTo(new Vector2d(-25, 20), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        // go to take
//                        .setReversed(true)
//                        .splineTo(new Vector2d(-12, 45), Math.toRadians(90))
//                        .waitSeconds(0.3)
//
//                        //move to shoot
//                        .setReversed(false)
//                        .splineTo(new Vector2d(-25, 20), Math.toRadians(180))
//                        .waitSeconds(0.3)
//
//                        .splineTo(new Vector2d(-50, -15), Math.toRadians(180))
//                        .waitSeconds(0.3)


//                        .splineTo(new Vector2d(0, 0), Math.toRadians(-45))

//                        .lineToLinearHeading(new Pose2d(36,-55,Math.toRadians(90)))
//                        .splineToLinearHeading(new Pose2d(36, -63, Math.toRadians(90)), Math.toRadians(-90))

                        .build());


        try {
            meepMeep.setBackground(ImageIO.read(new File("C:/Users/BenGr/StudioProjects/DecodeSDK/FtcRobotController-10.0/MeepMeepTesting/src/main/resources/field-decode-dark.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        meepMeep.setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}