package com.sun.xml.internal.ws.transport.http.client;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import java.util.List;
import java.util.Map;

final class HttpResponseProperties extends BasePropertySet {
  private final HttpClientTransport deferedCon;
  
  private static final BasePropertySet.PropertyMap model = parse(HttpResponseProperties.class);
  
  public HttpResponseProperties(@NotNull HttpClientTransport paramHttpClientTransport) { this.deferedCon = paramHttpClientTransport; }
  
  @Property({"javax.xml.ws.http.response.headers"})
  public Map<String, List<String>> getResponseHeaders() { return this.deferedCon.getHeaders(); }
  
  @Property({"javax.xml.ws.http.response.code"})
  public int getResponseCode() { return this.deferedCon.statusCode; }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpResponseProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */