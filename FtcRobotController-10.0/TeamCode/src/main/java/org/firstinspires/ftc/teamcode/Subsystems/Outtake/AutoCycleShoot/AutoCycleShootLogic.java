package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

/**
 * Select sequence transitions using elapsed time and sensor feedback.
 * The historical class name is retained for folder compatibility; no shooting behavior remains.
 * OuttakeControl calls Logic then Control each loop. Use timers, never sleep(), for future sequences.
 */
public class AutoCycleShootLogic {
    public void update() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case idle:
                break;
        }
    }
}
