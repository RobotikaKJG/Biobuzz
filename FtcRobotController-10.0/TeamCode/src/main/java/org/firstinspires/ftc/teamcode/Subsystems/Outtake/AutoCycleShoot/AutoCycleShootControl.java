package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Translate the selected sequence stage into child subsystem state requests.
 * The historical class name is retained for folder compatibility; no shooting behavior remains.
 * OuttakeControl calls Logic then Control each loop. Use timers, never sleep(), for future sequences.
 */
public class AutoCycleShootControl {
    public void update() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case idle:
                break;
        }
    }
}
