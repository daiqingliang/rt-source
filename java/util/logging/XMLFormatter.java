package java.util.logging;

import java.nio.charset.Charset;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class XMLFormatter extends Formatter {
  private LogManager manager = LogManager.getLogManager();
  
  private void a2(StringBuilder paramStringBuilder, int paramInt) {
    if (paramInt < 10)
      paramStringBuilder.append('0'); 
    paramStringBuilder.append(paramInt);
  }
  
  private void appendISO8601(StringBuilder paramStringBuilder, long paramLong) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    gregorianCalendar.setTimeInMillis(paramLong);
    paramStringBuilder.append(gregorianCalendar.get(1));
    paramStringBuilder.append('-');
    a2(paramStringBuilder, gregorianCalendar.get(2) + 1);
    paramStringBuilder.append('-');
    a2(paramStringBuilder, gregorianCalendar.get(5));
    paramStringBuilder.append('T');
    a2(paramStringBuilder, gregorianCalendar.get(11));
    paramStringBuilder.append(':');
    a2(paramStringBuilder, gregorianCalendar.get(12));
    paramStringBuilder.append(':');
    a2(paramStringBuilder, gregorianCalendar.get(13));
  }
  
  private void escape(StringBuilder paramStringBuilder, String paramString) {
    if (paramString == null)
      paramString = "<null>"; 
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '<') {
        paramStringBuilder.append("&lt;");
      } else if (c == '>') {
        paramStringBuilder.append("&gt;");
      } else if (c == '&') {
        paramStringBuilder.append("&amp;");
      } else {
        paramStringBuilder.append(c);
      } 
    } 
  }
  
  public String format(LogRecord paramLogRecord) {
    StringBuilder stringBuilder = new StringBuilder(500);
    stringBuilder.append("<record>\n");
    stringBuilder.append("  <date>");
    appendISO8601(stringBuilder, paramLogRecord.getMillis());
    stringBuilder.append("</date>\n");
    stringBuilder.append("  <millis>");
    stringBuilder.append(paramLogRecord.getMillis());
    stringBuilder.append("</millis>\n");
    stringBuilder.append("  <sequence>");
    stringBuilder.append(paramLogRecord.getSequenceNumber());
    stringBuilder.append("</sequence>\n");
    String str = paramLogRecord.getLoggerName();
    if (str != null) {
      stringBuilder.append("  <logger>");
      escape(stringBuilder, str);
      stringBuilder.append("</logger>\n");
    } 
    stringBuilder.append("  <level>");
    escape(stringBuilder, paramLogRecord.getLevel().toString());
    stringBuilder.append("</level>\n");
    if (paramLogRecord.getSourceClassName() != null) {
      stringBuilder.append("  <class>");
      escape(stringBuilder, paramLogRecord.getSourceClassName());
      stringBuilder.append("</class>\n");
    } 
    if (paramLogRecord.getSourceMethodName() != null) {
      stringBuilder.append("  <method>");
      escape(stringBuilder, paramLogRecord.getSourceMethodName());
      stringBuilder.append("</method>\n");
    } 
    stringBuilder.append("  <thread>");
    stringBuilder.append(paramLogRecord.getThreadID());
    stringBuilder.append("</thread>\n");
    if (paramLogRecord.getMessage() != null) {
      String str1 = formatMessage(paramLogRecord);
      stringBuilder.append("  <message>");
      escape(stringBuilder, str1);
      stringBuilder.append("</message>");
      stringBuilder.append("\n");
    } 
    ResourceBundle resourceBundle = paramLogRecord.getResourceBundle();
    try {
      if (resourceBundle != null && resourceBundle.getString(paramLogRecord.getMessage()) != null) {
        stringBuilder.append("  <key>");
        escape(stringBuilder, paramLogRecord.getMessage());
        stringBuilder.append("</key>\n");
        stringBuilder.append("  <catalog>");
        escape(stringBuilder, paramLogRecord.getResourceBundleName());
        stringBuilder.append("</catalog>\n");
      } 
    } catch (Exception exception) {}
    Object[] arrayOfObject = paramLogRecord.getParameters();
    if (arrayOfObject != null && arrayOfObject.length != 0 && paramLogRecord.getMessage().indexOf("{") == -1)
      for (byte b = 0; b < arrayOfObject.length; b++) {
        stringBuilder.append("  <param>");
        try {
          escape(stringBuilder, arrayOfObject[b].toString());
        } catch (Exception exception) {
          stringBuilder.append("???");
        } 
        stringBuilder.append("</param>\n");
      }  
    if (paramLogRecord.getThrown() != null) {
      Throwable throwable = paramLogRecord.getThrown();
      stringBuilder.append("  <exception>\n");
      stringBuilder.append("    <message>");
      escape(stringBuilder, throwable.toString());
      stringBuilder.append("</message>\n");
      StackTraceElement[] arrayOfStackTraceElement = throwable.getStackTrace();
      for (byte b = 0; b < arrayOfStackTraceElement.length; b++) {
        StackTraceElement stackTraceElement = arrayOfStackTraceElement[b];
        stringBuilder.append("    <frame>\n");
        stringBuilder.append("      <class>");
        escape(stringBuilder, stackTraceElement.getClassName());
        stringBuilder.append("</class>\n");
        stringBuilder.append("      <method>");
        escape(stringBuilder, stackTraceElement.getMethodName());
        stringBuilder.append("</method>\n");
        if (stackTraceElement.getLineNumber() >= 0) {
          stringBuilder.append("      <line>");
          stringBuilder.append(stackTraceElement.getLineNumber());
          stringBuilder.append("</line>\n");
        } 
        stringBuilder.append("    </frame>\n");
      } 
      stringBuilder.append("  </exception>\n");
    } 
    stringBuilder.append("</record>\n");
    return stringBuilder.toString();
  }
  
  public String getHead(Handler paramHandler) {
    String str;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<?xml version=\"1.0\"");
    if (paramHandler != null) {
      str = paramHandler.getEncoding();
    } else {
      str = null;
    } 
    if (str == null)
      str = Charset.defaultCharset().name(); 
    try {
      Charset charset = Charset.forName(str);
      str = charset.name();
    } catch (Exception exception) {}
    stringBuilder.append(" encoding=\"");
    stringBuilder.append(str);
    stringBuilder.append("\"");
    stringBuilder.append(" standalone=\"no\"?>\n");
    stringBuilder.append("<!DOCTYPE log SYSTEM \"logger.dtd\">\n");
    stringBuilder.append("<log>\n");
    return stringBuilder.toString();
  }
  
  public String getTail(Handler paramHandler) { return "</log>\n"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\XMLFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */