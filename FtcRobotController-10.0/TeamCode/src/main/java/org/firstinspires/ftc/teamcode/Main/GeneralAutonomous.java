package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Autonomous.SelectStartVariables;

/**
 * Driver Station autonomous entry point. The pre-start selector never blocks START/STOP.
 * Dependencies owns hardware; AutonomousControl owns sequencing; Road Runner owns optional motion.
 */
@Autonomous(name = "GeneralAutonomous", group = "Starter")
public class GeneralAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        GlobalVariables.reset(Alliance.Red, true);
        AutonomousDependencies dependencies = new AutonomousDependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        try {
            SelectStartVariables selector = new SelectStartVariables(gamepad1, telemetry);
            while (!isStarted() && !isStopRequested()) {
                selector.update();
                telemetry.update();
                sleep(20);
            }
            // Check STOP before starting a routine or moving a servo.
            if (isStopRequested()) return;
            dependencies.servoControl.setServoStartPos();
            dependencies.autonomousControl.startAutonomous();
            while (opModeIsActive()) {
                dependencies.autonomousControl.runAutonomous();
                if (dependencies.drive != null) dependencies.drive.update();
                telemetry.addData("Routine", GlobalVariables.autonomousMode);
                telemetry.update();
                idle();
            }
        } finally {
            dependencies.stop();
        }
    }
}
