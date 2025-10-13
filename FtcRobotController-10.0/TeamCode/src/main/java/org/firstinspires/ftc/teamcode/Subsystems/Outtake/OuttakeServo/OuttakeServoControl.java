package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo;

import com.qualcomm.hardware.limelightvision.LLResult;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeServoControl {
    private OuttakeServoStates prevOuttakeServoState;
    private ServoControl servoControl;
    private SensorControl sensorControl;
    private double max = OuttakeConstants.outtakeServoMaxPos;
    private double min = OuttakeConstants.outtakeServoMinPos;
    private double maxD = OuttakeConstants.maxDistance;
    private double minD = OuttakeConstants.minDistance;

    public OuttakeServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if (prevOuttakeServoState != OuttakeStates.getOuttakeServoState()) {
            updateStates();
            prevOuttakeServoState = OuttakeStates.getOuttakeServoState();
        }
    }

    private void updateStates() {
        switch (OuttakeStates.getOuttakeServoState()) {
            case setPosAuto:
                double distance = sensorControl.getTagDistance();
                double servoPos = Math.min(max, Math.max(min, min + (distance - minD) * (max - min) / (maxD - minD)));

                System.out.println(Math.min(max, Math.max(min, min + (distance - minD) * (max - min) / (maxD - minD))));

                servoControl.setServoPos(ServoConstants.outtakeServo, servoPos);
                break;
            case idle:
                break;
        }
    }
}
