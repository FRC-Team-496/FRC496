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
    Timer autoMove;
    DigitalInput kickerTop, kickerBottom;
    boolean armSafe, kicked, movedForward;

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

        kickerTop = new DigitalInput(1);
        kickerBottom = new DigitalInput(2);

        leftArm = new Victor(8);
        rightArm = new Victor(7);

        armPot = new AnalogChannel(1);

        autoMove = new Timer();

        spinner = new Relay(1);

    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        armSafe = false;
        kicked = false;
        movedForward = false;
        int i;

        while (isAutonomous() && isEnabled()) {
            while (!movedForward) {
                for (i = 0; i < 4000; i++) {
                    frontLeft.set(-0.5);
                    rearLeft.set(-0.5);
                    frontRight.set(0.5);
                    rearRight.set(0.5);
                }
                frontLeft.set(0);
                rearLeft.set(0);
                frontRight.set(0);
                rearRight.set(0);
                movedForward = !movedForward;
            }
            while (!armSafe) {
                double arm = armPot.getVoltage();

                if (arm > 1.76) {
                    armdrive = 0.7;
                } else {
                    armdrive = 0;
                    armSafe = !armSafe;
                }
                leftArm.set(armdrive);
                rightArm.set(-(armdrive)); //Set one of these in reverse
            }
            leftArm.set(0);
            rightArm.set(0);

            while (!kicked) {
                boolean safeToKick = kickerBottom.get();
                if (!safeToKick) {
                    kicker1.set(0.7);
                } else if (safeToKick) {
                    kicker1.set(0);
                    kicked = !kicked;
                }
            }
            kicker1.set(0);
        }

        leftArm.set(0);
        rightArm.set(0);
        kicker1.set(0);
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        drivetrain.setSafetyEnabled(false);

        boolean readyArm = false;
        boolean spinnerOn = false;
        boolean kick = false;
        boolean load = false;
        boolean pass = false;
        kickerdrive = 0;
        while (isOperatorControl() && isEnabled()) {
            //updateDashboard();
            drivetrain.mecanumDrive_Polar(driverStick.getMagnitude(), driverStick.getDirectionDegrees(), driverStick.getTwist());

            System.out.println(armPot.getVoltage());
            boolean safeToKick = kickerBottom.get();
            boolean safeToLoad = kickerTop.get();
            //System.out.println("Safe To Kick:" + safeToKick);
            //System.out.println("safe To Load: " + safeToLoad);
            /**
             * ********* ARM UP AND DOWN XBOX CONTROLLER *******
             */
            xbox2 = -operatorStick.getRawAxis(2);
            double arm = armPot.getVoltage();

            if (xbox2 < 0.1 && xbox2 > -0.1) {
                armdrive = 0;
            } else {
                if (arm <= 1.5 && xbox2 > 0) {
                    armdrive = 0;
                } else if (arm >= 4.80 && xbox2 < 0) {
                    armdrive = 0;
                } else {
                    armdrive = xbox2 / 1.5;
                }

            }

            leftArm.set(armdrive);
            rightArm.set(-(armdrive)); //Set one of these in reverse

            if (operatorStick.getRawButton(1)) {
                readyArm = true;
            }

            if (readyArm) {
                if (arm > 1.80) {
                    armdrive = 0.7;
                } else if (arm < 1.75) {
                    armdrive = -0.2;
                } else {
                    armdrive = 0;
                    readyArm = !readyArm;
                }
            }

            leftArm.set(armdrive);
            rightArm.set(-(armdrive)); //Set one of these in reverse

            if (operatorStick.getRawButton(3) == true) { //X
                kick = true;
            }
            //System.out.println("kick: " + kick);

            if (kick && !safeToKick) {
                kickerdrive = 1;
            } else if (kick && safeToKick) {
                kickerdrive = 0;
                kick = false;
            }

            if (operatorStick.getRawButton(4) == true) {
                load = true;
            }

            if (load && !safeToLoad) {
                kickerdrive = -0.6;
            } else if (load && safeToLoad) {
                kickerdrive = 0;
                load = false;
            }
            
            if (operatorStick.getRawButton(1) == true) {
                pass = true;
            }
            
            if (pass && !safeToKick) {
                kickerdrive = 1;
            } else if (pass && safeToKick) {
                kickerdrive = 0;
                pass = false;
            }
            //System.out.println("load: " + load);
            kicker1.set(kickerdrive);

            /**
             * *********************Ball Pick Up Roller
             * ++++++++++++++++++++++++++++++++++
             */
            spinner.setDirection(Relay.Direction.kReverse);
            if (operatorStick.getRawButton(8)) {
                //spinnerOn++;
                spinnerOn = !spinnerOn;
            }

            if (spinnerOn) {
                spinner.set(Relay.Value.kOn);
            } else {
                spinner.set(Relay.Value.kOff);
            }

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
