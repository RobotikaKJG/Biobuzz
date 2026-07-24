import ControlPanel from './ControlPanel'
import ControlTelemetry from './ControlTelemetry'
import type { ConnStatus } from '../lib/robot'
import type { RemoteCmd } from '../lib/remoteCmd'
import type { Sample } from '../lib/types'

type Props = {
  status: ConnStatus
  canSend: boolean
  onSend: (cmd: RemoteCmd) => boolean
  samples: Sample[]
  dataVersion: number
  ticksPerRev: number
}

/**
 * Control tab: hero stats across the top, keyboard dock on the left,
 * graphs using the remaining width/height.
 */
export default function ControlView({
  status,
  canSend,
  onSend,
  samples,
  dataVersion,
  ticksPerRev,
}: Props) {
  const latest = samples.length ? samples[samples.length - 1] : null
  return (
    <div className="control-body">
      <ControlTelemetry
        samples={samples}
        dataVersion={dataVersion}
        ticksPerRev={ticksPerRev}
      />
      <aside className="control-dock">
        <ControlPanel
          status={status}
          canSend={canSend}
          onSend={onSend}
          robotState={{
            shooting: !!latest && latest.ss !== 'idle',
            shooterSpinning: !!latest && latest.ms !== 'idle',
            intakeRunning: !!latest?.ims && latest.ims !== 'idle',
            transferRunning: !!latest?.tms && latest.tms !== 'idle',
            turretTracking: latest?.tr === 1,
          }}
        />
      </aside>
    </div>
  )
}
