package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAutonSolo;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton.GoalAutonState;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories.BlueGoalSoloTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories.GoalSoloTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalSoloTrajectories.RedGoalSoloTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.BlueGoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.GoalTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.GoalTrajectories.RedGoalTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class GoalAutonSolo implements Auton {
    private final SampleMecanumDrive drive;
    private GoalSoloTrajectories trajectories;
    private ServoControl servoControl;
    private GoalAutonSoloState goalAutonSoloState = GoalAutonSoloState.moveToShootFirst;
    private double currentWait = 0;

    public GoalAutonSolo(SampleMecanumDrive drive) {
        this.drive = drive;
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        goalAutonSoloState = GoalAutonSoloState.moveToShootFirst;
        addWaitTime(3.5);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedGoalSoloTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueGoalSoloTrajectories(drive);
                break;
        }
    }

    @Override
    public void run() {
        switch (goalAutonSoloState)
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
            case goToTakeThirdBalls:
                goToTakeThirdBalls();
                break;
            case moveToShootThird:
                moveToShootThird();
                break;
            case shootBallsThird:
                shootBallsThird();
                break;
            case goToTakeFourthBalls:
                goToTakeFourthBalls();
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
        goalAutonSoloState = GoalAutonSoloState.shootBallsFirst;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsFirst() {
        if(getSeconds() < currentWait) return;
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeSecondBalls());
        goalAutonSoloState = GoalAutonSoloState.goToTakeSecondBalls;
    }

    private void goToTakeSecondBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootSecond());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonSoloState = GoalAutonSoloState.moveToShootSecond;
        addWaitTime(1.5);
    }

    private void moveToShootSecond() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shootBallsSecond;
        addWaitTime(AutonomousConstants.shootTime);

    }

    private void shootBallsSecond() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeThirdBalls());
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonSoloState = GoalAutonSoloState.goToTakeThirdBalls;
    }

    private void goToTakeThirdBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootThird());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonSoloState = GoalAutonSoloState.moveToShootThird;
        addWaitTime(1);
    }

    private void moveToShootThird() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shootBallsThird;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsThird() {
        if(getSeconds() < currentWait) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeFourthBalls());
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        goalAutonSoloState = GoalAutonSoloState.goToTakeFourthBalls;
    }

    private void goToTakeFourthBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFourth());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        goalAutonSoloState = GoalAutonSoloState.moveToShootFourth;
        addWaitTime(1);
    }

    private void moveToShootFourth() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shootBallsFourth;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsFourth() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.park());
        goalAutonSoloState = GoalAutonSoloState.stop;
    }

    private void goToTakeFifthBalls() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFifth());
        goalAutonSoloState = GoalAutonSoloState.moveToShootFifth;
        addWaitTime(1);
    }

    private void moveToShootFifth() {
        if (drive.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shootBallsFifth;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootBallsFifth() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stop);
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.park());
        goalAutonSoloState = GoalAutonSoloState.stop;

    }

    private void stop() {
        if(drive.isBusy()) return;
        goalAutonSoloState = GoalAutonSoloState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}
