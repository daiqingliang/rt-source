package com.sun.activation.registries;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogSupport {
  private static boolean debug = false;
  
  private static Logger logger;
  
  private static final Level level = Level.FINE;
  
  public static void log(String paramString) {
    if (debug)
      System.out.println(paramString); 
    logger.log(level, paramString);
  }
  
  public static void log(String paramString, Throwable paramThrowable) {
    if (debug)
      System.out.println(paramString + "; Exception: " + paramThrowable); 
    logger.log(level, paramString, paramThrowable);
  }
  
  public static boolean isLoggable() { return (debug || logger.isLoggable(level)); }
  
  static  {
    try {
      debug = Boolean.getBoolean("javax.activation.debug");
    } catch (Throwable throwable) {}
    logger = Logger.getLogger("javax.activation");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\activation\registries\LogSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */