package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.server.HttpEndpoint;

public abstract class HttpEndpoint {
  public static HttpEndpoint create(@NotNull WSEndpoint paramWSEndpoint) { return new HttpEndpoint(null, HttpAdapter.createAlone(paramWSEndpoint)); }
  
  public abstract void publish(@NotNull String paramString);
  
  public abstract void stop();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\HttpEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */