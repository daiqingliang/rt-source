package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.Locator;

public final class WSDLBoundPortTypeImpl extends AbstractFeaturedObjectImpl implements EditableWSDLBoundPortType {
  private final QName name;
  
  private final QName portTypeName;
  
  private EditableWSDLPortType portType;
  
  private BindingID bindingId;
  
  @NotNull
  private final EditableWSDLModel owner;
  
  private final QNameMap<EditableWSDLBoundOperation> bindingOperations = new QNameMap();
  
  private QNameMap<EditableWSDLBoundOperation> payloadMap;
  
  private EditableWSDLBoundOperation emptyPayloadOperation;
  
  private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
  
  public WSDLBoundPortTypeImpl(XMLStreamReader paramXMLStreamReader, @NotNull EditableWSDLModel paramEditableWSDLModel, QName paramQName1, QName paramQName2) {
    super(paramXMLStreamReader);
    this.owner = paramEditableWSDLModel;
    this.name = paramQName1;
    this.portTypeName = paramQName2;
    paramEditableWSDLModel.addBinding(this);
  }
  
  public QName getName() { return this.name; }
  
  @NotNull
  public EditableWSDLModel getOwner() { return this.owner; }
  
  public EditableWSDLBoundOperation get(QName paramQName) { return (EditableWSDLBoundOperation)this.bindingOperations.get(paramQName); }
  
  public void put(QName paramQName, EditableWSDLBoundOperation paramEditableWSDLBoundOperation) { this.bindingOperations.put(paramQName, paramEditableWSDLBoundOperation); }
  
  public QName getPortTypeName() { return this.portTypeName; }
  
  public EditableWSDLPortType getPortType() { return this.portType; }
  
  public Iterable<EditableWSDLBoundOperation> getBindingOperations() { return this.bindingOperations.values(); }
  
  public BindingID getBindingId() { return (this.bindingId == null) ? BindingID.SOAP11_HTTP : this.bindingId; }
  
  public void setBindingId(BindingID paramBindingID) { this.bindingId = paramBindingID; }
  
  public void setStyle(SOAPBinding.Style paramStyle) { this.style = paramStyle; }
  
  public SOAPBinding.Style getStyle() { return this.style; }
  
  public boolean isRpcLit() { return (SOAPBinding.Style.RPC == this.style); }
  
  public boolean isDoclit() { return (SOAPBinding.Style.DOCUMENT == this.style); }
  
  public ParameterBinding getBinding(QName paramQName, String paramString, WebParam.Mode paramMode) {
    EditableWSDLBoundOperation editableWSDLBoundOperation = get(paramQName);
    return (editableWSDLBoundOperation == null) ? null : ((WebParam.Mode.IN == paramMode || WebParam.Mode.INOUT == paramMode) ? editableWSDLBoundOperation.getInputBinding(paramString) : editableWSDLBoundOperation.getOutputBinding(paramString));
  }
  
  public EditableWSDLBoundOperation getOperation(String paramString1, String paramString2) { return (paramString1 == null && paramString2 == null) ? this.emptyPayloadOperation : (EditableWSDLBoundOperation)this.payloadMap.get((paramString1 == null) ? "" : paramString1, paramString2); }
  
  public void freeze() {
    this.portType = this.owner.getPortType(this.portTypeName);
    if (this.portType == null)
      throw new LocatableWebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(this.portTypeName), new Locator[] { getLocation() }); 
    this.portType.freeze();
    for (EditableWSDLBoundOperation editableWSDLBoundOperation : this.bindingOperations.values())
      editableWSDLBoundOperation.freeze(this.owner); 
    freezePayloadMap();
    this.owner.finalizeRpcLitBinding(this);
  }
  
  private void freezePayloadMap() {
    if (this.style == SOAPBinding.Style.RPC) {
      this.payloadMap = new QNameMap();
      for (EditableWSDLBoundOperation editableWSDLBoundOperation : this.bindingOperations.values())
        this.payloadMap.put(editableWSDLBoundOperation.getRequestPayloadName(), editableWSDLBoundOperation); 
    } else {
      this.payloadMap = new QNameMap();
      for (EditableWSDLBoundOperation editableWSDLBoundOperation : this.bindingOperations.values()) {
        QName qName = editableWSDLBoundOperation.getRequestPayloadName();
        if (qName == null) {
          this.emptyPayloadOperation = editableWSDLBoundOperation;
          continue;
        } 
        this.payloadMap.put(qName, editableWSDLBoundOperation);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundPortTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */