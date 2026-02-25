package org.firstinspires.ftc.teamcode.Autonomous.Autos;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueGoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedGoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton {
    private final SampleMecanumDrive drive;
    private GoalTrajectories trajectories;
    private ServoControl servoControl;
    private GoalAutonState goalAutonState = GoalAutonState.moveToShootFirst;
    private double currentWait = 0;

    public GoalAuton(SampleMecanumDrive drive) {
        this.drive = drive;
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
        goalAutonState = GoalAutonState.moveToShootFirst;
        addWaitTime(1);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedGoalTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueGoalTrajectories(drive);
                break;
        }
    }

    @Override
    public void run() {
        switch (goalAutonState)
        {
            case moveToShootFirst:
                moveToShootFirst();
                break;
            case shootBallsFirst:
                shootFirstFirst();
                break;
            case goToTakeSecondBalls:
                goToTakeSecondBalls();
                break;
            case takeSecondBalls:
                takeSecondBalls();
                break;
            case goToRelease:
                goToRelease();
                break;
            case moveToShootSecond:
                moveToShootSecond();
                break;
            case shootBallsSecond:
                shootSecondFirst();
                break;
            case goToTakeThirdBalls:
                goToTakeThirdBalls();
                break;
            case takeThirdBalls:
                takeThirdBalls();
                break;
            case moveToShootThird:
                moveToShootThird();
                break;
            case shootBallsThird:
                shootThirdFirst();
                break;
            case goToTakeFourthBalls:
                goToTakeFourthBalls();
                break;
            case takeFourthBalls:
                takeFourthBalls();
                break;
            case moveToShootFourth:
                moveToShootFourth();
                break;
            case shootBallsFourth:
                shootFourthFirst();
                break;
            case stop:
                stop();
                break;
            case idle:
                break;
        }
    }

//AUTONOTE FILL IN THE LOGIC

    private void moveToShootFirst() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting+
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsFirst;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootFirstFirst() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeSecondBalls());
        goalAutonState = GoalAutonState.goToTakeSecondBalls;
    }

    private void goToTakeSecondBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.takeSecondBalls());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonState = GoalAutonState.takeSecondBalls;
    }

    private void takeSecondBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.goToRelease());
        goalAutonState = GoalAutonState.goToRelease;
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        addWaitTime(3);
    }

    private void goToRelease() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootSecond());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
        goalAutonState = GoalAutonState.moveToShootSecond;
        addWaitTime(1);

    }

    private void moveToShootSecond() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting+
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsSecond;
        addWaitTime(AutonomousConstants.shootTime);

    }

    private void shootSecondFirst() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeThirdBalls());
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        goalAutonState = GoalAutonState.goToTakeThirdBalls;
    }

    private void goToTakeThirdBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.takeThirdBalls());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonState = GoalAutonState.takeThirdBalls;
    }

    private void takeThirdBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootThird());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonState = GoalAutonState.moveToShootThird;
        addWaitTime(1);
    }

    private void moveToShootThird() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsThird;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootThirdFirst() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeFourthBalls());
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        goalAutonState = GoalAutonState.goToTakeFourthBalls;
    }

    private void goToTakeFourthBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.takeFourthBalls());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonState = GoalAutonState.takeFourthBalls;
    }

    private void takeFourthBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFourth());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonState = GoalAutonState.moveToShootFourth;
        addWaitTime(1);
    }

    private void moveToShootFourth() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsFourth;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootFourthFirst() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.park());
        goalAutonState = GoalAutonState.stop;
    }

    private void stop() {
        if(drive.isBusy()) return;
        goalAutonState = GoalAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}