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

 *        (forwardStart)
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

    public static double PARALLEL_X = 0; // X is the up and down direction
    public static double PARALLEL_Y = -7.63; // Y is the strafe direction

    public static double PERPENDICULAR_X = -2.55;
    public static double PERPENDICULAR_Y = 0;

    // Parallel/Perpendicular to the forwardStart axis
    // Parallel wheel is parallel to the forwardStart axis
    // Perpendicular is perpendicular to the forwardStart axis
    private final Encoder parallelEncoder;
    private final Encoder perpendicularEncoder;

    private final GoBildaPinpointDriver imu;

    public TwoWheelTrackingLocalizer(HardwareMap hardwareMap, GoBildaPinpointDriver imu) {
        super(Arrays.asList(
                new Pose2d(PARALLEL_X, PARALLEL_Y, 0),
                new Pose2d(PERPENDICULAR_X, PERPENDICULAR_Y, Math.toRadians(90))
        ));

        this.imu = imu;

        parallelEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "frontRightMotor"));
        perpendicularEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "intakeMotor"));

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
        parallelEncoder.setDirection(Encoder.Direction.REVERSE);
        perpendicularEncoder.setDirection(Encoder.Direction.REVERSE);
    }

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
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
        return Arrays.asList(
                encoderTicksToInches(-imu.getEncoderX()),
                encoderTicksToInches(-imu.getEncoderY())
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
                encoderTicksToInches(-imu.getVelX()),
                encoderTicksToInches(-imu.getVelY())
        );
    }
}
