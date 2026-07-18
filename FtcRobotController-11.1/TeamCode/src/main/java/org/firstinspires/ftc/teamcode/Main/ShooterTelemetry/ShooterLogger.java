package org.firstinspires.ftc.teamcode.Main.ShooterTelemetry;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot.AutoCycleShootStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Records shooter (flywheel) telemetry to timestamped JSONL session files and feeds
 * the live-stream server ({@link ShooterTelemetryServer}).
 *
 * Threading: {@code sample()} is called from exactly one loop (the control loop in
 * TeleOp, the main loop in Autonomous) and must never block or throw — it builds a
 * sample and offers it to a bounded queue (drop-oldest on overflow). A background
 * writer thread drains the queue to disk and to the server. Same resilience
 * philosophy as ControlThread: a telemetry failure must never break robot control.
 *
 * Decimation: full loop rate while the shooter is active (motor commanded, shoot
 * sequence running, or a ball in the queue) plus a 2s tail — so every shot is
 * captured at full resolution — and ~10 Hz keepalive otherwise to keep files small.
 */
public class ShooterLogger {
    private static final String TAG = "ShooterLogger";
    public static final File LOG_DIR = new File(AppUtil.ROBOT_DATA_DIR, "shooter-logs");
    public static final int TICKS_PER_REV = 28; // goBILDA bare-motor encoder, 1:1 flywheel

    private static final int QUEUE_CAPACITY = 4096;
    private static final long ACTIVE_TAIL_MS = 2000;   // keep full rate this long after activity
    private static final long IDLE_PERIOD_MS = 100;    // ~10 Hz keepalive when idle
    private static final long BATTERY_PERIOD_MS = 250; // voltage read throttle (blocking ADC)
    private static final int MAX_LOG_FILES = 40;

    private final MotorControl motorControl;
    private final SensorControl sensorControl;

    private final ArrayBlockingQueue<ShooterSample> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private volatile boolean running = false;
    private Thread writerThread;
    private long sessionStartMs;

    // sample()-thread state (single caller thread; no sync needed)
    private long lastActiveMs = 0;
    private long lastIdleSampleMs = 0;
    private long lastBatteryMs = 0;
    private double cachedBattery = Double.NaN;

    public ShooterLogger(MotorControl motorControl, SensorControl sensorControl) {
        this.motorControl = motorControl;
        this.sensorControl = sensorControl;
    }

    /** Start a logging session. Call after waitForStart(). Never throws. */
    public void start(String opModeName) {
        if (running) return;
        try {
            //noinspection ResultOfMethodCallIgnored
            LOG_DIR.mkdirs();
            pruneOldLogs();

            sessionStartMs = System.currentTimeMillis();
            String stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
                    .format(new Date(sessionStartMs));
            File file = new File(LOG_DIR, stamp + "_" + opModeName + ".jsonl");

            String headerJson = "{\"type\":\"header\",\"version\":1"
                    + ",\"epochMs\":" + sessionStartMs
                    + ",\"opMode\":\"" + opModeName + "\""
                    + ",\"alliance\":\"" + GlobalVariables.alliance + "\""
                    + ",\"ticksPerRev\":" + TICKS_PER_REV
                    + "}";

            queue.clear();
            running = true;
            writerThread = new Thread(() -> writerLoop(file, headerJson), TAG + "-writer");
            writerThread.setDaemon(true);
            writerThread.start();

            ShooterTelemetryServer server = ShooterTelemetryServer.getInstanceOrNull();
            if (server != null) server.onSessionStart(opModeName, headerJson);

            RobotLog.ii(TAG, "session started: %s", file.getName());
        } catch (Throwable t) {
            running = false;
            RobotLog.ee(TAG, t, "failed to start session (continuing without logging)");
        }
    }

