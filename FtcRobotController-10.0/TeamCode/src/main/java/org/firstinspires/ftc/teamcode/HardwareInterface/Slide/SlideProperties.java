package org.firstinspires.ftc.teamcode.HardwareInterface.Slide;

/**
 * Calibration supplied to SlideLogic by a future mechanism. Position and step values use encoder
 * ticks; speed is normalized motor power. Keep measured values in that subsystem Constants class.
 */
public interface SlideProperties {
    int getSlideMaxExtension();
    int getSlideMinExtension();
    double getSlideMovementMaxSpeed();
    int getSlideExtensionStep();
    int getSlideFirstExtensionStep();

    void setSlideMaxSpeed(double slideMaxSpeed);
}
