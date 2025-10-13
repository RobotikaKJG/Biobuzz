package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoControl {

    private final HardwareMap hardwareMap;
    private Servo[] servos;
    private CRServo[] crservos;

    public ServoControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getServos();
    }

    private void getServos() {
        crservos = new CRServo[]{
                hardwareMap.get(CRServo.class, "transferServo")
        };
        servos = new Servo[]{
                hardwareMap.get(Servo.class, "outtakeServo")
        };
    }

    public void setServoStartPos() {
        setServoPos(ServoConstants.outtakeServo, OuttakeConstants.outtakeDownServoMaxPos);
    }

    public void setServoPos(int index, double position) {
        if (isInBounds(index, position))
            servos[index].setPosition(position);
    }

    private boolean isInBounds(int index, double position) {
        return position >= ServoConstants.servoMinPos[index] && position <= ServoConstants.servoMaxPos[index];
    }
}