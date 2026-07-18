import { useCallback, useRef, useState } from 'react'

export interface UseSSEReturn {
  lines: string[]
  running: boolean
  exitCode: number | null
  start: (url: string) => void
  cancel: () => void
  clear: () => void
}

/** EventSource helper matching ftc-decode dashboard/src/hooks/useSSE.ts */
export function useSSE(): UseSSEReturn {
  const [lines, setLines] = useState<string[]>([])
  const [running, setRunning] = useState(false)
  const [exitCode, setExitCode] = useState<number | null>(null)
  const esRef = useRef<EventSource | null>(null)

  const start = useCallback((url: string) => {
    esRef.current?.close()
    setLines([])
    setRunning(true)
    setExitCode(null)

    const es = new EventSource(url)
    esRef.current = es

    es.onmessage = (e) => {
      try {
        const text = JSON.parse(e.data) as string
        setLines((prev) => [...prev, text])
      } catch {
        setLines((prev) => [...prev, e.data])
      }
    }

    es.addEventListener('done', (e) => {
      try {
        const { code } = JSON.parse((e as MessageEvent).data)
        setExitCode(typeof code === 'number' ? code : 1)
      } catch {
        setExitCode(1)
      }
      setRunning(false)
      es.close()
      esRef.current = null
    })

    es.onerror = () => {
      setRunning(false)
      setExitCode((c) => (c === null ? 1 : c))
      setLines((prev) => [
        ...prev,
        '\n[viewer] SSE connection lost — is `npm run dev` running the local API on :5174?\n',
      ])
      es.close()
      esRef.current = null
    }
  }, [])

  const cancel = useCallback(() => {
    esRef.current?.close()
    esRef.current = null
    setRunning(false)
    setExitCode(-1)
    setLines((prev) => [...prev, '\n--- cancelled ---\n'])
  }, [])

  const clear = useCallback(() => {
    setLines([])
    setExitCode(null)
  }, [])

  return { lines, running, exitCode, start, cancel, clear }
}
