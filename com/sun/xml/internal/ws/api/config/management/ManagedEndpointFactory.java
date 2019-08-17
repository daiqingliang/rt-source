package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

public interface ManagedEndpointFactory {
  <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> paramWSEndpoint, EndpointCreationAttributes paramEndpointCreationAttributes);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\config\management\ManagedEndpointFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */