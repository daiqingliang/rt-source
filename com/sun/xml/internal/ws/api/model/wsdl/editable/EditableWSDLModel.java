package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Map;
import javax.xml.namespace.QName;

public interface EditableWSDLModel extends WSDLModel {
  EditableWSDLPortType getPortType(@NotNull QName paramQName);
  
  void addBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType);
  
  EditableWSDLBoundPortType getBinding(@NotNull QName paramQName);
  
  EditableWSDLBoundPortType getBinding(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  EditableWSDLService getService(@NotNull QName paramQName);
  
  @NotNull
  Map<QName, ? extends EditableWSDLMessage> getMessages();
  
  void addMessage(EditableWSDLMessage paramEditableWSDLMessage);
  
  @NotNull
  Map<QName, ? extends EditableWSDLPortType> getPortTypes();
  
  void addPortType(EditableWSDLPortType paramEditableWSDLPortType);
  
  @NotNull
  Map<QName, ? extends EditableWSDLBoundPortType> getBindings();
  
  @NotNull
  Map<QName, ? extends EditableWSDLService> getServices();
  
  void addService(EditableWSDLService paramEditableWSDLService);
  
  EditableWSDLMessage getMessage(QName paramQName);
  
  void setPolicyMap(PolicyMap paramPolicyMap);
  
  void finalizeRpcLitBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType);
  
  void freeze();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */