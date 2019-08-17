package sun.net.www.protocol.mailto;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
  public URLConnection openConnection(URL paramURL) { return new MailToURLConnection(paramURL); }
  
  public void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2) {
    String str1 = paramURL.getProtocol();
    String str2 = "";
    int i = paramURL.getPort();
    String str3 = "";
    if (paramInt1 < paramInt2)
      str3 = paramString.substring(paramInt1, paramInt2); 
    boolean bool = false;
    if (str3 == null || str3.equals("")) {
      bool = true;
    } else {
      boolean bool1 = true;
      for (byte b = 0; b < str3.length(); b++) {
        if (!Character.isWhitespace(str3.charAt(b)))
          bool1 = false; 
      } 
      if (bool1)
        bool = true; 
    } 
    if (bool)
      throw new RuntimeException("No email address"); 
    setURLHandler(paramURL, str1, str2, i, str3, null);
  }
  
  private void setURLHandler(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4) { setURL(paramURL, paramString1, paramString2, paramInt, paramString3, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\mailto\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */