package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class DOMHeader<N extends Element> extends AbstractHeaderImpl {
  protected final N node;
  
  private final String nsUri;
  
  private final String localName;
  
  public DOMHeader(N paramN) {
    assert paramN != null;
    this.node = paramN;
    this.nsUri = fixNull(paramN.getNamespaceURI());
    this.localName = paramN.getLocalName();
  }
  
  public String getNamespaceURI() { return this.nsUri; }
  
  public String getLocalPart() { return this.localName; }
  
  public XMLStreamReader readHeader() throws XMLStreamException {
    DOMStreamReader dOMStreamReader = new DOMStreamReader(this.node);
    dOMStreamReader.nextTag();
    return dOMStreamReader;
  }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException { return (T)paramUnmarshaller.unmarshal(this.node); }
  
  public <T> T readAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)paramBridge.unmarshal(this.node); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { DOMUtil.serializeNode(this.node, paramXMLStreamWriter); }
  
  private static String fixNull(String paramString) { return (paramString != null) ? paramString : ""; }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    DOMScanner dOMScanner = new DOMScanner();
    dOMScanner.setContentHandler(paramContentHandler);
    dOMScanner.scan(this.node);
  }
  
  public String getAttribute(String paramString1, String paramString2) {
    if (paramString1.length() == 0)
      paramString1 = null; 
    return this.node.getAttributeNS(paramString1, paramString2);
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    SOAPHeader sOAPHeader = paramSOAPMessage.getSOAPHeader();
    if (sOAPHeader == null)
      sOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader(); 
    Node node1 = sOAPHeader.getOwnerDocument().importNode(this.node, true);
    sOAPHeader.appendChild(node1);
  }
  
  public String getStringContent() { return this.node.getTextContent(); }
  
  public N getWrappedNode() { return (N)this.node; }
  
  public int hashCode() { return getWrappedNode().hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof DOMHeader) ? getWrappedNode().equals(((DOMHeader)paramObject).getWrappedNode()) : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\DOMHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */