package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class WSHTTPConnection extends BasePropertySet {
  public static final int OK = 200;
  
  public static final int ONEWAY = 202;
  
  public static final int UNSUPPORTED_MEDIA = 415;
  
  public static final int MALFORMED_XML = 400;
  
  public static final int INTERNAL_ERR = 500;
  
  public abstract void setResponseHeaders(@NotNull Map<String, List<String>> paramMap);
  
  public void setResponseHeader(String paramString1, String paramString2) { setResponseHeader(paramString1, Collections.singletonList(paramString2)); }
  
  public abstract void setResponseHeader(String paramString, List<String> paramList);
  
  public abstract void setContentTypeResponseHeader(@NotNull String paramString);
  
  public abstract void setStatus(int paramInt);
  
  public abstract int getStatus();
  
  @NotNull
  public abstract InputStream getInput() throws IOException;
  
  @NotNull
  public abstract OutputStream getOutput() throws IOException;
  
  @NotNull
  public abstract WebServiceContextDelegate getWebServiceContextDelegate();
  
  @NotNull
  public abstract String getRequestMethod();
  
  @NotNull
  public abstract Map<String, List<String>> getRequestHeaders();
  
  @NotNull
  public abstract Set<String> getRequestHeaderNames();
  
  public abstract Map<String, List<String>> getResponseHeaders();
  
  @Nullable
  public abstract String getRequestHeader(@NotNull String paramString);
  
  @Nullable
  public abstract List<String> getRequestHeaderValues(@NotNull String paramString);
  
  @Nullable
  public abstract String getQueryString();
  
  @Nullable
  public abstract String getPathInfo();
  
  @NotNull
  public abstract String getRequestURI();
  
  @NotNull
  public abstract String getRequestScheme();
  
  @NotNull
  public abstract String getServerName();
  
  public abstract int getServerPort();
  
  @NotNull
  public String getContextPath() { return ""; }
  
  public Object getContext() { return null; }
  
  @NotNull
  public String getBaseAddress() { throw new UnsupportedOperationException(); }
  
  public abstract boolean isSecure();
  
  public Principal getUserPrincipal() { return null; }
  
  public boolean isUserInRole(String paramString) { return false; }
  
  public Object getRequestAttribute(String paramString) { return null; }
  
  public void close() { this.closed = true; }
  
  public boolean isClosed() { return this.closed; }
  
  public String getProtocol() { return "HTTP/1.1"; }
  
  public String getCookie(String paramString) { return null; }
  
  public void setCookie(String paramString1, String paramString2) {}
  
  public void setContentLengthResponseHeader(int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\WSHTTPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */