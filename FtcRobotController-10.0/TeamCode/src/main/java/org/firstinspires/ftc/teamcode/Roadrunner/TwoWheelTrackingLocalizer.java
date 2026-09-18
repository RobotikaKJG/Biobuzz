package org.firstinspires.ftc.teamcode.Roadrunner;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

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
    // Retained chassis calibration: verify these offsets if the odometry mounting changes.
    public static double PARALLEL_X = 0; // X is the up and down direction
    public static double PARALLEL_Y = -2.85; // Y is the strafe direction

    public static double PERPENDICULAR_X = 2.5;
    public static double PERPENDICULAR_Y = 0;

    // Parallel/Perpendicular to the forward axis
    // Parallel wheel is parallel to the forward axis
    // Perpendicular is perpendicular to the forward axis

    private final GoBildaPinpointDriver imu;

    public TwoWheelTrackingLocalizer(HardwareMap hardwareMap, GoBildaPinpointDriver imu) {
        super(Arrays.asList(
                new Pose2d(PARALLEL_X, PARALLEL_Y, 0),
                new Pose2d(PERPENDICULAR_X, PERPENDICULAR_Y, Math.toRadians(90))
        ));

        this.imu = imu;

        // Encoder measurements come from the Pinpoint directly, not REV motor ports.
        // No intake motor or separate encoder motor configuration is required here.

    }

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @Override
    public double getHeading() {
        return -imu.getHeading();
    }

    @Override
    public Double getHeadingVelocity() {
        return -imu.getHeadingVelocity();
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(-imu.getEncoderX()),
                encoderTicksToInches(-imu.getEncoderY())
        );
    }

    /**
     * The Pinpoint driver exposes robot velocity in mm/s, not raw tracking-wheel ticks/s.
     * Returning null lets Road Runner omit velocity feedback; do not convert those values as ticks.
     * Supply actual wheel velocities here only after adding a source with known units and frame.
     */
    @Override
    public List<Double> getWheelVelocities() {
        return null;
    }
}
