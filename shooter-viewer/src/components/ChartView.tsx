import { useEffect, useMemo, useRef } from 'react'
import uPlot from 'uplot'
import 'uplot/dist/uPlot.min.css'
import type { Sample } from '../lib/types'
import { ticksToRpm } from '../lib/types'
import type { Burst } from '../lib/analysis'
import { stateBands } from '../lib/analysis'

interface Props {
  samples: Sample[]
  ticksPerRev: number
  bursts: Burst[]
  variant: 'live' | 'diagnose'
  showCurrent: boolean
  showBattery: boolean
  /** live follow mode: keep the view pinned to the last N seconds */
  follow: boolean
  followWindowSec: number
  /** bumped on every live batch so we know to refresh data */
  dataVersion: number
  onUserZoom?: () => void
}

/** Fixed flywheel range — hides encoder jitter and makes motor split obvious. */
const RPM_RANGE: [number, number] = [0, 4000]

/** Belt-sync mismatch (RPM1 − RPM2); symmetric so zero is the midline. */
const DELTA_RANGE: [number, number] = [-200, 200]

function currentRange(_u: uPlot, _dataMin: number, dataMax: number): [number, number] {
  return [0, Math.max(5, Number.isFinite(dataMax) ? dataMax * 1.2 : 5)]
}

/** mouse-wheel zoom around cursor on the x axis */
function wheelZoomPlugin(onUserZoom?: () => void): uPlot.Plugin {
  return {
    hooks: {
      ready: (u) => {
        u.over.addEventListener(
          'wheel',
          (e) => {
            e.preventDefault()
            onUserZoom?.()
            const rect = u.over.getBoundingClientRect()
            const xVal = u.posToVal(e.clientX - rect.left, 'x')
            const min = u.scales.x.min ?? 0
            const max = u.scales.x.max ?? 1
            const f = e.deltaY < 0 ? 0.85 : 1 / 0.85
            const nmin = xVal - (xVal - min) * f
            const nmax = xVal + (max - xVal) * f
            u.setScale('x', { min: nmin, max: nmax })
          },
          { passive: false },
        )
      },
    },
  }
}

export default function ChartView({
  samples,
  ticksPerRev,
  bursts,
  variant,
  showCurrent,
  showBattery,
  follow,
  followWindowSec,
  dataVersion,
  onUserZoom,
}: Props) {
  const containerRef = useRef<HTMLDivElement>(null)
  const plotRef = useRef<uPlot | null>(null)
  const bandsRef = useRef<{ active: Array<[number, number]>; feeding: Array<[number, number]> }>({
    active: [],
    feeding: [],
  })
  const burstsRef = useRef<Burst[]>([])
  const onUserZoomRef = useRef(onUserZoom)
  onUserZoomRef.current = onUserZoom

  // Build aligned arrays: time, RPMs, Δ, target, motor currents, carried battery.
  const data = useMemo<uPlot.AlignedData>(() => {
    const n = samples.length
    const t = new Float64Array(n)
    const r1 = new Float64Array(n)
    const r2 = new Float64Array(n)
    const dRpm = new Float64Array(n)
    const tg = new Float64Array(n)
    const current1: (number | null)[] = new Array(n)
    const current2: (number | null)[] = new Array(n)
    const bat: (number | null)[] = new Array(n)
    let lastTg = NaN
    let lastBat: number | null = null
    for (let i = 0; i < n; i++) {
      const s = samples[i]
      t[i] = s.t / 1000
      r1[i] = ticksToRpm(s.v1, ticksPerRev)
      r2[i] = ticksToRpm(s.v2, ticksPerRev)
      dRpm[i] = r1[i] - r2[i]
      if (typeof s.tg === 'number') lastTg = s.tg
      tg[i] = ticksToRpm(lastTg, ticksPerRev)
      current1[i] = typeof s.i1 === 'number' ? s.i1 : null
      current2[i] = typeof s.i2 === 'number' ? s.i2 : null
      if (typeof s.bat === 'number') lastBat = s.bat
      bat[i] = lastBat
    }
    return [t, r1, r2, dRpm, tg, current1, current2, bat] as unknown as uPlot.AlignedData
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [samples, ticksPerRev, dataVersion])

  bandsRef.current = useMemo(() => stateBands(samples), [samples, dataVersion])
  burstsRef.current = bursts

  useEffect(() => {
    const el = containerRef.current
    if (!el) return

    const drawBands = (u: uPlot) => {
      const ctx = u.ctx
      ctx.save()
      const { top, height } = u.bbox
      const paint = (ranges: Array<[number, number]>, fill: string) => {
        ctx.fillStyle = fill
        for (const [aMs, bMs] of ranges) {
          const x0 = u.valToPos(aMs / 1000, 'x', true)
          const x1 = u.valToPos(bMs / 1000, 'x', true)
          if (x1 < u.bbox.left || x0 > u.bbox.left + u.bbox.width) continue
          ctx.fillRect(x0, top, Math.max(x1 - x0, 1), height)
        }
      }
      paint(bandsRef.current.active, 'rgba(120, 140, 255, 0.07)')
      paint(bandsRef.current.feeding, 'rgba(80, 220, 120, 0.10)')
      ctx.restore()
    }

    const drawShotMarkers = (u: uPlot) => {
      const ctx = u.ctx
      ctx.save()
      const { top, height } = u.bbox
      ctx.strokeStyle = 'rgba(255, 90, 90, 0.85)'
      ctx.fillStyle = 'rgba(255, 90, 90, 0.95)'
      ctx.setLineDash([4, 4])
      ctx.lineWidth = 1
      ctx.font = `${11 * devicePixelRatio}px system-ui`
      ctx.textAlign = 'center'
      let n = 0
      for (const burst of burstsRef.current) {
        for (const shot of burst.shots) {
          n++
          const x = u.valToPos(shot.markerMs / 1000, 'x', true)
          if (x < u.bbox.left || x > u.bbox.left + u.bbox.width) continue
          ctx.beginPath()
          ctx.moveTo(x, top)
          ctx.lineTo(x, top + height)
          ctx.stroke()
          ctx.fillText(`#${n}`, x, top + 14 * devicePixelRatio)
        }
      }
      ctx.restore()
    }

    const opts: uPlot.Options = {
      width: Math.max(el.clientWidth - 16, 320),
      height: Math.max(el.clientHeight - 16, 320),
      scales: {
        x: { time: false },
        rpm: { auto: false, range: RPM_RANGE },
        delta: { auto: false, range: DELTA_RANGE },
        amps: { auto: true, range: currentRange },
        volts: { auto: false, range: [10, 14.5] },
      },
      series: [
        {
          label: 't',
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} s`),
        },
        { label: 'RPM 1', scale: 'rpm', stroke: '#4fc3f7', width: 1.5, points: { show: false } },
        { label: 'RPM 2', scale: 'rpm', stroke: '#ffb74d', width: 1.5, points: { show: false } },
        {
          label: 'Δ (1−2)',
          scale: 'delta',
          stroke: '#80cbc4',
          width: 1.2,
          points: { show: false },
          value: (_u, v) => (v == null ? '' : `${v >= 0 ? '+' : ''}${v.toFixed(0)} RPM`),
        },
        {
          label: 'Target',
          scale: 'rpm',
          stroke: '#ce93d8',
          width: 1,
          dash: [8, 5],
          points: { show: false },
        },
        {
          label: 'M1 current',
          scale: 'amps',
          stroke: '#ef5350',
          width: 1.2,
          points: { show: false },
          show: showCurrent,
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} A`),
        },
        {
          label: 'M2 current',
          scale: 'amps',
          stroke: '#ab47bc',
          width: 1.2,
          points: { show: false },
          show: showCurrent,
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} A`),
        },
        {
          label: 'Battery',
          scale: 'volts',
          stroke: '#81c784',
          width: 1,
          points: { show: false },
          show: showBattery,
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} V`),
        },
      ],
      axes: [
        {
          label: 'Time (s)',
          stroke: '#9aa',
          grid: { stroke: 'rgba(255,255,255,0.06)' },
          ticks: { stroke: 'rgba(255,255,255,0.15)' },
        },
        {
          scale: 'rpm',
          label: 'Flywheel (RPM)',
          stroke: '#9aa',
          grid: { stroke: 'rgba(255,255,255,0.06)' },
          ticks: { stroke: 'rgba(255,255,255,0.15)' },
          size: 68,
          values: (_u, splits) => splits.map((v) => (v >= 1000 ? `${(v / 1000).toFixed(v % 1000 === 0 ? 0 : 1)}k` : String(v))),
        },
        {
          scale: 'delta',
          side: 1,
          label: 'Δ RPM (1−2)',
          stroke: '#80cbc4',
          grid: { show: false },
          size: 58,
          // Hide when amps take the right axis — Δ still in legend / live readout.
          show: !showCurrent,
        },
        {
          scale: 'amps',
          side: 1,
          label: 'Motor current (A)',
          stroke: '#ef6c6c',
          grid: { show: false },
          size: 62,
          show: showCurrent,
        },
        {
          scale: 'volts',
          side: 1,
          label: 'Battery (V)',
          stroke: '#81c784',
          grid: { show: false },
          size: 58,
          show: showBattery,
        },
      ],
      cursor: {
        drag: { x: true, y: false },
        points: { size: 6 },
      },
      hooks: {
        drawClear: variant === 'diagnose' ? [drawBands] : [],
        draw: variant === 'diagnose' ? [drawShotMarkers] : [],
        setSelect: [
          (u) => {
            if (u.select.width > 0) onUserZoomRef.current?.()
          },
        ],
      },
      plugins: [wheelZoomPlugin(() => onUserZoomRef.current?.())],
    }

    const plot = new uPlot(opts, data, el)
    plotRef.current = plot

    const ro = new ResizeObserver(() => {
      plot.setSize({
        width: Math.max(el.clientWidth - 16, 320),
        height: Math.max(el.clientHeight - 16, 320),
      })
    })
    ro.observe(el)

    return () => {
      ro.disconnect()
      plot.destroy()
      plotRef.current = null
    }
    // Recreate when axes/mode change; sample updates are handled below.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [showBattery, showCurrent, variant])

  // data updates
  useEffect(() => {
    const plot = plotRef.current
    if (!plot) return
    plot.setData(data, !follow)
    if (follow && samples.length) {
      const tEnd = samples[samples.length - 1].t / 1000
      plot.setScale('x', { min: Math.max(0, tEnd - followWindowSec), max: tEnd + 0.5 })
    }
  }, [data, follow, followWindowSec, samples])

  return <div ref={containerRef} className={`chart-container ${variant}`} />
}
