package com.sun.xml.internal.ws.assembler;

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
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.policy.PolicyMap;

class DefaultClientTubelineAssemblyContext extends TubelineAssemblyContextImpl implements ClientTubelineAssemblyContext {
  @NotNull
  private final ClientTubeAssemblerContext wrappedContext;
  
  private final PolicyMap policyMap;
  
  private final WSPortInfo portInfo;
  
  private final WSDLPort wsdlPort;
  
  public DefaultClientTubelineAssemblyContext(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    this.wrappedContext = paramClientTubeAssemblerContext;
    this.wsdlPort = paramClientTubeAssemblerContext.getWsdlModel();
    this.portInfo = paramClientTubeAssemblerContext.getPortInfo();
    this.policyMap = paramClientTubeAssemblerContext.getPortInfo().getPolicyMap();
  }
  
  public PolicyMap getPolicyMap() { return this.policyMap; }
  
  public boolean isPolicyAvailable() { return (this.policyMap != null && !this.policyMap.isEmpty()); }
  
  public WSDLPort getWsdlPort() { return this.wsdlPort; }
  
  public WSPortInfo getPortInfo() { return this.portInfo; }
  
  @NotNull
  public EndpointAddress getAddress() { return this.wrappedContext.getAddress(); }
  
  @NotNull
  public WSService getService() { return this.wrappedContext.getService(); }
  
  @NotNull
  public WSBinding getBinding() { return this.wrappedContext.getBinding(); }
  
  @Nullable
  public SEIModel getSEIModel() { return this.wrappedContext.getSEIModel(); }
  
  public Container getContainer() { return this.wrappedContext.getContainer(); }
  
  @NotNull
  public Codec getCodec() { return this.wrappedContext.getCodec(); }
  
  public void setCodec(@NotNull Codec paramCodec) { this.wrappedContext.setCodec(paramCodec); }
  
  public ClientTubeAssemblerContext getWrappedContext() { return this.wrappedContext; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\DefaultClientTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */