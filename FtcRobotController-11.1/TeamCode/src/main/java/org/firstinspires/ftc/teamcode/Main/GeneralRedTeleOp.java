package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

import java.util.List;

@TeleOp
public class GeneralRedTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        GlobalVariables.isAutonomous = false;
        GlobalVariables.subCycles = false;
        GlobalVariables.hang = false;
        GlobalVariables.alliance = Alliance.Red;
        Dependencies dependencies = new Dependencies(hardwareMap, gamepad1, gamepad2, telemetry);
        IterativeController iterativeController = new IterativeController(dependencies);

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        waitForStart();

        dependencies.servoControl.setServoStartPos();

        if (isStopRequested()) return;

        LoopTimeLogger loopTimeLogger = new LoopTimeLogger(hardwareMap.appContext, "GeneralRedTeleOp");
        dependencies.setLoopTimeLogger(loopTimeLogger);
        iterativeController.setLoopTimeLogger(loopTimeLogger);
        loopTimeLogger.start();

        try {
            while (opModeIsActive()) {
                loopTimeLogger.startLoop();

                for (LynxModule hub : allHubs) {
                    hub.clearBulkCache();
                }
                loopTimeLogger.recordSection("clearBulkCache");

                iterativeController.TeleOp();
                loopTimeLogger.recordSection("iterativeController.TeleOp");

                telemetry.addData("yaw", dependencies.sensorControl.getLocalizerAngle());
                telemetry.addData("PosX mm", dependencies.sensorControl.getLocalizerPose().getX());
                telemetry.addData("PosY mm", dependencies.sensorControl.getLocalizerPose().getY());
                telemetry.addData("TagDist", dependencies.sensorControl.getTagDistance());
                telemetry.addData("PinpointDist", dependencies.sensorControl.getDistanceFromLocalizer());
                telemetry.addData("TurretAngle", dependencies.turretServoControl.getTurretAngleDeg());
                telemetry.addData("TurretTarget", dependencies.sensorControl.getTurretTargetAngleDegrees());
                telemetry.addData("OuttakeSpeed", dependencies.motorControl.getMotorVelocity(MotorConstants.outtake1));
                telemetry.addData("Outtake current", dependencies.motorControl.getMotorCurrent(MotorConstants.outtake1));
                telemetry.addData("Transfer current", dependencies.motorControl.getMotorCurrent(MotorConstants.transfer) + dependencies.motorControl.getMotorCurrent(MotorConstants.intake));
                loopTimeLogger.recordSection("telemetryData");

                telemetry.addData("Loop time ms", loopTimeLogger.getCurrentLoopMs());
                telemetry.update();
                loopTimeLogger.recordSection("telemetry.update");
                loopTimeLogger.finishLoop();

                if (gamepad1.triangle) break;
            }
        } finally {
            loopTimeLogger.saveToTelemetry(telemetry);
        }
    }
}
