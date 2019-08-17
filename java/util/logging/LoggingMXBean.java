package java.util.logging;

import java.util.List;

public interface LoggingMXBean {
  List<String> getLoggerNames();
  
  String getLoggerLevel(String paramString);
  
  void setLoggerLevel(String paramString1, String paramString2);
  
  String getParentLoggerName(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\LoggingMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */