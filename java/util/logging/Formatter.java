package java.util.logging;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class Formatter {
  public abstract String format(LogRecord paramLogRecord);
  
  public String getHead(Handler paramHandler) { return ""; }
  
  public String getTail(Handler paramHandler) { return ""; }
  
  public String formatMessage(LogRecord paramLogRecord) {
    String str = paramLogRecord.getMessage();
    ResourceBundle resourceBundle = paramLogRecord.getResourceBundle();
    if (resourceBundle != null)
      try {
        str = resourceBundle.getString(paramLogRecord.getMessage());
      } catch (MissingResourceException missingResourceException) {
        str = paramLogRecord.getMessage();
      }  
    try {
      Object[] arrayOfObject = paramLogRecord.getParameters();
      return (arrayOfObject == null || arrayOfObject.length == 0) ? str : ((str.indexOf("{0") >= 0 || str.indexOf("{1") >= 0 || str.indexOf("{2") >= 0 || str.indexOf("{3") >= 0) ? MessageFormat.format(str, arrayOfObject) : str);
    } catch (Exception exception) {
      return str;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Formatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */