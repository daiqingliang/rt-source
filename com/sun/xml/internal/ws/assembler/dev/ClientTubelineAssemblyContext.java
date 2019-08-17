package com.sun.xml.internal.ws.assembler.dev;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;

public interface ClientTubelineAssemblyContext extends TubelineAssemblyContext {
  @NotNull
  EndpointAddress getAddress();
  
  @NotNull
  WSBinding getBinding();
  
  @NotNull
  Codec getCodec();
  
  Container getContainer();
  
  PolicyMap getPolicyMap();
  
  WSPortInfo getPortInfo();
  
  @Nullable
  SEIModel getSEIModel();
  
  @NotNull
  WSService getService();
  
  ClientTubeAssemblerContext getWrappedContext();
  
  WSDLPort getWsdlPort();
  
  boolean isPolicyAvailable();
  
  void setCodec(@NotNull Codec paramCodec);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\dev\ClientTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */