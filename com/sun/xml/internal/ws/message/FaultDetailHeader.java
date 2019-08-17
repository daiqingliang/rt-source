package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
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

public class FaultDetailHeader extends AbstractHeaderImpl {
  private AddressingVersion av;
  
  private String wrapper;
  
  private String problemValue = null;
  
  public FaultDetailHeader(AddressingVersion paramAddressingVersion, String paramString, QName paramQName) {
    this.av = paramAddressingVersion;
    this.wrapper = paramString;
    this.problemValue = paramQName.toString();
  }
  
  public FaultDetailHeader(AddressingVersion paramAddressingVersion, String paramString1, String paramString2) {
    this.av = paramAddressingVersion;
    this.wrapper = paramString1;
    this.problemValue = paramString2;
  }
  
  @NotNull
  public String getNamespaceURI() { return this.av.nsUri; }
  
  @NotNull
  public String getLocalPart() { return this.av.faultDetailTag.getLocalPart(); }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2) { return null; }
  
  public XMLStreamReader readHeader() throws XMLStreamException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    XMLStreamWriter xMLStreamWriter = mutableXMLStreamBuffer.createFromXMLStreamWriter();
    writeTo(xMLStreamWriter);
    return mutableXMLStreamBuffer.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    paramXMLStreamWriter.writeStartElement("", this.av.faultDetailTag.getLocalPart(), this.av.faultDetailTag.getNamespaceURI());
    paramXMLStreamWriter.writeDefaultNamespace(this.av.nsUri);
    paramXMLStreamWriter.writeStartElement("", this.wrapper, this.av.nsUri);
    paramXMLStreamWriter.writeCharacters(this.problemValue);
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndElement();
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (sOAPHeader == null)
      sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
    SOAPHeaderElement sOAPHeaderElement = sOAPHeader.addHeaderElement(this.av.faultDetailTag);
    sOAPHeaderElement = sOAPHeader.addHeaderElement(new QName(this.av.nsUri, this.wrapper));
    sOAPHeaderElement.addTextNode(this.problemValue);
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    String str1 = this.av.nsUri;
    String str2 = this.av.faultDetailTag.getLocalPart();
    paramContentHandler.startPrefixMapping("", str1);
    paramContentHandler.startElement(str1, str2, str2, EMPTY_ATTS);
    paramContentHandler.startElement(str1, this.wrapper, this.wrapper, EMPTY_ATTS);
    paramContentHandler.characters(this.problemValue.toCharArray(), 0, this.problemValue.length());
    paramContentHandler.endElement(str1, this.wrapper, this.wrapper);
    paramContentHandler.endElement(str1, str2, str2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\FaultDetailHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */