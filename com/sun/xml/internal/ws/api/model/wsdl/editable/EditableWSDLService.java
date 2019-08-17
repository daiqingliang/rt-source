package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import javax.xml.namespace.QName;

public interface EditableWSDLService extends WSDLService {
  @NotNull
  EditableWSDLModel getParent();
  
  EditableWSDLPort get(QName paramQName);
  
  EditableWSDLPort getFirstPort();
  
  @Nullable
  EditableWSDLPort getMatchingPort(QName paramQName);
  
  Iterable<? extends EditableWSDLPort> getPorts();
  
  void put(QName paramQName, EditableWSDLPort paramEditableWSDLPort);
  
  void freeze(EditableWSDLModel paramEditableWSDLModel);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */