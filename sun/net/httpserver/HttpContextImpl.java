package sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class HttpContextImpl extends HttpContext {
  private String path;
  
  private String protocol;
  
  private HttpHandler handler;
  
  private Map<String, Object> attributes = new HashMap();
  
  private ServerImpl server;
  
  private LinkedList<Filter> sfilters = new LinkedList();
  
  private LinkedList<Filter> ufilters = new LinkedList();
  
  private Authenticator authenticator;
  
  private AuthFilter authfilter;
  
  HttpContextImpl(String paramString1, String paramString2, HttpHandler paramHttpHandler, ServerImpl paramServerImpl) {
    if (paramString2 == null || paramString1 == null || paramString2.length() < 1 || paramString2.charAt(0) != '/')
      throw new IllegalArgumentException("Illegal value for path or protocol"); 
    this.protocol = paramString1.toLowerCase();
    this.path = paramString2;
    if (!this.protocol.equals("http") && !this.protocol.equals("https"))
      throw new IllegalArgumentException("Illegal value for protocol"); 
    this.handler = paramHttpHandler;
    this.server = paramServerImpl;
    this.authfilter = new AuthFilter(null);
    this.sfilters.add(this.authfilter);
  }
  
  public HttpHandler getHandler() { return this.handler; }
  
  public void setHandler(HttpHandler paramHttpHandler) {
    if (paramHttpHandler == null)
      throw new NullPointerException("Null handler parameter"); 
    if (this.handler != null)
      throw new IllegalArgumentException("handler already set"); 
    this.handler = paramHttpHandler;
  }
  
  public String getPath() { return this.path; }
  
  public HttpServer getServer() { return this.server.getWrapper(); }
  
  ServerImpl getServerImpl() { return this.server; }
  
  public String getProtocol() { return this.protocol; }
  
  public Map<String, Object> getAttributes() { return this.attributes; }
  
  public List<Filter> getFilters() { return this.ufilters; }
  
  List<Filter> getSystemFilters() { return this.sfilters; }
  
  public Authenticator setAuthenticator(Authenticator paramAuthenticator) {
    Authenticator authenticator1 = this.authenticator;
    this.authenticator = paramAuthenticator;
    this.authfilter.setAuthenticator(paramAuthenticator);
    return authenticator1;
  }
  
  public Authenticator getAuthenticator() { return this.authenticator; }
  
  Logger getLogger() { return this.server.getLogger(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\HttpContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */