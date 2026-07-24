import { useCallback, useEffect, useMemo, useState } from 'react'
import FieldMap from './FieldMap'
import type { Sample } from '../lib/types'
import type { BallResult, CalibFile, CalibPoint, CalibTrial, FieldMeta } from '../lib/field'
import {
  INCH_TO_CM,
  newPointId,
  newTrialId,
  pedroToTeleop,
  softwareGoalDistanceCm,
  softwareTurretAngleDeg,
  ticksToRpm,
  toPedroPose,
} from '../lib/field'

type Props = {
  samples: Sample[]
  dataVersion: number
  liveConnected: boolean
}

function trialSummary(trials: CalibTrial[]) {
  const valid = trials.filter((trial) => trial.rpm != null && Number.isFinite(trial.rpm))
  const complete = valid.filter((trial) =>
    (['first', 'second', 'third'] as const).every((ball) => trial.balls[ball] === 'scored'),
  )
  if (complete.length === 0) {
    const scored = valid.reduce(
      (total, trial) =>
        total
        + (['first', 'second', 'third'] as const).filter(
          (ball) => trial.balls[ball] === 'scored',
        ).length,
      0,
    )
    return valid.length
      ? `No complete 3-ball setting yet · ${scored}/${valid.length * 3} balls scored`
      : 'No RPM attempts yet'
  }

  const rpms = complete.map((trial) => trial.rpm as number)
  const min = Math.min(...rpms)
  const max = Math.max(...rpms)
  return min === max
    ? `${min} RPM verified · ${complete.length} complete 3-ball run${complete.length === 1 ? '' : 's'}`
    : `Observed successful span ${min}–${max} RPM · ${complete.length} complete 3-ball runs`
}

