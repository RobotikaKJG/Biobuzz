package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.BlueTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.RedTrajectories;
import org.firstinspires.ftc.teamcode.Autonomous.Trajectories.Trajectories;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;

/**
 * Autonomous sequence extension point, retaining the original class location.
 * AutonomousControl calls start once and run every active loop. The default routine stays idle.
 * A nullable drive means Road Runner is disabled; never construct it just to execute the idle mode.
 * To add motion, add a stage enum, start a trajectory asynchronously, and wait on isBusy() in run().
 */
public class GoalAuton implements Auton {
    private final SampleMecanumDrive drive;
    private Trajectories trajectories;
    private GoalAutonState state = GoalAutonState.idle;

    public GoalAuton(SampleMecanumDrive drive) { this.drive = drive; }

    @Override
    public void setTrajectorySide() {
        trajectories = GlobalVariables.alliance == Alliance.Red
                ? new RedTrajectories() : new BlueTrajectories();
    }

    @Override
    public void start() {
        setTrajectorySide();
        state = GoalAutonState.idle;
        if (drive != null) drive.setPoseEstimate(trajectories.getStartPose());
    }

    @Override
    public void run() {
        switch (state) {
            case idle:
                // Add explicit stages here. No paths or mechanism commands run by default.
                break;
        }
    }
}
