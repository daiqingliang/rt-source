package sun.util.logging;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.List;

public class LoggingSupport {
  private static final LoggingProxy proxy = (LoggingProxy)AccessController.doPrivileged(new PrivilegedAction<LoggingProxy>() {
        public LoggingProxy run() {
          try {
            Class clazz = Class.forName("java.util.logging.LoggingProxyImpl", true, null);
            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            return (LoggingProxy)field.get(null);
          } catch (ClassNotFoundException classNotFoundException) {
            return null;
          } catch (NoSuchFieldException noSuchFieldException) {
            throw new AssertionError(noSuchFieldException);
          } catch (IllegalAccessException illegalAccessException) {
            throw new AssertionError(illegalAccessException);
          } 
        }
      });
  
  private static final String DEFAULT_FORMAT = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
  
  private static final String FORMAT_PROP_KEY = "java.util.logging.SimpleFormatter.format";
  
  public static boolean isAvailable() { return (proxy != null); }
  
  private static void ensureAvailable() {
    if (proxy == null)
      throw new AssertionError("Should not here"); 
  }
  
  public static List<String> getLoggerNames() {
    ensureAvailable();
    return proxy.getLoggerNames();
  }
  
  public static String getLoggerLevel(String paramString) {
    ensureAvailable();
    return proxy.getLoggerLevel(paramString);
  }
  
  public static void setLoggerLevel(String paramString1, String paramString2) {
    ensureAvailable();
    proxy.setLoggerLevel(paramString1, paramString2);
  }
  
  public static String getParentLoggerName(String paramString) {
    ensureAvailable();
    return proxy.getParentLoggerName(paramString);
  }
  
  public static Object getLogger(String paramString) {
    ensureAvailable();
    return proxy.getLogger(paramString);
  }
  
  public static Object getLevel(Object paramObject) {
    ensureAvailable();
    return proxy.getLevel(paramObject);
  }
  
  public static void setLevel(Object paramObject1, Object paramObject2) {
    ensureAvailable();
    proxy.setLevel(paramObject1, paramObject2);
  }
  
  public static boolean isLoggable(Object paramObject1, Object paramObject2) {
    ensureAvailable();
    return proxy.isLoggable(paramObject1, paramObject2);
  }
  
  public static void log(Object paramObject1, Object paramObject2, String paramString) {
    ensureAvailable();
    proxy.log(paramObject1, paramObject2, paramString);
  }
  
  public static void log(Object paramObject1, Object paramObject2, String paramString, Throwable paramThrowable) {
    ensureAvailable();
    proxy.log(paramObject1, paramObject2, paramString, paramThrowable);
  }
  
  public static void log(Object paramObject1, Object paramObject2, String paramString, Object... paramVarArgs) {
    ensureAvailable();
    proxy.log(paramObject1, paramObject2, paramString, paramVarArgs);
  }
  
  public static Object parseLevel(String paramString) {
    ensureAvailable();
    return proxy.parseLevel(paramString);
  }
  
  public static String getLevelName(Object paramObject) {
    ensureAvailable();
    return proxy.getLevelName(paramObject);
  }
  
  public static int getLevelValue(Object paramObject) {
    ensureAvailable();
    return proxy.getLevelValue(paramObject);
  }
  
  public static String getSimpleFormat() { return getSimpleFormat(true); }
  
  static String getSimpleFormat(boolean paramBoolean) {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty("java.util.logging.SimpleFormatter.format"); }
        });
    if (paramBoolean && proxy != null && str == null)
      str = proxy.getProperty("java.util.logging.SimpleFormatter.format"); 
    if (str != null) {
      try {
        String.format(str, new Object[] { new Date(), "", "", "", "", "" });
      } catch (IllegalArgumentException illegalArgumentException) {
        str = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
      } 
    } else {
      str = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\logging\LoggingSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */