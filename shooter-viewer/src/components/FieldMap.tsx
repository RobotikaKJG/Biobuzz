import { useMemo, useRef, useState } from 'react'
import type { CalibPoint, FieldMeta } from '../lib/field'
import {
  FIELD_INCHES,
  INCH_TO_CM,
  pedroToPixel,
  pedroToTeleop,
  pixelToPedro,
  softwareTurretAngleDeg,
  toPedroPose,
} from '../lib/field'

type LivePose = { x: number; y: number; headingDeg: number } | null

type Props = {
  field: FieldMeta
  points: CalibPoint[]
  selectedId: string | null
  mode: 'select' | 'add'
  livePose: LivePose
  alliance: 'red' | 'blue'
  onSelect: (id: string) => void
  onMapClick: (pedro: { x: number; y: number }) => void
}

export default function FieldMap({
  field,
  points,
  selectedId,
  mode,
  livePose,
  alliance,
  onSelect,
  onMapClick,
}: Props) {
  const wrapRef = useRef<HTMLDivElement>(null)
  const [cursor, setCursor] = useState<{
    px: number
    py: number
    x: number
    y: number
  } | null>(null)

  const imgW = field.image.pixelWidth
  const imgH = field.image.pixelHeight
  const robot = field.robotDraft
  const selectedPoint = points.find((point) => point.id === selectedId) ?? null

  const livePedro = useMemo(() => {
    if (!livePose || !Number.isFinite(livePose.x) || !Number.isFinite(livePose.y)) return null
    const p = toPedroPose(livePose.x, livePose.y)
    return { ...p, headingDeg: livePose.headingDeg }
  }, [livePose])

  const clientToPedro = (clientX: number, clientY: number) => {
    const el = wrapRef.current
    if (!el) return null
    const rect = el.getBoundingClientRect()
    const px = ((clientX - rect.left) / rect.width) * imgW
    const py = ((clientY - rect.top) / rect.height) * imgH
    const pedro = pixelToPedro(px, py, imgW, imgH)
    return { px, py, ...pedro }
  }

  return (
    <div className="field-map-wrap">
      <div className="field-map-stage">
      <div
        ref={wrapRef}
        className={`field-map ${mode === 'add' ? 'add-mode' : ''}`}
        onMouseMove={(e) => setCursor(clientToPedro(e.clientX, e.clientY))}
        onMouseLeave={() => setCursor(null)}
        onClick={(e) => {
          const p = clientToPedro(e.clientX, e.clientY)
          if (!p) return
          if (mode === 'add') onMapClick({ x: p.x, y: p.y })
        }}
      >
        <img src={field.image.path} alt="DECODE field" draggable={false} />
        <svg
          className="field-overlay"
          viewBox={`0 0 ${imgW} ${imgH}`}
          preserveAspectRatio="none"
        >
          {/* tile grid */}
          {Array.from({ length: 7 }, (_, i) => {
            const a = (i / 6) * imgW
            return (
              <g key={i} opacity={0.18}>
                <line x1={a} y1={0} x2={a} y2={imgH} stroke="#fff" strokeWidth={1} />
                <line x1={0} y1={a} x2={imgW} y2={a} stroke="#fff" strokeWidth={1} />
              </g>
            )
          })}

          {/* goals */}
          {(['red', 'blue'] as const).map((a) => {
            const g = field.goalsInches.pedroCorner[a]
            const { px, py } = pedroToPixel(g.x, g.y, imgW, imgH)
            return (
              <g key={a}>
                <circle
                  cx={px}
                  cy={py}
                  r={10}
                  fill={a === 'red' ? '#e53935' : '#1e88e5'}
                  stroke="#fff"
                  strokeWidth={2}
                  opacity={a === alliance ? 1 : 0.35}
                />
                <text x={px + 14} y={py + 4} fill="#fff" fontSize={14} fontWeight={600}>
                  {a === 'red' ? 'Red goal' : 'Blue goal'}
                </text>
              </g>
            )
          })}

          {/* saved robot/turret geometry for the selected calibration point */}
          {selectedPoint && (() => {
            const center = pedroToPixel(
              selectedPoint.pose.x,
              selectedPoint.pose.y,
              imgW,
              imgH,
            )
            const heading = selectedPoint.pose.headingDeg ?? 0
            const width = (robot.widthInches / FIELD_INCHES) * imgW
            const length = (robot.lengthInches / FIELD_INCHES) * imgH
            const pointGoal =
              field.goalsInches.pedroCorner[
                selectedPoint.alliance === 'either' ? alliance : selectedPoint.alliance
              ]
            const uiTurretAngle = softwareTurretAngleDeg(selectedPoint, pointGoal)
            const robotTurretAngle = selectedPoint.turret.robotEstimateDeg
            const vectorLengthInches = 30
            const vectorEnd = (relativeAngle: number) => {
              const worldHeading = heading + relativeAngle
              return pedroToPixel(
                selectedPoint.pose.x
                  + Math.cos((worldHeading * Math.PI) / 180) * vectorLengthInches,
                selectedPoint.pose.y
                  + Math.sin((worldHeading * Math.PI) / 180) * vectorLengthInches,
                imgW,
                imgH,
              )
            }
            return (
              <g className="selected-robot-overlay" pointerEvents="none">
                <g
                  transform={`translate(${center.px} ${center.py}) rotate(${-90 - heading})`}
                >
                  <rect
                    x={-length / 2}
                    y={-width / 2}
                    width={length}
                    height={width}
                    fill="rgba(255,183,77,0.12)"
                    stroke="#ffb74d"
                    strokeWidth={3}
                    strokeDasharray="7 4"
                    rx={4}
                  />
                  {selectedPoint.pose.headingDeg != null && (
                    <line
                      x1={0}
                      y1={0}
                      x2={length / 2}
                      y2={0}
                      stroke="#ffb74d"
                      strokeWidth={3}
                    />
                  )}
                </g>
                {selectedPoint.pose.headingDeg == null && (
                  <text
                    x={center.px + length / 2 + 6}
                    y={center.py + 4}
                    fill="#ffb74d"
                    fontSize={12}
                  >
                    heading unknown
                  </text>
                )}
                {selectedPoint.pose.headingDeg != null && uiTurretAngle != null && (() => {
                  const end = vectorEnd(uiTurretAngle)
                  return (
                    <line
                      x1={center.px}
                      y1={center.py}
                      x2={end.px}
                      y2={end.py}
                      stroke="#4fc3f7"
                      strokeWidth={3}
                      strokeDasharray="7 4"
                    />
                  )
                })()}
                {selectedPoint.pose.headingDeg != null && robotTurretAngle != null && (() => {
                  const end = vectorEnd(robotTurretAngle)
                  return (
                    <>
                      <line
                        x1={center.px}
                        y1={center.py}
                        x2={end.px}
                        y2={end.py}
                        stroke="#ce93d8"
                        strokeWidth={4}
                      />
                      <circle cx={end.px} cy={end.py} r={5} fill="#ce93d8" />
                    </>
                  )
                })()}
              </g>
            )
          })()}

          {/* calibration points */}
          {points.map((pt) => {
            const { px, py } = pedroToPixel(pt.pose.x, pt.pose.y, imgW, imgH)
            const pointGoal =
              field.goalsInches.pedroCorner[pt.alliance === 'either' ? alliance : pt.alliance]
            const selected = pt.id === selectedId
            return (
              <g
                key={pt.id}
                className={`calib-marker ${selected ? 'selected' : ''}`}
                onClick={(e) => {
                  e.stopPropagation()
                  onSelect(pt.id)
                }}
                style={{ cursor: 'pointer' }}
              >
                <circle
                  cx={px}
                  cy={py}
                  r={selected ? 11 : 8}
                  fill={selected ? '#ffb74d' : '#4fc3f7'}
                  stroke="#0d1117"
                  strokeWidth={2}
                />
                <line
                  x1={px}
                  y1={py}
                  x2={pedroToPixel(pointGoal.x, pointGoal.y, imgW, imgH).px}
                  y2={pedroToPixel(pointGoal.x, pointGoal.y, imgW, imgH).py}
                  stroke={selected ? 'rgba(255,183,77,0.55)' : 'rgba(79,195,247,0.25)'}
                  strokeWidth={selected ? 2 : 1}
                  strokeDasharray="6 4"
                />
                <text x={px + 12} y={py - 10} fill="#e8eaed" fontSize={13} fontWeight={600}>
                  {pt.label}
                </text>
              </g>
            )
          })}

          {/* live robot */}
          {livePedro && (
            <g
              transform={`translate(${pedroToPixel(livePedro.x, livePedro.y, imgW, imgH).px} ${
                pedroToPixel(livePedro.x, livePedro.y, imgW, imgH).py
              }) rotate(${-90 - (livePedro.headingDeg ?? 0)})`}
            >
              {(() => {
                const w = (robot.widthInches / FIELD_INCHES) * imgW
                const l = (robot.lengthInches / FIELD_INCHES) * imgH
                const sx = (robot.shooterOffsetInches.x / FIELD_INCHES) * imgW
                const sy = -(robot.shooterOffsetInches.y / FIELD_INCHES) * imgH
                const lx = (robot.limelightOffsetInches.x / FIELD_INCHES) * imgW
                const ly = -(robot.limelightOffsetInches.y / FIELD_INCHES) * imgH
                return (
                  <>
                    <rect
                      x={-l / 2}
                      y={-w / 2}
                      width={l}
                      height={w}
                      fill="rgba(102,224,111,0.35)"
                      stroke="#66e06f"
                      strokeWidth={2}
                      rx={4}
                    />
                    <line x1={0} y1={0} x2={l / 2} y2={0} stroke="#66e06f" strokeWidth={2} />
                    <circle cx={sx} cy={sy} r={4} fill="#ffb74d">
                      <title>Shooter</title>
                    </circle>
                    <circle cx={lx} cy={ly} r={3} fill="#ce93d8">
                      <title>Limelight</title>
                    </circle>
                  </>
                )
              })()}
            </g>
          )}
        </svg>
      </div>
      </div>

      <div className="field-cursor-readout">
        {cursor ? (
          <>
            <span>
              Pedro{' '}
              <strong>
                {cursor.x.toFixed(1)}, {cursor.y.toFixed(1)} in
              </strong>
            </span>
            <span>
              TeleOp{' '}
              <strong>
                {pedroToTeleop(cursor.x, cursor.y).x.toFixed(1)},{' '}
                {pedroToTeleop(cursor.x, cursor.y).y.toFixed(1)} in
              </strong>
            </span>
            <span>
              <strong>
                {(cursor.x * INCH_TO_CM).toFixed(0)}, {(cursor.y * INCH_TO_CM).toFixed(0)} cm
              </strong>
            </span>
            {mode === 'add' && <span className="hint">Click map to place point</span>}
          </>
        ) : (
          <span className="hint">
            Hover for coordinates · {mode === 'add' ? 'click to add' : 'click a marker to select'}
          </span>
        )}
        {livePedro && (
          <span className="live-pose">
            Robot @ {livePedro.x.toFixed(1)}, {livePedro.y.toFixed(1)} in ({livePedro.frame}) ·{' '}
            {livePedro.headingDeg.toFixed(0)}°
          </span>
        )}
        {selectedPoint && (
          <span className="field-vector-legend">
            <i className="body" /> robot <i className="ui" /> UI turret{' '}
            <i className="robot" /> robot turret
          </span>
        )}
      </div>
    </div>
  )
}
