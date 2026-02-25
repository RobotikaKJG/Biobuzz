package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose.AutoOuttakeFarCloseStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class OuttakeMotorControl {
    private final MotorControl motorControl;
    private final SensorControl sensorControl;
    private OuttakeMotorStates prevMotorStates = OuttakeMotorStates.idle;
    private double currentWait = 0;
    private double minD = OuttakeConstants.minDistance;
    private double maxD = OuttakeConstants.maxDistance;

    private double speed;
    private double prevspeed = OuttakeConstants.outtakeSpeedCloseClose;

    public OuttakeMotorControl(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;
    }

    public void update() {
        if(OuttakeStates.getMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = OuttakeStates.getMotorState();
        } else if (OuttakeStates.getMotorState() == OuttakeMotorStates.forwardClose) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getMotorState()) {
            case forwardFar:
                motorControl.setMotorSpeed(MotorConstants.outtake1, OuttakeConstants.outtakeSpeedFar);
                motorControl.setMotorSpeed(MotorConstants.outtake2, OuttakeConstants.outtakeSpeedFar);
                break;
            case forwardClose:
//                calculateSpeed();
                motorControl.setMotorSpeed(MotorConstants.outtake1, OuttakeConstants.outtakeSpeedCloseFar);
                motorControl.setMotorSpeed(MotorConstants.outtake2, OuttakeConstants.outtakeSpeedCloseFar);

                motorControl.setMotorSpeed(MotorConstants.outtake1, speed);
                motorControl.setMotorSpeed(MotorConstants.outtake2, speed);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.outtake1, -0.5);
                motorControl.setMotorSpeed(MotorConstants.outtake2, -0.5);
                break;
            case idle:
                motorControl.setMotorSpeed(MotorConstants.outtake1, 0);
                motorControl.setMotorSpeed(MotorConstants.outtake2, 0);
                motorControl.setMotors(MotorConstants.outtake1);
                motorControl.setMotors(MotorConstants.outtake2);
                break;
        }
    }

    private void calculateSpeed() {
//        double distance = sensorControl.getTagDistance();
        double distance = GlobalVariables.distanceToTarget/1000;

        // If no valid tag detected, default to closeClose speed
        if (distance < 0) {
            speed = prevspeed;
        }
        // Closer than minimum → use closeClose speed
        else if (distance <= minD) {
            speed = OuttakeConstants.outtakeSpeedCloseClose;
        }
        // Farther than maximum → use closeFar speed
        else if (distance >= maxD) {
            speed = OuttakeConstants.outtakeSpeedCloseFar;
        }
        // Between min and max → interpolate
        else {
            double t = (distance - minD) / (maxD - minD); // 0 → 1
            speed = OuttakeConstants.outtakeSpeedCloseClose +
                    t * (OuttakeConstants.outtakeSpeedCloseFar -
                            OuttakeConstants.outtakeSpeedCloseClose);
        }
        GlobalVariables.outtakeTargetSpeed = speed;
        prevspeed = speed;
    }
}