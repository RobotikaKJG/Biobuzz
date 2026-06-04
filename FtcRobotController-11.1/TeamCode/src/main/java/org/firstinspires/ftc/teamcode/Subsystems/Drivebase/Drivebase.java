package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;


public class Drivebase {

    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final Gamepad currentGamepad = new Gamepad();
    private final MotorControl motorControl;
    private final SensorControl sensorControl;
    private boolean isDriverOriented = true;

    public Drivebase(Gamepad gamepad1,Gamepad gamepad2, MotorControl motorControl, SensorControl sensorControl) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;
    }

    private void selectGamepad(){
        if(GlobalVariables.slowMode)
            currentGamepad.copy(gamepad2);
        else
            currentGamepad.copy(gamepad1);
    }

    public void gamepadDrive(double maxSpeed) {
        selectGamepad();
        double y = -currentGamepad.left_stick_y;// * yGain;
        double x = -currentGamepad.left_stick_x;// * xGain;
        double rotation = currentGamepad.right_stick_x;// * rotationGain;
        robotOrientedGamepadDrive(y, x, rotation, maxSpeed);
    }

    public void driverOrientedGamepadDrive(double maxSpeed) {
        selectGamepad();
        double y = currentGamepad.left_stick_y;
        double x = currentGamepad.left_stick_x;
        double rotation = currentGamepad.right_stick_x;
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
        double botHeading = sensorControl.getLocalizerAngle();
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
        isDriverOriented = !isDriverOriented;
    }

    public void drive(double maxSpeed) {
        if (isDriverOriented)
            driverOrientedGamepadDrive(maxSpeed);
        else
            gamepadDrive(maxSpeed);
    }
}