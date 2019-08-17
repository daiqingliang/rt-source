package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class PortInfo implements WSPortInfo {
  @NotNull
  private final WSServiceDelegate owner;
  
  @NotNull
  public final QName portName;
  
  @NotNull
  public final EndpointAddress targetEndpoint;
  
  @NotNull
  public final BindingID bindingId;
  
  @NotNull
  public final PolicyMap policyMap;
  
  @Nullable
  public final WSDLPort portModel;
  
  public PortInfo(WSServiceDelegate paramWSServiceDelegate, EndpointAddress paramEndpointAddress, QName paramQName, BindingID paramBindingID) {
    this.owner = paramWSServiceDelegate;
    this.targetEndpoint = paramEndpointAddress;
    this.portName = paramQName;
    this.bindingId = paramBindingID;
    this.portModel = getPortModel(paramWSServiceDelegate, paramQName);
    this.policyMap = createPolicyMap();
  }
  
  public PortInfo(@NotNull WSServiceDelegate paramWSServiceDelegate, @NotNull WSDLPort paramWSDLPort) {
    this.owner = paramWSServiceDelegate;
    this.targetEndpoint = paramWSDLPort.getAddress();
    this.portName = paramWSDLPort.getName();
    this.bindingId = paramWSDLPort.getBinding().getBindingId();
    this.portModel = paramWSDLPort;
    this.policyMap = createPolicyMap();
  }
  
  public PolicyMap getPolicyMap() { return this.policyMap; }
  
  public PolicyMap createPolicyMap() {
    PolicyMap policyMap1;
    if (this.portModel != null) {
      policyMap1 = this.portModel.getOwner().getParent().getPolicyMap();
    } else {
      policyMap1 = PolicyResolverFactory.create().resolve(new PolicyResolver.ClientContext(null, this.owner.getContainer()));
    } 
    if (policyMap1 == null)
      policyMap1 = PolicyMap.createPolicyMap(null); 
    return policyMap1;
  }
  
  public BindingImpl createBinding(WebServiceFeature[] paramArrayOfWebServiceFeature, Class<?> paramClass) { return createBinding(new WebServiceFeatureList(paramArrayOfWebServiceFeature), paramClass, null); }
  
  public BindingImpl createBinding(WebServiceFeatureList paramWebServiceFeatureList, Class<?> paramClass, BindingImpl paramBindingImpl) {
    Collection collection;
    if (paramBindingImpl != null)
      paramWebServiceFeatureList.addAll(paramBindingImpl.getFeatures()); 
    if (this.portModel != null) {
      collection = this.portModel.getFeatures();
    } else {
      collection = PolicyUtil.getPortScopedFeatures(this.policyMap, this.owner.getServiceName(), this.portName);
    } 
    paramWebServiceFeatureList.mergeFeatures(collection, false);
    paramWebServiceFeatureList.mergeFeatures(this.owner.serviceInterceptor.preCreateBinding(this, paramClass, paramWebServiceFeatureList), false);
    BindingImpl bindingImpl = BindingImpl.create(this.bindingId, paramWebServiceFeatureList.toArray());
    this.owner.getHandlerConfigurator().configureHandlers(this, bindingImpl);
    return bindingImpl;
  }
  
  private WSDLPort getPortModel(WSServiceDelegate paramWSServiceDelegate, QName paramQName) {
    if (paramWSServiceDelegate.getWsdlService() != null) {
      Iterable iterable = paramWSServiceDelegate.getWsdlService().getPorts();
      for (WSDLPort wSDLPort : iterable) {
        if (wSDLPort.getName().equals(paramQName))
          return wSDLPort; 
      } 
    } 
    return null;
  }
  
  @Nullable
  public WSDLPort getPort() { return this.portModel; }
  
  @NotNull
  public WSService getOwner() { return this.owner; }
  
  @NotNull
  public BindingID getBindingId() { return this.bindingId; }
  
  @NotNull
  public EndpointAddress getEndpointAddress() { return this.targetEndpoint; }
  
  public QName getServiceName() { return this.owner.getServiceName(); }
  
  public QName getPortName() { return this.portName; }
  
  public String getBindingID() { return this.bindingId.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\PortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */