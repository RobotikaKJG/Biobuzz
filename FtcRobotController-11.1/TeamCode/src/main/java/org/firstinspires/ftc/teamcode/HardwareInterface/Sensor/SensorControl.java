package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

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

    private final LimitSwitch turretLimitSwitch;
    private final Limelight3A limelight;
    private final EdgeDetection edgeDetection;
    private final StandardTrackingWheelLocalizer localizer;

    public final LynxI2cColorRangeSensor rangeSensor;
    private double currentDistanceInches;

    // Goal coordinates in INCHES (Standardized)
    public static final double RED_GOAL_X_IN = 66.0;
    public static final double RED_GOAL_Y_IN = 62.0;
    public static final double BLUE_GOAL_X_IN = -66.0;
    public static final double BLUE_GOAL_Y_IN = 62.0;

    // Field geometry: half-size offset in inches (~1.7m)
    private static final double FIELD_HALF_SIZE_IN = 66.93;

    /** When set, allows external classes to react to a position jump (e.g., updating a drive train class) */
    private Consumer<Pose2d> roadRunnerPoseUpdater = null;

    /** Average filter for Limelight position jitter */
    private static final int LIMELIGHT_COLLECT_SIZE = 7;
    private final List<Double> limelightXReadingsIn = new ArrayList<>();
    private final List<Double> limelightYReadingsIn = new ArrayList<>();

    private static final long LIMELIGHT_RESET_TIMEOUT_MS = 3000;
    private long resetStartTimeMs = -1;

    // Vision Fusion Constants
    private static final double FUSION_ALPHA_MULTI_TAG = 0.15;
    private static final double FUSION_ALPHA_SINGLE_TAG_CLOSE = 0.05;
    private static final double FUSION_MAX_VELOCITY_IN_S = 11.8; // ~300mm/s
    private static final double FUSION_MAX_TURRET_ANGLE_DEG = 15.0;

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection, StandardTrackingWheelLocalizer localizer) {
        this.localizer = localizer;
        this.edgeDetection = edgeDetection;

        // Limit Switch Initialization
        turretLimitSwitch = hardwareMap.get(LimitSwitch.class, "turretRightLimitSwitch");
        turretLimitSwitch.setMode(LimitSwitch.SwitchConfig.NC);

        // Distance Sensor (using the Color/Range combo)
        rangeSensor = hardwareMap.get(LynxI2cColorRangeSensor.class, "ColorSensor");

        // Limelight Initialization
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
    }

    // --- LOCALIZER WRAPPERS ---

    public void updateLocalizer() {
        localizer.update();
    }

    public void initLocalizerPose() {
        localizer.setPoseEstimate(new Pose2d(0, 0, 0));
    }

    public double getLocalizerAngle() {
        resetLocalizerAngle();
        return localizer.getPoseEstimate().getHeading();
    }

    public double getDistanceFromLocalizer() {
        Pose2d currentPose = localizer.getPoseEstimate();

        // Match the coordinate mapping used in turret targeting:
        // Localizer Y is side-to-side (Robot X), Localizer X is forward-back (Robot Y)
        double robotX = currentPose.getY();
        double robotY = currentPose.getX();

        double targetX = (GlobalVariables.alliance == Alliance.Red) ? RED_GOAL_X_IN : BLUE_GOAL_X_IN;
        double targetY = (GlobalVariables.alliance == Alliance.Red) ? RED_GOAL_Y_IN : BLUE_GOAL_Y_IN;

        double dx = targetX - robotX;
        double dy = targetY - robotY;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public void resetLocalizerAngle() {
        if (edgeDetection.rising(GamepadIndexValues.options)) {
            Pose2d current = localizer.getPoseEstimate();
            localizer.setPoseEstimate(new Pose2d(current.getX(), current.getY(), 0));
        }
    }

    // --- HARDWARE STATE ---

    public boolean isLimitPressed() {
        return turretLimitSwitch.getIsPressed();
    }

    public Pose2d getLocalizerPose() {
        return localizer.getPoseEstimate();
    }

    public void updateDistance() {
        currentDistanceInches = rangeSensor.getDistance(DistanceUnit.INCH);
    }

    public double getDistance() {
        return currentDistanceInches;
    }

    // --- LIMELIGHT & TARGETING ---

    public void initLimelight(int pipelineNr) {
        limelight.start();
        limelight.pipelineSwitch(pipelineNr);
    }

    public LLResult limelightResult() {
        return limelight.getLatestResult();
    }

    /**
     * Collects and averages Limelight readings to reset the Localizer.
     * Returns true when the reset is complete or timed out.
     */
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
                // Converting Limelight meters to inches
                double xIn = botpose.getPosition().x * 39.37;
                double yIn = botpose.getPosition().y * 39.37;

                if (GlobalVariables.alliance == Alliance.Red) {
                    limelightXReadingsIn.add(xIn);
                    limelightYReadingsIn.add(-yIn);
                } else {
                    limelightXReadingsIn.add(xIn);
                    limelightYReadingsIn.add(yIn);
                }
            }
        }

        if (limelightXReadingsIn.size() < LIMELIGHT_COLLECT_SIZE) return false;

        // Trimmed Mean filter (removes outliers)
        double avgX = getTrimmedAverage(limelightXReadingsIn);
        double avgY = getTrimmedAverage(limelightYReadingsIn);

        clearLimelightBuffers();

        double currentHeading = localizer.getPoseEstimate().getHeading();
        // Standard RR Localizer uses (x, y, heading)
        // Here we swap/invert coordinates to match your original pinpoint logic:
        // Pinpoint X was RR Y, Pinpoint Y was RR X.
        Pose2d newPose = new Pose2d(avgY, avgX, currentHeading);
        localizer.setPoseEstimate(newPose);

        if (roadRunnerPoseUpdater != null) {
            roadRunnerPoseUpdater.accept(newPose);
        }

        return true;
    }

    private double getTrimmedAverage(List<Double> data) {
        Collections.sort(data);
        double sum = 0;
        for (int i = 1; i < data.size() - 1; i++) {
            sum += data.get(i);
        }
        return sum / (data.size() - 2);
    }

    private void clearLimelightBuffers() {
        limelightXReadingsIn.clear();
        limelightYReadingsIn.clear();
        resetStartTimeMs = -1;
    }

    /**
     * Calculates required turret angle to face the goal based on Localizer position.
     */
    public double getTurretTargetAngleDegrees() {
        Pose2d currentPose = localizer.getPoseEstimate();

        // Match original logic: localizer Y is robot side-to-side (X-axis in turret math)
        // localizer X is robot forward-back (Y-axis in turret math)
        double robotX = currentPose.getY();
        double robotY = currentPose.getX();
        double robotHeading = currentPose.getHeading();

        double targetX = (GlobalVariables.alliance == Alliance.Red) ? RED_GOAL_X_IN : BLUE_GOAL_X_IN;
        double targetY = (GlobalVariables.alliance == Alliance.Red) ? RED_GOAL_Y_IN : BLUE_GOAL_Y_IN;

        double dx = targetX - robotX;
        double dy = targetY - robotY;

        double angleToTargetRad;

        // Targeting Logic
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

    /**
     * Continuous background vision fusion.
     * Corrects Standard Localizer position using Limelight when the robot is steady.
     */
    public void continuousVisionUpdate(double turretAngleDeg) {
        if (Math.abs(turretAngleDeg) > FUSION_MAX_TURRET_ANGLE_DEG) return;

        // Get velocity from localizer to ensure we aren't sliding/moving fast
        Pose2d velocityPose = localizer.getPoseVelocity();
        if (velocityPose == null) return;

        double velocity = Math.sqrt(Math.pow(velocityPose.getX(), 2) + Math.pow(velocityPose.getY(), 2));
        if (velocity > FUSION_MAX_VELOCITY_IN_S) return;

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        Pose3D botpose = result.getBotpose();
        if (botpose == null) return;

        int tagCount = (result.getFiducialResults() != null) ? result.getFiducialResults().size() : 0;
        if (tagCount == 0) return;

        double alpha = (tagCount >= 2) ? FUSION_ALPHA_MULTI_TAG : FUSION_ALPHA_SINGLE_TAG_CLOSE;

        // Convert Vision meters to inches
        double visionX = -botpose.getPosition().x * 39.37;
        double visionY = botpose.getPosition().y * 39.37;

        Pose2d currentPose = localizer.getPoseEstimate();

        // Blending logic
        // Note: Swapping back to RR coordinate convention (X = forward, Y = left)
        double correctedX = currentPose.getX() + alpha * (visionY - currentPose.getX());
        double correctedY = currentPose.getY() + alpha * (visionX - currentPose.getY());

        Pose2d correctedPose = new Pose2d(correctedX, correctedY, currentPose.getHeading());
        localizer.setPoseEstimate(correctedPose);

        if (roadRunnerPoseUpdater != null) {
            roadRunnerPoseUpdater.accept(correctedPose);
        }
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
                            y = botpose.getPosition().y - FIELD_HALF_SIZE_IN * 25.4;
                        else
                            y = botpose.getPosition().y + FIELD_HALF_SIZE_IN * 25.4;

                        double x = botpose.getPosition().x + FIELD_HALF_SIZE_IN * 25.4;
                        return Math.sqrt(x * x + y * y);
                    }
                }
            }
        }
        return -1;
    }

    private double normalizeDegrees(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public void setPositionFromRoadRunner(Pose2d poseInches) {
        // We directly update the localizer's estimate so all internal
        // calculations (turret, distance, etc.) use this new coordinate.
        localizer.setPoseEstimate(poseInches);
    }

    public void setRoadRunnerPoseUpdater(Consumer<Pose2d> updater) {
        this.roadRunnerPoseUpdater = updater;
    }
}