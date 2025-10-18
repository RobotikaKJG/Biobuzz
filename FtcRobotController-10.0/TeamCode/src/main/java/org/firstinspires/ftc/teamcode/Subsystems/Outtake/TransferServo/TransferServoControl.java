package org.firstinspires.ftc.teamcode.Subsystems.Outtake.TransferServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class TransferServoControl {
    private final ServoControl servoControl;
    private TransferServoStates prevTransferServoStates = TransferServoStates.down;

    public TransferServoControl(ServoControl servoControl) {
        this.servoControl = servoControl;
    }

    public void update() {
        if(OuttakeStates.getTransferServoState() != prevTransferServoStates) {
            updateStates();
            prevTransferServoStates = OuttakeStates.getTransferServoState();
        }

    }

    public void updateStates() {
        switch (OuttakeStates.getTransferServoState()) {
            case up:
                servoControl.setServoPos(ServoConstants.transferServo, OuttakeConstants.transferServoMaxPos);
                break;
            case down:
                servoControl.setServoPos(ServoConstants.transferServo, OuttakeConstants.transferServoMinPos);
                break;
        }

    }
}
