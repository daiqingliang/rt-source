package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import sun.net.www.protocol.http.Handler;

public class Handler extends Handler {
  protected String proxy = null;
  
  protected int proxyPort = -1;
  
  protected int getDefaultPort() { return 443; }
  
  public Handler() {}
  
  public Handler(String paramString, int paramInt) {}
  
  protected URLConnection openConnection(URL paramURL) throws IOException { return openConnection(paramURL, (Proxy)null); }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException { return new HttpsURLConnectionImpl(paramURL, paramProxy, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\https\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */