package org.firstinspires.ftc.teamcode.Subsystems.Drivebase;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;

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
