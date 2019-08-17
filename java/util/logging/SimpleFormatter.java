package java.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import sun.util.logging.LoggingSupport;

public class SimpleFormatter extends Formatter {
  private static final String format = LoggingSupport.getSimpleFormat();
  
  private final Date dat = new Date();
  
  public String format(LogRecord paramLogRecord) {
    String str1;
    this.dat.setTime(paramLogRecord.getMillis());
    if (paramLogRecord.getSourceClassName() != null) {
      str1 = paramLogRecord.getSourceClassName();
      if (paramLogRecord.getSourceMethodName() != null)
        str1 = str1 + " " + paramLogRecord.getSourceMethodName(); 
    } else {
      str1 = paramLogRecord.getLoggerName();
    } 
    String str2 = formatMessage(paramLogRecord);
    String str3 = "";
    if (paramLogRecord.getThrown() != null) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      printWriter.println();
      paramLogRecord.getThrown().printStackTrace(printWriter);
      printWriter.close();
      str3 = stringWriter.toString();
    } 
    return String.format(format, new Object[] { this.dat, str1, paramLogRecord.getLoggerName(), paramLogRecord.getLevel().getLocalizedLevelName(), str2, str3 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\SimpleFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */