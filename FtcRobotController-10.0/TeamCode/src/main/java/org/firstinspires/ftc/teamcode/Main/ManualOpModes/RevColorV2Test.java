package org.firstinspires.ftc.teamcode.Main.ManualOpModes;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Roadrunner.StandardTrackingWheelLocalizer;


@TeleOp
public class RevColorV2Test extends LinearOpMode {
    // Define a variable for our color sensor
    EdgeDetection edgeDetection = new EdgeDetection();

    @Override
    public void runOpMode() {
        // Get the color sensor from hardwareMap

        SensorControl sensorControl = new SensorControl(hardwareMap,edgeDetection, new StandardTrackingWheelLocalizer(hardwareMap));

        // Wait for the Play button to be pressed
        waitForStart();

        // While the Op Mode is running, update the telemetry values.
        while (opModeIsActive()) {
            sensorControl.updateColor();
//            sensorControl.updateDistance();
            telemetry.addData("Red", sensorControl.isRed());
            telemetry.addData("Yellow", sensorControl.isYellow());
            telemetry.addData("Blue", sensorControl.isBlue());

            telemetry.addData("Red", Color.red(sensorControl.currentColor));
            telemetry.addData("Green", Color.green(sensorControl.currentColor));
            telemetry.addData("Blue", Color.blue(sensorControl.currentColor));
            telemetry.addData("Distance correct:",sensorControl.getDistance());
            telemetry.addData("Distance correct:",sensorControl.getDistance() < 70);

            telemetry.update();
        }
    }
}
