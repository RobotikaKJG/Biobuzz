package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SensorControl {
    private final EdgeDetection edgeDetection;
    private final PinpointLocalizer pedroLocalizer;

    private static final double FieldHalfInches = 66.93;

    private Pose lastPose = new Pose(0, 0, 0);
    private long lastVelocityUpdateTimeMs = System.currentTimeMillis();
    public volatile double robotVelocityXInPerSec = 0.0;
    public volatile double robotVelocityYInPerSec = 0.0;
    private volatile double robotLinearVelocityInPerSec = 0.0;
    private volatile double robotAngularVelocityRadPerSec = 0.0;

    private static final long LIMELIGHT_RESET_TIMEOUT_MS = 3000;
    private long resetStartTimeMs = -1;
    private final List<Double> limelightXReadingsIn = new ArrayList<>();
    private final List<Double> limelightYReadingsIn = new ArrayList<>();
    private final List<Double> limelightYawReadingsRad = new ArrayList<>();

    // Fusion tuning: how much we nudge towards vision per frame (0.02 = 2% vision, 98% odometry)

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection, PinpointLocalizer pedroLocalizer) {
        this.pedroLocalizer = pedroLocalizer;
        this.edgeDetection = edgeDetection;

        setInitialLocalisationAngle();
    }

    private void setInitialLocalisationAngle() {
        if (!GlobalVariables.wasAutonomous) {
            pedroLocalizer.setPose(new Pose(0, 0, 0));
        } else {
            // GlobalVariables.wasAutonomous = false; // MOVED: Handled in GeneralAutonomous begin()
            pedroLocalizer.setPose(GlobalVariables.lastPose);
        }
        lastPose = pedroLocalizer.getPose();
    }


    //
    //  pedroLocalizer Loop Updates
    //

    public void updateLocalizer() {
        pedroLocalizer.update();
        GlobalVariables.lastPose = pedroLocalizer.getPose();

        calculateRobotVelocity();
    }

    public void calculateRobotVelocity() {
        long currentTime = System.currentTimeMillis();
        double dt = (currentTime - lastVelocityUpdateTimeMs) / 1000.0;

        if (dt > 0.005) { // Protect against divide-by-zero
            Pose currentPose = pedroLocalizer.getPose();

            Pose poseVelocity = pedroLocalizer.getVelocity();
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

    public void initLocalizerPose() {
        pedroLocalizer.setPose(new Pose(0, 0, 0));
        lastPose = new Pose(0, 0, 0);
    }

    public double getLocalizerAngle() {
        double heading = pedroLocalizer.getPose().getHeading();
        return heading;
    }

    public void resetLocalizerAngleNow() {
        Pose current = pedroLocalizer.getPose();
        Pose newPose = new Pose(current.getX(), current.getY(), 0);
        pedroLocalizer.setPose(newPose);
    }

    public Pose getLocalizerPose() {
        Pose pose = pedroLocalizer.getPose();
        return pose;
    }

    //
    //  Other Utilities
    //

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
