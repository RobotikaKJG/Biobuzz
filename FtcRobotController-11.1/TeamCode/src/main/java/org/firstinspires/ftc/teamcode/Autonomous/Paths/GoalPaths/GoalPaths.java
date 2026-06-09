package org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalPaths;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public interface GoalPaths {

    Pose getStartPose();
    PathChain drive_startPos_shootPos();
}