package org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
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
        } else if (OuttakeStates.getMotorState() == OuttakeMotorStates.forwardClose || OuttakeStates.getMotorState() == OuttakeMotorStates.forwardStart || OuttakeStates.getMotorState() == OuttakeMotorStates.forwardFar) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getMotorState()) {
            case autonomous:
                motorControl.setMotorRPM(MotorConstants.outtake, OuttakeConstants.outtakeSpeedFar - 100);
                break;
            case forwardStart:
                motorControl.setMotorRPM(MotorConstants.outtake, 1670 + GlobalVariables.rpmOffset);
                break;
            case forwardFar:
                motorControl.setMotorRPM(MotorConstants.outtake, OuttakeConstants.outtakeSpeedFar + GlobalVariables.rpmOffset);
                break;
            case forwardClose:
                calculateSpeed();
                motorControl.setMotorRPM(MotorConstants.outtake, 1500 + GlobalVariables.rpmOffset);
                break;
            case backward:
                motorControl.setMotorSpeed(MotorConstants.outtake, -0.5);
                break;
            case idle:
                motorControl.setMotorRPM(MotorConstants.outtake, 0);
                break;
        }
    }

    private void calculateSpeed() {
        double distance = sensorControl.getDistanceFromLocalizer();

        // If distance is still invalid (shouldn't happen with Localizer fallback), keep previous
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