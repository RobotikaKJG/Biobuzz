package org.firstinspires.ftc.teamcode.HardwareInterface.Sensor;

import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

/**
 * Driver for goBilda RGB Signal Indicator (2701-0001-0001).
 * Maps colors to specific PWM microsecond values.
 */
public class GoBildaIndicator {
    private final ServoImplEx led;

    public enum Color {
        RED(1000),
        ORANGE(1143),
        YELLOW(1286),
        GREEN(1429),
        CYAN(1571),
        BLUE(1714),
        PURPLE(1857),
        WHITE(2000),
        OFF(500);

        public final int us;
        Color(int us) { this.us = us; }
    }

    public GoBildaIndicator(Servo ledServo) {
        this.led = (ServoImplEx) ledServo;
        // Set range to 500-2500ms as per goBilda specs
        this.led.setPwmRange(new PwmControl.PwmRange(500, 2500));
        setOff();
    }

    public void setColor(Color color) {
        // Map us to 0.0-1.0 range: (us - 500) / (2500 - 500)
        double pos = (color.us - 500.0) / 2000.0;
        led.setPosition(pos);
    }

    public void setOff() {
        setColor(Color.OFF);
    }
}
