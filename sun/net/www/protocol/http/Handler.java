package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
  protected String proxy = null;
  
  protected int proxyPort = -1;
  
  protected int getDefaultPort() { return 80; }
  
  public Handler() {}
  
  public Handler(String paramString, int paramInt) {}
  
  protected URLConnection openConnection(URL paramURL) throws IOException { return openConnection(paramURL, (Proxy)null); }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException { return new HttpURLConnection(paramURL, paramProxy, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */