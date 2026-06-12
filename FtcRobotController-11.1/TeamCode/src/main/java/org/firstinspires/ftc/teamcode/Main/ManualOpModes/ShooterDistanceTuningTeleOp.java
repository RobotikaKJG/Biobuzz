package org.firstinspires.ftc.teamcode.Main.ManualOpModes;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.Main.ControlThread;
import org.firstinspires.ftc.teamcode.Main.Dependencies;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Main.IterativeController;
import org.firstinspires.ftc.teamcode.Main.LoopTimer;
import org.firstinspires.ftc.teamcode.Main.TurretThread;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;

import java.util.List;

@TeleOp(name = "ShooterDistanceTuningTeleOp", group = "Tuning")
public class ShooterDistanceTuningTeleOp extends LinearOpMode {

    private static final long DRIVE_TARGET_PERIOD_MS = 2;
    private static final long TELEMETRY_INTERVAL_MS = 100;
    private boolean isLimelightResetting = false;

    @Override
    public void runOpMode() throws InterruptedException {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        // Standard Robot Setup
        GlobalVariables.isAutonomous = false;
        GlobalVariables.subCycles = false;
        GlobalVariables.hang = false;
        GlobalVariables.alliance = Alliance.Red;
        GlobalVariables.rpmOffset = 0; // Initialize tuning offset

        Dependencies dependencies = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        IterativeController iterativeController = new IterativeController(dependencies);
        DrivebaseController drivebaseController = dependencies.createDrivebaseController();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        waitForStart();

        dependencies.servoControl.setServoStartPos();

        if (isStopRequested()) return;

        // Start background threads for normal robot functioning
        ControlThread controlThread = new ControlThread(this, allHubs, iterativeController);
        TurretThread turretThread = new TurretThread(this, dependencies.sensorControl,
                dependencies.turretServoControl, gamepad1);
        controlThread.start();
        turretThread.start();

        LoopTimer driveTimer = new LoopTimer(10);
        long lastTelemetryMs = 0;
        long prevLoopNs = System.nanoTime();

        try {
            while (opModeIsActive()) {
                long startNs = System.nanoTime();
                driveTimer.record(startNs - prevLoopNs);
                prevLoopNs = startNs;

                // 1. Shooter RPM Tuning (D-pad Up/Down)
                // Modifies the offset used by OuttakeMotorControl in the background
                if (dependencies.edgeDetection.rising(GamepadIndexValues.dpadLeft)) {
                    GlobalVariables.rpmOffset += 20;
                }
                if (dependencies.edgeDetection.rising(GamepadIndexValues.dpadDown)) {
                    GlobalVariables.rpmOffset -= 20;
                }

                // 3. Continuous Camera-based Position Adjustment
                dependencies.sensorControl.applyContinuousVisionFusion();

                // 4. Standard Drivebase Control (Main thread)
                drivebaseController.updateState();
                dependencies.motorControl.setMotors(MotorConstants.allDrive);

                // 5. Specialized Tuning Telemetry
                long nowMs = System.currentTimeMillis();
                if (nowMs - lastTelemetryMs >= TELEMETRY_INTERVAL_MS) {
                    telemetry.addLine("--- SHOOTER TUNING ---");
                    telemetry.addData("Distance from Target", "%.2f inches", dependencies.sensorControl.getDistanceFromLocalizer());
                    telemetry.addData("Target Base RPM", "%.0f", GlobalVariables.outtakeTargetSpeed);
                    telemetry.addData("Manual RPM Offset", "%.0f", GlobalVariables.rpmOffset);
                    telemetry.addData("Final Target RPM", "%.0f", GlobalVariables.outtakeTargetSpeed + GlobalVariables.rpmOffset);
                    telemetry.addData("Actual Shooter RPM", "%.0f", dependencies.motorControl.getMotorVelocity(MotorConstants.outtake));
                    
                    telemetry.addLine("\n--- ROBOT POSITION ---");
                    telemetry.addData("Pose", dependencies.sensorControl.getLocalizerPose().toString());
                    telemetry.addData("Turret Angle", "%.1f°", dependencies.turretServoControl.getTurretAngleDeg());
                    
                    telemetry.addLine("\n--- SYSTEM STATUS ---");
                    telemetry.addData("Drive Loop", "%.1f Hz", 1000.0 / driveTimer.getAvgMs());
                    telemetry.addData("Control Loop", "%.1f Hz", 1000.0 / controlThread.getAvgLoopMs());
                    
                    telemetry.update();
                    lastTelemetryMs = nowMs;
                }

                // Maintain loop timing
                long workMs = (System.nanoTime() - startNs) / 1_000_000L;
                long sleepMs = DRIVE_TARGET_PERIOD_MS - workMs;
                if (sleepMs > 0) sleep(sleepMs);
            }
        } finally {
            // Clean shutdown of background threads
            controlThread.stopLoop();
            turretThread.stopLoop();
            try { controlThread.join(500); } catch (InterruptedException ignored) {}
            try { turretThread.join(500); } catch (InterruptedException ignored) {}
            
            dependencies.motorControl.setMotorSpeed(MotorConstants.all, 0);
            dependencies.motorControl.setMotors(MotorConstants.all);
        }
    }
}
