package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class DOMMessage extends AbstractMessageImpl {
  private MessageHeaders headers;
  
  private final Element payload;
  
  public DOMMessage(SOAPVersion paramSOAPVersion, Element paramElement) { this(paramSOAPVersion, null, paramElement); }
  
  public DOMMessage(SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, Element paramElement) { this(paramSOAPVersion, paramMessageHeaders, paramElement, null); }
  
  public DOMMessage(SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, Element paramElement, AttachmentSet paramAttachmentSet) {
    super(paramSOAPVersion);
    this.headers = paramMessageHeaders;
    this.payload = paramElement;
    this.attachmentSet = paramAttachmentSet;
    assert paramElement != null;
  }
  
  private DOMMessage(DOMMessage paramDOMMessage) {
    super(paramDOMMessage);
    this.headers = HeaderList.copy(paramDOMMessage.headers);
    this.payload = paramDOMMessage.payload;
  }
  
  public boolean hasHeaders() { return getHeaders().hasHeaders(); }
  
  public MessageHeaders getHeaders() {
    if (this.headers == null)
      this.headers = new HeaderList(getSOAPVersion()); 
    return this.headers;
  }
  
  public String getPayloadLocalPart() { return this.payload.getLocalName(); }
  
  public String getPayloadNamespaceURI() { return this.payload.getNamespaceURI(); }
  
  public boolean hasPayload() { return true; }
  
  public Source readPayloadAsSource() { return new DOMSource(this.payload); }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    if (hasAttachments())
      paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments())); 
    try {
      object = paramUnmarshaller.unmarshal(this.payload);
      return (T)object;
    } finally {
      paramUnmarshaller.setAttachmentUnmarshaller(null);
    } 
  }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)paramBridge.unmarshal(this.payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null); }
  
  public XMLStreamReader readPayload() throws XMLStreamException {
    DOMStreamReader dOMStreamReader = new DOMStreamReader();
    dOMStreamReader.setCurrentNode(this.payload);
    dOMStreamReader.nextTag();
    assert dOMStreamReader.getEventType() == 1;
    return dOMStreamReader;
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) {
    try {
      if (this.payload != null)
        DOMUtil.serializeNode(this.payload, paramXMLStreamWriter); 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    if (paramBoolean)
      paramContentHandler = new FragmentContentHandler(paramContentHandler); 
    DOMScanner dOMScanner = new DOMScanner();
    dOMScanner.setContentHandler(paramContentHandler);
    dOMScanner.scan(this.payload);
  }
  
  public Message copy() { return new DOMMessage(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\DOMMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */