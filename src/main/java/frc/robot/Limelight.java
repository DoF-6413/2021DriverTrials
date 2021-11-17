// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/** Add your docs here. */
public class Limelight 
{
     public double getTx ()
     {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        double x = tx.getDouble(0);
        return x;
     }

     public double getTy ()
     {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry ty = table.getEntry("ty"); 
        double y = ty.getDouble(0);
        return y;
     }

     public void setDriverCamera (boolean x) 
     {
         if (x == true)
         {
            NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
            table.getEntry("camMode").setNumber(1);
         }
         else
         {
            NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
            table.getEntry("camMode").setNumber(0); 
         }
     }

    


}
