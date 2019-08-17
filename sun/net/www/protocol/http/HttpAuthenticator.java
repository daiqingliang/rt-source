package sun.net.www.protocol.http;

import java.net.URL;

@Deprecated
public interface HttpAuthenticator {
  boolean schemeSupported(String paramString);
  
  String authString(URL paramURL, String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\HttpAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */