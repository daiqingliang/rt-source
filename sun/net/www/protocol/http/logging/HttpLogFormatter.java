package sun.net.www.protocol.http.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpLogFormatter extends SimpleFormatter {
  public HttpLogFormatter() {
    if (pattern == null)
      cpattern = (pattern = Pattern.compile("\\{[^\\}]*\\}")).compile("[^,\\] ]{2,}"); 
  }
  
  public String format(LogRecord paramLogRecord) {
    String str1 = paramLogRecord.getSourceClassName();
    if (str1 == null || (!str1.startsWith("sun.net.www.protocol.http") && !str1.startsWith("sun.net.www.http")))
      return super.format(paramLogRecord); 
    String str2 = paramLogRecord.getMessage();
    StringBuilder stringBuilder = new StringBuilder("HTTP: ");
    if (str2.startsWith("sun.net.www.MessageHeader@")) {
      Matcher matcher = pattern.matcher(str2);
      while (matcher.find()) {
        int i = matcher.start();
        int j = matcher.end();
        String str = str2.substring(i + 1, j - 1);
        if (str.startsWith("null: "))
          str = str.substring(6); 
        if (str.endsWith(": null"))
          str = str.substring(0, str.length() - 6); 
        stringBuilder.append("\t").append(str).append("\n");
      } 
    } else if (str2.startsWith("Cookies retrieved: {")) {
      String str = str2.substring(20);
      stringBuilder.append("Cookies from handler:\n");
      while (str.length() >= 7) {
        if (str.startsWith("Cookie=[")) {
          String str3 = str.substring(8);
          int i = str3.indexOf("Cookie2=[");
          if (i > 0) {
            str3 = str3.substring(0, i - 1);
            str = str3.substring(i);
          } else {
            str = "";
          } 
          if (str3.length() < 4)
            continue; 
          Matcher matcher = cpattern.matcher(str3);
          while (matcher.find()) {
            int j = matcher.start();
            int k = matcher.end();
            if (j >= 0) {
              String str4 = str3.substring(j + 1, (k > 0) ? (k - 1) : (str3.length() - 1));
              stringBuilder.append("\t").append(str4).append("\n");
            } 
          } 
        } 
        if (str.startsWith("Cookie2=[")) {
          String str3 = str.substring(9);
          int i = str3.indexOf("Cookie=[");
          if (i > 0) {
            str3 = str3.substring(0, i - 1);
            str = str3.substring(i);
          } else {
            str = "";
          } 
          Matcher matcher = cpattern.matcher(str3);
          while (matcher.find()) {
            int j = matcher.start();
            int k = matcher.end();
            if (j >= 0) {
              String str4 = str3.substring(j + 1, (k > 0) ? (k - 1) : (str3.length() - 1));
              stringBuilder.append("\t").append(str4).append("\n");
            } 
          } 
        } 
      } 
    } else {
      stringBuilder.append(str2).append("\n");
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\logging\HttpLogFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */