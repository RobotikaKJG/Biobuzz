package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import android.graphics.Color;

import com.acmerobotics.roadrunner.geometry.Pose2d;
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
import org.firstinspires.ftc.teamcode.Pose2D;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import java.util.List;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.BallDetectionPipeline;


public class SensorControl {

//    private final LimitSwitch[] limitSwitches;
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
    private static final double redGoalX = 72.0;   // example
    private static final double redGoalY = -72;  // example
    private static final double blueGoalX = 72.0;  // example
    private static final double blueGoalY = 72;  // example

    private OpenCvCamera webcam;
    private BallDetectionPipeline ballPipeline;
    private double cameraHFOVDegrees = 78.0; // Logitech C720 approx HFOV
    private int cameraWidthPx = 640;

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection,  StandardTrackingWheelLocalizer localizer) {
//        limitSwitches = getLimitSwitches(hardwareMap);

//        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "ColorSensor");
//        rangeSensor = hardwareMap.get(LynxI2cColorRangeSensor.class, "ColorSensor");
        pinpointImu = hardwareMap.get(GoBildaPinpointDriver.class, "pinpointIMU");
//        colorSensor.setGain(15);//2);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        this.localizer = localizer;
        setInitialLocalisationAngle();

        this.edgeDetection = edgeDetection;
    }

//    private LimitSwitch[] getLimitSwitches(HardwareMap hardwareMap) {
//        final LimitSwitch[] limitSwitches;
//        limitSwitches = new LimitSwitch[]{
//                hardwareMap.get(LimitSwitch.class, "slidesLimitSwitch"),
//                hardwareMap.get(LimitSwitch.class, "pivotLimitSwitch")
//        };
//
//        limitSwitches[0].setMode(LimitSwitch.SwitchConfig.NC);
//        limitSwitches[1].setMode(LimitSwitch.SwitchConfig.NC);
//        return limitSwitches;
//    }

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

    public void initBallCamera(HardwareMap hardwareMap) {
        int camMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), camMonitorViewId);

        ballPipeline = new BallDetectionPipeline();
        webcam.setPipeline(ballPipeline);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                // Handle camera error
            }
        });
    }

    public void initLimelight(int pipelineNr) {
        limelight.start();
        limelight.pipelineSwitch(pipelineNr);
    }

    public LLResult limelightResult() {
        return limelight.getLatestResult();
    }

    public boolean resetPinpointPoseWithLimelight() {
        LLResult result = limelightResult();
        if (result == null || !result.isValid()) return false;

        Pose3D botpose = result.getBotpose();
        if (botpose == null) return false;

        // Limelight pose is in meters → convert to millimeters
        double xMM = -botpose.getPosition().x * 1000.0;
        double yMM = -botpose.getPosition().y * 1000.0;

        // Keep current heading (Pinpoint uses radians)
        double currentHeadingRad = pinpointImu.getHeading();

        // Set pinpoint pose (X, Y updated, heading unchanged)
        pinpointImu.setPosition(new Pose2D(DistanceUnit.MM, xMM, yMM,
                AngleUnit.RADIANS, currentHeadingRad));

        return true;
    }

    public double getTurretTargetAngleDegrees() {
        // Get robot pose from pinpoint (mm and radians)
        double robotXmm = pinpointImu.getPosX();
        double robotYmm = pinpointImu.getPosY();
        double robotHeadingRad = pinpointImu.getHeading();

        // Convert robot position to inches to match goal constants
        double robotX = robotXmm / 25.4;
        double robotY = robotYmm / 25.4;

        // Select target corner based on alliance
        double targetX;
        double targetY;

        if (GlobalVariables.alliance == Alliance.Red) {
            targetX = redGoalX;
            targetY = redGoalY;
        } else {
            targetX = blueGoalX;
            targetY = blueGoalY;
        }

        // Vector from robot to target
        double dx = targetX - robotX;
        double dy = targetY - robotY;

        // Absolute angle to target (field frame)
        double angleToTargetRad = Math.atan2(dy, dx);

        // Turret angle relative to robot heading
        double turretAngleRad = angleToTargetRad - robotHeadingRad;

        // Convert to degrees and normalize
        double turretAngleDeg = Math.toDegrees(turretAngleRad);
        return normalizeDegrees(turretAngleDeg);
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

    public double getBallOffsetPx() {
        if (ballPipeline == null) return Double.NaN;
        return ballPipeline.getCenterOffsetPx();
    }

    public double getBallOffsetDegrees() {
        double px = getBallOffsetPx();
        if (Double.isNaN(px)) return Double.NaN;
        return (px / cameraWidthPx) * cameraHFOVDegrees;
    }

    public static double degreesToPixels(double offsetDegrees, double imageWidthPx, double cameraHFOVDegrees) {
        if (Double.isNaN(offsetDegrees) || imageWidthPx <= 0 || cameraHFOVDegrees <= 0) return Double.NaN;
        // fraction across horizontal FOV (center = 0)
        double fraction = offsetDegrees / cameraHFOVDegrees;
        // pixel offset from center
        return fraction * imageWidthPx;
    }

    public double getPinpointAngle() {
        resetPinpointAngle();
        pinpointImu.update();
        return pinpointImu.getHeading();
    }

    public void resetPinpointAngle() {
        if (edgeDetection.rising(GamepadIndexValues.options))
            pinpointImu.setPosition(new Pose2D(pinpointImu.getPosX(), pinpointImu.getPosY(), 90));
    }
}