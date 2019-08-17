package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class W3CAddressingMetadataWSDLParserExtension extends W3CAddressingWSDLParserExtension {
  String METADATA_WSDL_EXTN_NS = "http://www.w3.org/2007/05/addressing/metadata";
  
  QName METADATA_WSDL_ACTION_TAG = new QName(this.METADATA_WSDL_EXTN_NS, "Action", "wsam");
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return false; }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  protected void patchAnonymousDefault(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {}
  
  protected String getNamespaceURI() { return this.METADATA_WSDL_EXTN_NS; }
  
  protected QName getWsdlActionTag() { return this.METADATA_WSDL_ACTION_TAG; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\W3CAddressingMetadataWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */