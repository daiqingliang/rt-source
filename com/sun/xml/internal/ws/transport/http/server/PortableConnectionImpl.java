package com.sun.xml.internal.ws.transport.http.server;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.http.HttpExchange;

final class PortableConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate {
  private final HttpExchange httpExchange;
  
  private int status;
  
  private final HttpAdapter adapter;
  
  private boolean outputWritten;
  
  private static final BasePropertySet.PropertyMap model = parse(PortableConnectionImpl.class);
  
  public PortableConnectionImpl(@NotNull HttpAdapter paramHttpAdapter, @NotNull HttpExchange paramHttpExchange) {
    this.adapter = paramHttpAdapter;
    this.httpExchange = paramHttpExchange;
  }
  
  @Property({"javax.xml.ws.http.request.headers", "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers"})
  @NotNull
  public Map<String, List<String>> getRequestHeaders() { return this.httpExchange.getRequestHeaders(); }
  
  public String getRequestHeader(String paramString) { return this.httpExchange.getRequestHeader(paramString); }
  
  public void setResponseHeaders(Map<String, List<String>> paramMap) {
    Map map = this.httpExchange.getResponseHeaders();
    map.clear();
    for (Map.Entry entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      List list = (List)entry.getValue();
      if (!str.equalsIgnoreCase("Content-Length") && !str.equalsIgnoreCase("Content-Type"))
        map.put(str, new ArrayList(list)); 
    } 
  }
  
  public void setResponseHeader(String paramString, List<String> paramList) { this.httpExchange.getResponseHeaders().put(paramString, paramList); }
  
  public Set<String> getRequestHeaderNames() { return this.httpExchange.getRequestHeaders().keySet(); }
  
  public List<String> getRequestHeaderValues(String paramString) { return (List)this.httpExchange.getRequestHeaders().get(paramString); }
  
  @Property({"javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers"})
  public Map<String, List<String>> getResponseHeaders() { return this.httpExchange.getResponseHeaders(); }
  
  public void setContentTypeResponseHeader(@NotNull String paramString) { this.httpExchange.addResponseHeader("Content-Type", paramString); }
  
  public void setStatus(int paramInt) { this.status = paramInt; }
  
  @Property({"javax.xml.ws.http.response.code"})
  public int getStatus() { return this.status; }
  
  @NotNull
  public InputStream getInput() throws IOException { return this.httpExchange.getRequestBody(); }
  
  @NotNull
  public OutputStream getOutput() throws IOException {
    assert !this.outputWritten;
    this.outputWritten = true;
    this.httpExchange.setStatus(getStatus());
    return this.httpExchange.getResponseBody();
  }
  
  @NotNull
  public WebServiceContextDelegate getWebServiceContextDelegate() { return this; }
  
  public Principal getUserPrincipal(Packet paramPacket) { return this.httpExchange.getUserPrincipal(); }
  
  public boolean isUserInRole(Packet paramPacket, String paramString) { return this.httpExchange.isUserInRole(paramString); }
  
  @NotNull
  public String getEPRAddress(Packet paramPacket, WSEndpoint paramWSEndpoint) {
    PortAddressResolver portAddressResolver = this.adapter.owner.createPortAddressResolver(getBaseAddress(), paramWSEndpoint.getImplementationClass());
    String str = portAddressResolver.getAddressFor(paramWSEndpoint.getServiceName(), paramWSEndpoint.getPortName().getLocalPart());
    if (str == null)
      throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(paramWSEndpoint.getPortName())); 
    return str;
  }
  
  @Property({"javax.xml.ws.servlet.context"})
  public Object getServletContext() { return this.httpExchange.getAttribute("javax.xml.ws.servlet.context"); }
  
  @Property({"javax.xml.ws.servlet.response"})
  public Object getServletResponse() { return this.httpExchange.getAttribute("javax.xml.ws.servlet.response"); }
  
  @Property({"javax.xml.ws.servlet.request"})
  public Object getServletRequest() { return this.httpExchange.getAttribute("javax.xml.ws.servlet.request"); }
  
  public String getWSDLAddress(@NotNull Packet paramPacket, @NotNull WSEndpoint paramWSEndpoint) {
    String str = getEPRAddress(paramPacket, paramWSEndpoint);
    return (this.adapter.getEndpoint().getPort() != null) ? (str + "?wsdl") : null;
  }
  
  public boolean isSecure() { return this.httpExchange.getScheme().equals("https"); }
  
  @Property({"javax.xml.ws.http.request.method"})
  @NotNull
  public String getRequestMethod() { return this.httpExchange.getRequestMethod(); }
  
  @Property({"javax.xml.ws.http.request.querystring"})
  public String getQueryString() { return this.httpExchange.getQueryString(); }
  
  @Property({"javax.xml.ws.http.request.pathinfo"})
  public String getPathInfo() { return this.httpExchange.getPathInfo(); }
  
  @Property({"com.sun.xml.internal.ws.http.exchange"})
  public HttpExchange getExchange() { return this.httpExchange; }
  
  @NotNull
  public String getBaseAddress() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.httpExchange.getScheme());
    stringBuilder.append("://");
    stringBuilder.append(this.httpExchange.getLocalAddress().getHostName());
    stringBuilder.append(":");
    stringBuilder.append(this.httpExchange.getLocalAddress().getPort());
    stringBuilder.append(this.httpExchange.getContextPath());
    return stringBuilder.toString();
  }
  
  public String getProtocol() { return this.httpExchange.getProtocol(); }
  
  public void setContentLengthResponseHeader(int paramInt) { this.httpExchange.addResponseHeader("Content-Length", "" + paramInt); }
  
  public String getRequestURI() { return this.httpExchange.getRequestURI().toString(); }
  
  public String getRequestScheme() { return this.httpExchange.getScheme(); }
  
  public String getServerName() { return this.httpExchange.getLocalAddress().getHostName(); }
  
  public int getServerPort() { return this.httpExchange.getLocalAddress().getPort(); }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\PortableConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */