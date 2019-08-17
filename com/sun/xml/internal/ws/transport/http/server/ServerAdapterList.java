package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;

public class ServerAdapterList extends HttpAdapterList<ServerAdapter> {
  protected ServerAdapter createHttpAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint) { return new ServerAdapter(paramString1, paramString2, paramWSEndpoint, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerAdapterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */