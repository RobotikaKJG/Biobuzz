package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import android.graphics.Color;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Main.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import java.util.List;


public class SensorControl {

//    private final LimitSwitch[] limitSwitches;
    private Limelight3A limelight;
    private final EdgeDetection edgeDetection;
    private final StandardTrackingWheelLocalizer localizer;
//    public final NormalizedColorSensor colorSensor;
    public final LynxI2cColorRangeSensor rangeSensor;
    public final GoBildaPinpointDriver pinpointImu;
    public int currentColor;
    public int currentRed;
    public int currentGreen;
    public int currentBlue;
    private double currentDistance;
    double y = 0;

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection,  StandardTrackingWheelLocalizer localizer) {
//        limitSwitches = getLimitSwitches(hardwareMap);

//        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "ColorSensor");
        rangeSensor = hardwareMap.get(LynxI2cColorRangeSensor.class, "ColorSensor");
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
    }

    public void initLimelight(int pipelineNr) {
        limelight.start();
        limelight.pipelineSwitch(pipelineNr);
    }

    public LLResult limelightResult() {
        return limelight.getLatestResult();
    }

    public double getTagDistance() {
        LLResult result = limelightResult();

        if (result != null && result.isValid()) {
            // Get botpose relative to field (make sure your Limelight is configured to Field mode)
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
        return -1;
    }

    public double getDisToCenter() {
        LLResult result = limelightResult();

        if (result != null && result.isValid()) {
            // Prefer per-fiducial result if present (more specific for AprilTags)
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null && !fiducials.isEmpty()) {
                // use the first (primary) fiducial result
                LLResultTypes.FiducialResult f = fiducials.get(0);
                // getTargetXDegrees() gives horizontal offset in degrees (left/right)
                return f.getTargetXDegrees();
            }

            // fallback to generic tx from parent result (also in degrees)
            try {
                return result.getTx();
            } catch (Exception e) {
                // in case getTx() isn't available in particular SDK build
                return Double.NaN;
            }
        }

        // no valid result
        return Double.NaN;
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
            pinpointImu.resetPosAndIMU();
    }

    public double getLocalizerAngle() {
        resetLocalizerAngle(); //Checks every time, resets only when button pressed
        Pose2d currentPose = localizer.getPoseEstimate();
        return currentPose.getHeading();
    }

    public void resetLocalizerAngle() {
        if (edgeDetection.rising(GamepadIndexValues.options))
            localizer.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(0)));
    }

//    public boolean isLimitSwitchPressed(LimitSwitches state) {
//        switch (state) {
//            case slides:
//                return limitSwitches[0].getIsPressed();
//            case pivot:
//                return limitSwitches[1].getIsPressed();
//            default:
//                return false; // Or throw an exception
//        }
//    }

    public void updateColor(){
//        currentColor = colorSensor.getNormalizedColors().toColor();
        currentRed = Color.red(currentColor);
        currentGreen = Color.green(currentColor);
        currentBlue = Color.blue(currentColor);
    }

    public void resetColor(){
        currentColor = 0;
        currentRed = 0;
        currentGreen = 0;
        currentBlue = 0;
    }

    public void updateDistance(){
        currentDistance = rangeSensor.getDistance(DistanceUnit.MM);
    }

    public void resetDistance(){
        currentDistance = 100;
    }

    public boolean isRed(){
        //return currentGreen < 5 && currentRed > 7 || (currentBlue == 2 && currentGreen == 2 && currentRed == 5);
        return currentRed < 28 && currentRed > 22;
    }

    public boolean isYellow(){
        return currentGreen > 27;
    }

    public boolean isBlue(){
//        return (currentRed < 5 && currentBlue > 3 && currentGreen < 8) || ( currentRed == 1 && currentBlue == 3 && currentGreen < 4);
        return  currentRed < 25 && currentBlue > 20;
    }

    public double getDistance(){
        return currentDistance;
    }
}