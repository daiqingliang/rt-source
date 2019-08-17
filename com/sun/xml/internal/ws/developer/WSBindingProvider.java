package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Header;
import java.io.Closeable;
import java.util.List;
import javax.xml.ws.BindingProvider;

public interface WSBindingProvider extends BindingProvider, Closeable, ComponentRegistry {
  void setOutboundHeaders(List<Header> paramList);
  
  void setOutboundHeaders(Header... paramVarArgs);
  
  void setOutboundHeaders(Object... paramVarArgs);
  
  List<Header> getInboundHeaders();
  
  void setAddress(String paramString);
  
  WSEndpointReference getWSEndpointReference();
  
  WSPortInfo getPortInfo();
  
  @NotNull
  ManagedObjectManager getManagedObjectManager();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\WSBindingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */