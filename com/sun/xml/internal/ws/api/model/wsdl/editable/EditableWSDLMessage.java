package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;

public interface EditableWSDLMessage extends WSDLMessage {
  Iterable<? extends EditableWSDLPart> parts();
  
  void add(EditableWSDLPart paramEditableWSDLPart);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */