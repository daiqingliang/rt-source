package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class OutboundReferenceParameterHeader extends AbstractHeaderImpl {
  private final XMLStreamBuffer infoset;
  
  private final String nsUri;
  
  private final String localName;
  
  private FinalArrayList<Attribute> attributes;
  
  private static final String TRUE_VALUE = "1";
  
  private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
  
  OutboundReferenceParameterHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2) {
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
      xMLStreamReader.nextTag();
      this.attributes = new FinalArrayList();
      boolean bool = false;
      for (byte b = 0; b < xMLStreamReader.getAttributeCount(); b++) {
        String str1 = xMLStreamReader.getAttributeLocalName(b);
        String str2 = xMLStreamReader.getAttributeNamespace(b);
        String str3 = xMLStreamReader.getAttributeValue(b);
        if (str2.equals(AddressingVersion.W3C.nsUri) && str1.equals("IS_REFERENCE_PARAMETER"))
          bool = true; 
        this.attributes.add(new Attribute(str2, str1, str3));
      } 
      if (!bool)
        this.attributes.add(new Attribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1")); 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", xMLStreamException);
    } 
  }
  
  public XMLStreamReader readHeader() throws XMLStreamException { return new StreamReaderDelegate(this.infoset.readAsXMLStreamReader()) {
        int state = 0;
        
        public int next() throws XMLStreamException { return check(super.next()); }
        
        public int nextTag() throws XMLStreamException { return check(super.nextTag()); }
        
        private int check(int param1Int) {
          switch (this.state) {
            case 0:
              if (param1Int == 1)
                this.state = 1; 
              break;
            case 1:
              this.state = 2;
              break;
          } 
          return param1Int;
        }
        
        public int getAttributeCount() throws XMLStreamException { return (this.state == 1) ? (super.getAttributeCount() + 1) : super.getAttributeCount(); }
        
        public String getAttributeLocalName(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? "IsReferenceParameter" : super.getAttributeLocalName(param1Int); }
        
        public String getAttributeNamespace(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? AddressingVersion.W3C.nsUri : super.getAttributeNamespace(param1Int); }
        
        public String getAttributePrefix(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? "wsa" : super.getAttributePrefix(param1Int); }
        
        public String getAttributeType(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? "CDATA" : super.getAttributeType(param1Int); }
        
        public String getAttributeValue(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? "1" : super.getAttributeValue(param1Int); }
        
        public QName getAttributeName(int param1Int) { return (this.state == 1 && param1Int == super.getAttributeCount()) ? new QName(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa") : super.getAttributeName(param1Int); }
        
        public String getAttributeValue(String param1String1, String param1String2) { return (this.state == 1 && param1String2.equals("IsReferenceParameter") && param1String1.equals(AddressingVersion.W3C.nsUri)) ? "1" : super.getAttributeValue(param1String1, param1String2); }
      }; }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(paramXMLStreamWriter) {
          private boolean root = true;
          
          private boolean onRootEl = true;
          
          public void writeStartElement(String param1String) throws XMLStreamException {
            super.writeStartElement(param1String);
            writeAddedAttribute();
          }
          
          private void writeAddedAttribute() {
            if (!this.root) {
              this.onRootEl = false;
              return;
            } 
            this.root = false;
            writeNamespace("wsa", AddressingVersion.W3C.nsUri);
            super.writeAttribute("wsa", AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1");
          }
          
          public void writeStartElement(String param1String1, String param1String2) throws XMLStreamException {
            super.writeStartElement(param1String1, param1String2);
            writeAddedAttribute();
          }
          
          public void writeStartElement(String param1String1, String param1String2, String param1String3) throws SAXException {
            boolean bool = isPrefixDeclared(param1String1, param1String3);
            super.writeStartElement(param1String1, param1String2, param1String3);
            if (!bool && !param1String1.equals(""))
              super.writeNamespace(param1String1, param1String3); 
            writeAddedAttribute();
          }
          
          public void writeNamespace(String param1String1, String param1String2) throws XMLStreamException {
            if (!isPrefixDeclared(param1String1, param1String2))
              super.writeNamespace(param1String1, param1String2); 
          }
          
          public void writeAttribute(String param1String1, String param1String2, String param1String3, String param1String4) throws XMLStreamException {
            if (this.onRootEl && param1String2.equals(AddressingVersion.W3C.nsUri) && param1String3.equals("IsReferenceParameter"))
              return; 
            this.writer.writeAttribute(param1String1, param1String2, param1String3, param1String4);
          }
          
          public void writeAttribute(String param1String1, String param1String2, String param1String3) throws SAXException { this.writer.writeAttribute(param1String1, param1String2, param1String3); }
          
          private boolean isPrefixDeclared(String param1String1, String param1String2) { return param1String2.equals(getNamespaceContext().getNamespaceURI(param1String1)); }
        }true); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    try {
      SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (sOAPHeader == null)
        sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
      Element element = (Element)this.infoset.writeTo(sOAPHeader);
      element.setAttributeNS(AddressingVersion.W3C.nsUri, AddressingVersion.W3C.getPrefix() + ":" + "IsReferenceParameter", "1");
    } catch (XMLStreamBufferException xMLStreamBufferException) {
      throw new SOAPException(xMLStreamBufferException);
    } 
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    class Filter extends XMLFilterImpl {
      private int depth = 0;
      
      Filter(ContentHandler param1ContentHandler) { setContentHandler(param1ContentHandler); }
      
      public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) throws SAXException {
        if (this.depth++ == 0) {
          startPrefixMapping("wsa", AddressingVersion.W3C.nsUri);
          if (param1Attributes.getIndex(AddressingVersion.W3C.nsUri, "IsReferenceParameter") == -1) {
            AttributesImpl attributesImpl = new AttributesImpl(param1Attributes);
            attributesImpl.addAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa:IsReferenceParameter", "CDATA", "1");
            param1Attributes = attributesImpl;
          } 
        } 
        super.startElement(param1String1, param1String2, param1String3, param1Attributes);
      }
      
      public void endElement(String param1String1, String param1String2, String param1String3) throws SAXException {
        super.endElement(param1String1, param1String2, param1String3);
        if (--this.depth == 0)
          endPrefixMapping("wsa"); 
      }
    };
    this.infoset.writeTo(new Filter(paramContentHandler), paramErrorHandler);
  }
  
  static final class Attribute {
    final String nsUri;
    
    final String localName;
    
    final String value;
    
    public Attribute(String param1String1, String param1String2, String param1String3) throws SAXException {
      this.nsUri = fixNull(param1String1);
      this.localName = param1String2;
      this.value = param1String3;
    }
    
    private static String fixNull(String param1String) { return (param1String == null) ? "" : param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\OutboundReferenceParameterHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */