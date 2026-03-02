package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

import java.util.List;

@TeleOp
public class GeneralRedTeleOp extends LinearOpMode {

    private double prevTime;
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
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        waitForStart();

        dependencies.servoControl.setServoStartPos();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            iterativeController.TeleOp();
            if (gamepad1.triangle) break;

            telemetry.addData("yaw", dependencies.sensorControl.getPinpointAngle());
            telemetry.addData("Motor velocity: ", dependencies.motorControl.getMotorVelocity(MotorConstants.outtake1));
            telemetry.addData(" Distance ", dependencies.sensorControl.getTagDistance());
            telemetry.addData("TurretAngle", dependencies.turretMotorControl.getTurretAngleDeg());
            telemetry.addData("Turret should", dependencies.sensorControl.getTurretTargetAngleDegrees());
            telemetry.addData("Outtake motor state", OuttakeStates.getMotorState());
            telemetry.addData("Turret encoder", dependencies.motorControl.getMotorPosition(MotorConstants.turret));
            telemetry.addData("PosX", dependencies.sensorControl.getPinpointPos().getX(DistanceUnit.INCH));
            telemetry.addData("PosY", dependencies.sensorControl.getPinpointPos().getY(DistanceUnit.INCH));
            
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