package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import android.graphics.Color;
import android.provider.Settings;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Main.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Main.Pose2D;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.BallDetectionPipeline;


public class SensorControl {

    private final LimitSwitch[] limitSwitches;
    private Limelight3A limelight;
    private final EdgeDetection edgeDetection;
    private final StandardTrackingWheelLocalizer localizer;
//    public final NormalizedColorSensor colorSensor;
    public final GoBildaPinpointDriver pinpointImu;
    public int currentColor;
    public int currentRed;
    public int currentGreen;
    public int currentBlue;
    private double currentDistance;
    double y = 0;
    private static final double redGoalX = 66.0;
    private static final double redGoalY = 59.0;
    private static final double blueGoalX = 66.0;
    private static final double blueGoalY = -59.0;

    private double cameraHFOVDegrees = 78.0; // Logitech C720 approx HFOV
    private int cameraWidthPx = 640;

    /** When set, called with the new pose (inches, radians) when position is reset from Limelight so drive/localizer can stay in sync. */
    private Consumer<Pose2d> roadRunnerPoseUpdater = null;

    /** Last drive pose from Road Runner (position + heading in one consistent frame). Used for turret calc. */
    private Pose2d lastDrivePose = new Pose2d(0, 0, 0);

    /** Average filter for Limelight position to remove ±2–3 inch jitter. Keeps last N readings. */
    private static final int LIMELIGHT_AVERAGE_FILTER_SIZE = 5;
    private final List<Double> limelightXReadingsM = new ArrayList<>();
    private final List<Double> limelightYReadingsM = new ArrayList<>();

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection,  StandardTrackingWheelLocalizer localizer) {
        limitSwitches = getLimitSwitches(hardwareMap);

//        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "ColorSensor");
//        rangeSensor = hardwareMap.get(LynxI2cColorRangeSensor.class, "ColorSensor");
        pinpointImu = hardwareMap.get(GoBildaPinpointDriver.class, "pinpointIMU");
//        colorSensor.setGain(15);//2);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        this.localizer = localizer;
        setInitialLocalisationAngle();

        this.edgeDetection = edgeDetection;
    }

    private LimitSwitch[] getLimitSwitches(HardwareMap hardwareMap) {
        final LimitSwitch[] limitSwitches;
        limitSwitches = new LimitSwitch[]{
                hardwareMap.get(LimitSwitch.class, "turretRightLimitSwitch")
        };

        limitSwitches[0].setMode(LimitSwitch.SwitchConfig.NC);
        return limitSwitches;
    }

    private void setInitialLocalisationAngle() {
        if (!GlobalVariables.wasAutonomous)
            localizer.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(0)));
        else {
            GlobalVariables.wasAutonomous = false;
            localizer.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(-45)));
        }
    }

    public void initPinpoint() {
        pinpointImu.initialize();
        pinpointImu.resetPosAndIMU();
        pinpointImu.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);
    }

    public void initLimelight(int pipelineNr) {
        limelight.start();
        limelight.pipelineSwitch(pipelineNr);
    }

    public boolean isLimitSwitchPressed() {
        return limitSwitches[0].getIsPressed();
    }

    public LLResult limelightResult() {
        return limelight.getLatestResult();
    }

    /**
     * Called repeatedly by AutoResetPosControl when square is pressed.
     * Collects one Limelight reading per call. Returns false until N readings
     * are gathered, then averages them, resets position, and returns true.
     */
    public boolean resetPinpointPoseWithLimelight() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            Pose3D botpose = result.getBotpose();
            if (botpose != null) {
                limelightXReadingsM.add(-botpose.getPosition().x);
                limelightYReadingsM.add(-botpose.getPosition().y);
            }
        }

        if (limelightXReadingsM.size() < LIMELIGHT_AVERAGE_FILTER_SIZE) return false;

        double sumX = 0, sumY = 0;
        int n = limelightXReadingsM.size();
        for (int i = 0; i < n; i++) {
            sumX += limelightXReadingsM.get(i);
            sumY += limelightYReadingsM.get(i);
        }
        double avgXmm = (sumX / n) * 1000.0;
        double avgYmm = (sumY / n) * -1000.0;

        limelightXReadingsM.clear();
        limelightYReadingsM.clear();

        double currentHeadingRad = pinpointImu.getHeading();

        pinpointImu.setPosition(new Pose2D(DistanceUnit.MM, avgXmm, avgYmm,
                AngleUnit.RADIANS, currentHeadingRad));

        if (roadRunnerPoseUpdater != null) {
            double leftInches = avgXmm / 25.4;
            double forwardInches = avgYmm / 25.4;
            roadRunnerPoseUpdater.accept(new Pose2d(forwardInches, leftInches, currentHeadingRad));
        }

        return true;
    }

    /** Call from teleop (e.g. Dependencies) so Limelight position reset also updates the drive pose. */
    public void setRoadRunnerPoseUpdater(Consumer<Pose2d> updater) {
        this.roadRunnerPoseUpdater = updater;
    }

    public double getTurretTargetAngleDegrees() {
        double angleToTargetRad = 0;
        // Pinpoint position is in telemetry convention: X=left, Y=forward/up
        double robotX = pinpointImu.getPosX() / 25.4;
        double robotY = pinpointImu.getPosY() / 25.4;
        double robotHeadingRad = pinpointImu.getHeading();

        double targetX;
        double targetY;

        if (GlobalVariables.alliance == Alliance.Red) {
            targetX = redGoalX;
            targetY = redGoalY;
        } else {
            targetX = blueGoalX;
            targetY = blueGoalY;
        }

        double dx = targetX - robotX;
        double dy = targetY - robotY;

        // atan2(dx, dy) measures angle from +Y axis (forward), matching heading convention
        if (!GlobalVariables.isAutonomous) {
            angleToTargetRad = Math.atan2(dx, dy);
        }
        else {
            switch(GlobalVariables.alliance) {
                case Red:
                    angleToTargetRad = Math.toRadians(45.0);
                    break;
                case Blue:
                    angleToTargetRad = Math.toRadians(135.0);
                    break;
            }

        }

        double turretAngleRad = angleToTargetRad - robotHeadingRad;

        return normalizeDegrees(Math.toDegrees(turretAngleRad));
    }

    private double normalizeDegrees(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public double getTagDistance() {
        LLResult result = limelightResult();

        int targetID = (GlobalVariables.alliance == Alliance.Red) ? 24 : 20;

        if (result != null && result.isValid()) {

            // Prefer per-fiducial result if present
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null) {
                for (LLResultTypes.FiducialResult f : fiducials) {
                    if (f.getFiducialId() == targetID) {
                        Pose3D botpose = result.getBotpose();

                        if (GlobalVariables.alliance == Alliance.Red)
                            y = botpose.getPosition().y - 1.7;
                        else
                            y = botpose.getPosition().y + 1.7;
                        double x = botpose.getPosition().x + 1.7;

                        // red: x+ y-    blue: x+ y+

                        // Calculate distance to tag (in meters)
                        return Math.sqrt(x * x + y * y);
                    }
                }
            }
        }
        return -1;
    }

    public double getDisToCenter() {
        LLResult result = limelightResult();

        int targetID = (GlobalVariables.alliance == Alliance.Red) ? 24 : 20;

        if (result != null && result.isValid()) {

            // Prefer per-fiducial result if present
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null && !fiducials.isEmpty()) {
                for (LLResultTypes.FiducialResult f : fiducials) {
                    if (f.getFiducialId() == targetID) {
                        // getTargetXDegrees() gives horizontal offset in degrees
                        return f.getTargetXDegrees();
                    }
                }
            }

            // fallback to generic tx ONLY if no fiducials match target ID
            try {
                return result.getTx();
            } catch (Exception e) {
                return Double.NaN;
            }
        }

        return Double.NaN;
    }

    public static double degreesToPixels(double offsetDegrees, double imageWidthPx, double cameraHFOVDegrees) {
        if (Double.isNaN(offsetDegrees) || imageWidthPx <= 0 || cameraHFOVDegrees <= 0) return Double.NaN;
        // fraction across horizontal FOV (center = 0)
        double fraction = offsetDegrees / cameraHFOVDegrees;
        // pixel offset from center
        return fraction * imageWidthPx;
    }

    /** Robot forward = non-intake side. Same convention as localizer for teleop + autonomous. */
    private double getRobotHeadingRad() {
        return Angle.norm(pinpointImu.getHeading() + Math.PI);
    }

    public double getPinpointAngle() {
        resetPinpointAngle();
        return getRobotHeadingRad();
    }

    public void updatePinpoint() {
        pinpointImu.update();
    }

    public void resetPinpointAngle() {
        if (edgeDetection.rising(GamepadIndexValues.options))
            pinpointImu.setPosition(new Pose2D(pinpointImu.getPosX(), pinpointImu.getPosY(), 0));
    }

    public Pose2D getPinpointPos() {
        return pinpointImu.getPosition();
    }

    /**
     * Updates Pinpoint X and Y from Road Runner pose; heading is not written so the device gyro integrates normally
     * and matches the robot's rotation (avoids heading lag). Gyro reset (options) still works since we never overwrite heading.
     */
    public void setPositionFromRoadRunner(Pose2d poseInches) {
        lastDrivePose = poseInches;
        // Map Road Runner (X=forward, Y=left) to telemetry convention (X=left, Y=forward/up)
        double xMM = poseInches.getY() * 25.4;
        double yMM = poseInches.getX() * 25.4;
        pinpointImu.setPositionXY(xMM, yMM);
    }
}