export default function CalibrationView({ samples, dataVersion, liveConnected }: Props) {
  const [field, setField] = useState<FieldMeta | null>(null)
  const [file, setFile] = useState<CalibFile | null>(null)
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const [mode, setMode] = useState<'select' | 'add'>('select')
  const [alliance, setAlliance] = useState<'red' | 'blue'>('red')
  const [error, setError] = useState<string | null>(null)
  const [dirty, setDirty] = useState(false)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        const [f, p] = await Promise.all([
          fetch('/calibration/field.json').then((r) => r.json()),
          fetch('/calibration/points.json').then((r) => r.json()),
        ])
        if (cancelled) return
        setField(f)
        setFile(p)
        if (p.points?.[0]) setSelectedId(p.points[0].id)
      } catch (e) {
        if (!cancelled) setError(`Failed to load calibration data: ${e}`)
      }
    })()
    return () => {
      cancelled = true
    }
  }, [])

  const selected = useMemo(
    () => file?.points.find((p) => p.id === selectedId) ?? null,
    [file, selectedId],
  )

  const livePose = useMemo(() => {
    for (let i = samples.length - 1; i >= 0; i--) {
      const s = samples[i]
      if (typeof s.px === 'number' && typeof s.py === 'number') {
        return {
          x: s.px,
          y: s.py,
          headingDeg: typeof s.ph === 'number' ? s.ph : 0,
        }
      }
    }
    return null
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [samples, dataVersion])

  const liveDistanceCm = useMemo(() => {
    for (let i = samples.length - 1; i >= 0; i--) {
      if (typeof samples[i].d === 'number') return samples[i].d! * INCH_TO_CM
    }
    return null
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [samples, dataVersion])

  const liveTelemetry = useMemo(() => {
    const latest = samples.at(-1)
    let headingDeg: number | null = null
    let turretAngleDeg: number | null = null
    let targetTicks: number | null = null
    let batteryV: number | null = null
    for (let i = samples.length - 1; i >= 0; i--) {
      const sample = samples[i]
      if (headingDeg == null && typeof sample.ph === 'number') headingDeg = sample.ph
      if (turretAngleDeg == null && typeof sample.ta === 'number') turretAngleDeg = sample.ta
      if (targetTicks == null && typeof sample.tg === 'number') targetTicks = sample.tg
      if (batteryV == null && typeof sample.bat === 'number') batteryV = sample.bat
      if (
        headingDeg != null
        && turretAngleDeg != null
        && targetTicks != null
        && batteryV != null
      ) break
    }
    return {
      headingDeg,
      turretAngleDeg,
      targetTicks,
      batteryV,
      v1: latest?.v1 ?? null,
      v2: latest?.v2 ?? null,
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [samples, dataVersion])

  const updatePoint = useCallback((id: string, patch: Partial<CalibPoint>) => {
    setFile((prev) => {
      if (!prev) return prev
      return {
        ...prev,
        updatedAt: new Date().toISOString().slice(0, 10),
        points: prev.points.map((p) => (p.id === id ? { ...p, ...patch } : p)),
      }
    })
    setDirty(true)
  }, [])

  const addAt = useCallback(
    (pedro: { x: number; y: number }) => {
      if (!file || !field) return
      const id = newPointId()
      const pt: CalibPoint = {
        id,
        label: `Point ${file.points.length + 1}`,
        alliance,
        pose: {
          x: Math.round(pedro.x * 10) / 10,
          y: Math.round(pedro.y * 10) / 10,
          headingDeg: liveTelemetry.headingDeg,
        },
        distance: {
          robotEstimateCm: liveDistanceCm == null ? null : Math.round(liveDistanceCm),
          tapeMeasuredCm: null,
        },
        turret: {
          robotEstimateDeg: liveTelemetry.turretAngleDeg,
        },
        trials: [],
        notes: '',
      }
      setFile({
        ...file,
        updatedAt: new Date().toISOString().slice(0, 10),
        points: [...file.points, pt],
      })
      setSelectedId(id)
      setMode('select')
      setDirty(true)
    },
    [alliance, field, file, liveDistanceCm, liveTelemetry],
  )

  const removeSelected = () => {
    if (!file || !selected) return
    if (selected.id === 'triangle-tip-close') {
      if (!confirm('Remove the seeded triangle-tip anchor point?')) return
    }
    const next = file.points.filter((p) => p.id !== selected.id)
    setFile({ ...file, points: next, updatedAt: new Date().toISOString().slice(0, 10) })
    setSelectedId(next[0]?.id ?? null)
    setDirty(true)
  }

  const downloadJson = () => {
    if (!file) return
    const blob = new Blob([JSON.stringify(file, null, 2) + '\n'], {
      type: 'application/json',
    })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = 'points.json'
    a.click()
    URL.revokeObjectURL(a.href)
    setDirty(false)
  }

  const addTrial = () => {
    if (!selected) return
    const commandedRpm =
      liveTelemetry.targetTicks == null
        ? null
        : Math.round(ticksToRpm(liveTelemetry.targetTicks, file?.ticksPerRev ?? 28))
    const trial: CalibTrial = {
      id: newTrialId(),
      rpm: commandedRpm,
      balls: { first: 'unknown', second: 'unknown', third: 'unknown' },
      telemetry: liveConnected
        ? {
            capturedAt: new Date().toISOString(),
            robotHeadingDeg: liveTelemetry.headingDeg,
            turretAngleDeg: liveTelemetry.turretAngleDeg,
            commandedTicksPerSec: liveTelemetry.targetTicks,
            measuredRpm1:
              liveTelemetry.v1 == null
                ? null
                : Math.round(ticksToRpm(liveTelemetry.v1, file?.ticksPerRev ?? 28)),
            measuredRpm2:
              liveTelemetry.v2 == null
                ? null
                : Math.round(ticksToRpm(liveTelemetry.v2, file?.ticksPerRev ?? 28)),
            batteryV: liveTelemetry.batteryV,
            distanceCm: liveDistanceCm == null ? null : Math.round(liveDistanceCm),
          }
        : undefined,
      notes: '',
    }
    updatePoint(selected.id, {
      trials: [...(selected.trials ?? []), trial],
      pose: {
        ...selected.pose,
        headingDeg: selected.pose.headingDeg ?? liveTelemetry.headingDeg,
      },
      turret: {
        robotEstimateDeg:
          selected.turret.robotEstimateDeg ?? liveTelemetry.turretAngleDeg,
      },
      distance: {
        ...selected.distance,
        robotEstimateCm:
          selected.distance.robotEstimateCm
          ?? (liveDistanceCm == null ? null : Math.round(liveDistanceCm)),
      },
    })
  }

  const capturePointFromLive = () => {
    if (!selected || !livePose) return
    const pedro = toPedroPose(livePose.x, livePose.y)
    updatePoint(selected.id, {
      pose: {
        ...selected.pose,
        x: Math.round(pedro.x * 10) / 10,
        y: Math.round(pedro.y * 10) / 10,
        headingDeg: liveTelemetry.headingDeg,
      },
      turret: {
        robotEstimateDeg: liveTelemetry.turretAngleDeg,
      },
      distance: {
        ...selected.distance,
        robotEstimateCm: liveDistanceCm == null ? null : Math.round(liveDistanceCm),
      },
    })
  }

  const updateTrial = (trialId: string, patch: Partial<CalibTrial>) => {
    if (!selected) return
    updatePoint(selected.id, {
      trials: (selected.trials ?? []).map((trial) =>
        trial.id === trialId ? { ...trial, ...patch } : trial,
      ),
    })
  }

  const removeTrial = (trialId: string) => {
    if (!selected) return
    updatePoint(selected.id, {
      trials: (selected.trials ?? []).filter((trial) => trial.id !== trialId),
    })
  }

  if (error) {
    return <div className="calibration-body"><p className="error-bar">{error}</p></div>
  }
  if (!field || !file) {
    return <div className="calibration-body"><p className="control-hero-empty">Loading field…</p></div>
  }

  const curve = field.distancePipeline.closeCurve
  const tpr = file.ticksPerRev || curve.ticksPerRev
  const goalForPoint = (point: CalibPoint) =>
    field.goalsInches.pedroCorner[point.alliance === 'either' ? alliance : point.alliance]
  const selectedGoal = selected ? goalForPoint(selected) : null
  const selectedSoftwareDistance =
    selected && selectedGoal ? softwareGoalDistanceCm(selected, selectedGoal) : null
  const selectedSoftwareTurret =
    selected && selectedGoal ? softwareTurretAngleDeg(selected, selectedGoal) : null

  return (
    <div className="calibration-body">
      <section className="calib-ref">
        <span className="readonly-badge">Read-only field reference</span>
        <div>
          <strong>Field</strong> {field.field.sizeInches}" ({field.field.sizeCm.toFixed(1)} cm) ·{' '}
          {field.field.tiles}×{field.field.tiles} tiles · {field.field.tileInches}" /{' '}
          {field.field.tileCm} cm
        </div>
        <div>
          <strong>Frames</strong> Pedro corner [0,144] in (auton) · TeleOp center = Pedro − 72
        </div>
        <div>
          <strong>Close curve</strong> √(d/{curve.anchorDistanceCm} cm) × {curve.anchorTicksPerSec}{' '}
          ticks/s (~{ticksToRpm(curve.anchorTicksPerSec, tpr).toFixed(0)} RPM) · clamp{' '}
          {curve.minDistanceCm}–{curve.maxDistanceCm} cm
        </div>
        <div>
          <strong>Goals (Pedro)</strong> Red ({field.goalsInches.pedroCorner.red.x},{' '}
          {field.goalsInches.pedroCorner.red.y}) · Blue ({field.goalsInches.pedroCorner.blue.x},{' '}
          {field.goalsInches.pedroCorner.blue.y})
        </div>
      </section>

      {selected && (
        <section className="calib-snapshot" aria-label="Read-only selected point metadata">
          <div className="calib-snapshot-title">
            <div>
              <strong>{selected.label}</strong>
              <span className="readonly-badge">Read-only metadata</span>
            </div>
            <button
              type="button"
              className="secondary"
              onClick={capturePointFromLive}
              disabled={!livePose}
              title="Replace robot-sourced metadata with the latest live telemetry"
            >
              Refresh from live robot
            </button>
          </div>

          <div className="calib-snapshot-grid">
            <article>
              <h3>Position</h3>
              <div className="snapshot-row">
                <span>Pedro</span>
                <strong>{selected.pose.x.toFixed(1)}, {selected.pose.y.toFixed(1)} in</strong>
              </div>
              <div className="snapshot-row">
                <span>TeleOp</span>
                <strong>
                  {pedroToTeleop(selected.pose.x, selected.pose.y).x.toFixed(1)},{' '}
                  {pedroToTeleop(selected.pose.x, selected.pose.y).y.toFixed(1)} in
                </strong>
              </div>
            </article>

            <article>
              <h3>Robot orientation</h3>
              <div className="snapshot-row">
                <span>Robot telemetry</span>
                <strong>
                  {selected.pose.headingDeg == null
                    ? 'Not captured'
                    : `${selected.pose.headingDeg.toFixed(1)}°`}
                </strong>
              </div>
            </article>

            <article>
              <h3>Distance to goal</h3>
              <div className="snapshot-row computed">
                <span>UI estimate</span>
                <strong>
                  {selectedSoftwareDistance == null
                    ? '—'
                    : `${selectedSoftwareDistance.toFixed(1)} cm`}
                </strong>
              </div>
              <div className="snapshot-row robot">
                <span>Robot estimate</span>
                <strong>
                  {selected.distance.robotEstimateCm == null
                    ? 'Not captured'
                    : `${selected.distance.robotEstimateCm.toFixed(1)} cm`}
                </strong>
              </div>
            </article>

            <article>
              <h3>Turret angle</h3>
              <div className="snapshot-row computed">
                <span>UI estimate</span>
                <strong>
                  {selectedSoftwareTurret == null
                    ? 'Needs heading'
                    : `${selectedSoftwareTurret.toFixed(1)}°`}
                </strong>
              </div>
              <div className="snapshot-row robot">
                <span>Robot estimate</span>
                <strong>
                  {selected.turret.robotEstimateDeg == null
                    ? 'Not captured'
                    : `${selected.turret.robotEstimateDeg.toFixed(1)}°`}
                </strong>
              </div>
            </article>
          </div>
        </section>
      )}

      <div className="calib-workspace">
        <aside className="calib-points-side">
          <div className="calib-side-header">
            <div>
              <h2>Calibration points</h2>
              <span>{file.points.length} saved</span>
            </div>
            <button type="button" onClick={() => setMode('add')}>
              + Add
            </button>
          </div>
          <ul className="calib-list">
            {file.points.map((point) => {
              const softwareDistance = softwareGoalDistanceCm(point, goalForPoint(point))
              return (
                <li
                  key={point.id}
                  className={point.id === selectedId ? 'selected' : ''}
                  onClick={() => setSelectedId(point.id)}
                >
                  <strong>{point.label}</strong>
                  <span>
                    UI {softwareDistance.toFixed(0)} cm · tape{' '}
                    {point.distance.tapeMeasuredCm?.toFixed(0) ?? '—'} cm
                  </span>
                  <span>{point.trials?.length ?? 0} attempts</span>
                  <span className="calib-point-summary">
                    {trialSummary(point.trials ?? [])}
                  </span>
                </li>
              )
            })}
          </ul>
        </aside>

        <div className="calib-map-col">
          <div className="calib-toolbar">
            <div className="mode-toggle">
              <button
                type="button"
                className={mode === 'select' ? 'active' : ''}
                onClick={() => setMode('select')}
              >
                Select
              </button>
              <button
                type="button"
                className={mode === 'add' ? 'active' : ''}
                onClick={() => setMode('add')}
              >
                Add point
              </button>
            </div>
            <label>
              Alliance goal
              <select
                value={alliance}
                onChange={(e) => setAlliance(e.target.value as 'red' | 'blue')}
              >
                <option value="red">Red</option>
                <option value="blue">Blue</option>
              </select>
            </label>
            <button type="button" onClick={downloadJson} title="Download points.json to commit">
              {dirty ? 'Save JSON *' : 'Save JSON'}
            </button>
            {liveConnected && liveDistanceCm != null && (
              <span className="live-d">Live d ≈ {liveDistanceCm.toFixed(0)} cm</span>
            )}
          </div>

          <FieldMap
            field={field}
            points={file.points}
            selectedId={selectedId}
            mode={mode}
            livePose={livePose}
            alliance={alliance}
            onSelect={setSelectedId}
            onMapClick={addAt}
          />
        </div>

        <aside className="calib-records-side">
          <div className="calib-side-header">
            <div>
              <h2>Calibration inputs</h2>
              <span>{selected ? trialSummary(selected.trials ?? []) : 'Select a point'}</span>
            </div>
          </div>
          {selected ? (
            <div className="calib-editor">
              <div className="editable-notice">
                <span className="editable-badge">Editable</span>
                Values entered during calibration
              </div>
              <label>
                Point label
                <input
                  value={selected.label}
                  onChange={(e) => updatePoint(selected.id, { label: e.target.value })}
                />
              </label>
              <section className="editable-section">
                <div className="editable-section-title">
                  <h3>Measured distance</h3>
                  <span className="editable-badge">Editable</span>
                </div>
                <label>
                  Tape measurement (cm)
                  <input
                    type="number"
                    step={0.1}
                    value={selected.distance.tapeMeasuredCm ?? ''}
                    placeholder="Enter measured distance"
                    onChange={(e) =>
                      updatePoint(selected.id, {
                        distance: {
                          ...selected.distance,
                          tapeMeasuredCm:
                            e.target.value === '' ? null : Number(e.target.value),
                        },
                      })
                    }
                  />
                </label>
              </section>

              <section className="trial-section">
                <div className="trial-section-header">
                  <div>
                    <h3>RPM records</h3>
                    <span>
                      <span className="editable-badge">Editable</span> One row per attempt; duplicates
                      allowed.
                    </span>
                  </div>
                  <button type="button" className="secondary" onClick={addTrial}>
                    + Record
                  </button>
                </div>

                {(selected.trials ?? []).length === 0 && (
                  <p className="calib-muted">No attempts recorded at this point yet.</p>
                )}

                {(selected.trials ?? []).map((trial, index) => (
                  <article className="trial-card" key={trial.id}>
                    <div className="trial-card-header">
                      <strong>Record {index + 1}</strong>
                      <button
                        type="button"
                        className="trial-delete"
                        onClick={() => removeTrial(trial.id)}
                        title="Delete this RPM record"
                      >
                        ×
                      </button>
                    </div>

                    <label>
                      Attempted RPM
                      <input
                        type="number"
                        step={1}
                        value={trial.rpm ?? ''}
                        onChange={(e) =>
                          updateTrial(trial.id, {
                            rpm: e.target.value === '' ? null : Number(e.target.value),
                          })
                        }
                      />
                    </label>

                    <div className="ball-results">
                      {(['first', 'second', 'third'] as const).map((ball, ballIndex) => (
                        <label key={ball}>
                          Ball {ballIndex + 1}
                          <select
                            value={trial.balls?.[ball] ?? 'unknown'}
                            onChange={(e) =>
                              updateTrial(trial.id, {
                                balls: {
                                  ...trial.balls,
                                  [ball]: e.target.value as BallResult,
                                },
                              })
                            }
                          >
                            <option value="unknown">Not recorded</option>
                            <option value="undershot">Undershot</option>
                            <option value="overshot">Overshot</option>
                            <option value="backspin_out">Reached target — backspin out</option>
                            <option value="scored">Scored successfully</option>
                          </select>
                        </label>
                      ))}
                    </div>

                    {trial.telemetry && (
                      <div className="trial-telemetry">
                        <span>
                          Robot {trial.telemetry.robotHeadingDeg?.toFixed(1) ?? '—'}° · turret{' '}
                          {trial.telemetry.turretAngleDeg?.toFixed(1) ?? '—'}°
                        </span>
                        <span>
                          Wheels {trial.telemetry.measuredRpm1 ?? '—'} /{' '}
                          {trial.telemetry.measuredRpm2 ?? '—'} RPM
                          {trial.telemetry.batteryV != null
                            ? ` · ${trial.telemetry.batteryV.toFixed(1)} V`
                            : ''}
                        </span>
                      </div>
                    )}

                    <label>
                      Attempt notes
                      <input
                        value={trial.notes ?? ''}
                        placeholder="Optional"
                        onChange={(e) => updateTrial(trial.id, { notes: e.target.value })}
                      />
                    </label>
                  </article>
                ))}
              </section>

              <label>
                Point notes (editable)
                <textarea
                  rows={3}
                  value={selected.notes ?? ''}
                  onChange={(e) => updatePoint(selected.id, { notes: e.target.value })}
                />
              </label>

              <div className="calib-actions">
                <button type="button" className="danger" onClick={removeSelected}>
                  Delete point
                </button>
              </div>
            </div>
          ) : (
            <p className="calib-muted">Select a point or switch to Add point and click the map.</p>
          )}

          <div className="calib-help">
            <h3>How to use</h3>
            <ol>
              <li>Add point → click shooter location on the field.</li>
              <li>Capture robot/turret geometry, then add one record per attempted RPM.</li>
              <li>Mark each ball outcome; successful RPM bounds are inferred automatically.</li>
              <li>Save JSON → replace <code>public/calibration/points.json</code> and commit.</li>
            </ol>
            <p>
              Robot footprint / shooter & Limelight offsets are drafts in{' '}
              <code>field.json</code> until you measure them. Live pose overlay needs a TeamCode
              redeploy (<code>px/py/ph</code> in telemetry).
            </p>
          </div>
        </aside>
      </div>
    </div>
  )
}
