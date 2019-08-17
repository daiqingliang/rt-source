package com.sun.xml.internal.ws.model.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding;

public class SOAPBindingImpl extends SOAPBinding {
  public SOAPBindingImpl() {}
  
  public SOAPBindingImpl(SOAPBinding paramSOAPBinding) {
    this.use = paramSOAPBinding.getUse();
    this.style = paramSOAPBinding.getStyle();
    this.soapVersion = paramSOAPBinding.getSOAPVersion();
    this.soapAction = paramSOAPBinding.getSOAPAction();
  }
  
  public void setStyle(SOAPBinding.Style paramStyle) { this.style = paramStyle; }
  
  public void setSOAPVersion(SOAPVersion paramSOAPVersion) { this.soapVersion = paramSOAPVersion; }
  
  public void setSOAPAction(String paramString) { this.soapAction = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\soap\SOAPBindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */