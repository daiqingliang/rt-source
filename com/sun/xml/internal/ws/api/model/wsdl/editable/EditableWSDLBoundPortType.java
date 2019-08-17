package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;

public interface EditableWSDLBoundPortType extends WSDLBoundPortType {
  @NotNull
  EditableWSDLModel getOwner();
  
  EditableWSDLBoundOperation get(QName paramQName);
  
  EditableWSDLPortType getPortType();
  
  Iterable<? extends EditableWSDLBoundOperation> getBindingOperations();
  
  @Nullable
  EditableWSDLBoundOperation getOperation(String paramString1, String paramString2);
  
  void put(QName paramQName, EditableWSDLBoundOperation paramEditableWSDLBoundOperation);
  
  void setBindingId(BindingID paramBindingID);
  
  void setStyle(SOAPBinding.Style paramStyle);
  
  void freeze();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLBoundPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */