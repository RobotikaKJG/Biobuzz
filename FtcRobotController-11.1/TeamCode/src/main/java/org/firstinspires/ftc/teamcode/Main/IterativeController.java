package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class IterativeController {
    private final MotorControl motorControl;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final Gamepad currentGamepad1 = new Gamepad();
    private final Gamepad prevGamepad1 = new Gamepad();
    private final Gamepad currentGamepad2 = new Gamepad();
    private final Gamepad prevGamepad2 = new Gamepad();
    private final EdgeDetection edgeDetection;
    private final ButtonControl buttonControl;
    private final OuttakeControl outtakeControl;
    private final IntakeControl intakeControl;
    private final SensorControl sensorControl;
    private boolean isLimelightRecalibrating = false;

    public IterativeController(Dependencies dependencies) {
        gamepad1 = dependencies.gamepad1;
        gamepad2 = dependencies.gamepad2;
        edgeDetection = dependencies.edgeDetection;
        motorControl = dependencies.motorControl;
        currentGamepad1.copy(this.gamepad1);
        prevGamepad1.copy(currentGamepad1);
        currentGamepad2.copy(this.gamepad2);
        prevGamepad2.copy(currentGamepad2);
        buttonControl = dependencies.createSubsystemControl();
        outtakeControl = dependencies.createOuttakeControl();
        intakeControl = dependencies.createIntakeControl();
        sensorControl = dependencies.sensorControl;

        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
    }

    public void TeleOp() {
        updateCommonValues();

        buttonControl.update();
        intakeControl.update();
        outtakeControl.update();

        motorControl.setMotors(MotorConstants.notDrive);
    }

    private void updateCommonValues() {
        prevGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(gamepad1);
        edgeDetection.refreshGamepad1Index(currentGamepad1, prevGamepad1);

        prevGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(gamepad2);
        edgeDetection.refreshGamepad2Index(currentGamepad2, prevGamepad2);
        // localizer.update() + heading reset moved to TurretThread (owns the localizer).
    }
}
