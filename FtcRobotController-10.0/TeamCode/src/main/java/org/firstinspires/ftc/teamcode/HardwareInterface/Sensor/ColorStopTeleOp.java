package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Disabled bench-test extension point; the previous robot's hardware and setpoints were removed.
 * Read the new color/range sensor and define a measured stopping condition; no old thresholds remain.
 * Implement the test first, then remove @Disabled to show it on the Driver Station.
 * Production behavior belongs in a subsystem controller, not in this diagnostic OpMode.
 */
@Disabled
@TeleOp(name = "ColorStopTeleOp", group = "Templates")
public class ColorStopTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Unconfigured test template: see this class's documentation.");
        telemetry.update();
        waitForStart();
        // Add a STOP-aware loop and a finally block that stops any motor/CR-servo output.
    }
}
