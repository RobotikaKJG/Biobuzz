package org.firstinspires.ftc.teamcode.Main.ManualOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Disabled bench-test extension point; the previous robot's hardware and setpoints were removed.
 * Map a new LimitSwitch and select its normally-open or normally-closed wiring before reporting its state.
 * Implement the test first, then remove @Disabled to show it on the Driver Station.
 * Production behavior belongs in a subsystem controller, not in this diagnostic OpMode.
 */
@Disabled
@TeleOp(name = "LimitSwitchTest", group = "Templates")
public class LimitSwitchTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Unconfigured test template: see this class's documentation.");
        telemetry.update();
        waitForStart();
        // Add a STOP-aware loop and a finally block that stops any motor/CR-servo output.
    }
}
