package org.firstinspires.ftc.teamcode.Main.ShooterTelemetry;

/**
 * One telemetry sample of the shooter (flywheel) subsystem. Plain struct built on
 * the control loop and handed to the {@link ShooterLogger} writer thread via a queue.
 *
 * Units: velocities/targets are encoder ticks/sec (the viewer converts to RPM using
 * ticksPerRev from the session header); motor currents in amps; battery in volts;
 * distance in inches.
 */
public class ShooterSample {
    public long tMs;            // ms since session start
    public double v1;           // outtake1 measured velocity (ticks/s)
    public double v2;           // outtake2 measured velocity (ticks/s)
    public double i1;           // outtake1 current (amps)
    public double i2;           // outtake2 current (amps)
    public double target;       // last commanded velocity (ticks/s), NaN if power-controlled
    public String shootState;   // AutoCycleShootStates name
    public String motorState;   // OuttakeMotorStates name
    public boolean intakeBall;
    public boolean transferBall;
    public double battery;      // volts, NaN if not sampled this tick
    public double distance;     // inches to goal, NaN when not shooting

    /** Serialize as one compact JSONL line. Hand-built (no reflection) — this runs
     *  on the writer thread but keeps allocation predictable. */
    public String toJsonLine() {
        StringBuilder sb = new StringBuilder(160);
        sb.append("{\"t\":").append(tMs);
        appendNum(sb, "v1", v1);
        appendNum(sb, "v2", v2);
        appendNum(sb, "i1", i1);
        appendNum(sb, "i2", i2);
        appendNum(sb, "tg", target);
        sb.append(",\"ss\":\"").append(shootState).append('"');
        sb.append(",\"ms\":\"").append(motorState).append('"');
        sb.append(",\"ib\":").append(intakeBall ? 1 : 0);
        sb.append(",\"tb\":").append(transferBall ? 1 : 0);
        appendNum(sb, "bat", battery);
        appendNum(sb, "d", distance);
        sb.append('}');
        return sb.toString();
    }

    /** Append ,"key":value rounded to 2 decimals; omit NaN/Inf entirely (JSON-safe). */
    private static void appendNum(StringBuilder sb, String key, double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return;
        sb.append(",\"").append(key).append("\":");
        // 2-decimal rounding without String.format (locale-safe, cheaper)
        double r = Math.round(v * 100.0) / 100.0;
        if (r == Math.rint(r)) sb.append((long) r);
        else sb.append(r);
    }
}
