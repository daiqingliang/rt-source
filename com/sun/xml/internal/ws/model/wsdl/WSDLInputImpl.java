package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLInputImpl extends AbstractExtensibleImpl implements EditableWSDLInput {
  private String name;
  
  private QName messageName;
  
  private EditableWSDLOperation operation;
  
  private EditableWSDLMessage message;
  
  private String action;
  
  private boolean defaultAction = true;
  
  public WSDLInputImpl(XMLStreamReader paramXMLStreamReader, String paramString, QName paramQName, EditableWSDLOperation paramEditableWSDLOperation) {
    super(paramXMLStreamReader);
    this.name = paramString;
    this.messageName = paramQName;
    this.operation = paramEditableWSDLOperation;
  }
  
  public String getName() { return (this.name != null) ? this.name : (this.operation.isOneWay() ? this.operation.getName().getLocalPart() : (this.operation.getName().getLocalPart() + "Request")); }
  
  public EditableWSDLMessage getMessage() { return this.message; }
  
  public String getAction() { return this.action; }
  
  @NotNull
  public EditableWSDLOperation getOperation() { return this.operation; }
  
  public QName getQName() { return new QName(this.operation.getName().getNamespaceURI(), getName()); }
  
  public void setAction(String paramString) { this.action = paramString; }
  
  public boolean isDefaultAction() { return this.defaultAction; }
  
  public void setDefaultAction(boolean paramBoolean) { this.defaultAction = paramBoolean; }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel) { this.message = paramEditableWSDLModel.getMessage(this.messageName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLInputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */