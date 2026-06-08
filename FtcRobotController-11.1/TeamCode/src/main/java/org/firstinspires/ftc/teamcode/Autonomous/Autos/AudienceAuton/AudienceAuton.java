package org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.Autonomous.Auton;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.AudiencePaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.BlueAudiencePaths;
import org.firstinspires.ftc.teamcode.Autonomous.Paths.AudiencePaths.RedAudiencePaths;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AudienceAuton implements Auton {
    private SensorControl sensorControl;
    private Follower follower;
    private AudiencePaths paths;
    private AudienceAutonState audienceAutonState = AudienceAutonState.drive_startPos_shootPos;
    private double currentWait = 0;

    public AudienceAuton(Follower follower, SensorControl sensorControl) {
        this.follower = follower;
        this.sensorControl = sensorControl;
    }

    @Override
    public void start() {
        GlobalVariables.far = true;
        setTrajectorySide();
        follower.followPath(paths.drive_startPos_shootPos(), true);
        OuttakeStates.setMotorState(OuttakeMotorStates.autonomous);
        audienceAutonState = AudienceAutonState.drive_startPos_shootPos;
        addWaitTime(3.5);
    }

    @Override
    public void setTrajectorySide() {
        switch (GlobalVariables.alliance) {
            case Red:
                paths = new RedAudiencePaths(follower);
                break;
            case Blue:
                paths = new BlueAudiencePaths(follower);
                break;
        }
    }

    @Override
    public void run() {
        switch (audienceAutonState)
        {
            case drive_startPos_shootPos:
                drive_StartPos_ShootPos();
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
        audienceAutonState = AudienceAutonState.shoot_preload;
        addWaitTime(AutonomousConstants.shootTime);
    }

    private void stop() {
        if(follower.isBusy()) return;
        audienceAutonState = AudienceAutonState.idle;
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1_000.0;
    }
}