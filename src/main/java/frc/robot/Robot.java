// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private Motor motores;
  private final int IDMOTOR1 = 1, IDMOTOR2 = 2, IDMOTOR3 = 3, IDMOTOR4 = 4;
  private final int IDMOTORELEVARION = 10;
  private DifferentialDrive mydrive;
  private XboxController xboxControllerRobot = new XboxController(0);
  private XboxController xboxControllerIntake = new XboxController(1);

  private VictorSPX motorElevation = new VictorSPX(IDMOTORELEVARION);
  private int factorUp = 1;
  private int factorDown = 1;

  private final DigitalInput elevationInputUp = new DigitalInput(0);
  private final DigitalInput elevationInputDown = new DigitalInput(1);


  private final Compressor comp = new Compressor(9, PneumaticsModuleType.REVPH);
  private final DoubleSolenoid solenoid = new DoubleSolenoid(9, PneumaticsModuleType.REVPH, 0, 2);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    motores = new Motor(IDMOTOR2,IDMOTOR4,IDMOTOR1,IDMOTOR3);
    mydrive = new DifferentialDrive(motores.GetMotorLeft(), motores.GetMotorRight());
    mydrive.setSafetyEnabled(false);

    motorElevation.set(ControlMode.PercentOutput,0);

    comp.disable();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    comp.disable();
    solenoid.set(Value.kOff);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // TurboModeRobot();
    // ControlRobot();
    // ControlCompressor();
    ControlElevation();
    
  }

  private void ControlElevation(){
    if(elevationInputUp.get() == true){
      factorUp = 0;
    }
    else{
      factorUp = 1;
    }

    if(elevationInputDown.get() == true){
      factorDown = 0;
    }
    else{
      factorDown = 1;
    }

    if(xboxControllerIntake.getRightY()>0){
      motorElevation.set(ControlMode.PercentOutput,xboxControllerIntake.getRightY()*0.5*factorUp);
    }
    else if(xboxControllerIntake.getRightY()<0){
      motorElevation.set(ControlMode.PercentOutput,xboxControllerIntake.getRightY()*0.5*factorDown);
    }
    else{
      motorElevation.set(ControlMode.PercentOutput,0);
    }
    
  }

  private void ControlCompressor(){
    if (xboxControllerRobot.getAButton()) {
      comp.enableDigital();
    } 
    else if (xboxControllerRobot.getBButton()) {
      comp.disable();
    } 

    if (xboxControllerRobot.getRightBumper()) {
      solenoid.set(Value.kReverse);
    }
    if (xboxControllerRobot.getLeftBumper()) {
      solenoid.set(Value.kForward);
    }
  }

  private void ControlRobot(){
    if (Math.abs(xboxControllerRobot.getLeftY()) >= 0.05 || Math.abs(xboxControllerRobot.getLeftX()) >= 0.05) {  // Movendo Joystick
      mydrive.arcadeDrive(xboxControllerRobot.getLeftY(), xboxControllerRobot.getLeftX()*1.2);
    }
    else {
      motores.StopMotors();
    }
  }

  private void TurboModeRobot(){
    if (xboxControllerRobot.getLeftTriggerAxis() > 0) {
      mydrive.setMaxOutput(0.3);
    }
    else if (xboxControllerRobot.getRightTriggerAxis() > 0) {
      mydrive.setMaxOutput(1);
    }
    else {
      mydrive.setMaxOutput(0.5);
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

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
