package com.sun.corba.se.impl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ORBProperties {
  public static final String ORB_CLASS = "org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl";
  
  public static final String ORB_SINGLETON_CLASS = "org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton";
  
  public static void main(String[] paramArrayOfString) {
    try {
      String str = System.getProperty("java.home");
      File file = new File(str + File.separator + "lib" + File.separator + "orb.properties");
      if (file.exists())
        return; 
      fileOutputStream = new FileOutputStream(file);
      printWriter = new PrintWriter(fileOutputStream);
      try {
        printWriter.println("org.omg.CORBA.ORBClass=com.sun.corba.se.impl.orb.ORBImpl");
        printWriter.println("org.omg.CORBA.ORBSingletonClass=com.sun.corba.se.impl.orb.ORBSingleton");
      } finally {
        printWriter.close();
        fileOutputStream.close();
      } 
    } catch (Exception exception) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\ORBProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */