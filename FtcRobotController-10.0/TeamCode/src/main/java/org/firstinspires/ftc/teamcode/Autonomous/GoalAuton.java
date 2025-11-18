package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.Trajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;


public class GoalAuton implements Auton{
    private final SampleMecanumDrive drive;
    private Trajectories trajectories;
    private ServoControl servoControl;
    private GoalAutonState goalAutonState = GoalAutonState.moveToShootFirst;
    private double currentWait = 0;

    public GoalAuton(SampleMecanumDrive drive) {
        this.drive = drive;

        trajectories = new RedTrajectories(drive);
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        goalAutonState = GoalAutonState.moveToShootFirst;
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueTrajectories(drive);
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
            case shootFirstFirst:
                shootFirstFirst();
                break;
            case shootFirstSecond:
                shootFirstSecond();
                break;
            case shootFirstThird:
                shootFirstThird();
                break;
            case goToTakeSecondBalls:
                goToTakeSecondBalls();
                break;
            case takeSecondBalls:
                takeSecondBalls();
                break;
            case moveToShootSecond:
                moveToShootSecond();
                break;
            case shootSecondFirst:
                shootSecondFirst();
                break;
            case shootSecondSecond:
                shootSecondSecond();
                break;
            case shootSecondThird:
                shootSecondThird();
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
            case shootThirdFirst:
                shootThirdFirst();
                break;
            case shootThirdSecond:
                shootThirdSecond();
                break;
            case shootThirdThird:
                shootThirdThird();
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
            case shootFourthFirst:
                shootFourthFirst();
                break;
            case shootFourthSecond:
                shootFourthSecond();
                break;
            case shootFourthThird:
                shootFourthThird();
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
        if(drive.isBusy()) return;
        //activate shooting+
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFirstFirst;
    }

    private void shootFirstFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFirstSecond;
    }

    private void shootFirstSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFirstThird;
    }

    private void shootFirstThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
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
        drive.followTrajectorySequenceAsync(trajectories.moveToShootSecond());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        goalAutonState = GoalAutonState.moveToShootSecond;
    }

    private void moveToShootSecond() {
        if(drive.isBusy()) return;
        //activate shooting+
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootSecondFirst;

    }

    private void shootSecondFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootSecondSecond;
    }

    private void shootSecondSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootSecondThird;
    }

    private void shootSecondThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeThirdBalls());
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
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        goalAutonState = GoalAutonState.moveToShootThird;
    }

    private void moveToShootThird() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootThirdFirst;
    }

    private void shootThirdFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootThirdSecond;
    }

    private void shootThirdSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootThirdThird;
    }

    private void shootThirdThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
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
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        goalAutonState = GoalAutonState.moveToShootFourth;
    }

    private void moveToShootFourth() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFourthFirst;
    }

    private void shootFourthFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFourthSecond;
    }

    private void shootFourthSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonState = GoalAutonState.shootFourthThird;
    }

    private void shootFourthThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
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