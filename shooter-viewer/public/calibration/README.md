# Shooter distance calibration

Git-tracked data for the **Calibration** tab in the shooter viewer.

| File | Purpose |
|------|---------|
| `field.json` | DECODE field size, coordinate frames (Pedro vs TeleOp), goals, close RPM curve, draft robot footprint |
| `points.json` | Shooter locations with distance (cm) and RPM anchor / min–max |
| `/field/decode-field.webp` | Field art used as the map background |

## Workflow

1. Open the viewer → **Calibration**.
2. **Add point** → click the map at the robot’s shooter location.
3. Enter tape **distance (cm)** and capture the live robot pose/turret metadata;
   selecting the point draws that geometry on the map.
4. Add as many **RPM records** as needed. Each record is one attempted RPM and
   stores independent outcomes for balls 1, 2, and 3. Duplicate RPM records are
   valid; these are raw attempts, not unique settings.
5. The UI derives a verified RPM or observed successful span from records where
   all three balls scored. It does not assume a continuous range from incomplete
   or unsuccessful attempts.
6. **Save JSON** → overwrite `public/calibration/points.json` → commit.

The detail sidebar keeps three distance values separate:

- **UI geometry estimate** — calculated from the saved Pedro point to the field
  goal; read-only and without the robot's tuning offset.
- **Robot estimate** — captured from `SensorControl` telemetry; read-only.
- **Tape measurement** — the only manually editable distance.

Turret comparison is likewise read-only: the UI computes an angle from the
saved robot heading and field geometry, while the robot value is captured from
turret telemetry. The map draws both vectors when the required metadata exists.

The seeded **Close triangle tip** point matches `OuttakeConstants` (`275 cm` / `1697` ticks/s ≈ `3636` RPM).

Live robot overlay needs TeamCode that streams `px` / `py` / `ph` (localizer pose).
