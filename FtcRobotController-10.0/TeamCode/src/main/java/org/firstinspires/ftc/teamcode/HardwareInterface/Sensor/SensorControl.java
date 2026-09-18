package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.Main.GoBildaPinpointDriver;

/**
 * Sensor boundary shared by Drivebase and future mechanisms. Construction maps no hardware.
 * Dependencies may explicitly call initPinpoint() to enable field-oriented driving.
 * update() reads once per loop; getters never reset devices or perform additional I/O.
 * Add new sensor mapping here, then expose measurements rather than game-specific decisions.
 */
public class SensorControl {
    private final HardwareMap hardwareMap;
    private final EdgeDetection edgeDetection;
    private GoBildaPinpointDriver pinpoint;
    private double headingRadians;

    public SensorControl(HardwareMap hardwareMap, EdgeDetection edgeDetection) {
        this.hardwareMap = hardwareMap;
        this.edgeDetection = edgeDetection;
    }

    /** Opt-in hardware: configure "pinpointIMU" and calibrate its mounting before enabling. */
    public void initPinpoint() {
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpointIMU");
        pinpoint.initialize();
        pinpoint.resetPosAndIMU();
        headingRadians = 0;
    }

    public void update() {
        if (pinpoint == null) return;
        if (edgeDetection.rising(GamepadIndexValues.options)) pinpoint.resetPosAndIMU();
        pinpoint.update();
        headingRadians = pinpoint.getHeading();
    }

    public boolean hasHeading() { return pinpoint != null; }

    /** Radians; use hasHeading() to distinguish a real zero from an unconfigured sensor. */
    public double getPinpointAngle() { return headingRadians; }

    public void stop() {
        // Stop future streaming sensors/cameras here when the OpMode exits.
    }
}
