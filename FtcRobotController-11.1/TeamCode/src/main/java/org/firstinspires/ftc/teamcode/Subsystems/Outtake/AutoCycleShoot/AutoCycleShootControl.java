package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoCycleShoot;

import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.AutoIntakeTransfer.AutoIntakeTransferStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeMotor.IntakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.IntakeStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.LockServo.LockServoStates;
import org.firstinspires.ftc.teamcode.Subsystems.Intake.TransferMotor.TransferMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoCycleShootControl {
    private final MotorControl motorControl;
    private AutoCycleShootStates prevAutoCycleShootState = AutoCycleShootStates.idle;

    private static final double FAR_MIN_OUTTAKE_VELOCITY = 1900.0;

    /**
     * Mean flywheel velocity (ticks/s) latched once the wheel is near target at the
     * start of a feed — the "RPM before the first ball". Transfer stays off while
     * measured velocity is below this baseline (see {@link #canFeedBall()}).
     */
    private double preFirstBallVelocityTicks = Double.NaN;

    public AutoCycleShootControl(MotorControl motorControl) {
        this.motorControl = motorControl;
    }

    public void update() {
        if (OuttakeStates.getAutoCycleShootState() != prevAutoCycleShootState) {
            if (OuttakeStates.getAutoCycleShootState() != AutoCycleShootStates.turnTransfer) {
                preFirstBallVelocityTicks = Double.NaN;
            }
            updateStates();
            prevAutoCycleShootState = OuttakeStates.getAutoCycleShootState();
        } else if (OuttakeStates.getAutoCycleShootState() == AutoCycleShootStates.turnTransfer) {
            updateStates();
        }
    }

    public void updateStates() {
        switch (OuttakeStates.getAutoCycleShootState()) {
            case recalibrate:
                break;
            case activate:
                IntakeStates.setLockServoState(LockServoStates.unlock);
                // Reset intake state so they don't fight, and so it can be restarted after shooting
                IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
                break;
            case turnBack:
                IntakeStates.setTransferMotorState(TransferMotorStates.backward);
                break;
            case turnTransfer:
                if (canFeedBall()) {
                    IntakeStates.setIntakeMotorState(IntakeMotorStates.forward);
                    IntakeStates.setTransferMotorState(TransferMotorStates.forward);
                } else {
                    // Hold the queue: don't push the next ball into a recovering flywheel.
                    IntakeStates.setIntakeMotorState(IntakeMotorStates.idle);
                    IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                }
                break;
            case stop:
                break;
            case turnTransferBack:
                IntakeStates.setTransferMotorState(TransferMotorStates.backward);
                break;
            case deactivate:
                preFirstBallVelocityTicks = Double.NaN;
                IntakeStates.setIntakeMotorState(IntakeMotorStates.idle);
                IntakeStates.setTransferMotorState(TransferMotorStates.idle);
                IntakeStates.setLockServoState(LockServoStates.lock);
                // Ensure intake is ready for next trigger press
                IntakeStates.setAutoIntakeTransferState(AutoIntakeTransferStates.idle);
                break;
            case idle:
                preFirstBallVelocityTicks = Double.NaN;
                break;
        }
    }

    /**
     * TeleOp: latch pre-first-ball RPM once near target, then only feed while the
     * flywheel has recovered to that baseline. Autonomous far path keeps the old
     * absolute velocity gate.
     */
    private boolean canFeedBall() {
        double v1 = motorControl.getMotorVelocity(MotorConstants.outtake1);
        double v2 = motorControl.getMotorVelocity(MotorConstants.outtake2);
        double velocity = (v1 + v2) / 2.0;

        if (GlobalVariables.isAutonomous && GlobalVariables.far) {
            return velocity > FAR_MIN_OUTTAKE_VELOCITY;
        }

        maybeLatchPreFirstBallVelocity(velocity);

        if (Double.isNaN(preFirstBallVelocityTicks)) {
            // Still spinning up — don't feed yet.
            return false;
        }

        return velocity >= preFirstBallVelocityTicks - OuttakeConstants.feedResumeMarginTicks;
    }

    private void maybeLatchPreFirstBallVelocity(double velocity) {
        if (!Double.isNaN(preFirstBallVelocityTicks)) return;

        double target = motorControl.getLastCommandedVelocity(MotorConstants.outtake1);
        if (Double.isNaN(target) || target <= 0) {
            // Fall back to the distance-interpolated target when velocity mode
            // hasn't latched a command yet (power-assist spin-up).
            target = GlobalVariables.outtakeTargetSpeed;
        }
        if (target <= 0) return;

        if (velocity >= target * OuttakeConstants.feedArmTargetFrac) {
            preFirstBallVelocityTicks = velocity;
        }
    }
}
