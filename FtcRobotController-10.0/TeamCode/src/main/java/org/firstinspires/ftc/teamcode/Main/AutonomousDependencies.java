package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousControl;
//import org.firstinspires.ftc.teamcode.Autonomous.NewSampleAuton;
import org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton;
import org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;

public class AutonomousDependencies extends Dependencies {

    public SampleMecanumDrive drive;
    public final AutonomousControl autonomousControl;

    public AutonomousDependencies(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2, Telemetry telemetry) {
        super(hardwareMap, gamepad1, gamepad2, telemetry);
//        switch (GlobalVariables.autonomousMode)
//        {
//
//        }
        drive = new SampleMecanumDrive(hardwareMap);
        autonomousControl = createAutonomousControl();
    }

    public GoalAuton createGoalAuton() {
        return new GoalAuton(drive);
    }
    public AudienceAuton createAudienceAuton() {
        return new AudienceAuton(drive, sensorControl);
    }

    public AutonomousControl createAutonomousControl() {
        return new AutonomousControl(motorControl, createGoalAuton(), createAudienceAuton(),createIntakeControl(),createOuttakeControl(), sensorControl);
    }
}