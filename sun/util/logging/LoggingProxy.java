package sun.util.logging;

import java.util.List;

public interface LoggingProxy {
  Object getLogger(String paramString);
  
  Object getLevel(Object paramObject);
  
  void setLevel(Object paramObject1, Object paramObject2);
  
  boolean isLoggable(Object paramObject1, Object paramObject2);
  
  void log(Object paramObject1, Object paramObject2, String paramString);
  
  void log(Object paramObject1, Object paramObject2, String paramString, Throwable paramThrowable);
  
  void log(Object paramObject1, Object paramObject2, String paramString, Object... paramVarArgs);
  
  List<String> getLoggerNames();
  
  String getLoggerLevel(String paramString);
  
  void setLoggerLevel(String paramString1, String paramString2);
  
  String getParentLoggerName(String paramString);
  
  Object parseLevel(String paramString);
  
  String getLevelName(Object paramObject);
  
  int getLevelValue(Object paramObject);
  
  String getProperty(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\logging\LoggingProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */