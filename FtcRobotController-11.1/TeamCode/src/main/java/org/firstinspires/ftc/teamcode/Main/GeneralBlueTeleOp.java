package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;

import java.util.List;


@TeleOp
public class GeneralBlueTeleOp extends LinearOpMode {

    // Drive loop target period (main thread). 2 ms => ~500 Hz cap; the loop only
    // sleeps the leftover when idle (setMotors gates redundant writes) so it never
    // busy-spins. When actively driving it's bound by the Lynx bus, not this cap.
    private static final long DRIVE_TARGET_PERIOD_MS = 2;
    private static final long TELEMETRY_INTERVAL_MS = 150;

    @Override
    public void runOpMode() throws InterruptedException {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        GlobalVariables.isAutonomous = false;
        GlobalVariables.subCycles = false;
        GlobalVariables.hang = false;
        GlobalVariables.alliance = Alliance.Blue;

        Dependencies dependencies = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        IterativeController iterativeController = new IterativeController(dependencies);
        DrivebaseController drivebaseController = dependencies.createDrivebaseController();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        waitForStart();

        dependencies.servoControl.setServoStartPos();

        if (isStopRequested()) return;

        // Background loops. Control: bulk cache + non-drive/non-turret subsystems
        // (motors 4..7). Turret: Pinpoint localizer + turret servo tracking.
        ControlThread controlThread = new ControlThread(this, allHubs, iterativeController);
        TurretThread turretThread = new TurretThread(this, dependencies.sensorControl,
                dependencies.turretServoControl, gamepad1);
        controlThread.start();
        turretThread.start();

        LoopTimer driveTimer = new LoopTimer(10);
        long lastTelemetryMs = 0;
        long prevLoopNs = System.nanoTime();

        try {
            // Drive loop runs on the main thread (as fast as the bus allows). Owns motors 0..3.
            while (opModeIsActive()) {
                long startNs = System.nanoTime();
                driveTimer.record(startNs - prevLoopNs); // full loop PERIOD (incl. sleep) -> true rate
                prevLoopNs = startNs;

                drivebaseController.updateState();
                dependencies.motorControl.setMotors(MotorConstants.allDrive);

                if (gamepad1.triangle) break;

                // Throttle telemetry so it never caps the fast drive loop.
                long nowMs = System.currentTimeMillis();
                if (nowMs - lastTelemetryMs >= TELEMETRY_INTERVAL_MS) {
                    double driveMs = driveTimer.getAvgMs();
                    double turretMs = turretThread.getAvgLoopMs();
                    double controlMs = controlThread.getAvgLoopMs();
                    telemetry.addData("Drive loop",   "%.2f ms  (%.0f hz)", driveMs, hz(driveMs));
                    telemetry.addData("Turret loop",  "%.2f ms  (%.0f hz)", turretMs, hz(turretMs));
                    telemetry.addData("Control loop", "%.2f ms  (%.0f hz)", controlMs, hz(controlMs));
                    telemetry.update();
                    lastTelemetryMs = nowMs;
                }

                long workMs = (System.nanoTime() - startNs) / 1_000_000L;
                long sleepMs = DRIVE_TARGET_PERIOD_MS - workMs;
                if (sleepMs > 0) sleep(sleepMs);
            }
        } finally {
            // Stop the background loops, then zero every motor from this thread.
            controlThread.stopLoop();
            turretThread.stopLoop();
            controlThread.interrupt();
            turretThread.interrupt();
            try { controlThread.join(500); } catch (InterruptedException ignored) {}
            try { turretThread.join(500); } catch (InterruptedException ignored) {}

            dependencies.motorControl.setMotorSpeed(MotorConstants.all, 0);
            dependencies.motorControl.setMotors(MotorConstants.all);
        }
    }

    private static double hz(double ms) {
        return ms > 0 ? 1000.0 / ms : 0.0;
    }
}
