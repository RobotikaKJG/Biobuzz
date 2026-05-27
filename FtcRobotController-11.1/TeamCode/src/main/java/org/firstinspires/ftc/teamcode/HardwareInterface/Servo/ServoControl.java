package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoControl {

    private final HardwareMap hardwareMap;
    private Servo[] servos;
//    private CRServo[] crservos;
//    private AnalogInput[] analog;

    public ServoControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getServos();
    }

    private void getServos() {
//        crservos = new CRServo[]{
//                hardwareMap.get(CRServo.class, "turretServo")
//        };
        servos = new Servo[]{
                hardwareMap.get(Servo.class, "lockServo"),
                hardwareMap.get(Servo.class, "turretServo1"),
                hardwareMap.get(Servo.class, "turretServo2"),
                hardwareMap.get(Servo.class, "turretServo3")
        };
//        analog = new AnalogInput[]{
//                hardwareMap.get(AnalogInput.class, "turretAnalog")
//
//        };
    }

    public void setServoStartPos() {
        servos[ServoConstants.lockServo].setPosition(IntakeConstants.lockServoMinPos);

    }

    public void setServoPos(int index, double position) {
        if (isInBounds(index, position))
            servos[index].setPosition(position);
    }

    public double getServoPos(int index) {
        return servos[index].getPosition();
    }

    public void setTurretServosPos(double position) {
        servos[ServoConstants.turretServo1].setPosition(position * OuttakeConstants.turretServo1Mult);
        servos[ServoConstants.turretServo2].setPosition(position * OuttakeConstants.turretServo2Mult);
        servos[ServoConstants.turretServo3].setPosition(position * OuttakeConstants.turretServo3Mult);
    }

//    public void setServoSpeed(int index, double speed) {
//        crservos[index].setPower(speed);
//    }

    private boolean isInBounds(int index, double position) {
        return position >= ServoConstants.servoMinPos[index] && position <= ServoConstants.servoMaxPos[index];
    }

//    public double getCRSPos(int index) {
//        return analog[index].getVoltage() / analog[index].getMaxVoltage();
//    }

//    public double getCRSDegrees(int index) {
//        return getCRSPos(index) * 360.0;
//    }
}