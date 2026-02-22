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
//                if (sensorControl.getTagDistance() < 2.3) {
                calculateDistanceToTarget();
                if (GlobalVariables.distanceToTarget < OuttakeConstants.farShootingThreshold) {
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

    private void calculateDistanceToTarget() {
        Pose2D currentPos = sensorControl.getPinpointPos();
        double xDist = 0;
        switch (GlobalVariables.alliance){
            case Red:
                xDist = OuttakeConstants.redTargetX - currentPos.getX(DistanceUnit.MM);
                break;
            case Blue:
                xDist = OuttakeConstants.blueTargetX - currentPos.getX(DistanceUnit.MM);
                break;
        }
        double yDist = OuttakeConstants.targetY - currentPos.getY(DistanceUnit.MM);
        GlobalVariables.distanceToTarget =  Math.sqrt(Math.pow(xDist,2) + Math.pow(yDist,2));
    }
}
