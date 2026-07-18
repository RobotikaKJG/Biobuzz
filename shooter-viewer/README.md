# Shooter Telemetry Viewer

Laptop viewer for flywheel RPM logs recorded by the robot (`Main/ShooterTelemetry/` in
TeamCode). Shows RPM of both outtake motors vs target, highlights feed windows, and
computes **per-shot RPM drop / recovery** automatically.

## Setup (mac / windows — needs Node 18+)

```sh
cd shooter-viewer
npm install
npm run dev        # opens http://localhost:5173
```

## Network (important)

**You** join the robot’s WiFi (phone hotspot / RC AP). The robot does **not**
join your laptop’s network. On that robot AP, the RC is usually
`192.168.43.1` — the viewer opens a client connection to the robot’s server
on port `8765`. No reverse tunnel, no robot dialing out.

## Using it

- **Live**: join the robot WiFi on the laptop, then hit **Connect** (default
  `192.168.43.1:8765`). The dot goes green + **LIVE** while an OpMode is running;
  samples stream in real time. Uncheck *follow* (or drag/scroll the chart) to scrub
  back through the session while it's still recording. **Save .jsonl** keeps a copy.
- **Recorded sessions**: the robot keeps the last ~40 runs in
  `/sdcard/FIRST/shooter-logs/`. While connected (an OpMode has run at least once
  since boot), the sidebar lists them — click to view. Or `npm run pull` to download
  all of them into `shooter-viewer/logs/`, then drag-drop a file into the app.
- **Shot analysis**: each burst (auto-cycle shoot) gets a table — min RPM per shot,
  drop from target (absolute + %), per-motor minimums, recovery time, spacing.
  Red dashed lines on the chart mark detected shots.

Chart controls: drag = zoom, wheel = zoom at cursor, double-click = reset.

## Testing without the robot

```sh
npm run mock-robot   # fake robot on localhost:8766 (streams a live session every ~6s)
npm run dev          # connect the viewer to: localhost:8766  (or localhost:5173 — the
                     # dev server proxies /api and /ws to the mock)
```

Or click **Load demo data** in the sidebar (`public/demo.jsonl`, regenerate with
`npm run gen-demo` — it prints the true synthetic drop values to compare against the
analysis table).

## Log format

JSONL, one session per file. First line is a header
(`{"type":"header","epochMs":...,"opMode":...,"ticksPerRev":28}`), then samples:

```json
{"t":12345,"v1":1712.4,"v2":1698,"tg":1750,"ss":"turnTransfer","ms":"forwardClose","ib":1,"tb":0,"bat":12.44,"d":38.2}
```

`t` ms since start · `v1/v2` motor velocities (ticks/s) · `tg` commanded velocity ·
`ss` AutoCycleShootState · `ms` OuttakeMotorState · `ib/tb` intake/transfer ball IR ·
`bat` volts (throttled) · `d` distance to goal (in). RPM = ticks/s ÷ ticksPerRev × 60.

Full rate (~control-loop speed) while the shooter is active + 2 s tail; ~10 Hz when idle.
