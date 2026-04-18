package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose;

import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoOuttakeFarCloseControl {

    public void update() {
        switch (OuttakeStates.getAutoOuttakeFarCloseState()) {
            case cycle:
                if (GlobalVariables.far) {
                    OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
                } else {
                    OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
                }
                break;
            case idle:
                break;
        }
    }
}
