package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public interface WSDLOperation extends WSDLObject, WSDLExtensible {
  @NotNull
  QName getName();
  
  @NotNull
  WSDLInput getInput();
  
  @Nullable
  WSDLOutput getOutput();
  
  boolean isOneWay();
  
  Iterable<? extends WSDLFault> getFaults();
  
  @Nullable
  WSDLFault getFault(QName paramQName);
  
  @NotNull
  QName getPortTypeName();
  
  String getParameterOrder();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */