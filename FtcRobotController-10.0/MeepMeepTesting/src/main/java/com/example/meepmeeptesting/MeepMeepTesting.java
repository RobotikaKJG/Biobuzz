package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

/**
 * Desktop-only trajectory sandbox. Does not run on the robot or share mutable robot state.
 * Add new path geometry here, then implement its robot equivalent in Autonomous/Trajectories.
 * Distances are inches and angles are radians; constraints below are preview placeholders.
 */
public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep preview = new MeepMeep(800);
        RoadRunnerBotEntity robot = new DefaultBotBuilder(preview)
                .setConstraints(30, 30, Math.toRadians(90), Math.toRadians(90), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d())
                        // A wait is a valid sequence with no inherited competition path.
                        .waitSeconds(1)
                        .build());
        // Select/import the new field background when preparing the next season's paths.
        preview.addEntity(robot).start();
    }
}
