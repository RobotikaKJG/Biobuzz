package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;

public enum GoalAutonState {
    moveToShootFirst,
    shootBallsFirst,

    goToTakeSecondBalls,
    moveToShootSecond,
    shootBallsSecond,

    goToReleaseSecond,
    moveToShootThird,
    shootBallsThird,

    goToReleaseThird,
    goToTakeFourthBalls,
    moveToShootFourth,
    shootBallsFourth,

    goToTakeFifthBalls,
    moveToShootFifth,
    shootBallsFifth,

    stop,
    idle
}