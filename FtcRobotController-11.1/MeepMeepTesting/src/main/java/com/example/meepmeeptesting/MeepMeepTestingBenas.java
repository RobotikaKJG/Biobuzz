package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTestingBenas {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(0.5, 0.5, 6.9, Math.toRadians(180), 11)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(-5, -60, Math.toRadians(90)))
                        .lineToLinearHeading(new Pose2d(0, -28, Math.toRadians(0)))
                        .waitSeconds(0.5)
//
//                        .lineToLinearHeading(new Pose2d(-57, -49, Math.toRadians(70)))
//                        .waitSeconds(0.5)
//
//                        // third sample intake
//                        .lineToLinearHeading(new Pose2d(-57, -47, Math.toRadians(90)))
//                        .waitSeconds(0.5)
//
//                        // third sample outtake
//                        .lineToLinearHeading(new Pose2d(-57, -50, Math.toRadians(65)))
//                        .waitSeconds(0.5)
//
//                        //forth sample intake
//                        .lineToLinearHeading(new Pose2d(-57, -47, Math.toRadians(115)))
//                        .waitSeconds(0.5)
//
//                        // forth sample outtake
//                        .lineToLinearHeading(new Pose2d(-57, -50, Math.toRadians(45)))
//                        .waitSeconds(0.5)
//
////                        .lineTo(new Vector2d(-20, -16))
//                        .lineToSplineHeading(new Pose2d(-38, -8, Math.toRadians(0)))
//
//
//                        .lineToSplineHeading(new Pose2d(-55, -52, Math.toRadians(35)))


                        .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_INTOTHEDEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
