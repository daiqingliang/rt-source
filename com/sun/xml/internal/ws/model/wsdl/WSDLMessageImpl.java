package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLMessageImpl extends AbstractExtensibleImpl implements EditableWSDLMessage {
  private final QName name;
  
  private final ArrayList<EditableWSDLPart> parts;
  
  public WSDLMessageImpl(XMLStreamReader paramXMLStreamReader, QName paramQName) {
    super(paramXMLStreamReader);
    this.name = paramQName;
    this.parts = new ArrayList();
  }
  
  public QName getName() { return this.name; }
  
  public void add(EditableWSDLPart paramEditableWSDLPart) { this.parts.add(paramEditableWSDLPart); }
  
  public Iterable<EditableWSDLPart> parts() { return this.parts; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */