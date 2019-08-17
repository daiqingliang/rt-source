package sun.net.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;

public class DefaultHttpServerProvider extends HttpServerProvider {
  public HttpServer createHttpServer(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException { return new HttpServerImpl(paramInetSocketAddress, paramInt); }
  
  public HttpsServer createHttpsServer(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException { return new HttpsServerImpl(paramInetSocketAddress, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\DefaultHttpServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */