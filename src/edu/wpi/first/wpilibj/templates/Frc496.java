/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Frc496 extends SimpleRobot {

    Jaguar frontLeft, frontRight, rearLeft, rearRight;
    RobotDrive drivetrain;
    Joystick driverStick, operatorStick;
    Victor kicker1, kicker2, leftArm, rightArm;
    Relay spinner;
    double armdrive, xbox2, xbox5, kickerdrive;
    AnalogChannel armPot;
    Timer setArmTime;
    DigitalInput kickerLimit;

    public Frc496() {
        driverStick = new Joystick(1);
        operatorStick = new Joystick(2);

        frontLeft = new Jaguar(1);
        rearLeft = new Jaguar(2);
        frontRight = new Jaguar(3);
        rearRight = new Jaguar(4);

        drivetrain = new RobotDrive(frontLeft, rearLeft, frontRight, rearRight);
        drivetrain.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        drivetrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, false);
        drivetrain.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        drivetrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);

        kicker1 = new Victor(5);
        //kicker2 = new Victor(6);
        
        kickerLimit = new DigitalInput(1);

        leftArm = new Victor(8);
        rightArm = new Victor(7);

        armPot = new AnalogChannel(1);

        setArmTime = new Timer();

        spinner = new Relay(1);

    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {

        frontLeft.setSafetyEnabled(false);
        rearLeft.setSafetyEnabled(false);
        frontRight.setSafetyEnabled(false);
        rearRight.setSafetyEnabled(false);
        while (isAutonomous() && isEnabled()) {
            frontLeft.set(0.5);
            rearLeft.set(0.5);
            frontRight.set(0.5);
            rearRight.set(0.5);
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        drivetrain.setSafetyEnabled(false);
        while (isOperatorControl() && isEnabled()) {
            //updateDashboard();
            drivetrain.mecanumDrive_Polar(driverStick.getMagnitude(), driverStick.getDirectionDegrees(), driverStick.getTwist());

           //System.out.println(armPot.getVoltage());
            /**
             * ********* ARM UP AND DOWN XBOX CONTROLLER *******
             */
            xbox2 = operatorStick.getRawAxis(2);
            if (xbox2 < 0.1 && xbox2 > -0.1) {
                armdrive = 0;
            } else {
                armdrive = xbox2 / 2;
            }

            leftArm.set(armdrive);
            rightArm.set(-(armdrive)); //Set one of these in reverse

            
            /*Set Kicker */
            boolean setKicker = false;

            if (operatorStick.getRawButton(4) == true) {
                setKicker = true;
            }

            if (setKicker == true) {

                int i;
                for (i = 0; i < 500; i++) {
                    kicker1.set(-1);
                    System.out.println(i);
                }
                kicker1.set(0);

            }

            /*** Kicker****/
            boolean kick = false;
            if (operatorStick.getRawButton(3) == true) {
                kick = true;
            }

            if (kick == true) {
                int i;
                for (i = 0; i < 900; i++) {
                    kicker1.set(1);
                    System.out.println(i);
                }
                kicker1.set(0);
            }
            
/***********************Ball Pick Up Roller ++++++++++++++++++++++++++++++++++*/
            spinner.setDirection(Relay.Direction.kForward);
            spinner.set(Relay.Value.kOn);

            /**
             * **********Kicker Joystick XBOX **********
             */
            xbox5 = operatorStick.getRawAxis(5);
            if (xbox5 < 0.1 && xbox5 > -0.1) {
                kickerdrive = 0;
            } else {
                kickerdrive = xbox5;
            }
            kicker1.set(kickerdrive);
        }

    }

    /**
     * This function is called once each time the robot enters test mode.
     */
    public void test() {

    }

    void updateDashboard() {
        Dashboard lowDashData = DriverStation.getInstance().getDashboardPackerLow();
        lowDashData.addCluster();
        {
            lowDashData.addCluster();
            {     //analog modules
                lowDashData.addCluster();
                {
                    for (int i = 1; i <= 8; i++) {
                        lowDashData.addFloat((float) AnalogModule.getInstance(1).getAverageVoltage(i));
                    }
                }
                lowDashData.finalizeCluster();

            }
            lowDashData.finalizeCluster();

            lowDashData.addCluster();
            { //digital modules
                lowDashData.addCluster();
                {
                    lowDashData.addCluster();
                    {
                        int module = 1;
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addShort(DigitalModule.getInstance(module).getAllDIO());
                        lowDashData.addShort(DigitalModule.getInstance(module).getDIODirection());
                        lowDashData.addCluster();
                        {
                            for (int i = 1; i <= 10; i++) {
                                lowDashData.addByte((byte) DigitalModule.getInstance(module).getPWM(i));
                            }
                        }
                        lowDashData.finalizeCluster();
                    }
                    lowDashData.finalizeCluster();
                }
                lowDashData.finalizeCluster();

                lowDashData.finalizeCluster();

            }
            lowDashData.finalizeCluster();

            lowDashData.addByte(Solenoid.getAllFromDefaultModule());
        }
        lowDashData.finalizeCluster();
        lowDashData.commit();

    }
}
