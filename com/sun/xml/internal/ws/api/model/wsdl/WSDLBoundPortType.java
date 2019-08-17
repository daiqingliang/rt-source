package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface WSDLBoundPortType extends WSDLFeaturedObject, WSDLExtensible {
  QName getName();
  
  @NotNull
  WSDLModel getOwner();
  
  WSDLBoundOperation get(QName paramQName);
  
  QName getPortTypeName();
  
  WSDLPortType getPortType();
  
  Iterable<? extends WSDLBoundOperation> getBindingOperations();
  
  @NotNull
  SOAPBinding.Style getStyle();
  
  BindingID getBindingId();
  
  @Nullable
  WSDLBoundOperation getOperation(String paramString1, String paramString2);
  
  ParameterBinding getBinding(QName paramQName, String paramString, WebParam.Mode paramMode);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLBoundPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */