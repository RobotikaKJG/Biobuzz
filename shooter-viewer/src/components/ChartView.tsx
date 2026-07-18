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
  showBattery: boolean
  /** live follow mode: keep the view pinned to the last N seconds */
  follow: boolean
  followWindowSec: number
  /** bumped on every live batch so we know to refresh data */
  dataVersion: number
  onUserZoom?: () => void
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

  // Build aligned data arrays (t sec, rpm1, rpm2, target rpm, battery carried forward)
  const data = useMemo<uPlot.AlignedData>(() => {
    const n = samples.length
    const t = new Float64Array(n)
    const r1 = new Float64Array(n)
    const r2 = new Float64Array(n)
    const tg = new Float64Array(n)
    const bat: (number | null)[] = new Array(n)
    let lastTg = NaN
    let lastBat: number | null = null
    for (let i = 0; i < n; i++) {
      const s = samples[i]
      t[i] = s.t / 1000
      r1[i] = ticksToRpm(s.v1, ticksPerRev)
      r2[i] = ticksToRpm(s.v2, ticksPerRev)
      if (typeof s.tg === 'number') lastTg = s.tg
      tg[i] = ticksToRpm(lastTg, ticksPerRev)
      if (typeof s.bat === 'number') lastBat = s.bat
      bat[i] = lastBat
    }
    return [t, r1, r2, tg, bat] as unknown as uPlot.AlignedData
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
          const x = u.valToPos(shot.tMinMs / 1000, 'x', true)
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
      width: el.clientWidth,
      height: Math.max(el.clientHeight, 320),
      scales: {
        x: { time: false },
        rpm: { auto: true },
        v: { auto: true },
      },
      series: [
        {
          label: 't',
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} s`),
        },
        { label: 'RPM 1', scale: 'rpm', stroke: '#4fc3f7', width: 1.5, points: { show: false } },
        { label: 'RPM 2', scale: 'rpm', stroke: '#ffb74d', width: 1.5, points: { show: false } },
        {
          label: 'Target',
          scale: 'rpm',
          stroke: '#ce93d8',
          width: 1,
          dash: [8, 5],
          points: { show: false },
        },
        {
          label: 'Battery',
          scale: 'v',
          stroke: '#81c784',
          width: 1,
          points: { show: false },
          show: showBattery,
          value: (_u, v) => (v == null ? '' : `${v.toFixed(2)} V`),
        },
      ],
      axes: [
        { stroke: '#9aa', grid: { stroke: 'rgba(255,255,255,0.06)' }, ticks: { stroke: 'rgba(255,255,255,0.15)' } },
        {
          scale: 'rpm',
          stroke: '#9aa',
          grid: { stroke: 'rgba(255,255,255,0.06)' },
          ticks: { stroke: 'rgba(255,255,255,0.15)' },
          size: 60,
        },
        {
          scale: 'v',
          side: 1,
          stroke: '#81c784',
          grid: { show: false },
          size: 50,
        },
      ],
      cursor: {
        drag: { x: true, y: false },
        points: { size: 6 },
      },
      hooks: {
        drawClear: [drawBands],
        draw: [drawShotMarkers],
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
      plot.setSize({ width: el.clientWidth, height: Math.max(el.clientHeight, 320) })
    })
    ro.observe(el)

    return () => {
      ro.disconnect()
      plot.destroy()
      plotRef.current = null
    }
    // recreate only on mount; data/series updates handled below
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

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

  // battery series toggle
  useEffect(() => {
    plotRef.current?.setSeries(4, { show: showBattery })
  }, [showBattery])

  return <div ref={containerRef} className="chart-container" />
}
