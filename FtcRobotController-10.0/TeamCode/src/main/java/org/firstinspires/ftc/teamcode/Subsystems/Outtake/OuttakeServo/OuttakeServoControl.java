package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeServoControl {
    private final ServoControl servoControl;
    private final SensorControl sensorControl;
    private OuttakeServoStates prevServoStates = OuttakeServoStates.idle;



    private double currentWait = 0;

    public OuttakeServoControl(ServoControl servoControl, SensorControl sensorControl) {
        this.servoControl = servoControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if(OuttakeStates.getOuttakeServoState() != prevServoStates) {
            updateStates();
            prevServoStates = OuttakeStates.getOuttakeServoState();
        } else if (OuttakeStates.getOuttakeServoState() == OuttakeServoStates.adjust) {
            updateStates();
        }
    }

    public void updateStates() {
        switch (OuttakeStates.getOuttakeServoState()) {
            case adjust:
                break;
            case idle:
                break;
        }

    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
