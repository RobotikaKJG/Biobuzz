package org.firstinspires.ftc.teamcode.Camera.LimeLight;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.BallDetectionPipeline;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

@TeleOp(name = "Webcam Ball Centering Test", group = "Test")
public class BallCenter extends LinearOpMode {
    private OpenCvCamera webcam;
    private BallDetectionPipeline pipeline;
    private MotorControl motors;

    // Thresholds
    private static final double CENTERING_THRESHOLD_PX = 10;  // pixels from center
    private static final double STRAFE_KP = 0.002;
    private static final double MAX_STRAFE_POWER = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {

        motors = new MotorControl(hardwareMap);

        int camMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), camMonitorViewId);

        pipeline = new BallDetectionPipeline();
        webcam.setPipeline(pipeline);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
            }
        });

        telemetry.setMsTransmissionInterval(50);
        telemetry.addLine("Waiting for start...");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            double offsetPx = pipeline.getCenterOffsetPx();

            boolean targetVisible = !Double.isNaN(offsetPx);
            boolean targetCentered = targetVisible && Math.abs(offsetPx) <= CENTERING_THRESHOLD_PX;

            double strafe = 0;
//            double forward = 0;

            if (targetVisible) {
                if (!targetCentered) {
                    strafe = STRAFE_KP * offsetPx;
                    strafe = Math.max(-MAX_STRAFE_POWER, Math.min(MAX_STRAFE_POWER, strafe));
                }
//                else {
//                    forward = FORWARD_POWER;
//                }
            }

            mecanumDrive(strafe,0);

            telemetry.addData("Ball Offset (px)", offsetPx);
            telemetry.addData("Target Visible?", targetVisible);
            telemetry.addData("Target Centered?", targetCentered);
            telemetry.update();
        }
    }

    private void mecanumDrive(double strafe, double turn) {
        double fl = strafe + turn;
        double bl = -strafe + turn;
        double fr = -strafe - turn;
        double br = strafe - turn;

        double max = Math.max(1.0, Math.max(Math.abs(fl),
                Math.max(Math.abs(bl), Math.max(Math.abs(fr), Math.abs(br)))));
        fl /= max;
        bl /= max;
        fr /= max;
        br /= max;

        motors.setMotorSpeed(MotorConstants.frontLeft, fl);
        motors.setMotorSpeed(MotorConstants.backLeft, bl);
        motors.setMotorSpeed(MotorConstants.frontRight, fr);
        motors.setMotorSpeed(MotorConstants.backRight, br);
        motors.setMotors(MotorConstants.allDrive);
    }
}
