package org.firstinspires.ftc.teamcode.HardwareInterface.Motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * The only drive-motor owner during TeleOp. Dependencies creates one shared instance.
 * setMotorSpeed/add/multiply/divide stage commands; setMotors writes them to the REV Hub.
 * IterativeController flushes once AFTER drive and subsystem updates, avoiding a one-loop delay.
 * Road Runner owns drive outputs during autonomous; flush only notDrive in that mode.
 */
public class MotorControl {

    private final HardwareMap hardwareMap;
    private DcMotorEx[] motors;
    private final Utilities utilities = new Utilities();


    private final double[] motorSpeeds = new double[MotorConstants.MOTOR_COUNT];

    public MotorControl(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        getMotors();
    }

    private void getMotors() {
        motors = new DcMotorEx[]{
                hardwareMap.get(DcMotorEx.class, MotorConstants.FRONT_LEFT_NAME),
                hardwareMap.get(DcMotorEx.class, MotorConstants.BACK_LEFT_NAME),
                hardwareMap.get(DcMotorEx.class, MotorConstants.FRONT_RIGHT_NAME),
                hardwareMap.get(DcMotorEx.class, MotorConstants.BACK_RIGHT_NAME),
        };

        setMotorProperties();
    }

    private void setMotorProperties() {
        // Explicitly set every direction so a previous OpMode cannot leave stale settings behind.
        motors[MotorConstants.frontLeft].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[MotorConstants.backRight].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[MotorConstants.frontRight].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[MotorConstants.backLeft].setDirection(DcMotorSimple.Direction.REVERSE);
        resetMotors();
        setZeroPowerBehavior(MotorConstants.all, DcMotor.ZeroPowerBehavior.BRAKE);
        setMotorMode(MotorConstants.all, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        if (divisor == 0) throw new IllegalArgumentException("Motor speed divisor cannot be zero");
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

    private double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return Double.isInfinite(result) ? 12.0 : result;
    }

    private double compensateForVoltage(double desiredPower) {
        double voltage = getBatteryVoltage();
        double nominalVoltage = 12.0; // or 13.0 depending on your tuning
        double compensated = desiredPower * (nominalVoltage / voltage);
        return Math.max(-1.0, Math.min(1.0, compensated)); // clip to [-1, 1]
    }


    /** Stage voltage-compensated power; the normal end-of-loop flush applies it. */
    public void setMotorSpeedVoltage(int index, double power){
        for (int i = 0; i < Utilities.configLength(index); i++)
            motorSpeeds[Utilities.motorIndex(index, i)] = compensateForVoltage(power);
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
        java.util.Arrays.fill(motorSpeeds, 0);
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
}
