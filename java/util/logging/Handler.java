package java.util.logging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

public abstract class Handler {
  private static final int offValue = Level.OFF.intValue();
  
  private final LogManager manager = LogManager.getLogManager();
  
  boolean sealed = true;
  
  public abstract void publish(LogRecord paramLogRecord);
  
  public abstract void flush();
  
  public abstract void close();
  
  public void setFormatter(Formatter paramFormatter) throws SecurityException {
    checkPermission();
    paramFormatter.getClass();
    this.formatter = paramFormatter;
  }
  
  public Formatter getFormatter() { return this.formatter; }
  
  public void setEncoding(String paramString) throws SecurityException, UnsupportedEncodingException {
    checkPermission();
    if (paramString != null)
      try {
        if (!Charset.isSupported(paramString))
          throw new UnsupportedEncodingException(paramString); 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {
        throw new UnsupportedEncodingException(paramString);
      }  
    this.encoding = paramString;
  }
  
  public String getEncoding() { return this.encoding; }
  
  public void setFilter(Filter paramFilter) throws SecurityException {
    checkPermission();
    this.filter = paramFilter;
  }
  
  public Filter getFilter() { return this.filter; }
  
  public void setErrorManager(ErrorManager paramErrorManager) {
    checkPermission();
    if (paramErrorManager == null)
      throw new NullPointerException(); 
    this.errorManager = paramErrorManager;
  }
  
  public ErrorManager getErrorManager() {
    checkPermission();
    return this.errorManager;
  }
  
  protected void reportError(String paramString, Exception paramException, int paramInt) {
    try {
      this.errorManager.error(paramString, paramException, paramInt);
    } catch (Exception exception) {
      System.err.println("Handler.reportError caught:");
      exception.printStackTrace();
    } 
  }
  
  public void setLevel(Level paramLevel) throws SecurityException {
    if (paramLevel == null)
      throw new NullPointerException(); 
    checkPermission();
    this.logLevel = paramLevel;
  }
  
  public Level getLevel() { return this.logLevel; }
  
  public boolean isLoggable(LogRecord paramLogRecord) {
    int i = getLevel().intValue();
    if (paramLogRecord.getLevel().intValue() < i || i == offValue)
      return false; 
    Filter filter1 = getFilter();
    return (filter1 == null) ? true : filter1.isLoggable(paramLogRecord);
  }
  
  void checkPermission() {
    if (this.sealed)
      this.manager.checkPermission(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */