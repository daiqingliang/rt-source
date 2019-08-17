package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpsExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import javax.net.ssl.SSLSession;

class HttpsExchangeImpl extends HttpsExchange {
  ExchangeImpl impl;
  
  HttpsExchangeImpl(ExchangeImpl paramExchangeImpl) throws IOException { this.impl = paramExchangeImpl; }
  
  public Headers getRequestHeaders() { return this.impl.getRequestHeaders(); }
  
  public Headers getResponseHeaders() { return this.impl.getResponseHeaders(); }
  
  public URI getRequestURI() { return this.impl.getRequestURI(); }
  
  public String getRequestMethod() { return this.impl.getRequestMethod(); }
  
  public HttpContextImpl getHttpContext() { return this.impl.getHttpContext(); }
  
  public void close() { this.impl.close(); }
  
  public InputStream getRequestBody() { return this.impl.getRequestBody(); }
  
  public int getResponseCode() { return this.impl.getResponseCode(); }
  
  public OutputStream getResponseBody() { return this.impl.getResponseBody(); }
  
  public void sendResponseHeaders(int paramInt, long paramLong) throws IOException { this.impl.sendResponseHeaders(paramInt, paramLong); }
  
  public InetSocketAddress getRemoteAddress() { return this.impl.getRemoteAddress(); }
  
  public InetSocketAddress getLocalAddress() { return this.impl.getLocalAddress(); }
  
  public String getProtocol() { return this.impl.getProtocol(); }
  
  public SSLSession getSSLSession() { return this.impl.getSSLSession(); }
  
  public Object getAttribute(String paramString) { return this.impl.getAttribute(paramString); }
  
  public void setAttribute(String paramString, Object paramObject) { this.impl.setAttribute(paramString, paramObject); }
  
  public void setStreams(InputStream paramInputStream, OutputStream paramOutputStream) { this.impl.setStreams(paramInputStream, paramOutputStream); }
  
  public HttpPrincipal getPrincipal() { return this.impl.getPrincipal(); }
  
  ExchangeImpl getExchangeImpl() { return this.impl; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\HttpsExchangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */