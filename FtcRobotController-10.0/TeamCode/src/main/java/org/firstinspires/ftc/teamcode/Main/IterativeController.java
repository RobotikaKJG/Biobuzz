package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Drivebase.DrivebaseController;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * One non-blocking TeleOp iteration. The OpMode owns START/STOP; this class owns update order.
 * Input snapshots -> edges -> sensor reads -> button intentions -> subsystems -> motor writes.
 * Keep sleeps and long while-loops out of controllers so drive and STOP stay responsive.
 */
public class IterativeController {
    private final Dependencies dependencies;
    private final Gamepad currentGamepad1 = new Gamepad();
    private final Gamepad previousGamepad1 = new Gamepad();
    private final Gamepad currentGamepad2 = new Gamepad();
    private final Gamepad previousGamepad2 = new Gamepad();
    private final DrivebaseController drivebaseController;
    private final ButtonControl buttonControl;
    private final IntakeControl intakeControl;
    private final OuttakeControl outtakeControl;

    public IterativeController(Dependencies dependencies) {
        this.dependencies = dependencies;
        drivebaseController = dependencies.createDrivebaseController();
        buttonControl = dependencies.createSubsystemControl();
        intakeControl = dependencies.createIntakeControl();
        outtakeControl = dependencies.createOuttakeControl();
        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
        currentGamepad1.copy(dependencies.gamepad1);
        currentGamepad2.copy(dependencies.gamepad2);
    }

    public void TeleOp() {
        previousGamepad1.copy(currentGamepad1);
        currentGamepad1.copy(dependencies.gamepad1);
        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(dependencies.gamepad2);
        dependencies.edgeDetection.refreshGamepadIndex(currentGamepad1, previousGamepad1);
        dependencies.gamepad2EdgeDetection.refreshGamepadIndex(currentGamepad2, previousGamepad2);
        dependencies.sensorControl.update();
        buttonControl.update();
        // Gamepad 2 edges are ready above. Assign operator bindings deliberately here;
        // do not run the same button mappings twice against shared static subsystem states.
        drivebaseController.updateState();
        intakeControl.update();
        outtakeControl.update();
        dependencies.motorControl.setMotors(MotorConstants.all);
    }
}
