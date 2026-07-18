import { useEffect, useRef } from 'react'

interface Props {
  lines: string[]
}

export default function ConnectConsole({ lines }: Props) {
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [lines.length])

  if (lines.length === 0) return null

  return (
    <div className="connect-console">
      {lines.map((line, i) => (
        <div key={i} className="connect-console-line">
          {line}
        </div>
      ))}
      <div ref={bottomRef} />
    </div>
  )
}
