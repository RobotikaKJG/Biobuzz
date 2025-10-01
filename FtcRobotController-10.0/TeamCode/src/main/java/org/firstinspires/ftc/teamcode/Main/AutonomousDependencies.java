package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;

public class AutonomousDependencies extends Dependencies {

    public SampleMecanumDrive drive;

    public AutonomousDependencies(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {
        super(hardwareMap, gamepad1, gamepad2, telemetry);
//        switch (GlobalVariables.autonomousMode)
//        {
//
//        }
        drive = new SampleMecanumDrive(hardwareMap);
    }
}