    /** End the session: flush + close the file, notify the server. Never throws. */
    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (writerThread != null) writerThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        writerThread = null;
        ShooterTelemetryServer server = ShooterTelemetryServer.getInstanceOrNull();
        if (server != null) server.onSessionEnd();
        RobotLog.ii(TAG, "session stopped");
    }

    /**
     * Record one sample. Called once per loop iteration by exactly one thread.
     * Non-blocking, never throws. Velocity/IR reads hit the caller's Lynx bulk-cache
     * snapshot, so the added I2C cost is ~zero.
     */
    public void sample() {
        if (!running) return;
        try {
            long now = System.currentTimeMillis();

            OuttakeMotorStates motorState = OuttakeStates.getMotorState();
            boolean intakeBall = sensorControl.isIntakeBall();
            boolean transferBall = sensorControl.isTransferBall();

            boolean active = motorState != OuttakeMotorStates.idle
                    || OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.idle
                    || intakeBall || transferBall;
            if (active) lastActiveMs = now;

            boolean fullRate = (now - lastActiveMs) < ACTIVE_TAIL_MS;
            if (!fullRate) {
                if (now - lastIdleSampleMs < IDLE_PERIOD_MS) return;
                lastIdleSampleMs = now;
            }

            ShooterSample s = new ShooterSample();
            s.tMs = now - sessionStartMs;
            s.v1 = motorControl.getMotorVelocity(MotorConstants.outtake1);
            s.v2 = motorControl.getMotorVelocity(MotorConstants.outtake2);
            s.target = motorControl.getLastCommandedVelocity(MotorConstants.outtake1);
            s.shootState = OuttakeStates.getAutoCycleShootState().name();
            s.motorState = motorState.name();
            s.intakeBall = intakeBall;
            s.transferBall = transferBall;

            // Battery is a blocking ADC read not covered by bulk caching — throttle it.
            if (now - lastBatteryMs >= BATTERY_PERIOD_MS) {
                cachedBattery = motorControl.getBatteryVoltage();
                lastBatteryMs = now;
                s.battery = cachedBattery;
            } else {
                s.battery = Double.NaN; // omitted from JSON; viewer carries last value forward
            }

            // Distance drives the interpolated target — only meaningful (and only cheap
            // to justify) while the flywheel is commanded.
            s.distance = (motorState != OuttakeMotorStates.idle && motorState != OuttakeMotorStates.backward)
                    ? sensorControl.getDistanceFromLocalizer() : Double.NaN;

            if (!queue.offer(s)) {
                queue.poll(); // drop oldest, keep newest
                queue.offer(s);
            }
        } catch (Throwable t) {
            // Never let telemetry break the control loop. Rare, so log unthrottled.
            RobotLog.ee(TAG, t, "sample failed (continuing)");
        }
    }

    // ---------------- writer thread ----------------

    private void writerLoop(File file, String headerJson) {
        ShooterTelemetryServer server = ShooterTelemetryServer.getInstanceOrNull();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file), 1 << 16)) {
            out.write(headerJson);
            out.newLine();
            long lastFlushMs = System.currentTimeMillis();
            while (running || !queue.isEmpty()) {
                ShooterSample s = queue.poll(50, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (s != null) {
                    String line = s.toJsonLine();
                    out.write(line);
                    out.newLine();
                    if (server != null) server.publishSample(line);
                }
                long now = System.currentTimeMillis();
                if (now - lastFlushMs >= 1000) { // survive app kill with ≤1s data loss
                    out.flush();
                    lastFlushMs = now;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            RobotLog.ee(TAG, t, "writer thread failed; session file may be incomplete");
        }
    }

    private void pruneOldLogs() {
        File[] files = LOG_DIR.listFiles((d, n) -> n.endsWith(".jsonl"));
        if (files == null || files.length < MAX_LOG_FILES) return;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        for (int i = 0; i <= files.length - MAX_LOG_FILES; i++) {
            //noinspection ResultOfMethodCallIgnored
            files[i].delete();
        }
    }
}
