import { useEffect, useMemo, useRef } from 'react'
import uPlot from 'uplot'
import 'uplot/dist/uPlot.min.css'

type SeriesSpec = {
  label: string
  stroke: string
  /** values aligned with times; null = gap */
  values: ArrayLike<number | null>
}

type Props = {
  title: string
  /** seconds */
  times: Float64Array | number[]
  series: SeriesSpec[]
  yLabel: string
  yMin?: number
  yMax?: number
  followWindowSec?: number
  dataVersion: number
  height?: number
}

export default function CompactChart({
  title,
  times,
  series,
  yLabel,
  yMin,
  yMax,
  followWindowSec = 20,
  dataVersion,
  height = 120,
}: Props) {
  const elRef = useRef<HTMLDivElement>(null)
  const plotRef = useRef<uPlot | null>(null)

  const data = useMemo(() => {
    const cols: uPlot.AlignedData = [times as number[]]
    for (const s of series) cols.push(s.values as number[])
    return cols
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [times, series, dataVersion])

  useEffect(() => {
    const el = elRef.current
    if (!el) return

    const autoRange = (_u: uPlot, dataMin: number, dataMax: number): [number, number] => {
      if (yMin != null && yMax != null) return [yMin, yMax]
      const lo = yMin ?? 0
      const hi = Math.max(yMax ?? 1, Number.isFinite(dataMax) ? dataMax * 1.15 : 1, lo + 1)
      return [lo, hi]
    }

    const opts: uPlot.Options = {
      width: Math.max(el.clientWidth - 4, 200),
      height,
      legend: { show: series.length > 1 },
      scales: {
        x: { time: false },
        y: { auto: yMin == null || yMax == null, range: autoRange },
      },
      series: [
        { label: 't', value: (_u, v) => (v == null ? '' : `${v.toFixed(1)}s`) },
        ...series.map((s) => ({
          label: s.label,
          stroke: s.stroke,
          width: 1.4,
          points: { show: false },
          value: (_u: uPlot, v: number | null) =>
            v == null || !Number.isFinite(v) ? '' : v.toFixed(1),
        })),
      ],
      axes: [
        {
          stroke: '#6a7180',
          grid: { stroke: 'rgba(255,255,255,0.05)' },
          ticks: { show: false },
          size: 28,
          values: (_u, splits) => splits.map((v) => v.toFixed(0)),
        },
        {
          label: yLabel,
          stroke: '#6a7180',
          grid: { stroke: 'rgba(255,255,255,0.06)' },
          size: 36,
          values: (_u, splits) =>
            splits.map((v) => (Math.abs(v) >= 1000 ? `${(v / 1000).toFixed(1)}k` : String(Math.round(v)))),
        },
      ],
      cursor: { drag: { x: false, y: false }, points: { size: 5 } },
    }

    const plot = new uPlot(opts, data, el)
    plotRef.current = plot

    const ro = new ResizeObserver(() => {
      plot.setSize({ width: Math.max(el.clientWidth - 4, 200), height })
    })
    ro.observe(el)

    return () => {
      ro.disconnect()
      plot.destroy()
      plotRef.current = null
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [height, yLabel, yMin, yMax, series.length])

  useEffect(() => {
    const plot = plotRef.current
    if (!plot) return
    plot.setData(data, false)
    const n = times.length
    if (n > 0) {
      const tEnd = Number(times[n - 1])
      plot.setScale('x', { min: Math.max(0, tEnd - followWindowSec), max: tEnd + 0.3 })
    }
  }, [data, followWindowSec, times])

  return (
    <div className="compact-chart">
      <div className="compact-chart-title">{title}</div>
      <div ref={elRef} className="compact-chart-plot" style={{ height }} />
    </div>
  )
}
