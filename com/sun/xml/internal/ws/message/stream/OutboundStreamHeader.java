package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class OutboundStreamHeader extends AbstractHeaderImpl {
  private final XMLStreamBuffer infoset;
  
  private final String nsUri;
  
  private final String localName;
  
  private FinalArrayList<Attribute> attributes;
  
  private static final String TRUE_VALUE = "1";
  
  private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
  
  public OutboundStreamHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2) {
    this.infoset = paramXMLStreamBuffer;
    this.nsUri = paramString1;
    this.localName = paramString2;
  }
  
  @NotNull
  public String getNamespaceURI() { return this.nsUri; }
  
  @NotNull
  public String getLocalPart() { return this.localName; }
  
  public String getAttribute(String paramString1, String paramString2) {
    if (this.attributes == null)
      parseAttributes(); 
    for (int i = this.attributes.size() - 1; i >= 0; i--) {
      Attribute attribute = (Attribute)this.attributes.get(i);
      if (attribute.localName.equals(paramString2) && attribute.nsUri.equals(paramString1))
        return attribute.value; 
    } 
    return null;
  }
  
  private void parseAttributes() {
    try {
      XMLStreamReader xMLStreamReader = readHeader();
      this.attributes = new FinalArrayList();
      for (byte b = 0; b < xMLStreamReader.getAttributeCount(); b++) {
        String str1 = xMLStreamReader.getAttributeLocalName(b);
        String str2 = xMLStreamReader.getAttributeNamespace(b);
        String str3 = xMLStreamReader.getAttributeValue(b);
        this.attributes.add(new Attribute(str2, str1, str3));
      } 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", xMLStreamException);
    } 
  }
  
  public XMLStreamReader readHeader() throws XMLStreamException { return this.infoset.readAsXMLStreamReader(); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.infoset.writeToXMLStreamWriter(paramXMLStreamWriter, true); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    try {
      SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (sOAPHeader == null)
        sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
      this.infoset.writeTo(sOAPHeader);
    } catch (XMLStreamBufferException xMLStreamBufferException) {
      throw new SOAPException(xMLStreamBufferException);
    } 
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.infoset.writeTo(paramContentHandler, paramErrorHandler); }
  
  static final class Attribute {
    final String nsUri;
    
    final String localName;
    
    final String value;
    
    public Attribute(String param1String1, String param1String2, String param1String3) {
      this.nsUri = fixNull(param1String1);
      this.localName = param1String2;
      this.value = param1String3;
    }
    
    private static String fixNull(String param1String) { return (param1String == null) ? "" : param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\stream\OutboundStreamHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */