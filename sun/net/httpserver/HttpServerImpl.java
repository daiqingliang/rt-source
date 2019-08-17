package sun.net.httpserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class HttpServerImpl extends HttpServer {
  ServerImpl server;
  
  HttpServerImpl() throws IOException { this(new InetSocketAddress(80), 0); }
  
  HttpServerImpl(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException { this.server = new ServerImpl(this, "http", paramInetSocketAddress, paramInt); }
  
  public void bind(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException { this.server.bind(paramInetSocketAddress, paramInt); }
  
  public void start() throws IOException { this.server.start(); }
  
  public void setExecutor(Executor paramExecutor) { this.server.setExecutor(paramExecutor); }
  
  public Executor getExecutor() { return this.server.getExecutor(); }
  
  public void stop(int paramInt) { this.server.stop(paramInt); }
  
  public HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler) { return this.server.createContext(paramString, paramHttpHandler); }
  
  public HttpContextImpl createContext(String paramString) { return this.server.createContext(paramString); }
  
  public void removeContext(String paramString) throws IllegalArgumentException { this.server.removeContext(paramString); }
  
  public void removeContext(HttpContext paramHttpContext) throws IllegalArgumentException { this.server.removeContext(paramHttpContext); }
  
  public InetSocketAddress getAddress() { return this.server.getAddress(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\HttpServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */