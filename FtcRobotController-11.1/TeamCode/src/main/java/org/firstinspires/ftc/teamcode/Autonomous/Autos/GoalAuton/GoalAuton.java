package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.BlueGoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.RedGoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.GoalTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton {
    private final SampleMecanumDrive drive;
    private GoalTrajectories trajectories;
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
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        goalAutonState = GoalAutonState.moveToShootFirst;
        addWaitTime(3.5);
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
                shootBallsFirst();
                break;
            case goToTakeSecondBalls:
                goToTakeSecondBalls();
                break;
            case moveToShootSecond:
                moveToShootSecond();
                break;
            case shootBallsSecond:
                shootBallsSecond();
                break;
            case goToReleaseSecond:
                goToReleaseSecond();
                break;
            case moveToShootThird:
                moveToShootThird();
                break;
            case shootBallsThird:
                shootBallsThird();
                break;
            case goToReleaseThird:
                goToReleaseThird();
                break;
            case moveToShootFourth:
                moveToShootFourth();
                break;
            case shootBallsFourth:
                shootBallsFourth();
                break;
            case goToTakeFifthBalls:
                goToTakeFifthBalls();
                break;
            case moveToShootFifth:
                moveToShootFifth();
                break;
            case shootBallsFifth:
                shootBallsFifth();
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
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsFirst;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsFirst() {
        if(getSeconds() < currentWait) return;
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeSecondBalls());
        goalAutonState = GoalAutonState.goToTakeSecondBalls;
    }

    private void goToTakeSecondBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootSecond());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonState = GoalAutonState.moveToShootSecond;
        addWaitTime(1.5);
    }

    private void moveToShootSecond() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting+
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsSecond;
        addWaitTime(AutonomousConstants.shootTime);

    }

    private void shootBallsSecond() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToReleaseBalls());
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonState = GoalAutonState.goToReleaseSecond;
        addWaitTime(5);
    }

    private void goToReleaseSecond() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootBalls());
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

    private void shootBallsThird() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToReleaseBalls());
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonState = GoalAutonState.goToReleaseThird;
        addWaitTime(4);
    }

    private void goToReleaseThird() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootBalls());
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

    private void shootBallsFourth() {
        if(getSeconds() < currentWait) return;
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeFifthBalls());
        goalAutonState = GoalAutonState.goToTakeFifthBalls;
        addWaitTime(1);
    }

    private void goToTakeFifthBalls() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFifth());
        goalAutonState = GoalAutonState.moveToShootFifth;
        addWaitTime(1);
    }

    private void moveToShootFifth() {
        if (drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootBallsFifth;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsFifth() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
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