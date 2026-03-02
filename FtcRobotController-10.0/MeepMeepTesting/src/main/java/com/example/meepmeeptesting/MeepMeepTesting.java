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
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(-65, -35, Math.toRadians(-90)))
                        .lineToLinearHeading(new Pose2d(-25, -15, Math.toRadians(180)))
                        .waitSeconds(0.3)

                        .setReversed(true)
                        .lineTo(new Vector2d(-5, -15))
                        .splineTo(new Vector2d(12, -55), Math.toRadians(-90))
                        .waitSeconds(0.3)

                        //release

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(6, -55), Math.toRadians(-90))
                        .waitSeconds(0.3)

                        //move to shoot
                        .splineTo(new Vector2d(-25, -15), Math.toRadians(180))
                        .waitSeconds(0.3)

                        // release + take path

                        .setReversed(true)
                        .splineTo(new Vector2d(6, -55), Math.toRadians(-90))
                        .waitSeconds(0.3)

                        .setReversed(false)
                        .splineToLinearHeading(new Pose2d(20, -60, Math.toRadians(45)), Math.toRadians(180))
                        .waitSeconds(0.3)

                        .splineTo(new Vector2d(-25, -15), Math.toRadians(180))
                        .waitSeconds(0.3)

                        // release + take path

                        .setReversed(true)
                        .splineTo(new Vector2d(6, -55), Math.toRadians(-90))
                        .waitSeconds(0.3)

                        .setReversed(false)
                        .splineToLinearHeading(new Pose2d(20, -60, Math.toRadians(45)), Math.toRadians(180))
                        .waitSeconds(0.3)

                        .splineTo(new Vector2d(-25, -15), Math.toRadians(180))
                        .waitSeconds(0.3)

                        // go to take
                        .setReversed(true)
                        .splineTo(new Vector2d(-12, -55), Math.toRadians(-90))
                        .waitSeconds(0.3)

                        //move to shoot
                        .setReversed(false)
                        .splineTo(new Vector2d(-25, -15), Math.toRadians(180))
                        .waitSeconds(0.3)

                        .splineTo(new Vector2d(-50, -15), Math.toRadians(180))
                        .waitSeconds(0.3)


//                        .splineTo(new Vector2d(0, 0), Math.toRadians(-45))

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
