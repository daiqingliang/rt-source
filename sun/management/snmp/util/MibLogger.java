package sun.management.snmp.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MibLogger {
  final Logger logger;
  
  final String className;
  
  static String getClassName(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    if (paramClass.isArray())
      return getClassName(paramClass.getComponentType()) + "[]"; 
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    int j = str.length();
    return (i < 0 || i >= j) ? str : str.substring(i + 1, j);
  }
  
  static String getLoggerName(Class<?> paramClass) {
    if (paramClass == null)
      return "sun.management.snmp.jvminstr"; 
    Package package = paramClass.getPackage();
    if (package == null)
      return "sun.management.snmp.jvminstr"; 
    String str = package.getName();
    return (str == null) ? "sun.management.snmp.jvminstr" : str;
  }
  
  public MibLogger(Class<?> paramClass) { this(getLoggerName(paramClass), getClassName(paramClass)); }
  
  public MibLogger(Class<?> paramClass, String paramString) { this(getLoggerName(paramClass) + ((paramString == null) ? "" : ("." + paramString)), getClassName(paramClass)); }
  
  public MibLogger(String paramString) { this("sun.management.snmp.jvminstr", paramString); }
  
  public MibLogger(String paramString1, String paramString2) {
    Logger logger1 = null;
    try {
      logger1 = Logger.getLogger(paramString1);
    } catch (Exception exception) {}
    this.logger = logger1;
    this.className = paramString2;
  }
  
  protected Logger getLogger() { return this.logger; }
  
  public boolean isTraceOn() {
    Logger logger1 = getLogger();
    return (logger1 == null) ? false : logger1.isLoggable(Level.FINE);
  }
  
  public boolean isDebugOn() {
    Logger logger1 = getLogger();
    return (logger1 == null) ? false : logger1.isLoggable(Level.FINEST);
  }
  
  public boolean isInfoOn() {
    Logger logger1 = getLogger();
    return (logger1 == null) ? false : logger1.isLoggable(Level.INFO);
  }
  
  public boolean isConfigOn() {
    Logger logger1 = getLogger();
    return (logger1 == null) ? false : logger1.isLoggable(Level.CONFIG);
  }
  
  public void config(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.CONFIG, this.className, paramString1, paramString2); 
  }
  
  public void config(String paramString, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.CONFIG, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public void config(String paramString1, String paramString2, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.CONFIG, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public void error(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.SEVERE, this.className, paramString1, paramString2); 
  }
  
  public void info(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.INFO, this.className, paramString1, paramString2); 
  }
  
  public void info(String paramString, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.INFO, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public void info(String paramString1, String paramString2, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.INFO, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public void warning(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.WARNING, this.className, paramString1, paramString2); 
  }
  
  public void warning(String paramString, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.WARNING, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public void warning(String paramString1, String paramString2, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.WARNING, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public void trace(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINE, this.className, paramString1, paramString2); 
  }
  
  public void trace(String paramString, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINE, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public void trace(String paramString1, String paramString2, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINE, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public void debug(String paramString1, String paramString2) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINEST, this.className, paramString1, paramString2); 
  }
  
  public void debug(String paramString, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINEST, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public void debug(String paramString1, String paramString2, Throwable paramThrowable) {
    Logger logger1 = getLogger();
    if (logger1 != null)
      logger1.logp(Level.FINEST, this.className, paramString1, paramString2, paramThrowable); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\MibLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */