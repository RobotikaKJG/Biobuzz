package org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAutonSolo;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalSoloPaths.BlueGoalSoloPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalSoloPaths.GoalSoloPaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.GoalSoloPaths.RedGoalSoloPaths;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer.AutoIntakeTransferStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class GoalAutonSolo implements Auton {
    private Follower follower;
    private GoalSoloPaths paths;
    private GoalAutonSoloState goalAutonSoloState = GoalAutonSoloState.drive_startPos_shootPos;
    private double currentWait = 0;
    private boolean wasIfCalled = false;
    private int gateCount = 0;
    private int gateTotal = 3;

    public GoalAutonSolo(Follower follower) {
        this.follower = follower;
    }

    @Override
    public void start() {
        GlobalVariables.far = false;
        setTrajectorySide();
        follower.followPath(paths.startPos_shootPos(), true);
        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
        goalAutonSoloState = GoalAutonSoloState.drive_startPos_shootPos;
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                paths = new RedGoalSoloPaths(follower);
                break;
            case Blue:
                paths = new BlueGoalSoloPaths(follower);
                break;
        }
    }

    @Override
    public void run() {
        switch (goalAutonSoloState)
        {
            case drive_startPos_shootPos:
                drive_StartPos_ShootPos();
                break;
            case shoot_preload:
                shoot_preload();
                break;
            case drive_shootPos_takeFirstPos:
                drive_shootPos_takeFirstPos();
                break;
            case drive_takeFirstPos_shootPos:
                drive_takeFirstPos_shootPos();
                break;
            case shoot_first:
                shoot_first();
                break;
            case drive_shootPos_openGatePos:
                drive_shootPos_openGatePos();
                break;
            case drive_openGatePos_takeGatePos:
                drive_openGatePos_takeGatePos();
                break;
            case drive_takeGatePos_shootPos:
                drive_takeGatePos_shootPos();
                break;
            case shoot_gate:
                shoot_gate();
                break;
            case drive_shootPos_takeSecondPos_shootPos:
                drive_shootPos_takeSecondPos_shootPos();
                break;
            case shoot_second:
                shoot_second();
                break;
            case stop:
                stop();
                break;
            case idle:
                break;
        }
    }

//AUTONOTE FILL IN THE LOGIC

    private void drive_StartPos_ShootPos() {
        if(follower.isBusy() || getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shoot_preload;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot_preload() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
        follower.followPath(paths.shootPos_takeFirstPos(), false);
        goalAutonSoloState = GoalAutonSoloState.drive_shootPos_takeFirstPos;
    }

    private void drive_shootPos_takeFirstPos() {
        if (follower.isBusy()) return;
        follower.followPath(paths.takeFirstPos_shootPos());
        goalAutonSoloState = GoalAutonSoloState.drive_takeFirstPos_shootPos;
    }

    private void drive_takeFirstPos_shootPos() {
        if (follower.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shoot_first;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot_first() {
        if(getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stop);
        follower.followPath(paths.shootPos_openGatePos(), true);
        goalAutonSoloState = GoalAutonSoloState.drive_shootPos_openGatePos;
        gateCount = 0;
    }

    private void drive_shootPos_openGatePos() {
        if (follower.isBusy()) return;
        if (!wasIfCalled) {
            IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
            gateCount += 1;
            addWaitTime(1);
            wasIfCalled = true;
        }
        if (getSeconds() < currentWait) return;
        follower.followPath(paths.openGatePos_takeGatePos(), true);
        addWaitTime(2);
        wasIfCalled = false;
        goalAutonSoloState = GoalAutonSoloState.drive_openGatePos_takeGatePos;
    }

    private void drive_openGatePos_takeGatePos() {
        if (follower.isBusy() || getSeconds() < currentWait) return;
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.stop);

        if (gateCount >= gateTotal) follower.followPath(paths.takeGatePos_shootPos_last());
        else follower.followPath(paths.takeGatePos_shootPos());

        goalAutonSoloState = GoalAutonSoloState.drive_takeGatePos_shootPos;
    }

    private void drive_takeGatePos_shootPos() {
        if(follower.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shoot_gate;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot_gate() {
        if (getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.activate);
        if (gateCount >= gateTotal) {
            follower.followPath(paths.shootPos_openGatePos(), true);
            goalAutonSoloState = GoalAutonSoloState.drive_shootPos_openGatePos;
        }
        else {
            follower.followPath(paths.shootPos_takeSecondPos_shootPos(), true);
            goalAutonSoloState = GoalAutonSoloState.drive_shootPos_takeSecondPos_shootPos;
        }
    }

    private void drive_shootPos_takeSecondPos_shootPos() {
        if(follower.isBusy()) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.activate);
        goalAutonSoloState = GoalAutonSoloState.shoot_second;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void shoot_second() {
        if (getSeconds() < currentWait) return;
        OuttakeStates.setAutoCycleShootState(AutoCycleShootStates.idle);
        IntakeStates.setLockServoState(LockServoStates.lock);
        IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
        goalAutonSoloState = GoalAutonSoloState.stop;
    }

    private void stop() {
        if(follower.isBusy()) return;
        goalAutonSoloState = GoalAutonSoloState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}