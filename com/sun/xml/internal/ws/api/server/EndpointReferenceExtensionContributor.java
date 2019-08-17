package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;

public abstract class EndpointReferenceExtensionContributor {
  public abstract WSEndpointReference.EPRExtension getEPRExtension(WSEndpoint paramWSEndpoint, @Nullable WSEndpointReference.EPRExtension paramEPRExtension);
  
  public abstract QName getQName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\EndpointReferenceExtensionContributor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */