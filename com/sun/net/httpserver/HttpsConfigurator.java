package com.sun.net.httpserver;

import javax.net.ssl.SSLContext;
import jdk.Exported;

@Exported
public class HttpsConfigurator {
  private SSLContext context;
  
  public HttpsConfigurator(SSLContext paramSSLContext) {
    if (paramSSLContext == null)
      throw new NullPointerException("null SSLContext"); 
    this.context = paramSSLContext;
  }
  
  public SSLContext getSSLContext() { return this.context; }
  
  public void configure(HttpsParameters paramHttpsParameters) { paramHttpsParameters.setSSLParameters(getSSLContext().getDefaultSSLParameters()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\HttpsConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */