package org.firstinspires.ftc.teamcode.Autonomous.Autos;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.AudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueAudienceTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedAudienceTrajectories;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.BallDetectionPipeline;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.FeederMotor.FeederMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class AudienceAuton implements Auton {
    private final SampleMecanumDrive drive;
    private AudienceTrajectories trajectories;
    private ServoControl servoControl;
    private SensorControl sensorControl;
    private AudienceAutonState audienceAutonState = AudienceAutonState.moveToShootFirst;
    private double currentWait = 0;
    private BallDetectionPipeline pipeline;
    private OpenCvCamera webcam;
    private boolean wasIfCalled = false;

    private static final double CENTERING_THRESHOLD_PX = 10;
    private static final double STRAFE_KP = 0.002;
    private static final double SEARCH_STRAFE_POWER = 0.2;
    private static final double FORWARD_POWER = 0.4;
    private static final double TARGET_Y = -60;
    private static final double STRAFE_STEP = 3; // inches
    private static final double FORWARD_STEP = 5; // inches

    public AudienceAuton(SampleMecanumDrive drive, SensorControl sensorControl) {
        this.drive = drive;
        this.sensorControl = sensorControl;
    }

    @Override
    public void start() {
        setTrajectorySide();
        drive.setPoseEstimate(trajectories.getStartPose());
        drive.followTrajectorySequenceAsync(trajectories.moveToShootFirst());
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
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
            case shootFirst:
                shootFirst();
                break;
            case goToTakeFirst:
                goToTakeFirst();
                break;
            case takeFirst:
                takeFirst();
                break;
            case moveToShootSecond:
                moveToShootSecond();
                break;
            case shootSecond:
                shootSecond();
                break;
            case goToTake:
                goToTake();
                break;
            case searchForBall:
                searchForBall();
                break;
            case centerOnBall:
                centerOnBall();
                break;
            case driveToBall:
                driveToBall();
                break;
            case moveToShoot:
                moveToShoot();
                break;
            case shoot:
                shoot();
                break;
            case idle:
                break;
        }
    }

    private void moveToShootFirst() {
        if(drive.isBusy() || getSeconds() < currentWait) return;
        //activate shooting
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootFirst;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shootFirst() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeFirst());
        audienceAutonState = AudienceAutonState.goToTakeFirst;
    }

    private void goToTakeFirst() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.takeFirst());
        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.takeFirst;
    }

    private void takeFirst() {
        if(drive.isBusy()) return;
        drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
        IntakeStates.setMotorState(IntakeMotorStates.idle);
        OuttakeStates.setFeederMotorState(FeederMotorStates.idle);
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
        audienceAutonState = AudienceAutonState.moveToShootSecond;
    }


    private void moveToShootSecond() {
        if(drive.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shootSecond;
        addWaitTime(AutonomousConstants.shootTime);
    }


    private void shootSecond() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        audienceAutonState = AudienceAutonState.goToTake;
    }

    private void goToTake() {
        if (drive.isBusy()) return;

        IntakeStates.setMotorState(IntakeMotorStates.forward);
        audienceAutonState = AudienceAutonState.searchForBall;
    }

    private void searchForBall() {
        double offsetPx = sensorControl.getBallOffsetPx();

        if (!Double.isNaN(offsetPx)) {
            audienceAutonState = AudienceAutonState.centerOnBall;
            return;
        }

        Pose2d pose = drive.getPoseEstimate();
        Pose2d target = new Pose2d(
                pose.getX() - STRAFE_STEP,
                pose.getY(),
                pose.getHeading()
        );

        drive.followTrajectorySequenceAsync(
                drive.trajectorySequenceBuilder(pose, 50)
                        .lineToLinearHeading(target)
                        .build()
        );
    }

    private void centerOnBall() {
//        if (drive.isBusy()) return;

        double offsetPx = sensorControl.getBallOffsetPx();
        if (Double.isNaN(offsetPx)) {
            audienceAutonState = AudienceAutonState.searchForBall;
            return;
        }

        if (Math.abs(offsetPx) <= CENTERING_THRESHOLD_PX) {
            audienceAutonState = AudienceAutonState.driveToBall;
            return;
        }

        Pose2d pose = drive.getPoseEstimate();
        double correctionInches = offsetPx * 0.02; // tuning factor

        Pose2d target = new Pose2d(
                pose.getX() - correctionInches,
                pose.getY(),
                pose.getHeading()
        );

        drive.followTrajectorySequenceAsync(
                drive.trajectorySequenceBuilder(pose, 50)
                        .lineToLinearHeading(target)
                        .build()
        );
    }

    private void driveToBall() {
//        if (drive.isBusy()) return;

        Pose2d pose = drive.getPoseEstimate();

        Pose2d target = new Pose2d(
                pose.getX(),
                TARGET_Y,
                pose.getHeading()
        );

        drive.followTrajectorySequenceAsync(
                drive.trajectorySequenceBuilder(pose, 20)
                        .lineToLinearHeading(target)
                        .build()
        );

        audienceAutonState = AudienceAutonState.moveToShoot;
    }

    private void moveToShoot() {
        if (!wasIfCalled) {
            IntakeStates.setMotorState(IntakeMotorStates.idle);
            OuttakeStates.setFeederMotorState(FeederMotorStates.idle);
            OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
            drive.followTrajectorySequenceAsync(trajectories.moveToShoot());
            wasIfCalled = true;
        }
        if (drive.isBusy()) return;
        wasIfCalled = false;

        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        audienceAutonState = AudienceAutonState.shoot;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot() {
        if (getSeconds() < currentWait) return;

        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.stopTransfer);
        drive.followTrajectorySequenceAsync(trajectories.goToTakeBalls());
        audienceAutonState = AudienceAutonState.goToTake;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}
