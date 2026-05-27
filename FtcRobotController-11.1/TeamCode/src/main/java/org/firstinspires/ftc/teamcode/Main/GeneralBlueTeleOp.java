package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

import java.util.List;


@TeleOp
public class GeneralBlueTeleOp extends LinearOpMode {

    private double prevTime;

    @Override
    public void runOpMode() throws InterruptedException {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        GlobalVariables.isAutonomous = false;
        GlobalVariables.subCycles = false;
        GlobalVariables.hang = false;
        GlobalVariables.alliance = Alliance.Blue;
        Dependencies dependencies = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        IterativeController iterativeController = new IterativeController(dependencies);

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        waitForStart();

        dependencies.servoControl.setServoStartPos();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // Clear bulk cache once per loop — all hardware reads this iteration use cached data
            for (LynxModule hub : allHubs) {
                hub.clearBulkCache();
            }

            iterativeController.TeleOp();

//            dependencies.sensorControl.updateDistance();

            telemetry.addData("yaw", dependencies.sensorControl.getLocalizerAngle());
            telemetry.addData("PosX mm", dependencies.sensorControl.getLocalizerPose().getX());
            telemetry.addData("PosY mm", dependencies.sensorControl.getLocalizerPose().getY());
            telemetry.addData("TagDist", dependencies.sensorControl.getTagDistance());
            telemetry.addData("PinpointDist", dependencies.sensorControl.getDistanceFromLocalizer());
            telemetry.addData("TurretAngle", dependencies.turretServoControl.getTurretAngleDeg());
            telemetry.addData("TurretTarget", dependencies.sensorControl.getTurretTargetAngleDegrees());
            telemetry.addData("Front distance", dependencies.sensorControl.getDistanceFront());
            telemetry.addData("Mid distance", dependencies.sensorControl.getDistanceMid());
            telemetry.addData("forward", dependencies.sensorControl.isDrivingForward());
            telemetry.addData("backward", dependencies.sensorControl.isDrivingBackward());
            telemetry.addData("AutoIntakeMovement state", IntakeStates.getAutoIntakeMovementState());

            if (gamepad1.triangle) break;
            calculateLoopTime();
            telemetry.update();
        }
    }

    private void calculateLoopTime()
    {
        double currentTime = System.nanoTime() / 1_000_000.0;
        telemetry.addData("Loop time:", currentTime - prevTime);
        prevTime = currentTime;
    }
}