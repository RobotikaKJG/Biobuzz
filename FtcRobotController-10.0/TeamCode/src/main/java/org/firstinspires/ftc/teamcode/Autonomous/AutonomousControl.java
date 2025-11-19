package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.Autonomous.Autos.AudienceAuton;
import org.firstinspires.ftc.teamcode.Autonomous.Autos.GoalAuton;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
//import org.firstinspires.ftc.teamcode.Subsystems.Intake.Slides.IntakeSlideControl;


/**
 * This class runs the loop of the autonomous period
 */
public class AutonomousControl {

    private final MotorControl motorControl;
    private final GoalAuton goalAuton;
    private final AudienceAuton audienceAuton;
    private final IntakeControl intakeControl;
    private final OuttakeControl outtakeControl;
    private final SensorControl sensorControl;


    public AutonomousControl(MotorControl motorControl, GoalAuton goalAuton, AudienceAuton audienceAuton, IntakeControl intakeControl, OuttakeControl outtakeControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.goalAuton = goalAuton;
        this.audienceAuton = audienceAuton;
        this.intakeControl = intakeControl;
        this.outtakeControl = outtakeControl;
        this.sensorControl = sensorControl;
        IntakeStates.setInitialStates();
        OuttakeStates.setInitialStates();
        ButtonStates.setInitialStates();

        sensorControl.initLimelight(0);
    }

    public void startAutonomous() {
        switch (GlobalVariables.autonomousMode) {
            case audienceSide:
                audienceAuton.start();
                break;
            case goalSide:
                goalAuton.start();
        }
    }

    public void runAutonomous() {
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
//        sensorControl.updateDistance();
//        sensorControl.updateColor();
//        outtakeSlideControl.updateSlidePosition();
    }
}
