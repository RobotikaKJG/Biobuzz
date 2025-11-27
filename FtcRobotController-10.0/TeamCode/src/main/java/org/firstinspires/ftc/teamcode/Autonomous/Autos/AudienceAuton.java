package org.firstinspires.ftc.teamcode.Autonomous.Autos;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueAudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedAudienceTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AudienceAuton implements Auton {
    private final SampleMecanumDrive drive;
    private AudienceTrajectories trajectories;
    private ServoControl servoControl;
    private AudienceAutonState audienceAutonState = AudienceAutonState.moveToShootFirst;
    private double currentWait = 0;

    public AudienceAuton(SampleMecanumDrive drive) {
        this.drive = drive;
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        audienceAutonState = AudienceAutonState.moveToShootFirst;
        addWaitTime(AutonomousConstants.shooterToMaxSpeed);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                trajectories = new RedAudienceTrajectories(drive);
                break;
            case Blue:
                trajectories = new BlueAudienceTrajectories(drive);
                break;
        }
    }

    @Override
    public void run() {
        switch (audienceAutonState)
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
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFirstFirst;
    }

    private void shootFirstFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFirstSecond;
    }

    private void shootFirstSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFirstThird;
    }

    private void shootFirstThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        audienceAutonState = AudienceAutonState.goToTakeSecondBalls;
        IntakeStates.setMotorState(IntakeMotorStates.idle);
    }

    private void goToTakeSecondBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        audienceAutonState = AudienceAutonState.moveToShootSecond;
    }

    private void moveToShootSecond() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootSecondFirst;

    }

    private void shootSecondFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootSecondSecond;
    }

    private void shootSecondSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootSecondThird;
    }

    private void shootSecondThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.goToTakeThirdBalls;
    }

    private void goToTakeThirdBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        audienceAutonState = AudienceAutonState.moveToShootThird;
    }

    private void moveToShootThird() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootThirdFirst;
    }

    private void shootThirdFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootThirdSecond;
    }

    private void shootThirdSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootThirdThird;
    }

    private void shootThirdThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        OuttakeStates.setMotorState(OuttakeMotorStates.idle);
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.goToTakeFourthBalls;
    }

    private void goToTakeFourthBalls() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFull);
        audienceAutonState = AudienceAutonState.moveToShootFourth;
    }

    private void moveToShootFourth() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFourthFirst;
    }

    private void shootFourthFirst() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFourthSecond;
    }

    private void shootFourthSecond() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFourthThird;
    }

    private void shootFourthThird() {
        if(OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle) return;
        drive.followTrajectorySequenceAsync(trajectories.park());
        audienceAutonState = AudienceAutonState.stop;
    }

    private void stop() {
        if(drive.isBusy()) return;
        audienceAutonState = AudienceAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}
