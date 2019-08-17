package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.xml.internal.ws.api.model.ParameterBinding;

public interface WSDLPart extends WSDLObject {
  String getName();
  
  ParameterBinding getBinding();
  
  int getIndex();
  
  WSDLPartDescriptor getDescriptor();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */