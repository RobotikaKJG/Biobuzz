package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousConstants;
import org.firstinspires.ftc.teamcode.Autonomous.AutonomousControl;
import org.firstinspires.ftc.teamcode.Autonomous.GoalAuton;
import org.firstinspires.ftc.teamcode.Roadrunner.SampleMecanumDrive;

/**
 * Extends the shared hardware wiring with the autonomous routine and optional Road Runner drive.
 * With USE_ROAD_RUNNER=false, autonomous needs only the same four motors as TeleOp.
 * When enabled, Road Runner owns drive outputs; MotorControl only flushes mechanism commands.
 */
public class AutonomousDependencies extends Dependencies {
    public final SampleMecanumDrive drive;
    public final AutonomousControl autonomousControl;

    public AutonomousDependencies(HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2,
            Telemetry telemetry) {
        super(hardwareMap, gamepad1, gamepad2, telemetry);
        drive = AutonomousConstants.USE_ROAD_RUNNER ? new SampleMecanumDrive(hardwareMap) : null;
        autonomousControl = createAutonomousControl();
    }

    public GoalAuton createGoalAuton() { return new GoalAuton(drive); }

    public AutonomousControl createAutonomousControl() {
        return new AutonomousControl(motorControl, createGoalAuton(), createIntakeControl(),
                createOuttakeControl(), sensorControl);
    }
}
