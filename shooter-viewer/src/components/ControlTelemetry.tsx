import { useMemo } from 'react'
import CompactChart from './CompactChart'
import type { Sample } from '../lib/types'
import { latestCarried, ticksToRpm } from '../lib/types'

type Props = {
  samples: Sample[]
  dataVersion: number
  ticksPerRev: number
}

function fmt(v: number | null | undefined, digits = 0, suffix = '') {
  if (v == null || !Number.isFinite(v)) return '—'
  return `${v.toFixed(digits)}${suffix}`
}

export default function ControlTelemetry({ samples, dataVersion, ticksPerRev }: Props) {
  const carried = useMemo(() => latestCarried(samples), [samples, dataVersion])
  const { last, tg, d, bat, ta, i1, i2, ii, it, id0, id1, id2, id3 } = carried

  const chartData = useMemo(() => {
    // Only build what the 20-second charts can display. Rebuilding an entire
    // long session on every 100 ms telemetry batch can starve the command timer.
    const lastMs = samples.length ? samples[samples.length - 1].t : 0
    const cutoffMs = lastMs - 25_000
    let start = samples.length
    while (start > 0 && samples[start - 1].t >= cutoffMs) start--
    const n = samples.length - start
    const t = new Float64Array(n)
    const r1 = new Float64Array(n)
    const r2 = new Float64Array(n)
    const tgt = new Float64Array(n)
    const aIn: (number | null)[] = new Array(n)
    const aTr: (number | null)[] = new Array(n)
    let lastTg = NaN
    let lastIn: number | null = null
    let lastTr: number | null = null
    for (let i = 0; i < n; i++) {
      const s = samples[start + i]
      t[i] = s.t / 1000
      r1[i] = ticksToRpm(s.v1, ticksPerRev)
      r2[i] = ticksToRpm(s.v2, ticksPerRev)
      if (typeof s.tg === 'number') lastTg = s.tg
      tgt[i] = ticksToRpm(lastTg, ticksPerRev)
      if (typeof s.ii === 'number') lastIn = s.ii
      if (typeof s.it === 'number') lastTr = s.it
      aIn[i] = lastIn
      aTr[i] = lastTr
    }
    return { t, r1, r2, tgt, aIn, aTr }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [samples, ticksPerRev, dataVersion])

  const rpm1 = last ? ticksToRpm(last.v1, ticksPerRev) : null
  const rpm2 = last ? ticksToRpm(last.v2, ticksPerRev) : null
  const targetRpm = tg != null ? ticksToRpm(tg, ticksPerRev) : null
  const delta = rpm1 != null && rpm2 != null ? rpm1 - rpm2 : null
  const distCm = d != null ? d * 2.54 : null
  const driveSum =
    id0 != null && id1 != null && id2 != null && id3 != null
      ? id0 + id1 + id2 + id3
      : null
  const live = samples.length > 0

  return (
    <>
      <section className="control-hero" aria-label="Live stats">
        <div className="control-hero-primary">
          <HeroStat label="RPM 1" value={fmt(rpm1, 0)} hint="Flywheel motor 1" />
          <HeroStat label="RPM 2" value={fmt(rpm2, 0)} hint="Flywheel motor 2" />
          <HeroStat label="Target" value={fmt(targetRpm, 0)} hint="Commanded RPM" />
          <HeroStat
            label="Δ"
            value={delta == null ? '—' : `${delta >= 0 ? '+' : ''}${delta.toFixed(0)}`}
            hint="RPM1 − RPM2"
          />
          <HeroStat label="Distance" value={fmt(distCm, 0, ' cm')} hint="Goal range" />
          <HeroStat label="Turret" value={fmt(ta, 1, '°')} hint="Turret angle" />
        </div>
        <div className="control-hero-secondary">
          <SmallStat label="Intake A" value={fmt(ii, 1, ' A')} />
          <SmallStat label="Transfer A" value={fmt(it, 1, ' A')} />
          <SmallStat label="Outtake A" value={`${fmt(i1, 1)} / ${fmt(i2, 1)}`} />
          <SmallStat label="Drive FL" value={fmt(id0, 1, ' A')} />
          <SmallStat label="Drive BL" value={fmt(id1, 1, ' A')} />
          <SmallStat label="Drive FR" value={fmt(id2, 1, ' A')} />
          <SmallStat label="Drive BR" value={fmt(id3, 1, ' A')} />
          <SmallStat label="Drive Σ" value={fmt(driveSum, 1, ' A')} />
          <SmallStat label="Battery" value={fmt(bat, 2, ' V')} />
          <SmallStat label="Shoot" value={last?.ss ?? '—'} />
          <SmallStat label="Motor" value={last?.ms ?? '—'} />
          <SmallStat label="Intake" value={last?.ims ?? '—'} />
          <SmallStat label="Transfer" value={last?.tms ?? '—'} />
          <SmallStat label="Balls" value={live ? `I ${last?.ib ?? 0} · T ${last?.tb ?? 0}` : '—'} />
        </div>
        {!live && (
          <p className="control-hero-empty">
            Use Connect to Hub in the header, start a TeleOp, then Enable keyboard to drive.
          </p>
        )}
      </section>

      <section className="control-viz" aria-label="Live graphs">
        <CompactChart
          title="Flywheel RPM"
          times={chartData.t}
          series={[
            { label: 'RPM 1', stroke: '#4fc3f7', values: chartData.r1 },
            { label: 'RPM 2', stroke: '#ffb74d', values: chartData.r2 },
            { label: 'Target', stroke: '#ce93d8', values: chartData.tgt },
          ]}
          yLabel="RPM"
          yMin={0}
          yMax={4000}
          dataVersion={dataVersion}
          height={220}
        />
        <div className="control-amp-row">
          <CompactChart
            title="Intake current"
            times={chartData.t}
            series={[{ label: 'Intake', stroke: '#66bb6a', values: chartData.aIn }]}
            yLabel="A"
            yMin={0}
            dataVersion={dataVersion}
            height={140}
          />
          <CompactChart
            title="Transfer current"
            times={chartData.t}
            series={[{ label: 'Transfer', stroke: '#ffa726', values: chartData.aTr }]}
            yLabel="A"
            yMin={0}
            dataVersion={dataVersion}
            height={140}
          />
        </div>
      </section>
    </>
  )
}

function HeroStat({ label, value, hint }: { label: string; value: string; hint: string }) {
  return (
    <div className="hero-stat" title={hint}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  )
}

function SmallStat({ label, value }: { label: string; value: string }) {
  return (
    <div className="small-stat">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  )
}
