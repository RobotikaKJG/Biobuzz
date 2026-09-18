package org.firstinspires.ftc.teamcode.Subsystems;

/**
 * Aggregate activity summary used by IntakeStates and OuttakeStates. Child enums describe the
 * actual mechanism requests; each aggregate controller must update this summary when adding states.
 */
public enum SubsystemState {
    Run,
    Idle
}
