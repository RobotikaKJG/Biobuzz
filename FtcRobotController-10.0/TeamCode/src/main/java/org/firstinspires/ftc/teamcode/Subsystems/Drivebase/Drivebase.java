package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;


/**
 * Converts gamepad 1 sticks into staged mecanum wheel powers using the original drive signs.
 * DrivebaseController handles mode selection; MotorControl applies output at the end of the loop.
 * Robot-oriented driving needs only motors. Field-oriented mode additionally needs SensorControl
 * to be explicitly configured with a heading sensor. Slow mode changes speed, never driver ownership.
 */
public class Drivebase {

    private final Gamepad gamepad1;
    private final Gamepad currentGamepad = new Gamepad();
    private final MotorControl motorControl;
    private final SensorControl sensorControl;
    private boolean isDriverOriented = false;

    public Drivebase(Gamepad gamepad1,Gamepad gamepad2, MotorControl motorControl, SensorControl sensorControl) {
        this.gamepad1 = gamepad1;
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;
    }

    private void selectGamepad(){
        currentGamepad.copy(gamepad1);
    }

    public void gamepadDrive(double maxSpeed) {
        selectGamepad();
        double y = -currentGamepad.left_stick_y;
        double x = -currentGamepad.left_stick_x;
        double rotation = -currentGamepad.right_stick_x;
        robotOrientedGamepadDrive(y, x, rotation, maxSpeed);
    }

    public void driverOrientedGamepadDrive(double maxSpeed) {
        selectGamepad();
        double y = currentGamepad.left_stick_y;
        double x = currentGamepad.left_stick_x;
        double rotation = -currentGamepad.right_stick_x;
        driverOrientedGamepadDrive(y, x, rotation, maxSpeed);
    }

    private void robotOrientedGamepadDrive(double y, double x, double rotation, double maxSpeed) {

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rotation), 1);
        motorControl.setMotorSpeed(MotorConstants.allDrive, y);

        motorControl.addMotorSpeed(MotorConstants.leftDrive, rotation);
        motorControl.addMotorSpeed(MotorConstants.rightDrive, -rotation);

        motorControl.addMotorSpeed(MotorConstants.frontRightBackLeft, x);
        motorControl.addMotorSpeed(MotorConstants.frontLeftBackRight, -x);

        motorControl.divideMotorSpeed(MotorConstants.allDrive, denominator);
        motorControl.multiplyMotorSpeed(MotorConstants.allDrive, maxSpeed);
    }

    private void driverOrientedGamepadDrive(double y, double x, double rotation, double maxSpeed) {
        double botHeading = sensorControl.getPinpointAngle();
        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rotation), 1);

        motorControl.setMotorSpeed(MotorConstants.allDrive, -rotY);

        motorControl.addMotorSpeed(MotorConstants.leftDrive, rotation);
        motorControl.addMotorSpeed(MotorConstants.rightDrive, -rotation);

        motorControl.addMotorSpeed(MotorConstants.frontRightBackLeft, -rotX);
        motorControl.addMotorSpeed(MotorConstants.frontLeftBackRight, rotX);

        motorControl.divideMotorSpeed(MotorConstants.allDrive, denominator);
        motorControl.multiplyMotorSpeed(MotorConstants.allDrive, maxSpeed);
    }

    public void switchDrivingMode() {
        if (sensorControl.hasHeading()) isDriverOriented = !isDriverOriented;
    }

    public void drive(double maxSpeed) {
        if (isDriverOriented && sensorControl.hasHeading())
            driverOrientedGamepadDrive(maxSpeed);
        else
            gamepadDrive(maxSpeed);
    }
}