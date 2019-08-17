package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.namespace.QName;

public final class WSDLPortProperties extends WSDLProperties {
  @NotNull
  private final WSDLPort port;
  
  public WSDLPortProperties(@NotNull WSDLPort paramWSDLPort) { this(paramWSDLPort, null); }
  
  public WSDLPortProperties(@NotNull WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel) {
    super(paramSEIModel);
    this.port = paramWSDLPort;
  }
  
  public QName getWSDLService() { return this.port.getOwner().getName(); }
  
  public QName getWSDLPort() { return this.port.getName(); }
  
  public QName getWSDLPortType() { return this.port.getBinding().getPortTypeName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */