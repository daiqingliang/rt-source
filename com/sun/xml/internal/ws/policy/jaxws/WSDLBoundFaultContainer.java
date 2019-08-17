package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import org.xml.sax.Locator;

class WSDLBoundFaultContainer implements WSDLObject {
  private final WSDLBoundFault boundFault;
  
  private final WSDLBoundOperation boundOperation;
  
  public WSDLBoundFaultContainer(WSDLBoundFault paramWSDLBoundFault, WSDLBoundOperation paramWSDLBoundOperation) {
    this.boundFault = paramWSDLBoundFault;
    this.boundOperation = paramWSDLBoundOperation;
  }
  
  public Locator getLocation() { return null; }
  
  public WSDLBoundFault getBoundFault() { return this.boundFault; }
  
  public WSDLBoundOperation getBoundOperation() { return this.boundOperation; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\WSDLBoundFaultContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */