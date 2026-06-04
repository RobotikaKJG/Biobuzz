package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Main.LoopTimeLogger;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;

public class ServoControl {

    private final HardwareMap hardwareMap;
    private Servo[] servos;
    private LoopTimeLogger loopTimeLogger;
//    private CRServo[] crservos;
//    private AnalogInput[] analog;

    public ServoControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getServos();
    }

    public void setLoopTimeLogger(LoopTimeLogger loopTimeLogger) {
        this.loopTimeLogger = loopTimeLogger;
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
        long startNs = System.nanoTime();
        servos[ServoConstants.lockServo].setPosition(IntakeConstants.lockServoMinPos);
        recordHardwareDuration("servo.setPosition." + servoName(ServoConstants.lockServo), startNs);

    }

    public void setServoPos(int index, double position) {
        if (isInBounds(index, position)) {
            long startNs = System.nanoTime();
            servos[index].setPosition(position);
            recordHardwareDuration("servo.setPosition." + servoName(index), startNs);
        }
    }

    public double getServoPos(int index) {
        long startNs = System.nanoTime();
        double position = servos[index].getPosition();
        recordHardwareDuration("servo.getPosition." + servoName(index), startNs);
        return position;
    }

    public void setTurretServosPos(double position) {
        long startNs = System.nanoTime();
        servos[ServoConstants.turretServo1].setPosition(position * OuttakeConstants.turretServo1Mult);
        recordHardwareDuration("servo.setPosition." + servoName(ServoConstants.turretServo1), startNs);

        startNs = System.nanoTime();
        servos[ServoConstants.turretServo2].setPosition(position * OuttakeConstants.turretServo2Mult);
        recordHardwareDuration("servo.setPosition." + servoName(ServoConstants.turretServo2), startNs);

        startNs = System.nanoTime();
        servos[ServoConstants.turretServo3].setPosition(position * OuttakeConstants.turretServo3Mult);
        recordHardwareDuration("servo.setPosition." + servoName(ServoConstants.turretServo3), startNs);
    }

//    public void setServoSpeed(int index, double speed) {
//        crservos[index].setPower(speed);
//    }

    private boolean isInBounds(int index, double position) {
        return position >= ServoConstants.servoMinPos[index] && position <= ServoConstants.servoMaxPos[index];
    }

    private void recordHardwareDuration(String name, long startNs) {
        if (loopTimeLogger != null) {
            loopTimeLogger.recordDurationNs(name, System.nanoTime() - startNs);
        }
    }

    private String servoName(int index) {
        switch (index) {
            case ServoConstants.lockServo:
                return "lockServo";
            case ServoConstants.turretServo1:
                return "turretServo1";
            case ServoConstants.turretServo2:
                return "turretServo2";
            case ServoConstants.turretServo3:
                return "turretServo3";
            default:
                return "unknownServo" + index;
        }
    }

//    public double getCRSPos(int index) {
//        return analog[index].getVoltage() / analog[index].getMaxVoltage();
//    }

//    public double getCRSDegrees(int index) {
//        return getCRSPos(index) * 360.0;
//    }
}
