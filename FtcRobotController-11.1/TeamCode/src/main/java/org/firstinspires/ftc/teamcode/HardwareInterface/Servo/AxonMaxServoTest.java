package org.firstinspires.ftc.teamcode.HardwareInterface.Servo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "AxonMaxServoTest", group = "TeleOp")
public class AxonMaxServoTest extends LinearOpMode {

    private Servo axonServoOne;
    private Servo axonServoTwo;

    @Override
    public void runOpMode() {
        axonServoOne = hardwareMap.get(Servo.class, "axonServoOne");
        axonServoTwo = hardwareMap.get(Servo.class, "axonServoTwo");

        // Map 150 degrees to servo position (assuming 0-300 deg -> 0.0-1.0)
        double degrees = 150.0;
        double position = degrees / 255.0;

        telemetry.addLine("Press A to move servo to 150 degrees");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) {
                axonServoOne.setPosition(position);
                axonServoTwo.setPosition(position);
                telemetry.addData("Commanded Position (deg)", degrees);
                telemetry.addData("Set Position (0.0-1.0)", position);
                telemetry.update();
            }
        }
    }
}
