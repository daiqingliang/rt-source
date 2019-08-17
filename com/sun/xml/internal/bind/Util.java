package com.sun.xml.internal.bind;

import java.util.logging.Logger;

public final class Util {
  public static Logger getClassLogger() {
    try {
      StackTraceElement[] arrayOfStackTraceElement = (new Exception()).getStackTrace();
      return Logger.getLogger(arrayOfStackTraceElement[1].getClassName());
    } catch (SecurityException securityException) {
      return Logger.getLogger("com.sun.xml.internal.bind");
    } 
  }
  
  public static String getSystemProperty(String paramString) {
    try {
      return System.getProperty(paramString);
    } catch (SecurityException securityException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */