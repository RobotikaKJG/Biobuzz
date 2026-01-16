package org.firstinspires.ftc.teamcode.Autonomous.Autos;

public enum GoalAutonState {
    moveToShootFirst,
    shootBallsFirst,

    goToTakeSecondBalls,
    takeSecondBalls,
    goToRelease,
    moveToShootSecond,
    shootBallsSecond,

    goToTakeThirdBalls,
    takeThirdBalls,
    moveToShootThird,
    shootBallsThird,

    goToTakeFourthBalls,
    takeFourthBalls,
    moveToShootFourth,
    shootBallsFourth,

    stop,
    idle
}