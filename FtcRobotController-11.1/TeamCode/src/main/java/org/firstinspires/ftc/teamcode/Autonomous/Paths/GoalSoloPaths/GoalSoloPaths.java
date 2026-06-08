package org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalSoloPaths;

import com.pedropathing.paths.PathChain;

public interface GoalSoloPaths {

    PathChain startPos_shootPos();
    PathChain shootPos_takeFirstPos();
    PathChain takeFirstPos_shootPos();
    PathChain shootPos_openGatePos();
    PathChain openGatePos_takeGatePos();
    PathChain takeGatePos_shootPos();
    PathChain takeGatePos_shootPos_last();
    PathChain shootPos_takeSecondPos_shootPos();
}