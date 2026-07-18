# Shooter Telemetry Viewer

Laptop viewer for flywheel RPM logs recorded by the robot (`Main/ShooterTelemetry/` in
TeamCode). Shows RPM of both outtake motors vs target, highlights feed windows, and
computes **per-shot RPM drop / recovery** automatically.

## Setup (mac / windows — needs Node 18+)

```sh
cd shooter-viewer
npm install
npm run dev        # starts local API (:5174) + Vite UI → http://localhost:5173
```

`npm run dev` starts the UI **and** a local backend. Pressing **Connect to Hub** in the
UI is the only step — the backend runs WiFi/ping/telemetry checks (via `hub-live.sh`)
and then opens the WebSocket. You do not run `hub-live.sh` yourself.

## Network (important)

**You** join the robot’s WiFi (RC AP). The robot does **not** join your laptop’s
network. Default hub address: `192.168.43.1:8765`.

## Using it

- **Live**: click **Connect to Hub**. `hub-live.sh` auto-detects if you’re already on
  the hub subnet (`192.168.43.x`) and skips the WiFi switch; otherwise it joins the
  robot AP, pings, and probes `:8765`. Console streams every step. WebSocket opens
  when ready. Badge shows hub/telemetry reachability. Start an OpMode once after
  boot so the telemetry server is listening.
- **Recorded sessions**: the robot keeps the last ~40 runs in
  `/sdcard/FIRST/shooter-logs/`. While connected (an OpMode has run at least once
  since boot), the sidebar lists them — click to view. Or `npm run pull` to download
  all of them into `shooter-viewer/logs/`, then drag-drop a file into the app.
- **Shot analysis**: each burst (auto-cycle shoot) gets a table — min RPM per shot,
  drop from target (absolute + %), per-motor minimums, recovery time, spacing.
  Red dashed lines on the chart mark detected shots.
- **Control**: keyboard remote over the same WebSocket. Enable keyboard, then
  WASD + `,`/`.` rotate, Shift intake, Space shoot, etc. Needs a running
  TeleOp (redeploy TeamCode so `RemoteControl` is on the robot).
- **Calibration**: DECODE field map with Pedro/TeleOp coordinates, goals, and
  shooter distance/RPM points (git-tracked under `public/calibration/`). Seeded
  with the close triangle-tip anchor (275 cm / ~3636 RPM).

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
