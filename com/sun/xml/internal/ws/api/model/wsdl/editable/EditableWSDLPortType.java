package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;

public interface EditableWSDLPortType extends WSDLPortType {
  EditableWSDLOperation get(String paramString);
  
  Iterable<? extends EditableWSDLOperation> getOperations();
  
  void put(String paramString, EditableWSDLOperation paramEditableWSDLOperation);
  
  void freeze();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLPortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */