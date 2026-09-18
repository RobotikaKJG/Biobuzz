package org.firstinspires.ftc.teamcode.HardwareInterface.Slide;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Contract for a future slide hardware adapter; no slide is created by the starter.
 * Implement using MotorControl and SensorControl, then pass the implementation to SlideLogic.
 * Define encoder zero, direction and the limit switch before allowing position commands.
 */
public interface SlideControl {
    void updateSlidePosition();
    int getSlidePosition();
    void setSlidePosition(int position);
    void setSlideMode(DcMotor.RunMode mode);
    void limitSpeed(double power);
    void resetEncoders();
    boolean isLimitSwitchPressed();
}
