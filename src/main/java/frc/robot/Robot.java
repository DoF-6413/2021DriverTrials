// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

// limelight imports
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //Motor & Control
  private final WPI_VictorSPX m_leftMotor = new WPI_VictorSPX(5);
  private final WPI_VictorSPX m_rightMotor = new WPI_VictorSPX(3);
  private final WPI_VictorSPX m_leftfollow = new WPI_VictorSPX(2);
  private final WPI_VictorSPX m_rightfollow = new WPI_VictorSPX(4);

  private final WPI_TalonSRX m_TopIntakeMotor1 = new WPI_TalonSRX(6); // gray
  private final WPI_TalonSRX m_BottomIntakeMotor2 = new WPI_TalonSRX(8); // pink :)
  private final WPI_TalonSRX m_ShooterLeft = new WPI_TalonSRX(7); // green 
  private final WPI_TalonSRX m_ShooterRight = new WPI_TalonSRX(9);

  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
  private final Joystick m_Extreme0 = new Joystick(0); //Left : shooter
  private final Joystick  m_Extreme1 = new Joystick(1); //Right : intake

  private Encoder m_encoder1 = new Encoder(0, 1);
  private Encoder m_encoder2 = new Encoder(2, 3);

  //Motor
  private final double TOPINTAKE_SPEED = .5;

  public Timer time = new Timer();
  public Timer time1 = new Timer();
  public double starttime = 0;

  double scaleJoystickInput(double joyIn) {
    return Math.signum(joyIn) * Math.pow( Math.abs(joyIn), 2);
  }

  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //Motor init
    /*m_leftMotor.setInverted(true);
    m_rightMotor.setInverted(true);
    m_leftfollow.setInverted(true);
    m_rightfollow.setInverted(true);*/

    m_leftfollow.follow(m_leftMotor);
    m_rightfollow.follow(m_rightMotor);

    // Use SetDistancePerPulse to set the multiplier for GetDistance
    // This is set up assuming a 6 inch wheel with a 360 CPR encoder.

    m_encoder1.setDistancePerPulse((Math.PI * 6) / 360.0);
    m_encoder2.setDistancePerPulse((Math.PI * 6) / 360.0); 
  }

  
  @Override
  public void robotPeriodic() {
    
  }

  
  @Override
  public void autonomousInit() {
    //print hello to the console
    System.out.println("hello :)");
    //power shooters
    m_ShooterLeft.set(-1);
    m_ShooterRight.set(-1);
    m_TopIntakeMotor1.set(0);
    m_BottomIntakeMotor2.set(0);
    m_encoder1.reset();
    m_encoder2.reset();
    m_robotDrive.arcadeDrive(0, 0);
    time.start();
    time1.start();
    starttime = time1.get();
    //start timer
    
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    //power intake motors
    if (time.hasPeriodPassed(2.5))
    {
      m_TopIntakeMotor1.set(-TOPINTAKE_SPEED);
      m_BottomIntakeMotor2.set(TOPINTAKE_SPEED);
      time.reset();
      time.stop();
    }
    if (time1.get()-starttime >= 7.5){
    { 
      System.out.println(m_encoder2.getDistance());
      if (Math.abs(m_encoder2.getDistance()) < 24){
        m_robotDrive.arcadeDrive(-0.75, 0);
      }
      else m_robotDrive.arcadeDrive(0, 0);
    }
  }


  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    m_robotDrive.arcadeDrive(0, 0);
    m_ShooterLeft.set(0);
    m_ShooterRight.set(0);
  }
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if (m_Extreme0.getTrigger()) { //Intake
      m_TopIntakeMotor1.set(-TOPINTAKE_SPEED);
      m_BottomIntakeMotor2.set(TOPINTAKE_SPEED);
    } else {
        m_TopIntakeMotor1.set(0);
        m_BottomIntakeMotor2.set(0);
       // stop motor
    }
    //grabbing values
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");

    //read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
        

    if(m_Extreme1.getTrigger()) { //Shooter
      m_ShooterLeft.set(-1);
      m_ShooterRight.set(-1);
    } else {
      m_ShooterLeft.set(0);
      m_ShooterRight.set(0);
    }

    double forward = scaleJoystickInput( m_Extreme0.getY(Hand.kLeft) );
    double rotation = scaleJoystickInput( m_Extreme1.getX(Hand.kRight) );
    m_robotDrive.arcadeDrive(forward, rotation);

    System.out.println("------");
    System.out.println("enc1 " + m_encoder1.getDistance() + " " + m_encoder1.getDirection());
    System.out.println("enc2 " + m_encoder2.getDistance() + " " + m_encoder2.getDirection());

    if(m_Extreme0.getRawButton(2) || m_Extreme1.getRawButton(2)) {
      m_encoder1.reset();
      m_encoder2.reset();
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
  // bring motors up to speed
}
