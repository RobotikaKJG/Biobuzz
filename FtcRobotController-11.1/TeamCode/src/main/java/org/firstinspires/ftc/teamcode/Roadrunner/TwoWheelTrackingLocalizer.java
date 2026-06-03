package org.firstinspires.ftc.teamcode.Roadrunner;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.FTCDashboard.Encoder;
import org.firstinspires.ftc.teamcode.Main.GoBildaPinpointDriver;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    ^
 *    |
 *    | ( x direction)
 *    |
 *    v
 *    <----( y direction )---->

 *        (forward)
 *    /--------------\
 *    |     ____     |
 *    |     ----     |    <- Perpendicular Wheel
 *    |           || |
 *    |           || |    <- Parallel Wheel
 *    |              |
 *    |              |
 *    \--------------/
 *
 */
@Config
public class TwoWheelTrackingLocalizer extends TwoTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 2000;
    public static double WHEEL_RADIUS = 0.629921; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed
    //AUTONOTE change these if too bad (check with localizationtest)T
    public static double PARALLEL_X = 0; // X is the up and down direction
    public static double PARALLEL_Y = -2; // Y is the strafe direction

    public static double PERPENDICULAR_X = 6;
    public static double PERPENDICULAR_Y = 0;

    // Parallel/Perpendicular to the forward axis
    // Parallel wheel is parallel to the forward axis
    // Perpendicular is perpendicular to the forward axis
//    private final Encoder parallelEncoder;
//    private final Encoder perpendicularEncoder;

    private final GoBildaPinpointDriver imu;

    /** When true, imu.update() is handled externally (e.g. by IterativeController in TeleOp). */
    private boolean externalUpdateEnabled = false;

    public void setExternalUpdateEnabled(boolean enabled) {
        this.externalUpdateEnabled = enabled;
    }

    public TwoWheelTrackingLocalizer(HardwareMap hardwareMap, GoBildaPinpointDriver imu) {
        super(Arrays.asList(
                new Pose2d(PARALLEL_X, PARALLEL_Y, 0),
                new Pose2d(PERPENDICULAR_X, PERPENDICULAR_Y, Math.toRadians(90))
        ));

        this.imu = imu;

//        parallelEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "frontRightMotor"));
//        perpendicularEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "intakeMotor"));

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
//        parallelEncoder.setDirection(Encoder.Direction.REVERSE);
//        perpendicularEncoder.setDirection(Encoder.Direction.REVERSE);

        this.imu.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);
    }

    public static double encoderTicksToInches(double ticks) {
//    return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
        return 0.0030065505 * ticks;
    }

    @Override
    public double getHeading() {
        return imu.getHeading();
    }

    @Override
    public Double getHeadingVelocity() {
        return imu.getHeadingVelocity();
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        // In TeleOp, updatePinpoint() is called before this — skip redundant read.
        // In Autonomous, this is the only call site, so we must update here.
        if (!externalUpdateEnabled) {
            imu.update();
        }
        // Must return a NEW list each call — Road Runner stores a reference to the previous
        // list for delta computation. Returning the same mutable list zeroes out all deltas.
        return Arrays.asList(
                encoderTicksToInches(-imu.getEncoderX()),
                encoderTicksToInches(-imu.getEncoderY())
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        return Arrays.asList(
                encoderTicksToInches(imu.getVelX()),
                encoderTicksToInches(-imu.getVelY())
        );
    }
}