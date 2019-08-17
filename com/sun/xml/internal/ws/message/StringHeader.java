package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StringHeader extends AbstractHeaderImpl {
  protected final QName name;
  
  protected final String value;
  
  protected boolean mustUnderstand = false;
  
  protected SOAPVersion soapVersion;
  
  protected static final String MUST_UNDERSTAND = "mustUnderstand";
  
  protected static final String S12_MUST_UNDERSTAND_TRUE = "true";
  
  protected static final String S11_MUST_UNDERSTAND_TRUE = "1";
  
  public StringHeader(@NotNull QName paramQName, @NotNull String paramString) {
    assert paramQName != null;
    assert paramString != null;
    this.name = paramQName;
    this.value = paramString;
  }
  
  public StringHeader(@NotNull QName paramQName, @NotNull String paramString, @NotNull SOAPVersion paramSOAPVersion, boolean paramBoolean) {
    this.name = paramQName;
    this.value = paramString;
    this.soapVersion = paramSOAPVersion;
    this.mustUnderstand = paramBoolean;
  }
  
  @NotNull
  public String getNamespaceURI() { return this.name.getNamespaceURI(); }
  
  @NotNull
  public String getLocalPart() { return this.name.getLocalPart(); }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2) { return (this.mustUnderstand && this.soapVersion.nsUri.equals(paramString1) && "mustUnderstand".equals(paramString2)) ? getMustUnderstandLiteral(this.soapVersion) : null; }
  
  public XMLStreamReader readHeader() throws XMLStreamException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    XMLStreamWriter xMLStreamWriter = mutableXMLStreamBuffer.createFromXMLStreamWriter();
    writeTo(xMLStreamWriter);
    return mutableXMLStreamBuffer.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    paramXMLStreamWriter.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(this.name.getNamespaceURI());
    if (this.mustUnderstand) {
      paramXMLStreamWriter.writeNamespace("S", this.soapVersion.nsUri);
      paramXMLStreamWriter.writeAttribute("S", this.soapVersion.nsUri, "mustUnderstand", getMustUnderstandLiteral(this.soapVersion));
    } 
    paramXMLStreamWriter.writeCharacters(this.value);
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (sOAPHeader == null)
      sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
    SOAPHeaderElement sOAPHeaderElement = sOAPHeader.addHeaderElement(this.name);
    if (this.mustUnderstand)
      sOAPHeaderElement.setMustUnderstand(true); 
    sOAPHeaderElement.addTextNode(this.value);
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    String str1 = this.name.getNamespaceURI();
    String str2 = this.name.getLocalPart();
    paramContentHandler.startPrefixMapping("", str1);
    if (this.mustUnderstand) {
      AttributesImpl attributesImpl = new AttributesImpl();
      attributesImpl.addAttribute(this.soapVersion.nsUri, "mustUnderstand", "S:mustUnderstand", "CDATA", getMustUnderstandLiteral(this.soapVersion));
      paramContentHandler.startElement(str1, str2, str2, attributesImpl);
    } else {
      paramContentHandler.startElement(str1, str2, str2, EMPTY_ATTS);
    } 
    paramContentHandler.characters(this.value.toCharArray(), 0, this.value.length());
    paramContentHandler.endElement(str1, str2, str2);
  }
  
  private static String getMustUnderstandLiteral(SOAPVersion paramSOAPVersion) { return (paramSOAPVersion == SOAPVersion.SOAP_12) ? "true" : "1"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\StringHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */