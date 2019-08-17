package java.util.logging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class Logging implements LoggingMXBean {
  private static LogManager logManager = LogManager.getLogManager();
  
  private static String EMPTY_STRING = "";
  
  public List<String> getLoggerNames() {
    Enumeration enumeration = logManager.getLoggerNames();
    ArrayList arrayList = new ArrayList();
    while (enumeration.hasMoreElements())
      arrayList.add(enumeration.nextElement()); 
    return arrayList;
  }
  
  public String getLoggerLevel(String paramString) {
    Logger logger = logManager.getLogger(paramString);
    if (logger == null)
      return null; 
    Level level = logger.getLevel();
    return (level == null) ? EMPTY_STRING : level.getLevelName();
  }
  
  public void setLoggerLevel(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("loggerName is null"); 
    Logger logger = logManager.getLogger(paramString1);
    if (logger == null)
      throw new IllegalArgumentException("Logger " + paramString1 + "does not exist"); 
    Level level = null;
    if (paramString2 != null) {
      level = Level.findLevel(paramString2);
      if (level == null)
        throw new IllegalArgumentException("Unknown level \"" + paramString2 + "\""); 
    } 
    logger.setLevel(level);
  }
  
  public String getParentLoggerName(String paramString) {
    Logger logger1 = logManager.getLogger(paramString);
    if (logger1 == null)
      return null; 
    Logger logger2 = logger1.getParent();
    return (logger2 == null) ? EMPTY_STRING : logger2.getName();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Logging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */