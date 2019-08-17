package sun.net.www.protocol.file;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import sun.net.www.ParseUtil;

public class Handler extends URLStreamHandler {
  private String getHost(URL paramURL) {
    String str = paramURL.getHost();
    if (str == null)
      str = ""; 
    return str;
  }
  
  protected void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2) { super.parseURL(paramURL, paramString.replace(File.separatorChar, '/'), paramInt1, paramInt2); }
  
  public URLConnection openConnection(URL paramURL) throws IOException { return openConnection(paramURL, null); }
  
  public URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException {
    URLConnection uRLConnection;
    String str2 = paramURL.getFile();
    String str3 = paramURL.getHost();
    String str1 = ParseUtil.decode(str2);
    str1 = str1.replace('/', '\\');
    str1 = str1.replace('|', ':');
    if (str3 == null || str3.equals("") || str3.equalsIgnoreCase("localhost") || str3.equals("~"))
      return createFileURLConnection(paramURL, new File(str1)); 
    str1 = "\\\\" + str3 + str1;
    File file = new File(str1);
    if (file.exists())
      return createFileURLConnection(paramURL, file); 
    try {
      URL uRL = new URL("ftp", str3, str2 + ((paramURL.getRef() == null) ? "" : ("#" + paramURL.getRef())));
      if (paramProxy != null) {
        uRLConnection = uRL.openConnection(paramProxy);
      } else {
        uRLConnection = uRL.openConnection();
      } 
    } catch (IOException iOException) {
      uRLConnection = null;
    } 
    if (uRLConnection == null)
      throw new IOException("Unable to connect to: " + paramURL.toExternalForm()); 
    return uRLConnection;
  }
  
  protected URLConnection createFileURLConnection(URL paramURL, File paramFile) { return new FileURLConnection(paramURL, paramFile); }
  
  protected boolean hostsEqual(URL paramURL1, URL paramURL2) {
    String str1 = paramURL1.getHost();
    String str2 = paramURL2.getHost();
    return ("localhost".equalsIgnoreCase(str1) && (str2 == null || "".equals(str2))) ? true : (("localhost".equalsIgnoreCase(str2) && (str1 == null || "".equals(str1))) ? true : super.hostsEqual(paramURL1, paramURL2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\file\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */