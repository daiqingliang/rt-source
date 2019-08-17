package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.namespace.QName;

public final class WSDLDirectProperties extends WSDLProperties {
  private final QName serviceName;
  
  private final QName portName;
  
  public WSDLDirectProperties(QName paramQName1, QName paramQName2) { this(paramQName1, paramQName2, null); }
  
  public WSDLDirectProperties(QName paramQName1, QName paramQName2, SEIModel paramSEIModel) {
    super(paramSEIModel);
    this.serviceName = paramQName1;
    this.portName = paramQName2;
  }
  
  public QName getWSDLService() { return this.serviceName; }
  
  public QName getWSDLPort() { return this.portName; }
  
  public QName getWSDLPortType() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLDirectProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */