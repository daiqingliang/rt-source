package com.sun.istack.internal.logging;

import com.sun.istack.internal.NotNull;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logger {
  private static final String WS_LOGGING_SUBSYSTEM_NAME_ROOT = "com.sun.metro";
  
  private static final String ROOT_WS_PACKAGE = "com.sun.xml.internal.ws.";
  
  private static final Level METHOD_CALL_LEVEL_VALUE = Level.FINEST;
  
  private final String componentClassName;
  
  private final Logger logger;
  
  protected Logger(String paramString1, String paramString2) {
    this.componentClassName = "[" + paramString2 + "] ";
    this.logger = Logger.getLogger(paramString1);
  }
  
  @NotNull
  public static Logger getLogger(@NotNull Class<?> paramClass) { return new Logger(getSystemLoggerName(paramClass), paramClass.getName()); }
  
  @NotNull
  public static Logger getLogger(@NotNull String paramString, @NotNull Class<?> paramClass) { return new Logger(paramString, paramClass.getName()); }
  
  static final String getSystemLoggerName(@NotNull Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder(paramClass.getPackage().getName());
    int i = stringBuilder.lastIndexOf("com.sun.xml.internal.ws.");
    if (i > -1) {
      stringBuilder.replace(0, i + "com.sun.xml.internal.ws.".length(), "");
      StringTokenizer stringTokenizer = new StringTokenizer(stringBuilder.toString(), ".");
      stringBuilder = (new StringBuilder("com.sun.metro")).append(".");
      if (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        if ("api".equals(str))
          str = stringTokenizer.nextToken(); 
        stringBuilder.append(str);
      } 
    } 
    return stringBuilder.toString();
  }
  
  public void log(Level paramLevel, String paramString) {
    if (!this.logger.isLoggable(paramLevel))
      return; 
    this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void log(Level paramLevel, String paramString, Object paramObject) {
    if (!this.logger.isLoggable(paramLevel))
      return; 
    this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramString, paramObject);
  }
  
  public void log(Level paramLevel, String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(paramLevel))
      return; 
    this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void log(Level paramLevel, String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(paramLevel))
      return; 
    this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void finest(String paramString) {
    if (!this.logger.isLoggable(Level.FINEST))
      return; 
    this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void finest(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.FINEST))
      return; 
    this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void finest(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.FINEST))
      return; 
    this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void finer(String paramString) {
    if (!this.logger.isLoggable(Level.FINER))
      return; 
    this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void finer(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.FINER))
      return; 
    this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void finer(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.FINER))
      return; 
    this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void fine(String paramString) {
    if (!this.logger.isLoggable(Level.FINE))
      return; 
    this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void fine(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.FINE))
      return; 
    this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void info(String paramString) {
    if (!this.logger.isLoggable(Level.INFO))
      return; 
    this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void info(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.INFO))
      return; 
    this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void info(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.INFO))
      return; 
    this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void config(String paramString) {
    if (!this.logger.isLoggable(Level.CONFIG))
      return; 
    this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void config(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.CONFIG))
      return; 
    this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void config(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.CONFIG))
      return; 
    this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void warning(String paramString) {
    if (!this.logger.isLoggable(Level.WARNING))
      return; 
    this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void warning(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.WARNING))
      return; 
    this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void warning(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.WARNING))
      return; 
    this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public void severe(String paramString) {
    if (!this.logger.isLoggable(Level.SEVERE))
      return; 
    this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramString);
  }
  
  public void severe(String paramString, Object[] paramArrayOfObject) {
    if (!this.logger.isLoggable(Level.SEVERE))
      return; 
    this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramString, paramArrayOfObject);
  }
  
  public void severe(String paramString, Throwable paramThrowable) {
    if (!this.logger.isLoggable(Level.SEVERE))
      return; 
    this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramString, paramThrowable);
  }
  
  public boolean isMethodCallLoggable() { return this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE); }
  
  public boolean isLoggable(Level paramLevel) { return this.logger.isLoggable(paramLevel); }
  
  public void setLevel(Level paramLevel) { this.logger.setLevel(paramLevel); }
  
  public void entering() {
    if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE))
      return; 
    this.logger.entering(this.componentClassName, getCallerMethodName());
  }
  
  public void entering(Object... paramVarArgs) {
    if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE))
      return; 
    this.logger.entering(this.componentClassName, getCallerMethodName(), paramVarArgs);
  }
  
  public void exiting() {
    if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE))
      return; 
    this.logger.exiting(this.componentClassName, getCallerMethodName());
  }
  
  public void exiting(Object paramObject) {
    if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE))
      return; 
    this.logger.exiting(this.componentClassName, getCallerMethodName(), paramObject);
  }
  
  public <T extends Throwable> T logSevereException(T paramT, Throwable paramThrowable) {
    if (this.logger.isLoggable(Level.SEVERE))
      if (paramThrowable == null) {
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        paramT.initCause(paramThrowable);
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramThrowable);
      }  
    return paramT;
  }
  
  public <T extends Throwable> T logSevereException(T paramT, boolean paramBoolean) {
    if (this.logger.isLoggable(Level.SEVERE))
      if (paramBoolean && paramT.getCause() != null) {
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      } else {
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      }  
    return paramT;
  }
  
  public <T extends Throwable> T logSevereException(T paramT) {
    if (this.logger.isLoggable(Level.SEVERE))
      if (paramT.getCause() == null) {
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      }  
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, Throwable paramThrowable, Level paramLevel) {
    if (this.logger.isLoggable(paramLevel))
      if (paramThrowable == null) {
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        paramT.initCause(paramThrowable);
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramThrowable);
      }  
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, boolean paramBoolean, Level paramLevel) {
    if (this.logger.isLoggable(paramLevel))
      if (paramBoolean && paramT.getCause() != null) {
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      } else {
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      }  
    return paramT;
  }
  
  public <T extends Throwable> T logException(T paramT, Level paramLevel) {
    if (this.logger.isLoggable(paramLevel))
      if (paramT.getCause() == null) {
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage());
      } else {
        this.logger.logp(paramLevel, this.componentClassName, getCallerMethodName(), paramT.getMessage(), paramT.getCause());
      }  
    return paramT;
  }
  
  private static String getCallerMethodName() { return getStackMethodName(5); }
  
  private static String getStackMethodName(int paramInt) {
    String str;
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    if (arrayOfStackTraceElement.length > paramInt + 1) {
      str = arrayOfStackTraceElement[paramInt].getMethodName();
    } else {
      str = "UNKNOWN METHOD";
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */