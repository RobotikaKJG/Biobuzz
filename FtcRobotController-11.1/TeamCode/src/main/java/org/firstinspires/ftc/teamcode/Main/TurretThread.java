package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.TurretServo.TurretServoControl;

/**
 * Turret tracking loop. Owns the Pinpoint odometry ({@code localizer.update()} +
 * the options-button heading reset) and the turret servos, so the turret always
 * aims from the freshest pose, independent of the heavier control loop.
 *
 * Threading: the Pinpoint is an I2C device read only here (it is NOT part of the
 * Lynx bulk cache, so it never races the control loop's cached reads). This is
 * the only writer of the turret servos and the only thread that mutates the
 * localizer pose; the drive and control loops only read the cached pose
 * (benign one-iteration staleness).
 */
public class TurretThread extends Thread {
    private static final long TARGET_PERIOD_MS = 5; // ~200 Hz cap; the Pinpoint read dominates

    private final LinearOpMode opMode;
    private final SensorControl sensorControl;
    private final TurretServoControl turretServoControl;
    private final Gamepad gamepad1;
    private final Gamepad currentGamepad = new Gamepad();
    private final Gamepad prevGamepad = new Gamepad();
    private final EdgeDetection edgeDetection = new EdgeDetection();
    private final LoopTimer loopTimer = new LoopTimer(10);
    private volatile boolean running = true;

    public TurretThread(LinearOpMode opMode, SensorControl sensorControl,
                        TurretServoControl turretServoControl, Gamepad gamepad1) {
        super("TurretThread");
        this.opMode = opMode;
        this.sensorControl = sensorControl;
        this.turretServoControl = turretServoControl;
        this.gamepad1 = gamepad1;
    }

    /** Average turret-loop time over the last 10 iterations (ms). */
    public double getAvgLoopMs() {
        return loopTimer.getAvgMs();
    }

    public void stopLoop() {
        running = false;
    }

    @Override
    public void run() {
        long prevLoopNs = System.nanoTime();
        while (running && !opMode.isStopRequested()) {
            long startNs = System.nanoTime();
            loopTimer.record(startNs - prevLoopNs); // full loop PERIOD (incl. sleep) -> true rate
            prevLoopNs = startNs;
            try {
                // Own gamepad snapshot + edge detection (options = reset heading).
                prevGamepad.copy(currentGamepad);
                currentGamepad.copy(gamepad1);
                edgeDetection.refreshGamepadIndex(currentGamepad, prevGamepad);

                sensorControl.updateLocalizer();              // Pinpoint read + velocity calc
                if (edgeDetection.rising(GamepadIndexValues.options)) {
                    sensorControl.resetLocalizerAngleNow();
                }
                turretServoControl.update();                  // aim from pose, write turret servos
            } catch (Exception e) {
                break;
            }

            long workMs = (System.nanoTime() - startNs) / 1_000_000L;
            long sleepMs = TARGET_PERIOD_MS - workMs;
            if (sleepMs > 0) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
