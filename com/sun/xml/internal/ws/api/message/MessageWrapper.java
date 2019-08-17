package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

class MessageWrapper extends StreamMessage {
  Packet packet;
  
  Message delegate;
  
  StreamMessage streamDelegate;
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException { this.streamDelegate.writePayloadTo(paramContentHandler, paramErrorHandler, paramBoolean); }
  
  public String getBodyPrologue() { return this.streamDelegate.getBodyPrologue(); }
  
  public String getBodyEpilogue() { return this.streamDelegate.getBodyEpilogue(); }
  
  MessageWrapper(Packet paramPacket, Message paramMessage) {
    super(paramMessage.getSOAPVersion());
    this.packet = paramPacket;
    this.delegate = paramMessage;
    this.streamDelegate = (paramMessage instanceof StreamMessage) ? (StreamMessage)paramMessage : null;
    setMessageMedadata(paramPacket);
  }
  
  public int hashCode() { return this.delegate.hashCode(); }
  
  public boolean equals(Object paramObject) { return this.delegate.equals(paramObject); }
  
  public boolean hasHeaders() { return this.delegate.hasHeaders(); }
  
  public AttachmentSet getAttachments() { return this.delegate.getAttachments(); }
  
  public String toString() { return this.delegate.toString(); }
  
  public boolean isOneWay(WSDLPort paramWSDLPort) { return this.delegate.isOneWay(paramWSDLPort); }
  
  public String getPayloadLocalPart() { return this.delegate.getPayloadLocalPart(); }
  
  public String getPayloadNamespaceURI() { return this.delegate.getPayloadNamespaceURI(); }
  
  public boolean hasPayload() { return this.delegate.hasPayload(); }
  
  public boolean isFault() { return this.delegate.isFault(); }
  
  public QName getFirstDetailEntryName() { return this.delegate.getFirstDetailEntryName(); }
  
  public Source readEnvelopeAsSource() { return this.delegate.readEnvelopeAsSource(); }
  
  public Source readPayloadAsSource() { return this.delegate.readPayloadAsSource(); }
  
  public SOAPMessage readAsSOAPMessage() throws SOAPException {
    if (!(this.delegate instanceof com.sun.xml.internal.ws.message.saaj.SAAJMessage))
      this.delegate = toSAAJ(this.packet, null); 
    return this.delegate.readAsSOAPMessage();
  }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean) throws SOAPException {
    if (!(this.delegate instanceof com.sun.xml.internal.ws.message.saaj.SAAJMessage))
      this.delegate = toSAAJ(paramPacket, Boolean.valueOf(paramBoolean)); 
    return this.delegate.readAsSOAPMessage();
  }
  
  public Object readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException { return this.delegate.readPayloadAsJAXB(paramUnmarshaller); }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)this.delegate.readPayloadAsJAXB(paramBridge); }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException { return (T)this.delegate.readPayloadAsJAXB(paramXMLBridge); }
  
  public XMLStreamReader readPayload() {
    try {
      return this.delegate.readPayload();
    } catch (XMLStreamException xMLStreamException) {
      xMLStreamException.printStackTrace();
      return null;
    } 
  }
  
  public void consume() { this.delegate.consume(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.delegate.writePayloadTo(paramXMLStreamWriter); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.delegate.writeTo(paramXMLStreamWriter); }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.delegate.writeTo(paramContentHandler, paramErrorHandler); }
  
  public Message copy() { return this.delegate.copy(); }
  
  public String getID(WSBinding paramWSBinding) { return this.delegate.getID(paramWSBinding); }
  
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion) { return this.delegate.getID(paramAddressingVersion, paramSOAPVersion); }
  
  public SOAPVersion getSOAPVersion() { return this.delegate.getSOAPVersion(); }
  
  @NotNull
  public MessageHeaders getHeaders() { return this.delegate.getHeaders(); }
  
  public void setMessageMedadata(MessageMetadata paramMessageMetadata) {
    super.setMessageMedadata(paramMessageMetadata);
    this.delegate.setMessageMedadata(paramMessageMetadata);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\MessageWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */