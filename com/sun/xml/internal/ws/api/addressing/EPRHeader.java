package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class EPRHeader extends AbstractHeaderImpl {
  private final String nsUri;
  
  private final String localName;
  
  private final WSEndpointReference epr;
  
  EPRHeader(QName paramQName, WSEndpointReference paramWSEndpointReference) {
    this.nsUri = paramQName.getNamespaceURI();
    this.localName = paramQName.getLocalPart();
    this.epr = paramWSEndpointReference;
  }
  
  @NotNull
  public String getNamespaceURI() { return this.nsUri; }
  
  @NotNull
  public String getLocalPart() { return this.localName; }
  
  @Nullable
  public String getAttribute(@NotNull String paramString1, @NotNull String paramString2) {
    try {
      XMLStreamReader xMLStreamReader = this.epr.read("EndpointReference");
      while (xMLStreamReader.getEventType() != 1)
        xMLStreamReader.next(); 
      return xMLStreamReader.getAttributeValue(paramString1, paramString2);
    } catch (XMLStreamException xMLStreamException) {
      throw new AssertionError(xMLStreamException);
    } 
  }
  
  public XMLStreamReader readHeader() throws XMLStreamException { return this.epr.read(this.localName); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.epr.writeTo(this.localName, paramXMLStreamWriter); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    try {
      Transformer transformer = XmlUtil.newTransformer();
      SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (sOAPHeader == null)
        sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      XMLStreamWriter xMLStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(byteArrayOutputStream);
      this.epr.writeTo(this.localName, xMLStreamWriter);
      xMLStreamWriter.flush();
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      Element element = documentBuilderFactory.newDocumentBuilder().parse(byteArrayInputStream).getDocumentElement();
      Node node = sOAPHeader.getOwnerDocument().importNode(element, true);
      sOAPHeader.appendChild(node);
    } catch (Exception exception) {
      throw new SOAPException(exception);
    } 
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.epr.writeTo(this.localName, paramContentHandler, paramErrorHandler, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\EPRHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */