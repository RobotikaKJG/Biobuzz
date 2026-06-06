package org.firstinspires.ftc.teamcode.Main;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.List;

/**
 * Background "control" loop. Owns the Lynx bulk cache and every non-drive,
 * non-turret subsystem: gamepad edge detection, LED, buttons, intake, outtake,
 * color sensors, and the single batched {@code setMotors(notDrive)} write.
 *
 * Threading: this is the ONLY thread that clears/reads the Lynx bulk cache and
 * the only writer of motors 4..7 ({@code notDrive}) and the lock servo. The drive
 * loop (main thread) owns motors 0..3; the turret loop owns the Pinpoint + turret
 * servos. The three loops touch disjoint hardware, so no locks are needed.
 */
public class ControlThread extends Thread {
    private final LinearOpMode opMode;
    private final List<LynxModule> allHubs;
    private final IterativeController iterativeController;
    private final LoopTimer loopTimer = new LoopTimer(10);
    private volatile boolean running = true;

    public ControlThread(LinearOpMode opMode, List<LynxModule> allHubs, IterativeController iterativeController) {
        super("ControlThread");
        this.opMode = opMode;
        this.allHubs = allHubs;
        this.iterativeController = iterativeController;
    }

    /** Average control-loop time over the last 10 iterations (ms). */
    public double getAvgLoopMs() {
        return loopTimer.getAvgMs();
    }

    public void stopLoop() {
        running = false;
    }

    @Override
    public void run() {
        while (running && !opMode.isStopRequested()) {
            long startNs = System.nanoTime();
            try {
                // Clear bulk cache once per loop — all hub reads this iteration use one snapshot.
                for (LynxModule hub : allHubs) {
                    hub.clearBulkCache();
                }
                iterativeController.TeleOp();
            } catch (Exception e) {
                // OpMode tearing down mid-call — exit cleanly.
                break;
            }
            loopTimer.record(System.nanoTime() - startNs);
        }
    }
}
