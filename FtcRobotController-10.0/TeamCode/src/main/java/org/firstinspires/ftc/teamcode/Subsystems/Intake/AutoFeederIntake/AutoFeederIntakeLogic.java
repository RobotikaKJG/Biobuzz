package org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoFeederIntake;

import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;

public class AutoFeederIntakeLogic {
    private double currentWait = 0;

    public void update() {
        switch (IntakeStates.getAutoFeederIntakeState()) {
            case stop:
                activate();
            case stopIntake:
                stopIntake();
                break;
            case stopFeeder:
                stopFeeder();
                break;
            case idle:
                break;
        }
    }

    private void activate() {
        IntakeStates.setAutoFeederIntakeState(AutoFeederIntakeStates.stopIntake);
        addWaitTime(IntakeConstants.stopFeederAfter);
    }

    private void stopIntake() {
        if(currentWait > getSeconds()) return;
        IntakeStates.setAutoFeederIntakeState(AutoFeederIntakeStates.stopFeeder);
    }

    private void stopFeeder() {
        IntakeStates.setAutoFeederIntakeState(AutoFeederIntakeStates.idle);
    }

    private void addWaitTime(double waitTime) {
        currentWait = getSeconds() + waitTime;
    }

    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }
}
