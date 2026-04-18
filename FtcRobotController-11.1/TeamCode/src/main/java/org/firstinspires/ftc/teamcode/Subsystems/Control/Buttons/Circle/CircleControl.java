package org.firstinspires.ftc.teamcode.Subsystems.Control.Buttons.Circle;

import org.firstinspires.ftc.teamcode.Subsystems.Control.ButtonStates;

public class CircleControl {
    public void update() {
        switch (ButtonStates.getCircleState()) {
            case turretAdjust:
//                OuttakeStates.setTurretServoState(TurretServoStates.adjust);
                break;
            case stopTurret:
//                OuttakeStates.setTurretServoState(TurretServoStates.idle);
                break;
            case idle:
                break;
        }
    }
}
