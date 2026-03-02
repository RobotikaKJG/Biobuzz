package org.firstinspires.ftc.teamcode.Subsystems.Outtake.AutoOuttakeFarClose;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.HardwareInterface.Sensor.SensorControl;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Main.Pose2D;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeMotor.OuttakeMotorStates;
import org.firstinspires.ftc.teamcode.Subsystems.Outtake.OuttakeStates;

public class AutoOuttakeFarCloseControl {
    private SensorControl sensorControl;

    public AutoOuttakeFarCloseControl(SensorControl sensorControl) {
        this.sensorControl = sensorControl;
    }

    public void update() {
        updateStates();
    }

    public void updateStates() {
        switch (OuttakeStates.getAutoOuttakeFarCloseState()) {
            case cycle:
                if (sensorControl.getTagDistance() < OuttakeConstants.maxDistance + 0.5) {
                        OuttakeStates.setMotorState(OuttakeMotorStates.forwardClose);
                        GlobalVariables.far = false;
                    }
                    else {
                        OuttakeStates.setMotorState(OuttakeMotorStates.forwardFar);
                        GlobalVariables.far = true;
                    }
                break;
            case idle:
                break;
        }
    }
}
