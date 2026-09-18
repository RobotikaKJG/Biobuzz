package org.firstinspires.ftc.teamcode.Autonomous;

/**
 * Non-blocking routine lifecycle used by AutonomousControl.
 * Select paths and seed localization in start(); run() advances at most one iteration.
 * Check drive.isBusy() between async trajectories so the OpMode can continue servicing STOP.
 */
public interface Auton {
    void start();
    void setTrajectorySide();
    void run();
}
