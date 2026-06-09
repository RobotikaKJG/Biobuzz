package org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public interface AudiencePaths {

    Pose getPt_startPose();
    PathChain drive_startPos_shootPos();
}