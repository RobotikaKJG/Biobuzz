package org.firstinspires.ftc.teamcode.Camera.LimeLight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Optional read-only camera diagnostic; the standard robot does not require a Limelight.
 * Configure "limelight" and the pipeline below before enabling. For AprilTags, also configure
 * the new field's tag map and camera mounting in the camera. No target IDs or field offsets remain.
 * Integrate production camera ownership through SensorControl, including shutdown in stop().
 */
@Disabled
@TeleOp(name = "Limelight Test", group = "Tests")
public class Limelight extends LinearOpMode {
    private static final int PIPELINE = 0; // Replace with your configured pipeline.

    @Override
    public void runOpMode() throws InterruptedException {
        Limelight3A camera = hardwareMap.get(Limelight3A.class, "limelight");
        try {
            camera.pipelineSwitch(PIPELINE);
            camera.start();
            waitForStart();
            while (opModeIsActive()) {
                LLResult result = camera.getLatestResult();
                telemetry.addData("Valid target", result != null && result.isValid());
                if (result != null && result.isValid()) {
                    telemetry.addData("Horizontal angle (deg)", result.getTx());
                    telemetry.addData("Vertical angle (deg)", result.getTy());
                    telemetry.addData("Target area (%)", result.getTa());
                }
                telemetry.update();
                idle();
            }
        } finally {
            camera.stop();
        }
    }
}
