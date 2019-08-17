package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

final class ServerMgr {
  private static final ServerMgr serverMgr = new ServerMgr();
  
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  
  private final Map<InetSocketAddress, ServerState> servers = new HashMap();
  
  static ServerMgr getInstance() { return serverMgr; }
  
  HttpContext createContext(String paramString) {
    try {
      ServerState serverState;
      URL uRL = new URL(paramString);
      int i = uRL.getPort();
      if (i == -1)
        i = uRL.getDefaultPort(); 
      InetSocketAddress inetSocketAddress = new InetSocketAddress(uRL.getHost(), i);
      synchronized (this.servers) {
        serverState = (ServerState)this.servers.get(inetSocketAddress);
        if (serverState == null) {
          int j = i;
          for (ServerState serverState1 : this.servers.values()) {
            if (serverState1.getServer().getAddress().getPort() == j) {
              serverState = serverState1;
              break;
            } 
          } 
          if (!inetSocketAddress.getAddress().isAnyLocalAddress() || serverState == null) {
            logger.fine("Creating new HTTP Server at " + inetSocketAddress);
            HttpServer httpServer1 = HttpServer.create(inetSocketAddress, 0);
            httpServer1.setExecutor(Executors.newCachedThreadPool());
            String str = uRL.toURI().getPath();
            logger.fine("Creating HTTP Context at = " + str);
            HttpContext httpContext1 = httpServer1.createContext(str);
            httpServer1.start();
            inetSocketAddress = httpServer1.getAddress();
            logger.fine("HTTP server started = " + inetSocketAddress);
            serverState = new ServerState(httpServer1, str);
            this.servers.put(inetSocketAddress, serverState);
            return httpContext1;
          } 
        } 
      } 
      HttpServer httpServer = serverState.getServer();
      if (serverState.getPaths().contains(uRL.getPath())) {
        String str = "Context with URL path " + uRL.getPath() + " already exists on the server " + httpServer.getAddress();
        logger.fine(str);
        throw new IllegalArgumentException(str);
      } 
      logger.fine("Creating HTTP Context at = " + uRL.getPath());
      HttpContext httpContext = httpServer.createContext(uRL.getPath());
      serverState.oneMoreContext(uRL.getPath());
      return httpContext;
    } catch (Exception exception) {
      throw new ServerRtException("server.rt.err", new Object[] { exception });
    } 
  }
  
  void removeContext(HttpContext paramHttpContext) {
    InetSocketAddress inetSocketAddress = paramHttpContext.getServer().getAddress();
    synchronized (this.servers) {
      ServerState serverState = (ServerState)this.servers.get(inetSocketAddress);
      int i = serverState.noOfContexts();
      if (i < 2) {
        ((ExecutorService)serverState.getServer().getExecutor()).shutdown();
        serverState.getServer().stop(0);
        this.servers.remove(inetSocketAddress);
      } else {
        serverState.getServer().removeContext(paramHttpContext);
        serverState.oneLessContext(paramHttpContext.getPath());
      } 
    } 
  }
  
  private static final class ServerState {
    private final HttpServer server;
    
    private int instances;
    
    private Set<String> paths = new HashSet();
    
    ServerState(HttpServer param1HttpServer, String param1String) {
      this.server = param1HttpServer;
      this.instances = 1;
      this.paths.add(param1String);
    }
    
    public HttpServer getServer() { return this.server; }
    
    public void oneMoreContext(String param1String) {
      this.instances++;
      this.paths.add(param1String);
    }
    
    public void oneLessContext(String param1String) {
      this.instances--;
      this.paths.remove(param1String);
    }
    
    public int noOfContexts() { return this.instances; }
    
    public Set<String> getPaths() { return this.paths; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */