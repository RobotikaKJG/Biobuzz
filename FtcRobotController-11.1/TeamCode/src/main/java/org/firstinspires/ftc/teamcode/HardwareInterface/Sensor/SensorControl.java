package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SensorControl {

    private final Limelight3A limelight;
    private final EdgeDetection edgeDetection;
    private final StandardTrackingWheelLocalizer localizer;

    public final LynxI2cColorRangeSensor rangeSensorMid;
    public final LynxI2cColorRangeSensor rangeSensorFront;
    private double currentDistanceInchesMid;
    private double currentDistanceInchesFront;

    private double ballDistanceIn = 4.0;
    private double flywheelOffset = 0.0;

    // Goal coordinates in INCHES
    public static final double RedXInches = 66.0;
    public static final double RedYInches = 62.0;
    public static final double BlueXInches = -66.0;
    public static final double BlueYInches = 62.0;

    public static Pose2d RedGoalPos = new Pose2d(RedXInches, RedYInches, 0);
    public static Pose2d BlueGoalPos = new Pose2d(BlueXInches, BlueYInches, 0);
    private static double scoreHeight = 26.0;
    private static double scoreAngle = Math.toRadians(-30);
    private static double passThroughtPointRadius = 5;

    private static final double FieldHalfInches = 66.93;

    private Consumer<Pose2d> roadRunnerPoseUpdater = null;

    // --- CONTINUOUS INJECTION STORAGE & CONFIG ---
    private static final int LimelightFrames = 7;
    // Continuous sliding windows (instead of one-shot buffers)
    private final List<Double> rollingX = new ArrayList<>();
    private final List<Double> rollingY = new ArrayList<>();
    private final List<Double> rollingYaw = new ArrayList<>();

    // Safety Thresholds
    private static final double MAX_ALLOWED_DISTANCE_IN = 70.0;  // Don't trust far away tags
    private static final double MIN_ALLOWED_DISTANCE_IN = 10.0;  // Don't trust if too close to lens
    private static final double LINEAR_VELOCITY_THRESHOLD = 3.0; // Inches per second max
    private static final double ANGULAR_VELOCITY_THRESHOLD = Math.toRadians(10); // Max rad/s rotation
    private static final double MAX_HEADING_ERROR_RAD = Math.toRadians(20); // Throw out massive outlier spikes

    // Velocity Tracking variables
    private Pose2d lastPose = new Pose2d(0, 0, 0);
    private long lastVelocityUpdateTimeMs = System.currentTimeMillis();
    public double robotVelocityXInPerSec = 0.0;
    public double robotVelocityYInPerSec = 0.0;
    private double robotLinearVelocityInPerSec = 0.0;
    private double robotAngularVelocityRadPerSec = 0.0;
    private double forwardBackwardSeparationDegrees = 90.0;
    private static final double MIN_DRIVE_DIRECTION_VELOCITY_IN_PER_SEC = 6.9;

    private static final long LIMELIGHT_RESET_TIMEOUT_MS = 3000;
    private long resetStartTimeMs = -1;
    private final List<Double> limelightXReadingsIn = new ArrayList<>();
    private final List<Double> limelightYReadingsIn = new ArrayList<>();
    private final List<Double> limelightYawReadingsRad = new ArrayList<>();

    // Fusion tuning: how much we nudge towards vision per frame (0.02 = 2% vision, 98% odometry)
    private static final double CONTINUOUS_FUSION_ALPHA = 0.05;

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection, StandardTrackingWheelLocalizer localizer) {
        this.localizer = localizer;
        this.edgeDetection = edgeDetection;

        rangeSensorMid = hardwareMap.get(LynxI2cColorRangeSensor.class, "MidColorSensor");
        rangeSensorFront = hardwareMap.get(LynxI2cColorRangeSensor.class, "FrontColorSensor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        setInitialLocalisationAngle();
    }

    private void setInitialLocalisationAngle() {
        if (!GlobalVariables.wasAutonomous) {
            localizer.setPoseEstimate(new Pose2d(0, 0, 0));
        } else {
            GlobalVariables.wasAutonomous = false;
            localizer.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(-45)));
        }
        lastPose = localizer.getPoseEstimate();
    }

    //
    //  Localizer Loop Updates
    //

    public void updateLocalizer() {
        localizer.update();
        calculateRobotVelocity();
    }

    /**
     * Calculates current velocities from the odometry localizer to ensure
     * we are stationary before injecting vision data.
     */
    public void calculateRobotVelocity() {
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastVelocityUpdateTimeMs) / 1000.0;

        if (dt > 0.005) { // Protect against divide-by-zero
            Pose2d currentPose = localizer.getPoseEstimate();
            Pose2d poseVelocity = localizer.getPoseVelocity();
            double dx = currentPose.getX() - lastPose.getX();
            double dy = currentPose.getY() - lastPose.getY();
            double dHeading = normalizeRadians(currentPose.getHeading() - lastPose.getHeading());

            if (poseVelocity != null) {
                robotVelocityXInPerSec = poseVelocity.getX();
                robotVelocityYInPerSec = poseVelocity.getY();
                robotLinearVelocityInPerSec = Math.hypot(robotVelocityXInPerSec, robotVelocityYInPerSec);
                robotAngularVelocityRadPerSec = Math.abs(poseVelocity.getHeading());
            } else {
                robotVelocityXInPerSec = dx / dt;
                robotVelocityYInPerSec = dy / dt;
                robotLinearVelocityInPerSec = Math.hypot(dx, dy) / dt;
                robotAngularVelocityRadPerSec = Math.abs(dHeading) / dt;
            }

            lastPose = currentPose;
            lastVelocityUpdateTimeMs = currentTime;
        }
    }

    /**
     * Continuous Odometry Injection Pipeline
     * Filters noise based on velocity, distance, orientation, and a rolling trimmed average.
     */
    public void applyContinuousVisionFusion() {
        // GATE 1: Is the robot moving too fast? (Eliminates motion blur and latency errors)
        if (robotLinearVelocityInPerSec > LINEAR_VELOCITY_THRESHOLD ||
                robotAngularVelocityRadPerSec > ANGULAR_VELOCITY_THRESHOLD) {
            return;
        }

        // GATE 2: Do we have a high-quality vision target?
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return;
        }

        // GATE 3: Is the distance to the tags reasonable?
        double distance = getTagDistance();
        if (distance < MIN_ALLOWED_DISTANCE_IN || distance > MAX_ALLOWED_DISTANCE_IN) {
            return;
        }

        Pose3D botpose = result.getBotpose();
        if (botpose == null) return;

        // Extract and map coordinates identically to your autonomous reset method
        double xIn = botpose.getPosition().x * 39.37;
        double yIn = botpose.getPosition().y * 39.37;
        double headingRad = botpose.getOrientation().getYaw(AngleUnit.RADIANS);

        double visionCalculatedX;
        double visionCalculatedY;
        double visionCalculatedHeading;

        if (GlobalVariables.alliance == Alliance.Red) {
            visionCalculatedX = -xIn;
            visionCalculatedY = yIn;
            visionCalculatedHeading = normalizeRadians(headingRad - Math.toRadians(90));
        } else {
            visionCalculatedX = xIn;
            visionCalculatedY = -yIn;
            visionCalculatedHeading = normalizeRadians(headingRad + Math.toRadians(90));
        }

        // Remap to match Road Runner configuration (RR X = Vision Y, RR Y = Vision X)
        double visionRR_X = visionCalculatedY;
        double visionRR_Y = visionCalculatedX;

        // GATE 4: Sanity check heading deviation to destroy wild anomalous frames
        Pose2d currentPose = localizer.getPoseEstimate();
        double headingError = Math.abs(normalizeRadians(visionCalculatedHeading - currentPose.getHeading()));
        if (headingError > MAX_HEADING_ERROR_RAD) {
            return;
        }

        // --- ROLLING SLIDING WINDOW BUFFER ---
        rollingX.add(visionRR_X);
        rollingY.add(visionRR_Y);
        rollingYaw.add(visionCalculatedHeading);

        // Keep buffer clamped to your preferred window size (7 frames)
        if (rollingX.size() > LimelightFrames) {
            rollingX.remove(0);
            rollingY.remove(0);
            rollingYaw.remove(0);
        }

        // Wait until the sliding window is full to begin filtering data
        if (rollingX.size() < LimelightFrames) {
            return;
        }

        // Compute the Trimmed Mean across the rolling frame window to isolate outliers
        double filteredVisionX = getTrimmedAverage(rollingX);
        double filteredVisionY = getTrimmedAverage(rollingY);
        double filteredVisionHeading = getTrimmedAverage(rollingYaw);

        // --- COMPLEMENTARY FUSION INJECTION ---
        // Smoothly blend the current odometry coordinates with the vision coordinates
        double fusedX = currentPose.getX() + CONTINUOUS_FUSION_ALPHA * (filteredVisionX - currentPose.getX());
        double fusedY = currentPose.getY() + CONTINUOUS_FUSION_ALPHA * (filteredVisionY - currentPose.getY());

        // Handle angle wrapping elegantly during blending to avoid sudden full 360 spins
        double angleDifference = normalizeRadians(filteredVisionHeading - currentPose.getHeading());
        double fusedHeading = normalizeRadians(currentPose.getHeading() + CONTINUOUS_FUSION_ALPHA * angleDifference);

        // Inject the fused position smoothly back into Road Runner
        Pose2d fusedPose = new Pose2d(fusedX, fusedY, fusedHeading);
        localizer.setPoseEstimate(fusedPose);

        if (roadRunnerPoseUpdater != null) {
            roadRunnerPoseUpdater.accept(fusedPose);
        }
    }

    public void initLocalizerPose() {
        localizer.setPoseEstimate(new Pose2d(0, 0, 0));
        lastPose = new Pose2d(0, 0, 0);
    }

    public void setPositionFromRoadRunner(Pose2d poseInches) {
        localizer.setPoseEstimate(poseInches);
        lastPose = poseInches;
    }

    public void setRoadRunnerPoseUpdater(Consumer<Pose2d> updater) {
        this.roadRunnerPoseUpdater = updater;
    }

    public double getLocalizerAngle() {
        return localizer.getPoseEstimate().getHeading();
    }

    public boolean isDrivingForward() {
        return isDrivingForward(forwardBackwardSeparationDegrees);
    }

    public boolean isDrivingForward(double separationDegrees) {
        if (robotLinearVelocityInPerSec < MIN_DRIVE_DIRECTION_VELOCITY_IN_PER_SEC / 2) {
            return false;
        }

        double clampedSeparationDegrees = Math.max(0.0, Math.min(180.0, separationDegrees));
        double drivingAngleDegrees = Math.toDegrees(Math.atan2(robotVelocityYInPerSec, robotVelocityXInPerSec));
        return Math.abs(normalizeDegrees(drivingAngleDegrees)) <= clampedSeparationDegrees;
    }

    public boolean isDrivingBackward() {
        return isDrivingBackward(forwardBackwardSeparationDegrees);
    }

    public boolean isDrivingBackward(double separationDegrees) {
        if (robotLinearVelocityInPerSec < MIN_DRIVE_DIRECTION_VELOCITY_IN_PER_SEC) {
            return false;
        }

        return !isDrivingForward(separationDegrees);
    }

    public double getDistanceFromLocalizer() {
        Pose2d currentPose = localizer.getPoseEstimate();
        double robotX = currentPose.getY();
        double robotY = currentPose.getX();

        double targetX = (GlobalVariables.alliance == Alliance.Red) ? RedXInches : BlueXInches;
        double targetY = (GlobalVariables.alliance == Alliance.Red) ? RedYInches : BlueYInches;

        double dx = targetX - robotX;
        double dy = targetY - robotY;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public void resetLocalizerAngle() {
        if (edgeDetection.rising(GamepadIndexValues.options)) {
            Pose2d current = localizer.getPoseEstimate();
            Pose2d newPose = new Pose2d(current.getX(), current.getY(), 0);
            localizer.setPoseEstimate(newPose);
            if (roadRunnerPoseUpdater != null) {
                roadRunnerPoseUpdater.accept(newPose);
            }
        }
    }

    public Pose2d getLocalizerPose() {
        return localizer.getPoseEstimate();
    }


    //
    //  Color / Range Sensor
    //

    public void updateDistance() {
        currentDistanceInchesMid = rangeSensorMid.getDistance(DistanceUnit.INCH);
        currentDistanceInchesFront = rangeSensorFront.getDistance(DistanceUnit.INCH);
    }

    public double getDistanceMid() {
        return currentDistanceInchesMid;
    }

    public double getDistanceFront() {
        return currentDistanceInchesFront;
    }

    public boolean isMidBall() {
        return currentDistanceInchesMid < ballDistanceIn;
    }

    public boolean isFrontBall() {
        return currentDistanceInchesFront < ballDistanceIn;
    }

    public boolean isNoBallSeen() {
        return !isMidBall() && !isFrontBall();
    }


    //
    //  Limelight
    //

    public void initLimelight(int pipelineNr) {
        limelight.start();
        limelight.pipelineSwitch(pipelineNr);
    }

    public LLResult limelightResult() {
        return limelight.getLatestResult();
    }

    public boolean resetLocalizerWithLimelight() {
        if (resetStartTimeMs < 0) resetStartTimeMs = System.currentTimeMillis();

        if (System.currentTimeMillis() - resetStartTimeMs > LIMELIGHT_RESET_TIMEOUT_MS) {
            clearLimelightBuffers();
            return true;
        }

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            Pose3D botpose = result.getBotpose();
            if (botpose != null) {
                double xIn = botpose.getPosition().x * 39.37;
                double yIn = botpose.getPosition().y * 39.37;
                double headingRad = botpose.getOrientation().getYaw(AngleUnit.RADIANS);

                if (GlobalVariables.alliance == Alliance.Red) {
                    limelightXReadingsIn.add(-xIn);
                    limelightYReadingsIn.add(yIn);
                    limelightYawReadingsRad.add(headingRad - Math.toRadians(90));
                } else {
                    limelightXReadingsIn.add(xIn);
                    limelightYReadingsIn.add(-yIn);
                    limelightYawReadingsRad.add(headingRad + Math.toRadians(90));
                }
            }
        }

        if (limelightXReadingsIn.size() < LimelightFrames) return false;

        double avgX = getTrimmedAverage(limelightXReadingsIn);
        double avgY = getTrimmedAverage(limelightYReadingsIn);
        double avgHeading = getTrimmedAverage(limelightYawReadingsRad);

        clearLimelightBuffers();

        Pose2d newPose = new Pose2d(avgY, avgX, avgHeading);
        localizer.setPoseEstimate(newPose);

        if (roadRunnerPoseUpdater != null) {
            roadRunnerPoseUpdater.accept(newPose);
        }

        return true;
    }

    private void clearLimelightBuffers() {
        limelightXReadingsIn.clear();
        limelightYReadingsIn.clear();
        limelightYawReadingsRad.clear();
        resetStartTimeMs = -1;
    }

    public double getTagDistance() {
        double y = 0;
        LLResult result = limelightResult();
        int targetID = (GlobalVariables.alliance == Alliance.Red) ? 24 : 20;

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null) {
                for (LLResultTypes.FiducialResult f : fiducials) {
                    if (f.getFiducialId() == targetID) {
                        Pose3D botpose = result.getBotpose();

                        if (GlobalVariables.alliance == Alliance.Red)
                            y = botpose.getPosition().y - FieldHalfInches * 25.4;
                        else
                            y = botpose.getPosition().y + FieldHalfInches * 25.4;

                        double x = botpose.getPosition().x + FieldHalfInches * 25.4;
                        return Math.sqrt(x * x + y * y) / 25.4; // Convert mm to inches
                    }
                }
            }
        }
        return -1;
    }

    //
    //  Predictive shooting
    //

    public double getHoodTicksFromDegrees(double degrees) {
        return 0.02 * degrees - 0.7;
    }

    public double getFlywheelTicksFromVelocity(double velocity) {
        return 94.501 * velocity / 12 - 187.96 + flywheelOffset;
    }

    //
    //  Other Utilities
    //

    public double getTurretTargetAngleDegrees() {
        Pose2d currentPose = localizer.getPoseEstimate();
        double robotX = currentPose.getY();
        double robotY = currentPose.getX();
        double robotHeading = currentPose.getHeading();

        double targetX = (GlobalVariables.alliance == Alliance.Red) ? RedXInches : BlueXInches;
        double targetY = (GlobalVariables.alliance == Alliance.Red) ? RedYInches : BlueYInches;

        double dx = targetX - robotX;
        double dy = targetY - robotY;

        double angleToTargetRad;

        if (!GlobalVariables.isAutonomous) {
            angleToTargetRad = (!GlobalVariables.far) ? Math.atan2(dx, dy) :
                    Math.toRadians(GlobalVariables.alliance == Alliance.Red ? 61.67 : -61.67);
        } else {
            if (!GlobalVariables.far) {
                angleToTargetRad = Math.toRadians(GlobalVariables.alliance == Alliance.Red ? 45.0 : -45.0);
            } else {
                angleToTargetRad = Math.toRadians(GlobalVariables.alliance == Alliance.Red ? 70.67 : -66.67);
            }
        }

        double turretAngleRad = angleToTargetRad - robotHeading;
        return normalizeDegrees(Math.toDegrees(turretAngleRad));
    }

    private double getTrimmedAverage(List<Double> data) {
        if (data.isEmpty()) return 0;
        List<Double> copy = new ArrayList<>(data);
        Collections.sort(copy);
        if (copy.size() <= 2) {
            double sum = 0;
            for (double d : copy) sum += d;
            return sum / copy.size();
        }
        double sum = 0;
        for (int i = 1; i < copy.size() - 1; i++) {
            sum += copy.get(i);
        }
        return sum / (copy.size() - 2);
    }

    private double normalizeDegrees(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    private double normalizeRadians(double radians) {
        while (radians > Math.PI) radians -= 2 * Math.PI;
        while (radians < -Math.PI) radians += 2 * Math.PI;
        return radians;
    }
}
