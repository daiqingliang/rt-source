package com.sun.net.httpserver;

import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.Exported;

@Exported
public abstract class HttpsServer extends HttpServer {
  public static HttpsServer create() throws IOException { return create(null, 0); }
  
  public static HttpsServer create(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
    HttpServerProvider httpServerProvider = HttpServerProvider.provider();
    return httpServerProvider.createHttpsServer(paramInetSocketAddress, paramInt);
  }
  
  public abstract void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator);
  
  public abstract HttpsConfigurator getHttpsConfigurator();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\HttpsServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */