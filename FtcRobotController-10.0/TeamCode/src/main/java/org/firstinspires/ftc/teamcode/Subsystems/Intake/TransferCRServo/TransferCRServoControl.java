package org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferCRServo;

import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Servo.ServoControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class TransferCRServoControl {
    private final ServoControl servoControl;
    private IntakeMotorStates prevMotorStates = IntakeMotorStates.idle;

    private double startPos = 0.0;
    private double currentTarget = startPos;
    private int stepIndex = 0;

    private double kP = 1.567;
    private double tolerance = 0.02;

    private boolean targetCalculated = false;
    private TransferCRServoStates prevState = TransferCRServoStates.idle;

    public TransferCRServoControl(ServoControl servoControl) {
        this.servoControl = servoControl;
    }

    public void update() {
        if (IntakeStates.getIntakeMotorState() != prevMotorStates) {
            updateStates();
            prevMotorStates = IntakeStates.getIntakeMotorState();
        } else if (
                IntakeStates.getTransferCRServoState() == TransferCRServoStates.turnIntake ||
                        IntakeStates.getTransferCRServoState() == TransferCRServoStates.turnOuttake
        ) {
            updateStates();
        }
    }

    public void updateStates() {
        switch (IntakeStates.getTransferCRServoState()) {
            case turnIntake:
                startPos = 0.33;
                if (!targetCalculated) {
                    nextTarget(); // calculate before turning
                    targetCalculated = true;
                }
                handleTurn();
                break;

            case turnOuttake:
                startPos = 0.165;
                if (!targetCalculated) {
                    nextTarget(); // calculate before turning
                    targetCalculated = true;
                }
                handleTurn();
                break;

            case idle:
            case turnedIntake:
            case turnedOuttake:
                servoControl.setServoSpeed(ServoConstants.transferCRServo, 0);
                break;
        }
    }

    private void handleTurn() {
        double currentPos = servoControl.getCRSPos(0);
        double error = currentTarget - currentPos;

        // wrap-around logic for continuous feedback
        if (Math.abs(error) > 0.5) {
            error = -Math.copySign(1.0 - Math.abs(error), error);
        }

        double power = -kP * error;
        power = Math.copySign(Math.min(0.6, Math.max(0.076, Math.abs(power))), power);

        System.out.println("Pos: " + currentPos + "  Target: " + currentTarget +
                "  Err: " + error + "  Pwr: " + power);

        if (Math.abs(error) < tolerance) {
            servoControl.setServoSpeed(0, 0);
            prevState = IntakeStates.getTransferCRServoState();

            switch(IntakeStates.getTransferCRServoState()) {
                case turnIntake:
                    IntakeStates.setTransferCRServoState(TransferCRServoStates.turnedIntake);
                    break;
                case turnOuttake:
                    IntakeStates.setTransferCRServoState(TransferCRServoStates.turnedOuttake);
                    break;
            }

            targetCalculated = false;
            System.out.println("STOP");
        } else {
            servoControl.setServoSpeed(ServoConstants.transferCRServo, power);
        }
    }

    private void nextTarget() {
        if(prevState == IntakeStates.getTransferCRServoState())
            stepIndex = (stepIndex + 1) % 3;
        currentTarget = startPos + 0.33 * stepIndex;// * (1.0 - startPos);

        if (currentTarget > 1.0) {
            currentTarget = startPos;
            stepIndex = 0;
        }

        System.out.println("Next Target: " + currentTarget);
    }
}
