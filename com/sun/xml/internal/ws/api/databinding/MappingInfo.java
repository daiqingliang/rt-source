package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;

public class MappingInfo {
  protected String targetNamespace;
  
  protected String databindingMode;
  
  protected SoapBodyStyle soapBodyStyle;
  
  protected BindingID bindingID;
  
  protected QName serviceName;
  
  protected QName portName;
  
  protected String defaultSchemaNamespaceSuffix;
  
  public String getTargetNamespace() { return this.targetNamespace; }
  
  public void setTargetNamespace(String paramString) { this.targetNamespace = paramString; }
  
  public String getDatabindingMode() { return this.databindingMode; }
  
  public void setDatabindingMode(String paramString) { this.databindingMode = paramString; }
  
  public SoapBodyStyle getSoapBodyStyle() { return this.soapBodyStyle; }
  
  public void setSoapBodyStyle(SoapBodyStyle paramSoapBodyStyle) { this.soapBodyStyle = paramSoapBodyStyle; }
  
  public BindingID getBindingID() { return this.bindingID; }
  
  public void setBindingID(BindingID paramBindingID) { this.bindingID = paramBindingID; }
  
  public QName getServiceName() { return this.serviceName; }
  
  public void setServiceName(QName paramQName) { this.serviceName = paramQName; }
  
  public QName getPortName() { return this.portName; }
  
  public void setPortName(QName paramQName) { this.portName = paramQName; }
  
  public String getDefaultSchemaNamespaceSuffix() { return this.defaultSchemaNamespaceSuffix; }
  
  public void setDefaultSchemaNamespaceSuffix(String paramString) { this.defaultSchemaNamespaceSuffix = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\MappingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */