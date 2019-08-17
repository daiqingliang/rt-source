package com.sun.corba.se.spi.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class LogWrapperBase {
  protected Logger logger;
  
  protected String loggerName;
  
  protected LogWrapperBase(Logger paramLogger) {
    this.logger = paramLogger;
    this.loggerName = paramLogger.getName();
  }
  
  protected void doLog(Level paramLevel, String paramString, Object[] paramArrayOfObject, Class paramClass, Throwable paramThrowable) {
    LogRecord logRecord = new LogRecord(paramLevel, paramString);
    if (paramArrayOfObject != null)
      logRecord.setParameters(paramArrayOfObject); 
    inferCaller(paramClass, logRecord);
    logRecord.setThrown(paramThrowable);
    logRecord.setLoggerName(this.loggerName);
    logRecord.setResourceBundle(this.logger.getResourceBundle());
    this.logger.log(logRecord);
  }
  
  private void inferCaller(Class paramClass, LogRecord paramLogRecord) {
    StackTraceElement[] arrayOfStackTraceElement = (new Throwable()).getStackTrace();
    StackTraceElement stackTraceElement = null;
    String str1 = paramClass.getName();
    String str2 = LogWrapperBase.class.getName();
    byte b;
    for (b = 0; b < arrayOfStackTraceElement.length; b++) {
      stackTraceElement = arrayOfStackTraceElement[b];
      String str = stackTraceElement.getClassName();
      if (!str.equals(str1) && !str.equals(str2))
        break; 
    } 
    if (b < arrayOfStackTraceElement.length) {
      paramLogRecord.setSourceClassName(stackTraceElement.getClassName());
      paramLogRecord.setSourceMethodName(stackTraceElement.getMethodName());
    } 
  }
  
  protected void doLog(Level paramLevel, String paramString, Class paramClass, Throwable paramThrowable) { doLog(paramLevel, paramString, null, paramClass, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\logging\LogWrapperBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */