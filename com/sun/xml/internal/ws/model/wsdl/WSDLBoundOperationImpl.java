package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLBoundOperationImpl extends AbstractExtensibleImpl implements EditableWSDLBoundOperation {
  private final QName name;
  
  private final Map<String, ParameterBinding> inputParts;
  
  private final Map<String, ParameterBinding> outputParts;
  
  private final Map<String, ParameterBinding> faultParts;
  
  private final Map<String, String> inputMimeTypes;
  
  private final Map<String, String> outputMimeTypes;
  
  private final Map<String, String> faultMimeTypes;
  
  private boolean explicitInputSOAPBodyParts = false;
  
  private boolean explicitOutputSOAPBodyParts = false;
  
  private boolean explicitFaultSOAPBodyParts = false;
  
  private Boolean emptyInputBody;
  
  private Boolean emptyOutputBody;
  
  private Boolean emptyFaultBody;
  
  private final Map<String, EditableWSDLPart> inParts;
  
  private final Map<String, EditableWSDLPart> outParts;
  
  private final List<EditableWSDLBoundFault> wsdlBoundFaults;
  
  private EditableWSDLOperation operation;
  
  private String soapAction;
  
  private WSDLBoundOperation.ANONYMOUS anonymous;
  
  private final EditableWSDLBoundPortType owner;
  
  private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
  
  private String reqNamespace;
  
  private String respNamespace;
  
  private QName requestPayloadName;
  
  private QName responsePayloadName;
  
  private boolean emptyRequestPayload;
  
  private boolean emptyResponsePayload;
  
  private Map<QName, ? extends EditableWSDLMessage> messages;
  
  public WSDLBoundOperationImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundPortType paramEditableWSDLBoundPortType, QName paramQName) {
    super(paramXMLStreamReader);
    this.name = paramQName;
    this.inputParts = new HashMap();
    this.outputParts = new HashMap();
    this.faultParts = new HashMap();
    this.inputMimeTypes = new HashMap();
    this.outputMimeTypes = new HashMap();
    this.faultMimeTypes = new HashMap();
    this.inParts = new HashMap();
    this.outParts = new HashMap();
    this.wsdlBoundFaults = new ArrayList();
    this.owner = paramEditableWSDLBoundPortType;
  }
  
  public QName getName() { return this.name; }
  
  public String getSOAPAction() { return this.soapAction; }
  
  public void setSoapAction(String paramString) { this.soapAction = (paramString != null) ? paramString : ""; }
  
  public EditableWSDLPart getPart(String paramString, WebParam.Mode paramMode) { return (paramMode == WebParam.Mode.IN) ? (EditableWSDLPart)this.inParts.get(paramString) : ((paramMode == WebParam.Mode.OUT) ? (EditableWSDLPart)this.outParts.get(paramString) : null); }
  
  public void addPart(EditableWSDLPart paramEditableWSDLPart, WebParam.Mode paramMode) {
    if (paramMode == WebParam.Mode.IN) {
      this.inParts.put(paramEditableWSDLPart.getName(), paramEditableWSDLPart);
    } else if (paramMode == WebParam.Mode.OUT) {
      this.outParts.put(paramEditableWSDLPart.getName(), paramEditableWSDLPart);
    } 
  }
  
  public Map<String, ParameterBinding> getInputParts() { return this.inputParts; }
  
  public Map<String, ParameterBinding> getOutputParts() { return this.outputParts; }
  
  public Map<String, ParameterBinding> getFaultParts() { return this.faultParts; }
  
  public Map<String, ? extends EditableWSDLPart> getInParts() { return Collections.unmodifiableMap(this.inParts); }
  
  public Map<String, ? extends EditableWSDLPart> getOutParts() { return Collections.unmodifiableMap(this.outParts); }
  
  @NotNull
  public List<? extends EditableWSDLBoundFault> getFaults() { return this.wsdlBoundFaults; }
  
  public void addFault(@NotNull EditableWSDLBoundFault paramEditableWSDLBoundFault) { this.wsdlBoundFaults.add(paramEditableWSDLBoundFault); }
  
  public ParameterBinding getInputBinding(String paramString) {
    if (this.emptyInputBody == null)
      if (this.inputParts.get(" ") != null) {
        this.emptyInputBody = Boolean.valueOf(true);
      } else {
        this.emptyInputBody = Boolean.valueOf(false);
      }  
    ParameterBinding parameterBinding = (ParameterBinding)this.inputParts.get(paramString);
    return (parameterBinding == null) ? ((this.explicitInputSOAPBodyParts || this.emptyInputBody.booleanValue()) ? ParameterBinding.UNBOUND : ParameterBinding.BODY) : parameterBinding;
  }
  
  public ParameterBinding getOutputBinding(String paramString) {
    if (this.emptyOutputBody == null)
      if (this.outputParts.get(" ") != null) {
        this.emptyOutputBody = Boolean.valueOf(true);
      } else {
        this.emptyOutputBody = Boolean.valueOf(false);
      }  
    ParameterBinding parameterBinding = (ParameterBinding)this.outputParts.get(paramString);
    return (parameterBinding == null) ? ((this.explicitOutputSOAPBodyParts || this.emptyOutputBody.booleanValue()) ? ParameterBinding.UNBOUND : ParameterBinding.BODY) : parameterBinding;
  }
  
  public ParameterBinding getFaultBinding(String paramString) {
    if (this.emptyFaultBody == null)
      if (this.faultParts.get(" ") != null) {
        this.emptyFaultBody = Boolean.valueOf(true);
      } else {
        this.emptyFaultBody = Boolean.valueOf(false);
      }  
    ParameterBinding parameterBinding = (ParameterBinding)this.faultParts.get(paramString);
    return (parameterBinding == null) ? ((this.explicitFaultSOAPBodyParts || this.emptyFaultBody.booleanValue()) ? ParameterBinding.UNBOUND : ParameterBinding.BODY) : parameterBinding;
  }
  
  public String getMimeTypeForInputPart(String paramString) { return (String)this.inputMimeTypes.get(paramString); }
  
  public String getMimeTypeForOutputPart(String paramString) { return (String)this.outputMimeTypes.get(paramString); }
  
  public String getMimeTypeForFaultPart(String paramString) { return (String)this.faultMimeTypes.get(paramString); }
  
  public EditableWSDLOperation getOperation() { return this.operation; }
  
  public EditableWSDLBoundPortType getBoundPortType() { return this.owner; }
  
  public void setInputExplicitBodyParts(boolean paramBoolean) { this.explicitInputSOAPBodyParts = paramBoolean; }
  
  public void setOutputExplicitBodyParts(boolean paramBoolean) { this.explicitOutputSOAPBodyParts = paramBoolean; }
  
  public void setFaultExplicitBodyParts(boolean paramBoolean) { this.explicitFaultSOAPBodyParts = paramBoolean; }
  
  public void setStyle(SOAPBinding.Style paramStyle) { this.style = paramStyle; }
  
  @Nullable
  public QName getRequestPayloadName() {
    if (this.emptyRequestPayload)
      return null; 
    if (this.requestPayloadName != null)
      return this.requestPayloadName; 
    if (this.style.equals(SOAPBinding.Style.RPC)) {
      String str = (getRequestNamespace() != null) ? getRequestNamespace() : this.name.getNamespaceURI();
      this.requestPayloadName = new QName(str, this.name.getLocalPart());
      return this.requestPayloadName;
    } 
    QName qName = this.operation.getInput().getMessage().getName();
    EditableWSDLMessage editableWSDLMessage = (EditableWSDLMessage)this.messages.get(qName);
    for (EditableWSDLPart editableWSDLPart : editableWSDLMessage.parts()) {
      ParameterBinding parameterBinding = getInputBinding(editableWSDLPart.getName());
      if (parameterBinding.isBody()) {
        this.requestPayloadName = editableWSDLPart.getDescriptor().name();
        return this.requestPayloadName;
      } 
    } 
    this.emptyRequestPayload = true;
    return null;
  }
  
  @Nullable
  public QName getResponsePayloadName() {
    if (this.emptyResponsePayload)
      return null; 
    if (this.responsePayloadName != null)
      return this.responsePayloadName; 
    if (this.style.equals(SOAPBinding.Style.RPC)) {
      String str = (getResponseNamespace() != null) ? getResponseNamespace() : this.name.getNamespaceURI();
      this.responsePayloadName = new QName(str, this.name.getLocalPart() + "Response");
      return this.responsePayloadName;
    } 
    QName qName = this.operation.getOutput().getMessage().getName();
    EditableWSDLMessage editableWSDLMessage = (EditableWSDLMessage)this.messages.get(qName);
    for (EditableWSDLPart editableWSDLPart : editableWSDLMessage.parts()) {
      ParameterBinding parameterBinding = getOutputBinding(editableWSDLPart.getName());
      if (parameterBinding.isBody()) {
        this.responsePayloadName = editableWSDLPart.getDescriptor().name();
        return this.responsePayloadName;
      } 
    } 
    this.emptyResponsePayload = true;
    return null;
  }
  
  public String getRequestNamespace() { return (this.reqNamespace != null) ? this.reqNamespace : this.name.getNamespaceURI(); }
  
  public void setRequestNamespace(String paramString) { this.reqNamespace = paramString; }
  
  public String getResponseNamespace() { return (this.respNamespace != null) ? this.respNamespace : this.name.getNamespaceURI(); }
  
  public void setResponseNamespace(String paramString) { this.respNamespace = paramString; }
  
  EditableWSDLBoundPortType getOwner() { return this.owner; }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel) {
    this.messages = paramEditableWSDLModel.getMessages();
    this.operation = this.owner.getPortType().get(this.name.getLocalPart());
    for (EditableWSDLBoundFault editableWSDLBoundFault : this.wsdlBoundFaults)
      editableWSDLBoundFault.freeze(this); 
  }
  
  public void setAnonymous(WSDLBoundOperation.ANONYMOUS paramANONYMOUS) { this.anonymous = paramANONYMOUS; }
  
  public WSDLBoundOperation.ANONYMOUS getAnonymous() { return this.anonymous; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */