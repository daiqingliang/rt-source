package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLPortTypeImpl extends AbstractExtensibleImpl implements EditableWSDLPortType {
  private QName name;
  
  private final Map<String, EditableWSDLOperation> portTypeOperations;
  
  private EditableWSDLModel owner;
  
  public WSDLPortTypeImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLModel paramEditableWSDLModel, QName paramQName) {
    super(paramXMLStreamReader);
    this.name = paramQName;
    this.owner = paramEditableWSDLModel;
    this.portTypeOperations = new Hashtable();
  }
  
  public QName getName() { return this.name; }
  
  public EditableWSDLOperation get(String paramString) { return (EditableWSDLOperation)this.portTypeOperations.get(paramString); }
  
  public Iterable<EditableWSDLOperation> getOperations() { return this.portTypeOperations.values(); }
  
  public void put(String paramString, EditableWSDLOperation paramEditableWSDLOperation) { this.portTypeOperations.put(paramString, paramEditableWSDLOperation); }
  
  EditableWSDLModel getOwner() { return this.owner; }
  
  public void freeze() {
    for (EditableWSDLOperation editableWSDLOperation : this.portTypeOperations.values())
      editableWSDLOperation.freeze(this.owner); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */