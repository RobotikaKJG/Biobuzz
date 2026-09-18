package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;

/**
 * Called once per TeleOp loop after EdgeDetection refreshes. A Share/Back rising edge requests
 * a drive-mode toggle, then Drivebase computes powers using the selected speed. Without a heading
 * sensor configured, Drivebase ignores the toggle and continues robot-oriented driving.
 */
public class DrivebaseController {
    private final Drivebase drivebase;
    private final EdgeDetection edgeDetection;
    private final DrivebaseTrigger drivebaseTrigger = new DrivebaseTrigger();
    public DrivebaseController(Drivebase drivebase, EdgeDetection edgeDetection) {
        this.drivebase = drivebase;
        this.edgeDetection = edgeDetection;
    }

    public void updateState() {
        if (edgeDetection.rising(drivebaseTrigger.getTrigger()))
            drivebase.switchDrivingMode();
        drivebase.drive(DrivebaseConstants.getDriveSpeed());
    }
}
