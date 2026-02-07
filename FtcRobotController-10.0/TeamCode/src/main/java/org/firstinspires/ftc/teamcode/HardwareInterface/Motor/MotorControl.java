package org.firstinspires.ftc.teamcode.HardwareInterface.Motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class MotorControl {

    private static class MotorNames {
        public static final String frontLeft = "frontLeftMotor";
        public static final String backLeft = "backLeftMotor";
        public static final String frontRight = "frontRightMotor";
        public static final String backRight = "backRightMotor";
        public static final String outtake1 = "outtake1Motor";
        public static final String intake = "intakeMotor";
        public static final String transfer = "transferMotor";
        public static final String outtake2 = "outtake2Motor";
    }

    private final HardwareMap hardwareMap;
    private DcMotorEx[] motors;
    private final Utilities utilities = new Utilities();


    private final double[] motorSpeeds = new double[8];

    public MotorControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getMotors();
    }

    private void getMotors() {
        motors = new DcMotorEx[]{
                hardwareMap.get(DcMotorEx.class, MotorNames.frontLeft),
                hardwareMap.get(DcMotorEx.class, MotorNames.backLeft),
                hardwareMap.get(DcMotorEx.class, MotorNames.frontRight),
                hardwareMap.get(DcMotorEx.class, MotorNames.backRight),
                hardwareMap.get(DcMotorEx.class, MotorNames.outtake1),
                hardwareMap.get(DcMotorEx.class, MotorNames.intake),
                hardwareMap.get(DcMotorEx.class, MotorNames.transfer),
                hardwareMap.get(DcMotorEx.class, MotorNames.outtake2),
        };

        setMotorProperties();
    }

    private void setMotorProperties() {
        motors[MotorConstants.frontLeft].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[MotorConstants.backLeft].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[MotorConstants.feeder].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[MotorConstants.outtake1].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[MotorConstants.outtake2].setDirection(DcMotorSimple.Direction.REVERSE);
        setZeroPowerBehavior(MotorConstants.all, DcMotor.ZeroPowerBehavior.BRAKE);
        setZeroPowerBehavior(MotorConstants.outtake1, DcMotor.ZeroPowerBehavior.FLOAT);
        setMotorMode(MotorConstants.all, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(MotorConstants.all, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        setMotorMode(MotorConstants.outtake1, DcMotor.RunMode.RUN_USING_ENCODER);
        setMotorMode(MotorConstants.outtake2, DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setZeroPowerBehavior(int index, DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motors[Utilities.motorIndex(index, i)].setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setMotorSpeed(int index, double speed) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motorSpeeds[Utilities.motorIndex(index, i)] = speed;
    }

    public void addMotorSpeed(int index, double speed) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motorSpeeds[Utilities.motorIndex(index, i)] += speed;
    }

    public void multiplyMotorSpeed(int index, double multiplier) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motorSpeeds[Utilities.motorIndex(index, i)] *= multiplier;
    }

    public void divideMotorSpeed(int index, double divisor) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motorSpeeds[Utilities.motorIndex(index, i)] /= divisor;
    }

    public void setMotors(int index) {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motors[Utilities.motorIndex(index, i)].setPower(motorSpeeds[Utilities.motorIndex(index, i)]);
    }

    public void setMotorMode(int index, DcMotor.RunMode mode)
    {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motors[Utilities.motorIndex(index, i)].setMode(mode);
    }

    public void setMotorPos(int index, int position){
        for (int i = 0; i < Utilities.configLength(index); i++)
            motors[Utilities.motorIndex(index, i)].setTargetPosition(position);
    }

    public double getMotorCurrent(int index)
    {
        return motors[Utilities.motorIndex(index, 0)].getCurrent(CurrentUnit.AMPS);
    }

    public void setMotorCurrentAlert(int index, double current)
    {
        for (int i = 0; i < Utilities.configLength(index); i++)
            motors[Utilities.motorIndex(index, i)].setCurrentAlert(current, CurrentUnit.AMPS);
    }

    public boolean isOverCurrent(int index)
    {
        boolean overCurrent = false;
        for (int i = 0; i < Utilities.configLength(index); i++)
            if(motors[Utilities.motorIndex(index, i)].isOverCurrent())
                overCurrent = true;
        return overCurrent;
    }

    /**
     * @noinspection unused
     */
    public void resetMotors() {
        for (DcMotor i : motors)
            i.setPower(0);
    }

    public int getMotorPosition(int index) {
        return Utilities.getMotorPosition(motors, index);
    }

    public double getMotorVelocity(int index) {
        return (motors[Utilities.motorIndex(index, 0)].getVelocity());
    }

    public void resetMotorEncoders(int index) {
        utilities.resetMotorEncoders(motors, index);
    }

    private double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }

    public void setMotorRPM(int index, double velocityTicksPerSecond) {
        for (int i = 0; i < Utilities.configLength(index); i++) {
            DcMotorEx motor = motors[Utilities.motorIndex(index, i)];
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            PIDFCoefficients pidf = new PIDFCoefficients(
                    0.02,   // P ↓ (lower to stop overshoot)
                    0.0,     // I off
                    0.03,   // D ↑ (more damping)
                    15.0     // F unchanged
            );

            motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
            motor.setVelocity(velocityTicksPerSecond);
        }
    }

}
