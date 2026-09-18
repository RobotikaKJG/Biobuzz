package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Autonomous counterpart to IterativeController: routine intentions -> mechanisms -> hardware.
 * GeneralAutonomous updates Road Runner separately; this class never flushes drive motor commands.
 * Both operating modes reuse the same subsystem state holders and mechanism controllers.
 */
public class AutonomousControl {
    private final MotorControl motorControl;
    private final GoalAuton goalAuton;
    private final IntakeControl intakeControl;
    private final OuttakeControl outtakeControl;
    private final SensorControl sensorControl;

    public AutonomousControl(MotorControl motorControl, GoalAuton goalAuton,
            IntakeControl intakeControl, OuttakeControl outtakeControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.goalAuton = goalAuton;
        this.intakeControl = intakeControl;
        this.outtakeControl = outtakeControl;
        this.sensorControl = sensorControl;
        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
    }

    public void startAutonomous() { goalAuton.start(); }

    public void runAutonomous() {
        sensorControl.update();
        switch (GlobalVariables.autonomousMode) {
            case IDLE:
                goalAuton.run();
                break;
        }
        intakeControl.update();
        outtakeControl.update();
        motorControl.setMotors(MotorConstants.notDrive);
    }
}
