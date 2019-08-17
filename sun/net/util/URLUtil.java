package sun.net.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLPermission;
import java.security.Permission;

public class URLUtil {
  public static String urlNoFragString(URL paramURL) {
    StringBuilder stringBuilder = new StringBuilder();
    String str1 = paramURL.getProtocol();
    if (str1 != null) {
      str1 = str1.toLowerCase();
      stringBuilder.append(str1);
      stringBuilder.append("://");
    } 
    String str2 = paramURL.getHost();
    if (str2 != null) {
      str2 = str2.toLowerCase();
      stringBuilder.append(str2);
      int i = paramURL.getPort();
      if (i == -1)
        i = paramURL.getDefaultPort(); 
      if (i != -1)
        stringBuilder.append(":").append(i); 
    } 
    String str3 = paramURL.getFile();
    if (str3 != null)
      stringBuilder.append(str3); 
    return stringBuilder.toString();
  }
  
  public static Permission getConnectPermission(URL paramURL) throws IOException {
    String str = paramURL.toString().toLowerCase();
    if (str.startsWith("http:") || str.startsWith("https:"))
      return getURLConnectPermission(paramURL); 
    if (str.startsWith("jar:http:") || str.startsWith("jar:https:")) {
      String str1 = paramURL.toString();
      int i = str1.indexOf("!/");
      str1 = str1.substring(4, (i > -1) ? i : str1.length());
      URL uRL = new URL(str1);
      return getURLConnectPermission(uRL);
    } 
    return paramURL.openConnection().getPermission();
  }
  
  private static Permission getURLConnectPermission(URL paramURL) throws IOException {
    String str = paramURL.getProtocol() + "://" + paramURL.getAuthority() + paramURL.getPath();
    return new URLPermission(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\ne\\util\URLUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */