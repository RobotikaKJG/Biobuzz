package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.EdgeDetection;
import org.firstinspires.ftc.teamcode.HardwareInterface.Gamepad.GamepadIndexValues;
import org.firstinspires.ftc.teamcode.HardwareInterface.Motor.MotorConstants;
import org.firstinspires.ftc.teamcode.Main.Alliance;
import org.firstinspires.ftc.teamcode.Main.AutonomousDependencies;
import org.firstinspires.ftc.teamcode.Main.Dependencies;
import org.firstinspires.ftc.teamcode.Main.GlobalVariables;
import org.firstinspires.ftc.teamcode.Main.IterativeController;
import org.firstinspires.ftc.teamcode.Autonomous.SelectStartVariables;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Regression tests for the usable drive-only starter. Hardware output is mocked, not simulated physics. */
public class StarterRobotTest {
    private HardwareMap hardware;
    private Gamepad driver;
    private Gamepad operator;
    private Telemetry telemetry;
    private DcMotorEx frontLeft, backLeft, frontRight, backRight;

    @Before
    public void setUp() {
        GlobalVariables.reset(Alliance.Red, false);
        hardware = mock(HardwareMap.class);
        driver = new Gamepad();
        operator = new Gamepad();
        telemetry = mock(Telemetry.class);
        frontLeft = mock(DcMotorEx.class);
        backLeft = mock(DcMotorEx.class);
        frontRight = mock(DcMotorEx.class);
        backRight = mock(DcMotorEx.class);
        // Literal names intentionally lock in compatibility with the existing robot configuration.
        when(hardware.get(DcMotorEx.class, "frontLeftMotor")).thenReturn(frontLeft);
        when(hardware.get(DcMotorEx.class, "backLeftMotor")).thenReturn(backLeft);
        when(hardware.get(DcMotorEx.class, "frontRightMotor")).thenReturn(frontRight);
        when(hardware.get(DcMotorEx.class, "backRightMotor")).thenReturn(backRight);
    }

    private Dependencies dependencies() { return new Dependencies(hardware, driver, operator, telemetry); }

    private void clearMotorCalls() { clearInvocations(frontLeft, backLeft, frontRight, backRight); }

    private void assertPowers(double fl, double bl, double fr, double br) {
        verify(frontLeft).setPower(doubleThat(value -> Math.abs(value - fl) < 1e-9));
        verify(backLeft).setPower(doubleThat(value -> Math.abs(value - bl) < 1e-9));
        verify(frontRight).setPower(doubleThat(value -> Math.abs(value - fr) < 1e-9));
        verify(backRight).setPower(doubleThat(value -> Math.abs(value - br) < 1e-9));
    }

    @Test
    public void starterMapsOnlyFourDriveMotorsAndPreservesDirections() {
        Dependencies robot = dependencies();
        new IterativeController(robot).TeleOp();
        verify(hardware).get(DcMotorEx.class, "frontLeftMotor");
        verify(hardware).get(DcMotorEx.class, "backLeftMotor");
        verify(hardware).get(DcMotorEx.class, "frontRightMotor");
        verify(hardware).get(DcMotorEx.class, "backRightMotor");
        verifyNoMoreInteractions(hardware);
        verify(frontLeft).setDirection(DcMotorSimple.Direction.FORWARD);
        verify(backLeft).setDirection(DcMotorSimple.Direction.REVERSE);
        verify(frontRight).setDirection(DcMotorSimple.Direction.REVERSE);
        verify(backRight).setDirection(DcMotorSimple.Direction.FORWARD);
        assertFalse(robot.sensorControl.hasHeading());
    }

    @Test
    public void newStickCommandReachesAllMotorsInTheSameLoopAndReleaseStopsThem() {
        IterativeController controller = new IterativeController(dependencies());
        clearMotorCalls();
        driver.left_stick_y = -1;
        controller.TeleOp();
        assertPowers(1, 1, 1, 1);
        clearMotorCalls();
        driver.left_stick_y = 0;
        controller.TeleOp();
        assertPowers(0, 0, 0, 0);
    }

    @Test
    public void strafeTurnAndCombinedInputPreserveMixingAndNormalization() {
        IterativeController controller = new IterativeController(dependencies());
        clearMotorCalls();
        driver.left_stick_x = 1;
        controller.TeleOp();
        assertPowers(1, -1, -1, 1);
        clearMotorCalls();
        driver.left_stick_x = 0;
        driver.right_stick_x = 1;
        controller.TeleOp();
        assertPowers(-1, -1, 1, 1);
        clearMotorCalls();
        driver.left_stick_y = -1;
        driver.left_stick_x = 1;
        controller.TeleOp();
        assertPowers(1.0 / 3, -1.0 / 3, 1.0 / 3, 1);
    }

    @Test
    public void slowModeScalesDriverInputWithoutSwitchingToOperator() {
        IterativeController controller = new IterativeController(dependencies());
        clearMotorCalls();
        GlobalVariables.slowMode = true;
        driver.left_stick_y = -1;
        operator.left_stick_y = 1;
        controller.TeleOp();
        assertPowers(0.5, 0.5, 0.5, 0.5);
    }

    @Test
    public void stoppingClearsStagedOutputSoLaterFlushCannotRestartMotors() {
        Dependencies robot = dependencies();
        robot.motorControl.setMotorSpeed(MotorConstants.allDrive, 0.75);
        clearMotorCalls();
        robot.stop();
        assertPowers(0, 0, 0, 0);
        clearMotorCalls();
        robot.motorControl.setMotors(MotorConstants.all);
        assertPowers(0, 0, 0, 0);
    }

    @Test
    public void idleAutonomousNeedsNoPeripheralsAndNeverCommandsMovement() {
        AutonomousDependencies robot = new AutonomousDependencies(hardware, driver, operator, telemetry);
        assertNull(robot.drive);
        clearMotorCalls();
        robot.autonomousControl.startAutonomous();
        robot.autonomousControl.runAutonomous();
        robot.autonomousControl.runAutonomous();
        verifyNoInteractions(frontLeft, backLeft, frontRight, backRight);
        verify(hardware).get(DcMotorEx.class, "frontLeftMotor");
        verify(hardware).get(DcMotorEx.class, "backLeftMotor");
        verify(hardware).get(DcMotorEx.class, "frontRightMotor");
        verify(hardware).get(DcMotorEx.class, "backRightMotor");
        verifyNoMoreInteractions(hardware);
    }

    @Test
    public void buttonEdgesFireOnceOnPressAndOnceOnRelease() {
        EdgeDetection edges = new EdgeDetection();
        Gamepad previous = new Gamepad();
        driver.square = true;
        edges.refreshGamepadIndex(driver, previous);
        assertTrue(edges.rising(GamepadIndexValues.square));
        assertFalse(edges.falling(GamepadIndexValues.square));
        previous.square = true;
        edges.refreshGamepadIndex(driver, previous);
        assertFalse(edges.isEdge(GamepadIndexValues.square));
        driver.square = false;
        edges.refreshGamepadIndex(driver, previous);
        assertTrue(edges.falling(GamepadIndexValues.square));
    }

    @Test(timeout = 1000)
    public void prestartSelectionDoesNotWaitForButtonInput() {
        SelectStartVariables selector = new SelectStartVariables(driver, telemetry);
        selector.update();
        assertEquals(Alliance.Red, GlobalVariables.alliance);
        driver.x = true;
        driver.square = true;
        selector.update();
        assertEquals(Alliance.Blue, GlobalVariables.alliance);
    }
}
