package org.firstinspires.ftc.teamcode.Main;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Autonomous.AutonomousControl;
import org.firstinspires.ftc.teamcode.Autonomous.SelectStartVariables;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;


@Autonomous
public class GeneralAutonomous extends LinearOpMode {

    AutonomousDependencies dependencies;
    private Follower follower;
    private AutonomousControl autonomousControl;

    @Override
    public void runOpMode() {

        initialise();

        while (!isStarted() && !isStopRequested()) {
            updateAutonData();
        }

        begin();

        if (isStopRequested()) return;

        LoopTimer loopTimer = new LoopTimer(10);

        try {
            while (opModeIsActive()) {
                long startNs = System.nanoTime();

                autonomousControl.runAutonomous();
                follower.update();

                loopTimer.record(System.nanoTime() - startNs);

                telemetry.addData("outtake vel", dependencies.motorControl.getMotorVelocity(MotorConstants.outtake1));
                telemetry.addData("Auto loop avg (ms, last 10)", "%.2f", loopTimer.getAvgMs());
                telemetry.update();

                if (gamepad1.triangle)
                    break;
            }
        } finally {
//            dependencies.shooterLogger.stop();
        }
    }

    private void initialise() {
        //noinspection unused
        SelectStartVariables selectStartVariables = new SelectStartVariables(gamepad1, telemetry);

        //Needs to be set to false for SensorControl initialisation through AutonomousDependencies
        GlobalVariables.wasAutonomous = false;
        GlobalVariables.isAutonomous = true;
        GlobalVariables.subCycles = false;

        dependencies = new AutonomousDependencies(hardwareMap, gamepad1,gamepad2, telemetry);

        follower = dependencies.follower;
        autonomousControl = dependencies.autonomousControl;

        dependencies.servoControl.setServoStartPos();
    }

    private void updateAutonData() {
        telemetry.addData("Auton:", GlobalVariables.autonomousMode);
        telemetry.addData("Alliance:", GlobalVariables.alliance);

        telemetry.addLine(" ");
        dependencies.sensorControl.updateLocalizer();
        telemetry.addData("yaw", dependencies.sensorControl.getLocalizerAngle());
        telemetry.addData("outtake vel", dependencies.motorControl.getMotorVelocity(MotorConstants.outtake1));

        telemetry.update();
        // Slow down CPU cycles
        sleep(100);
    }

    private void begin() {
        autonomousControl.startAutonomous();

        //Update variable that autonomous happened for the driver oriented rotation after it
        GlobalVariables.lastPose = follower.getPose();
        GlobalVariables.wasAutonomous = true;
    }
}
