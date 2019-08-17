package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBHeader extends AbstractHeaderImpl {
  private final Object jaxbObject;
  
  private final XMLBridge bridge;
  
  private String nsUri;
  
  private String localName;
  
  private Attributes atts;
  
  private XMLStreamBuffer infoset;
  
  public JAXBHeader(BindingContext paramBindingContext, Object paramObject) {
    this.jaxbObject = paramObject;
    this.bridge = paramBindingContext.createFragmentBridge();
    if (paramObject instanceof JAXBElement) {
      JAXBElement jAXBElement = (JAXBElement)paramObject;
      this.nsUri = jAXBElement.getName().getNamespaceURI();
      this.localName = jAXBElement.getName().getLocalPart();
    } 
  }
  
  public JAXBHeader(XMLBridge paramXMLBridge, Object paramObject) {
    this.jaxbObject = paramObject;
    this.bridge = paramXMLBridge;
    QName qName = (paramXMLBridge.getTypeInfo()).tagName;
    this.nsUri = qName.getNamespaceURI();
    this.localName = qName.getLocalPart();
  }
  
  private void parse() {
    RootElementSniffer rootElementSniffer = new RootElementSniffer();
    try {
      this.bridge.marshal(this.jaxbObject, rootElementSniffer, null);
    } catch (JAXBException jAXBException) {
      this.nsUri = rootElementSniffer.getNsUri();
      this.localName = rootElementSniffer.getLocalName();
      this.atts = rootElementSniffer.getAttributes();
    } 
  }
  
  @NotNull
  public String getNamespaceURI() {
    if (this.nsUri == null)
      parse(); 
    return this.nsUri;
  }
  
  @NotNull
  public String getLocalPart() {
    if (this.localName == null)
      parse(); 
    return this.localName;
  }
  
  public String getAttribute(String paramString1, String paramString2) {
    if (this.atts == null)
      parse(); 
    return this.atts.getValue(paramString1, paramString2);
  }
  
  public XMLStreamReader readHeader() throws XMLStreamException {
    if (this.infoset == null) {
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      writeTo(mutableXMLStreamBuffer.createFromXMLStreamWriter());
      this.infoset = mutableXMLStreamBuffer;
    } 
    return this.infoset.readAsXMLStreamReader();
  }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    try {
      JAXBResult jAXBResult = new JAXBResult(paramUnmarshaller);
      jAXBResult.getHandler().startDocument();
      this.bridge.marshal(this.jaxbObject, jAXBResult);
      jAXBResult.getHandler().endDocument();
      return (T)jAXBResult.getResult();
    } catch (SAXException sAXException) {
      throw new JAXBException(sAXException);
    } 
  }
  
  public <T> T readAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)paramBridge.unmarshal(new JAXBBridgeSource(this.bridge, this.jaxbObject)); }
  
  public <T> T readAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException { return (T)paramXMLBridge.unmarshal(new JAXBBridgeSource(this.bridge, this.jaxbObject), null); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    try {
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream outputStream = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if (outputStream != null && str != null && str.equalsIgnoreCase("utf-8")) {
        this.bridge.marshal(this.jaxbObject, outputStream, paramXMLStreamWriter.getNamespaceContext(), null);
      } else {
        this.bridge.marshal(this.jaxbObject, paramXMLStreamWriter, null);
      } 
    } catch (JAXBException jAXBException) {
      throw new XMLStreamException2(jAXBException);
    } 
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    try {
      SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (sOAPHeader == null)
        sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
      this.bridge.marshal(this.jaxbObject, sOAPHeader);
    } catch (JAXBException jAXBException) {
      throw new SOAPException(jAXBException);
    } 
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    try {
      this.bridge.marshal(this.jaxbObject, paramContentHandler, null);
    } catch (JAXBException jAXBException) {
      SAXParseException sAXParseException = new SAXParseException(jAXBException.getMessage(), null, null, -1, -1, jAXBException);
      paramErrorHandler.fatalError(sAXParseException);
      throw sAXParseException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */