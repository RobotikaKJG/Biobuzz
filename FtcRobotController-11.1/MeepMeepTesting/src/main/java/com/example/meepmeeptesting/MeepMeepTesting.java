package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(17.6, 17.6)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(70, 50, 50, Math.toRadians(180), 11)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(-60, 38, Math.toRadians(90)))
                        .lineToLinearHeading(new Pose2d(-10, 20, Math.toRadians(0)))
                        .splineToSplineHeading()
                        .splineToSplineHeading(new Pose2d(10, 60, Math.toRadians(90)), Math.toRadians(180)))
                        .build());


        try {
            URL fieldImage = MeepMeepTesting.class.getResource("/field-decode-dark.png");
            if (fieldImage == null) {
                throw new IOException("Missing resource: field-decode-dark.png");
            }
            meepMeep.setBackground(ImageIO.read(fieldImage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        meepMeep.setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
