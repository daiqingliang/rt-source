package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.PortInfo;

public class PortInfoImpl implements PortInfo {
  private BindingID bindingId;
  
  private QName portName;
  
  private QName serviceName;
  
  public PortInfoImpl(BindingID paramBindingID, QName paramQName1, QName paramQName2) {
    if (paramBindingID == null)
      throw new RuntimeException("bindingId cannot be null"); 
    if (paramQName1 == null)
      throw new RuntimeException("portName cannot be null"); 
    if (paramQName2 == null)
      throw new RuntimeException("serviceName cannot be null"); 
    this.bindingId = paramBindingID;
    this.portName = paramQName1;
    this.serviceName = paramQName2;
  }
  
  public String getBindingID() { return this.bindingId.toString(); }
  
  public QName getPortName() { return this.portName; }
  
  public QName getServiceName() { return this.serviceName; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof PortInfo) {
      PortInfo portInfo = (PortInfo)paramObject;
      if (this.bindingId.toString().equals(portInfo.getBindingID()) && this.portName.equals(portInfo.getPortName()) && this.serviceName.equals(portInfo.getServiceName()))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() { return this.bindingId.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\PortInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */