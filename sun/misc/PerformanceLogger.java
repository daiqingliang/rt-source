package sun.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class PerformanceLogger {
  private static final int START_INDEX = 0;
  
  private static final int LAST_RESERVED = 0;
  
  private static boolean perfLoggingOn = false;
  
  private static boolean useNanoTime = false;
  
  private static Vector<TimeData> times;
  
  private static String logFileName = null;
  
  private static Writer logWriter = null;
  
  private static long baseTime;
  
  public static boolean loggingEnabled() { return perfLoggingOn; }
  
  private static long getCurrentTime() { return useNanoTime ? System.nanoTime() : System.currentTimeMillis(); }
  
  public static void setStartTime(String paramString) {
    if (loggingEnabled()) {
      long l = getCurrentTime();
      setStartTime(paramString, l);
    } 
  }
  
  public static void setBaseTime(long paramLong) {
    if (loggingEnabled())
      baseTime = paramLong; 
  }
  
  public static void setStartTime(String paramString, long paramLong) {
    if (loggingEnabled())
      times.set(0, new TimeData(paramString, paramLong)); 
  }
  
  public static long getStartTime() { return loggingEnabled() ? ((TimeData)times.get(0)).getTime() : 0L; }
  
  public static int setTime(String paramString) {
    if (loggingEnabled()) {
      long l = getCurrentTime();
      return setTime(paramString, l);
    } 
    return 0;
  }
  
  public static int setTime(String paramString, long paramLong) {
    if (loggingEnabled())
      synchronized (times) {
        times.add(new TimeData(paramString, paramLong));
        return times.size() - 1;
      }  
    return 0;
  }
  
  public static long getTimeAtIndex(int paramInt) { return loggingEnabled() ? ((TimeData)times.get(paramInt)).getTime() : 0L; }
  
  public static String getMessageAtIndex(int paramInt) { return loggingEnabled() ? ((TimeData)times.get(paramInt)).getMessage() : null; }
  
  public static void outputLog(Writer paramWriter) {
    if (loggingEnabled())
      try {
        synchronized (times) {
          for (byte b = 0; b < times.size(); b++) {
            TimeData timeData = (TimeData)times.get(b);
            if (timeData != null)
              paramWriter.write(b + " " + timeData.getMessage() + ": " + (timeData.getTime() - baseTime) + "\n"); 
          } 
        } 
        paramWriter.flush();
      } catch (Exception exception) {
        System.out.println(exception + ": Writing performance log to " + paramWriter);
      }  
  }
  
  public static void outputLog() { outputLog(logWriter); }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.perflog"));
    if (str != null) {
      perfLoggingOn = true;
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.perflog.nano"));
      if (str1 != null)
        useNanoTime = true; 
      if (str.regionMatches(true, 0, "file:", 0, 5))
        logFileName = str.substring(5); 
      if (logFileName != null && logWriter == null)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                try {
                  File file = new File(logFileName);
                  file.createNewFile();
                  logWriter = new FileWriter(file);
                } catch (Exception exception) {
                  System.out.println(exception + ": Creating logfile " + logFileName + ".  Log to console");
                } 
                return null;
              }
            }); 
      if (logWriter == null)
        logWriter = new OutputStreamWriter(System.out); 
    } 
    times = new Vector(10);
    for (byte b = 0; b; b++)
      times.add(new TimeData("Time " + b + " not set", 0L)); 
  }
  
  static class TimeData {
    String message;
    
    long time;
    
    TimeData(String param1String, long param1Long) {
      this.message = param1String;
      this.time = param1Long;
    }
    
    String getMessage() { return this.message; }
    
    long getTime() { return this.time; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\PerformanceLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */