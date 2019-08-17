package com.sun.java.browser.net;

import java.io.IOException;
import java.net.URL;

public class ProxyService {
  private static ProxyServiceProvider provider = null;
  
  public static void setProvider(ProxyServiceProvider paramProxyServiceProvider) throws IOException {
    if (null == provider) {
      provider = paramProxyServiceProvider;
    } else {
      throw new IOException("Proxy service provider has already been set.");
    } 
  }
  
  public static ProxyInfo[] getProxyInfo(URL paramURL) throws IOException {
    if (null == provider)
      throw new IOException("Proxy service provider is not yet set"); 
    return provider.getProxyInfo(paramURL);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\net\ProxyService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */