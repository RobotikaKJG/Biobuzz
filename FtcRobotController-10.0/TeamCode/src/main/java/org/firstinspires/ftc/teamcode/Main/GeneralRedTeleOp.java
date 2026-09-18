package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/** Driver Station entry point. Dependencies wires the robot; IterativeController runs each loop. */
@TeleOp(name = "GeneralRedTeleOp", group = "Starter")
public class GeneralRedTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        GlobalVariables.reset(Alliance.Red, false);
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        Dependencies dependencies = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        try {
            IterativeController controller = new IterativeController(dependencies);
            telemetry.addLine("Ready: gamepad 1 left stick drives, right stick turns.");
            telemetry.addLine("Robot-oriented by default. Mechanism buttons are unassigned.");
            telemetry.update();
            waitForStart();
            if (isStopRequested()) return;
            dependencies.servoControl.setServoStartPos();
            double previousTime = getRuntime();
            while (opModeIsActive()) {
                controller.TeleOp();
                double now = getRuntime();
                telemetry.addData("Loop (ms)", (now - previousTime) * 1000);
                telemetry.addData("Alliance", GlobalVariables.alliance);
                telemetry.addData("Heading sensor configured", dependencies.sensorControl.hasHeading());
                previousTime = now;
                telemetry.update();
                idle();
            }
        } finally {
            dependencies.stop();
        }
    }
}
