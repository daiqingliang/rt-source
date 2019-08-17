package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import javax.xml.ws.WebServiceFeature;

public final class SEIPortInfo extends PortInfo {
  public final Class sei;
  
  public final SOAPSEIModel model;
  
  public SEIPortInfo(WSServiceDelegate paramWSServiceDelegate, Class paramClass, SOAPSEIModel paramSOAPSEIModel, @NotNull WSDLPort paramWSDLPort) {
    super(paramWSServiceDelegate, paramWSDLPort);
    this.sei = paramClass;
    this.model = paramSOAPSEIModel;
    assert paramClass != null && paramSOAPSEIModel != null;
  }
  
  public BindingImpl createBinding(WebServiceFeature[] paramArrayOfWebServiceFeature, Class<?> paramClass) {
    BindingImpl bindingImpl = super.createBinding(paramArrayOfWebServiceFeature, paramClass);
    return setKnownHeaders(bindingImpl);
  }
  
  public BindingImpl createBinding(WebServiceFeatureList paramWebServiceFeatureList, Class<?> paramClass) {
    BindingImpl bindingImpl = createBinding(paramWebServiceFeatureList, paramClass, null);
    return setKnownHeaders(bindingImpl);
  }
  
  private BindingImpl setKnownHeaders(BindingImpl paramBindingImpl) {
    if (paramBindingImpl instanceof SOAPBindingImpl)
      ((SOAPBindingImpl)paramBindingImpl).setPortKnownHeaders(this.model.getKnownHeaders()); 
    return paramBindingImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\SEIPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */