package org.firstinspires.ftc.teamcode.Autonomous.Autos;

public enum GoalAutonState {
    moveToShootFirst,
    shootBallsFirst,

    goToTakeSecondBalls,
    moveToShootSecond,
    shootBallsSecond,

    goToReleaseSecond,
    goToTakeThirdBalls,
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