package com.sun.corba.se.impl.naming.cosnaming;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.omg.CosNaming.NameComponent;

public class NamingUtils {
  public static boolean debug = false;
  
  public static PrintStream debugStream;
  
  public static PrintStream errStream;
  
  public static void dprint(String paramString) {
    if (debug && debugStream != null)
      debugStream.println(paramString); 
  }
  
  public static void errprint(String paramString) {
    if (errStream != null) {
      errStream.println(paramString);
    } else {
      System.err.println(paramString);
    } 
  }
  
  public static void printException(Exception paramException) {
    if (errStream != null) {
      paramException.printStackTrace(errStream);
    } else {
      paramException.printStackTrace();
    } 
  }
  
  public static void makeDebugStream(File paramFile) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
    debugStream = new PrintStream(dataOutputStream);
    debugStream.println("Debug Stream Enabled.");
  }
  
  public static void makeErrStream(File paramFile) throws IOException {
    if (debug) {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
      errStream = new PrintStream(dataOutputStream);
      dprint("Error stream setup completed.");
    } 
  }
  
  static String getDirectoryStructuredName(NameComponent[] paramArrayOfNameComponent) {
    StringBuffer stringBuffer = new StringBuffer("/");
    for (byte b = 0; b < paramArrayOfNameComponent.length; b++)
      stringBuffer.append((paramArrayOfNameComponent[b]).id + "." + (paramArrayOfNameComponent[b]).kind); 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\NamingUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */