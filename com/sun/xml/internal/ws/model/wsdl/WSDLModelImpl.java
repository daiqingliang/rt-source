package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class WSDLModelImpl extends AbstractExtensibleImpl implements EditableWSDLModel {
  private final Map<QName, EditableWSDLMessage> messages = new HashMap();
  
  private final Map<QName, EditableWSDLPortType> portTypes = new HashMap();
  
  private final Map<QName, EditableWSDLBoundPortType> bindings = new HashMap();
  
  private final Map<QName, EditableWSDLService> services = new LinkedHashMap();
  
  private PolicyMap policyMap;
  
  private final Map<QName, EditableWSDLBoundPortType> unmBindings = Collections.unmodifiableMap(this.bindings);
  
  public WSDLModelImpl(@NotNull String paramString) { super(paramString, -1); }
  
  public WSDLModelImpl() { super(null, -1); }
  
  public void addMessage(EditableWSDLMessage paramEditableWSDLMessage) { this.messages.put(paramEditableWSDLMessage.getName(), paramEditableWSDLMessage); }
  
  public EditableWSDLMessage getMessage(QName paramQName) { return (EditableWSDLMessage)this.messages.get(paramQName); }
  
  public void addPortType(EditableWSDLPortType paramEditableWSDLPortType) { this.portTypes.put(paramEditableWSDLPortType.getName(), paramEditableWSDLPortType); }
  
  public EditableWSDLPortType getPortType(QName paramQName) { return (EditableWSDLPortType)this.portTypes.get(paramQName); }
  
  public void addBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {
    assert !this.bindings.containsValue(paramEditableWSDLBoundPortType);
    this.bindings.put(paramEditableWSDLBoundPortType.getName(), paramEditableWSDLBoundPortType);
  }
  
  public EditableWSDLBoundPortType getBinding(QName paramQName) { return (EditableWSDLBoundPortType)this.bindings.get(paramQName); }
  
  public void addService(EditableWSDLService paramEditableWSDLService) { this.services.put(paramEditableWSDLService.getName(), paramEditableWSDLService); }
  
  public EditableWSDLService getService(QName paramQName) { return (EditableWSDLService)this.services.get(paramQName); }
  
  public Map<QName, EditableWSDLMessage> getMessages() { return this.messages; }
  
  @NotNull
  public Map<QName, EditableWSDLPortType> getPortTypes() { return this.portTypes; }
  
  @NotNull
  public Map<QName, ? extends EditableWSDLBoundPortType> getBindings() { return this.unmBindings; }
  
  @NotNull
  public Map<QName, EditableWSDLService> getServices() { return this.services; }
  
  public QName getFirstServiceName() { return this.services.isEmpty() ? null : ((EditableWSDLService)this.services.values().iterator().next()).getName(); }
  
  public EditableWSDLBoundPortType getBinding(QName paramQName1, QName paramQName2) {
    EditableWSDLService editableWSDLService = (EditableWSDLService)this.services.get(paramQName1);
    if (editableWSDLService != null) {
      EditableWSDLPort editableWSDLPort = editableWSDLService.get(paramQName2);
      if (editableWSDLPort != null)
        return editableWSDLPort.getBinding(); 
    } 
    return null;
  }
  
  public void finalizeRpcLitBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {
    assert paramEditableWSDLBoundPortType != null;
    QName qName = paramEditableWSDLBoundPortType.getPortTypeName();
    if (qName == null)
      return; 
    WSDLPortType wSDLPortType = (WSDLPortType)this.portTypes.get(qName);
    if (wSDLPortType == null)
      return; 
    for (EditableWSDLBoundOperation editableWSDLBoundOperation : paramEditableWSDLBoundPortType.getBindingOperations()) {
      WSDLOperation wSDLOperation = wSDLPortType.get(editableWSDLBoundOperation.getName().getLocalPart());
      WSDLMessage wSDLMessage1 = wSDLOperation.getInput().getMessage();
      if (wSDLMessage1 == null)
        continue; 
      EditableWSDLMessage editableWSDLMessage1 = (EditableWSDLMessage)this.messages.get(wSDLMessage1.getName());
      byte b = 0;
      if (editableWSDLMessage1 != null)
        for (EditableWSDLPart editableWSDLPart : editableWSDLMessage1.parts()) {
          String str = editableWSDLPart.getName();
          ParameterBinding parameterBinding = editableWSDLBoundOperation.getInputBinding(str);
          if (parameterBinding.isBody()) {
            editableWSDLPart.setIndex(b++);
            editableWSDLPart.setBinding(parameterBinding);
            editableWSDLBoundOperation.addPart(editableWSDLPart, WebParam.Mode.IN);
          } 
        }  
      b = 0;
      if (wSDLOperation.isOneWay())
        continue; 
      WSDLMessage wSDLMessage2 = wSDLOperation.getOutput().getMessage();
      if (wSDLMessage2 == null)
        continue; 
      EditableWSDLMessage editableWSDLMessage2 = (EditableWSDLMessage)this.messages.get(wSDLMessage2.getName());
      if (editableWSDLMessage2 != null)
        for (EditableWSDLPart editableWSDLPart : editableWSDLMessage2.parts()) {
          String str = editableWSDLPart.getName();
          ParameterBinding parameterBinding = editableWSDLBoundOperation.getOutputBinding(str);
          if (parameterBinding.isBody()) {
            editableWSDLPart.setIndex(b++);
            editableWSDLPart.setBinding(parameterBinding);
            editableWSDLBoundOperation.addPart(editableWSDLPart, WebParam.Mode.OUT);
          } 
        }  
    } 
  }
  
  public PolicyMap getPolicyMap() { return this.policyMap; }
  
  public void setPolicyMap(PolicyMap paramPolicyMap) { this.policyMap = paramPolicyMap; }
  
  public void freeze() {
    for (EditableWSDLService editableWSDLService : this.services.values())
      editableWSDLService.freeze(this); 
    for (EditableWSDLBoundPortType editableWSDLBoundPortType : this.bindings.values())
      editableWSDLBoundPortType.freeze(); 
    for (EditableWSDLPortType editableWSDLPortType : this.portTypes.values())
      editableWSDLPortType.freeze(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLModelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */