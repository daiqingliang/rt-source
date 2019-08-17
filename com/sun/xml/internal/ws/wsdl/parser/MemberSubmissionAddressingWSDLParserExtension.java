package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class MemberSubmissionAddressingWSDLParserExtension extends W3CAddressingWSDLParserExtension {
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) { return addressibleElement(paramXMLStreamReader, paramEditableWSDLBoundPortType); }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) { return addressibleElement(paramXMLStreamReader, paramEditableWSDLPort); }
  
  private boolean addressibleElement(XMLStreamReader paramXMLStreamReader, WSDLFeaturedObject paramWSDLFeaturedObject) {
    QName qName = paramXMLStreamReader.getName();
    if (qName.equals(AddressingVersion.MEMBER.wsdlExtensionTag)) {
      String str = paramXMLStreamReader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
      paramWSDLFeaturedObject.addFeature(new MemberSubmissionAddressingFeature(Boolean.parseBoolean(str)));
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return true;
    } 
    return false;
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) { return false; }
  
  protected void patchAnonymousDefault(EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {}
  
  protected String getNamespaceURI() { return AddressingVersion.MEMBER.wsdlNsUri; }
  
  protected QName getWsdlActionTag() { return AddressingVersion.MEMBER.wsdlActionTag; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\MemberSubmissionAddressingWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */