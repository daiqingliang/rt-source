package sun.net.www.protocol.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import sun.net.www.ParseUtil;

public class Handler extends URLStreamHandler {
  private static final String separator = "!/";
  
  protected URLConnection openConnection(URL paramURL) throws IOException { return new JarURLConnection(paramURL, this); }
  
  private static int indexOfBangSlash(String paramString) {
    for (int i = paramString.length(); (i = paramString.lastIndexOf('!', i)) != -1; i--) {
      if (i != paramString.length() - 1 && paramString.charAt(i + 1) == '/')
        return i + 1; 
    } 
    return -1;
  }
  
  protected boolean sameFile(URL paramURL1, URL paramURL2) {
    if (!paramURL1.getProtocol().equals("jar") || !paramURL2.getProtocol().equals("jar"))
      return false; 
    String str1 = paramURL1.getFile();
    String str2 = paramURL2.getFile();
    int i = str1.indexOf("!/");
    int j = str2.indexOf("!/");
    if (i == -1 || j == -1)
      return super.sameFile(paramURL1, paramURL2); 
    String str3 = str1.substring(i + 2);
    String str4 = str2.substring(j + 2);
    if (!str3.equals(str4))
      return false; 
    URL uRL1 = null;
    URL uRL2 = null;
    try {
      uRL1 = new URL(str1.substring(0, i));
      uRL2 = new URL(str2.substring(0, j));
    } catch (MalformedURLException malformedURLException) {
      return super.sameFile(paramURL1, paramURL2);
    } 
    return !!super.sameFile(uRL1, uRL2);
  }
  
  protected int hashCode(URL paramURL) {
    null = 0;
    String str1 = paramURL.getProtocol();
    if (str1 != null)
      null += str1.hashCode(); 
    String str2 = paramURL.getFile();
    int i = str2.indexOf("!/");
    if (i == -1)
      return null + str2.hashCode(); 
    URL uRL = null;
    String str3 = str2.substring(0, i);
    try {
      uRL = new URL(str3);
      null += uRL.hashCode();
    } catch (MalformedURLException malformedURLException) {
      null += str3.hashCode();
    } 
    String str4 = str2.substring(i + 2);
    return str4.hashCode();
  }
  
  protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2) {
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf('#', paramInt2);
    boolean bool = (i == paramInt1) ? 1 : 0;
    if (i > -1) {
      str2 = paramString.substring(i + 1, paramString.length());
      if (bool)
        str1 = paramURL.getFile(); 
    } 
    boolean bool1 = false;
    if (paramString.length() >= 4)
      bool1 = paramString.substring(0, 4).equalsIgnoreCase("jar:"); 
    paramString = paramString.substring(paramInt1, paramInt2);
    if (bool1) {
      str1 = parseAbsoluteSpec(paramString);
    } else if (!bool) {
      str1 = parseContextSpec(paramURL, paramString);
      int j = indexOfBangSlash(str1);
      String str3 = str1.substring(0, j);
      String str4 = str1.substring(j);
      ParseUtil parseUtil = new ParseUtil();
      str4 = parseUtil.canonizeString(str4);
      str1 = str3 + str4;
    } 
    setURL(paramURL, "jar", "", -1, str1, str2);
  }
  
  private String parseAbsoluteSpec(String paramString) {
    URL uRL = null;
    int i = -1;
    if ((i = indexOfBangSlash(paramString)) == -1)
      throw new NullPointerException("no !/ in spec"); 
    try {
      String str = paramString.substring(0, i - 1);
      uRL = new URL(str);
    } catch (MalformedURLException malformedURLException) {
      throw new NullPointerException("invalid url: " + paramString + " (" + malformedURLException + ")");
    } 
    return paramString;
  }
  
  private String parseContextSpec(URL paramURL, String paramString) {
    String str = paramURL.getFile();
    if (paramString.startsWith("/")) {
      int i = indexOfBangSlash(str);
      if (i == -1)
        throw new NullPointerException("malformed context url:" + paramURL + ": no !/"); 
      str = str.substring(0, i);
    } 
    if (!str.endsWith("/") && !paramString.startsWith("/")) {
      int i = str.lastIndexOf('/');
      if (i == -1)
        throw new NullPointerException("malformed context url:" + paramURL); 
      str = str.substring(0, i + 1);
    } 
    return str + paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\jar\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */