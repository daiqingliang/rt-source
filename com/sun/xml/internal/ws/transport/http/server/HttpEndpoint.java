package com.sun.xml.internal.ws.transport.http.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.xml.internal.ws.api.server.HttpEndpoint;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.spi.http.HttpContext;
import org.w3c.dom.Element;

public final class HttpEndpoint extends HttpEndpoint {
  private String address;
  
  private HttpContext httpContext;
  
  private final HttpAdapter adapter;
  
  private final Executor executor;
  
  public HttpEndpoint(Executor paramExecutor, HttpAdapter paramHttpAdapter) {
    this.executor = paramExecutor;
    this.adapter = paramHttpAdapter;
  }
  
  public void publish(String paramString) {
    this.address = paramString;
    this.httpContext = ServerMgr.getInstance().createContext(paramString);
    publish(this.httpContext);
  }
  
  public void publish(Object paramObject) {
    if (paramObject instanceof HttpContext) {
      setHandler((HttpContext)paramObject);
      return;
    } 
    if (paramObject instanceof HttpContext) {
      this.httpContext = (HttpContext)paramObject;
      setHandler(this.httpContext);
      return;
    } 
    throw new ServerRtException(ServerMessages.NOT_KNOW_HTTP_CONTEXT_TYPE(paramObject.getClass(), HttpContext.class, HttpContext.class), new Object[0]);
  }
  
  HttpAdapterList getAdapterOwner() { return this.adapter.owner; }
  
  private String getEPRAddress() {
    if (this.address == null)
      return this.httpContext.getServer().getAddress().toString(); 
    try {
      URL uRL = new URL(this.address);
      if (uRL.getPort() == 0)
        return (new URL(uRL.getProtocol(), uRL.getHost(), this.httpContext.getServer().getAddress().getPort(), uRL.getFile())).toString(); 
    } catch (MalformedURLException malformedURLException) {}
    return this.address;
  }
  
  public void stop() {
    if (this.httpContext != null)
      if (this.address == null) {
        this.httpContext.getServer().removeContext(this.httpContext);
      } else {
        ServerMgr.getInstance().removeContext(this.httpContext);
      }  
    this.adapter.getEndpoint().dispose();
  }
  
  private void setHandler(HttpContext paramHttpContext) { paramHttpContext.setHandler(new WSHttpHandler(this.adapter, this.executor)); }
  
  private void setHandler(HttpContext paramHttpContext) { paramHttpContext.setHandler(new PortableHttpHandler(this.adapter, this.executor)); }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs) {
    String str = getEPRAddress();
    return (T)(EndpointReference)paramClass.cast(this.adapter.getEndpoint().getEndpointReference(paramClass, str, str + "?wsdl", paramVarArgs));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\HttpEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */