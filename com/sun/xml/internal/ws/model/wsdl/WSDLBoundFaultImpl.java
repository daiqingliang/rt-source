package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public class WSDLBoundFaultImpl extends AbstractExtensibleImpl implements EditableWSDLBoundFault {
  private final String name;
  
  private EditableWSDLFault fault;
  
  private EditableWSDLBoundOperation owner;
  
  public WSDLBoundFaultImpl(XMLStreamReader paramXMLStreamReader, String paramString, EditableWSDLBoundOperation paramEditableWSDLBoundOperation) {
    super(paramXMLStreamReader);
    this.name = paramString;
    this.owner = paramEditableWSDLBoundOperation;
  }
  
  @NotNull
  public String getName() { return this.name; }
  
  public QName getQName() { return (this.owner.getOperation() != null) ? new QName(this.owner.getOperation().getName().getNamespaceURI(), this.name) : null; }
  
  public EditableWSDLFault getFault() { return this.fault; }
  
  @NotNull
  public EditableWSDLBoundOperation getBoundOperation() { return this.owner; }
  
  public void freeze(EditableWSDLBoundOperation paramEditableWSDLBoundOperation) {
    assert paramEditableWSDLBoundOperation != null;
    EditableWSDLOperation editableWSDLOperation = paramEditableWSDLBoundOperation.getOperation();
    if (editableWSDLOperation != null)
      for (EditableWSDLFault editableWSDLFault : editableWSDLOperation.getFaults()) {
        if (editableWSDLFault.getName().equals(this.name)) {
          this.fault = editableWSDLFault;
          break;
        } 
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundFaultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */