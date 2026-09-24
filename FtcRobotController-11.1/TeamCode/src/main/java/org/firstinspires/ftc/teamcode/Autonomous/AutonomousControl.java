package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton.AudienceAuton;
import org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton.GoalAuton;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;


public class AutonomousControl {
    private final MotorControl motorControl;
    private final GoalAuton goalAuton;
    private final AudienceAuton audienceAuton;
    private final IntakeControl intakeControl;
    private final OuttakeControl outtakeControl;
    private final SensorControl sensorControl;


    public AutonomousControl(MotorControl motorControl, GoalAuton goalAuton, AudienceAuton audienceAuton, IntakeControl intakeControl, OuttakeControl outtakeControl,  SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.goalAuton = goalAuton;
        this.audienceAuton = audienceAuton;
        this.intakeControl = intakeControl;
        this.outtakeControl = outtakeControl;
        this.sensorControl = sensorControl;
        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();
    }

    public void startAutonomous() {
        switch (GlobalVariables.autonomousMode) {
            case audienceSide:
                audienceAuton.start();
                break;
            case goalSide:
                goalAuton.start();
                break;
        }
    }

    public void runAutonomous() {
        sensorControl.updateLocalizer();
        switch (GlobalVariables.autonomousMode) {
            case audienceSide:
                audienceAuton.run();
                break;
            case goalSide:
                goalAuton.run();
                break;
        }
        updateSubsystems();
        motorControl.setMotors(MotorConstants.notDrive); // drive motors are controlled by roadrunner
    }

    public void updateSubsystems(){
        intakeControl.update();
        outtakeControl.update();
    }
}
