package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public interface EndpointAwareTube extends Tube {
  void setEndpoint(WSEndpoint<?> paramWSEndpoint);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\EndpointAwareTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */