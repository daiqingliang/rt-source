package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import sun.net.www.protocol.https.Handler;

public class Handler extends Handler {
  public Handler() {}
  
  public Handler(String paramString, int paramInt) { super(paramString, paramInt); }
  
  protected URLConnection openConnection(URL paramURL) throws IOException { return openConnection(paramURL, (Proxy)null); }
  
  protected URLConnection openConnection(URL paramURL, Proxy paramProxy) throws IOException { return new HttpsURLConnectionOldImpl(paramURL, paramProxy, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */