package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLServiceImpl extends AbstractExtensibleImpl implements EditableWSDLService {
  private final QName name;
  
  private final Map<QName, EditableWSDLPort> ports;
  
  private final EditableWSDLModel parent;
  
  public WSDLServiceImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLModel paramEditableWSDLModel, QName paramQName) {
    super(paramXMLStreamReader);
    this.parent = paramEditableWSDLModel;
    this.name = paramQName;
    this.ports = new LinkedHashMap();
  }
  
  @NotNull
  public EditableWSDLModel getParent() { return this.parent; }
  
  public QName getName() { return this.name; }
  
  public EditableWSDLPort get(QName paramQName) { return (EditableWSDLPort)this.ports.get(paramQName); }
  
  public EditableWSDLPort getFirstPort() { return this.ports.isEmpty() ? null : (EditableWSDLPort)this.ports.values().iterator().next(); }
  
  public Iterable<EditableWSDLPort> getPorts() { return this.ports.values(); }
  
  @Nullable
  public EditableWSDLPort getMatchingPort(QName paramQName) {
    for (EditableWSDLPort editableWSDLPort : getPorts()) {
      QName qName = editableWSDLPort.getBinding().getPortTypeName();
      assert qName != null;
      if (qName.equals(paramQName))
        return editableWSDLPort; 
    } 
    return null;
  }
  
  public void put(QName paramQName, EditableWSDLPort paramEditableWSDLPort) {
    if (paramQName == null || paramEditableWSDLPort == null)
      throw new NullPointerException(); 
    this.ports.put(paramQName, paramEditableWSDLPort);
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel) {
    for (EditableWSDLPort editableWSDLPort : this.ports.values())
      editableWSDLPort.freeze(paramEditableWSDLModel); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLServiceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */