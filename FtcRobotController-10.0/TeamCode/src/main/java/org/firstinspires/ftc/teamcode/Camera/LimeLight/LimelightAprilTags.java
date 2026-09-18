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
@TeleOp(name = "LimelightAprilTags Test", group = "Tests")
public class LimelightAprilTags extends LinearOpMode {
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
                if (result != null && result.isValid() && result.getBotpose() != null) {
                    // This is field pose from the configured pipeline, not distance to a game target.
                    telemetry.addData("Field X (m)", result.getBotpose().getPosition().x);
                    telemetry.addData("Field Y (m)", result.getBotpose().getPosition().y);
                }
                telemetry.update();
                idle();
            }
        } finally {
            camera.stop();
        }
    }
}
