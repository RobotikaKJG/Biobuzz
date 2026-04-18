package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class BallDetectionPipeline extends OpenCvPipeline {
    private Mat hsv = new Mat();
    private Mat mask = new Mat();
    private Mat hierarchy = new Mat();

    private double centerOffsetPx = Double.NaN;
    private int imageWidth = 640;

    // HSV bounds for GREEN and PURPLE balls (tune these!)
    private static final Scalar GREEN_LOW = new Scalar(40, 70, 70);
    private static final Scalar GREEN_HIGH = new Scalar(80, 255, 255);

    private static final Scalar PURPLE_LOW = new Scalar(125, 70, 70);
    private static final Scalar PURPLE_HIGH = new Scalar(155, 255, 255);

    @Override
    public Mat processFrame(Mat input) {
        imageWidth = input.width();

        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

        // Create masks for both colors
        Mat greenMask = new Mat();
        Mat purpleMask = new Mat();
        Core.inRange(hsv, GREEN_LOW, GREEN_HIGH, greenMask);
        Core.inRange(hsv, PURPLE_LOW, PURPLE_HIGH, purpleMask);

        // Combine masks
        Core.bitwise_or(greenMask, purpleMask, mask);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double bestOffset = Double.NaN;
        double smallestAbsOffset = Double.MAX_VALUE;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area < 300) continue; // Ignore small noise

            Rect rect = Imgproc.boundingRect(contour);
            double ballCenterX = rect.x + rect.width / 2.0;
            double imageCenterX = imageWidth / 2.0;
            double offset = ballCenterX - imageCenterX;

            // Choose the ball closest to center
            if (Math.abs(offset) < smallestAbsOffset) {
                smallestAbsOffset = Math.abs(offset);
                bestOffset = offset;
            }

            // Draw rectangle for debugging
            Imgproc.rectangle(input, rect, new Scalar(0, 255, 0), 2);
        }

        centerOffsetPx = bestOffset;

        return input;
    }

    /** Returns pixel offset from center. Positive = ball is to the right. */
    public double getCenterOffsetPx() {
        return centerOffsetPx;
    }
}
