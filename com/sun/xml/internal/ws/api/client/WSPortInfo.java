package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.policy.PolicyMap;
import javax.xml.ws.handler.PortInfo;

public interface WSPortInfo extends PortInfo {
  @NotNull
  WSService getOwner();
  
  @NotNull
  BindingID getBindingId();
  
  @NotNull
  EndpointAddress getEndpointAddress();
  
  @Nullable
  WSDLPort getPort();
  
  PolicyMap getPolicyMap();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\client\WSPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */