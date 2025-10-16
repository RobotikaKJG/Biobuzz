package org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class TransferCRServoControl {
    private final ServoControl servoControl;
    private IntakeMotorStates prevMotorStates = IntakeMotorStates.idle;

    private double startPos = 0;        // settable start position
    private double currentTarget = startPos;
    public int stepIndex = 0;            // 0, 1, 2
    private double kP = 0.767;              // proportional gain for smooth motion
    private double tolerance = 0.02;     // stop threshold

    public TransferCRServoControl(ServoControl servoControl) {
        this.servoControl = servoControl;
    }

    public void update() {
        if (IntakeStates.getIntakeMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = IntakeStates.getIntakeMotorState();
        }
        else if (IntakeStates.getTransferCRServoState() == TransferCRServoStates.turn) {
            updateStates();
        }

    }

    public void updateStates() {
        switch (IntakeStates.getTransferCRServoState()) {
            case turn:
                handleTurn();
                break;
            case idle:
                servoControl.setServoSpeed(0, 0);
                break;
        }

    }

    private void handleTurn() {
        double currentPos = servoControl.getCRSPos(0);
        double error = currentTarget - currentPos;

        if (Math.abs(error) > 0.5) {
            error = -Math.copySign(1.0 - Math.abs(error), error);
        }

        double power = -kP * error;

        power = Math.copySign(Math.min(0.4, Math.max(0.067, Math.abs(power))), power);

        System.out.println("Pos: " + currentPos + "  Target: " + currentTarget + "  Err: " + error + "  Pwr: " + power);

        if (Math.abs(error) < tolerance) {
            servoControl.setServoSpeed(0, 0);
            nextTarget();
            IntakeStates.setTransferCRServoState(TransferCRServoStates.idle);
            System.out.println("STOP");
        } else {
            servoControl.setServoSpeed(0, power);
        }
    }

    private void nextTarget() {
        stepIndex = (stepIndex + 1) % 3;
        currentTarget = startPos + 0.33 * stepIndex * (1.0 - startPos);

        if (currentTarget > 1.0) {
            currentTarget = startPos;
            stepIndex = 0;
        }
    }
}
