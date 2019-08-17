package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;

public interface WSDLPort extends WSDLFeaturedObject, WSDLExtensible {
  QName getName();
  
  @NotNull
  WSDLBoundPortType getBinding();
  
  EndpointAddress getAddress();
  
  @NotNull
  WSDLService getOwner();
  
  @Nullable
  WSEndpointReference getEPR();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */