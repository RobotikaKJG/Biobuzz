import type { Burst } from '../lib/analysis'
import { ticksToRpm } from '../lib/types'

interface Props {
  bursts: Burst[]
  ticksPerRev: number
}

const fmt = (v: number, d = 0) => v.toFixed(d)

export default function ShotStats({ bursts, ticksPerRev }: Props) {
  const toRpm = (t: number) => ticksToRpm(t, ticksPerRev)

  if (bursts.length === 0) {
    return (
      <div className="stats-empty">
        No shots detected yet — bursts appear here once the flywheel reaches target and balls go
        through.
      </div>
    )
  }

  const allShots = bursts.flatMap((b) => b.shots)
  const avgDrop = allShots.reduce((a, s) => a + s.dropV, 0) / allShots.length
  const maxDrop = Math.max(...allShots.map((s) => s.dropV))

  let shotNo = 0
  return (
    <div className="stats">
      <div className="stats-summary">
        <span>
          <b>{bursts.length}</b> burst{bursts.length > 1 ? 's' : ''}
        </span>
        <span>
          <b>{allShots.length}</b> shots
        </span>
        <span>
          avg drop <b>{fmt(toRpm(avgDrop))} RPM</b>
        </span>
        <span>
          worst drop <b>{fmt(toRpm(maxDrop))} RPM</b>
        </span>
      </div>
      {bursts.map((burst, bi) => (
        <div key={bi} className="burst">
          <div className="burst-title">
            Burst {bi + 1} @ {(burst.startMs / 1000).toFixed(1)}s — target{' '}
            {fmt(toRpm(burst.targetV))} RPM, {burst.shots.length} shot
            {burst.shots.length > 1 ? 's' : ''}
          </div>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>t (s)</th>
                <th>min RPM</th>
                <th>drop</th>
                <th>drop %</th>
                <th>min M1/M2</th>
                <th>recovery</th>
                <th>spacing</th>
              </tr>
            </thead>
            <tbody>
              {burst.shots.map((s, si) => {
                shotNo++
                return (
                  <tr key={si}>
                    <td>#{shotNo}</td>
                    <td>{(s.tMinMs / 1000).toFixed(2)}</td>
                    <td>{fmt(toRpm(s.minV))}</td>
                    <td className="drop">−{fmt(toRpm(s.dropV))}</td>
                    <td>{((s.dropV / s.targetV) * 100).toFixed(1)}%</td>
                    <td>
                      {fmt(toRpm(s.minV1))} / {fmt(toRpm(s.minV2))}
                    </td>
                    <td>{s.recoveryMs === null ? '—' : `${s.recoveryMs} ms`}</td>
                    <td>{s.spacingMs === null ? '—' : `${s.spacingMs} ms`}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  )
}
