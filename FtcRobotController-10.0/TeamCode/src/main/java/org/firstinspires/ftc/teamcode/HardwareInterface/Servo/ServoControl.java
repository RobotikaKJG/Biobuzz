package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Shared servo hardware adapter, built by Dependencies and injected into mechanism controllers.
 * The empty configuration requires no servos. Add names and bounds in ServoConstants before use.
 * Unlike staged motor commands, servo commands are applied immediately.
 */
public class ServoControl {
    private final Servo[] servos;
    private final CRServo[] crServos;

    public ServoControl(HardwareMap hardwareMap) {
        if (ServoConstants.SERVO_NAMES.length != ServoConstants.servoMinPos.length
                || ServoConstants.SERVO_NAMES.length != ServoConstants.servoMaxPos.length) {
            throw new IllegalArgumentException("Each servo needs a name, minimum and maximum");
        }
        servos = new Servo[ServoConstants.SERVO_NAMES.length];
        crServos = new CRServo[ServoConstants.CR_SERVO_NAMES.length];
        for (int i = 0; i < servos.length; i++) {
            servos[i] = hardwareMap.get(Servo.class, ServoConstants.SERVO_NAMES[i]);
        }
        for (int i = 0; i < crServos.length; i++) {
            crServos[i] = hardwareMap.get(CRServo.class, ServoConstants.CR_SERVO_NAMES[i]);
            crServos[i].setPower(0);
        }
    }

    /** Called after START. Add only positions calibrated for the new mechanism. */
    public void setServoStartPos() {
        // No positional servo is commanded by the starter.
    }

    public void setServoPos(int index, double position) {
        if (!Double.isFinite(position) || position < ServoConstants.servoMinPos[index]
                || position > ServoConstants.servoMaxPos[index]) {
            throw new IllegalArgumentException("Servo position outside calibrated bounds");
        }
        servos[index].setPosition(position);
    }

    public void setServoSpeed(int index, double speed) {
        crServos[index].setPower(speed);
    }

    /** Stop continuous rotation; positional servos retain their last commanded position. */
    public void stop() {
        for (CRServo servo : crServos) servo.setPower(0);
    }
}
