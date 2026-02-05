package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoControl {

    private final HardwareMap hardwareMap;
    private Servo[] servos;
    private CRServo[] crservos;
    private AnalogInput[] analog;

    public ServoControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getServos();
    }

    private void getServos() {
        crservos = new CRServo[]{
                hardwareMap.get(CRServo.class, "turretServo")
        };
        servos = new Servo[]{
                hardwareMap.get(Servo.class, "outtakeServo")
        };
        analog = new AnalogInput[]{
                hardwareMap.get(AnalogInput.class, "turretAnalog")

        };
    }

    public void setServoStartPos() {
        for (int i = 0; i< crservos.length; i++) {
            crservos[i].setDirection(DcMotorSimple.Direction.FORWARD);
        }

    }

    public void setServoPos(int index, double position) {
        if (isInBounds(index, position))
            servos[index].setPosition(position);
    }

    public void setServoSpeed(int index, double speed) {
        crservos[index].setPower(speed);
    }

    private boolean isInBounds(int index, double position) {
        return position >= ServoConstants.servoMinPos[index] && position <= ServoConstants.servoMaxPos[index];
    }

    public double getCRSPos(int index) {
        return analog[index].getVoltage() / analog[index].getMaxVoltage();
    }

    public double getCRSDegrees(int index) {
        return getCRSPos(index) * 360.0;
    }
}