package com.sun.xml.internal.ws.api.model.wsdl;

import javax.xml.namespace.QName;

public interface WSDLMessage extends WSDLObject, WSDLExtensible {
  QName getName();
  
  Iterable<? extends WSDLPart> parts();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */