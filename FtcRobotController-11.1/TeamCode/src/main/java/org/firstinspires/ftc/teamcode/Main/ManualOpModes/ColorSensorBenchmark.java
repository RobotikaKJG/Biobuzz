package org.firstinspires.ftc.teamcode.Main.ManualOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.Main.Dependencies;

@TeleOp(name = "Color Sensor Benchmark", group = "Testing")
public class ColorSensorBenchmark extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Dependencies deps = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        
        boolean midEnabled = false;
        boolean frontEnabled = false;

        telemetry.addLine("Press Cross to toggle Mid Sensor");
        telemetry.addLine("Press Circle to toggle Front Sensor");
        telemetry.update();

        waitForStart();

        long lastLoopTime = System.nanoTime();

        while (opModeIsActive()) {
            // Update edge detection for toggles
            deps.edgeDetection.refreshGamepadIndex(gamepad1, deps.gamepad1);
            
            if (deps.edgeDetection.rising(GamepadIndexValues.cross)) midEnabled = !midEnabled;
            if (deps.edgeDetection.rising(GamepadIndexValues.circle)) frontEnabled = !frontEnabled;

            // Direct sensor reads to measure blocking time
            if (midEnabled) {
                deps.sensorControl.rangeSensorMid.getDistance(DistanceUnit.INCH);
            }
            if (frontEnabled) {
                deps.sensorControl.rangeSensorFront.getDistance(DistanceUnit.INCH);
            }

            // Loop time calculation
            long currentTime = System.nanoTime();
            double loopTimeMs = (currentTime - lastLoopTime) / 1_000_000.0;
            double hz = 1000.0 / loopTimeMs;
            lastLoopTime = currentTime;

            telemetry.addData("Mid Sensor (Cross/A)", midEnabled ? "ACTIVE" : "OFF");
            telemetry.addData("Front Sensor (Circle/B)", frontEnabled ? "ACTIVE" : "OFF");
            telemetry.addLine("-------------------");
            telemetry.addData("Loop Time", "%.2f ms", loopTimeMs);
            telemetry.addData("Frequency", "%.1f Hz", hz);
            telemetry.update();

            // Store current gamepad state for next loop's edge detection
            deps.gamepad1.copy(gamepad1);
        }
    }
}
