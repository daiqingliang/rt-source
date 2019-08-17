package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
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

public class FilterMessageImpl extends Message {
  private final Message delegate;
  
  protected FilterMessageImpl(Message paramMessage) { this.delegate = paramMessage; }
  
  public boolean hasHeaders() { return this.delegate.hasHeaders(); }
  
  @NotNull
  public MessageHeaders getHeaders() { return this.delegate.getHeaders(); }
  
  @NotNull
  public AttachmentSet getAttachments() { return this.delegate.getAttachments(); }
  
  protected boolean hasAttachments() { return this.delegate.hasAttachments(); }
  
  public boolean isOneWay(@NotNull WSDLPort paramWSDLPort) { return this.delegate.isOneWay(paramWSDLPort); }
  
  @Nullable
  public String getPayloadLocalPart() { return this.delegate.getPayloadLocalPart(); }
  
  public String getPayloadNamespaceURI() { return this.delegate.getPayloadNamespaceURI(); }
  
  public boolean hasPayload() { return this.delegate.hasPayload(); }
  
  public boolean isFault() { return this.delegate.isFault(); }
  
  @Nullable
  public QName getFirstDetailEntryName() { return this.delegate.getFirstDetailEntryName(); }
  
  public Source readEnvelopeAsSource() { return this.delegate.readEnvelopeAsSource(); }
  
  public Source readPayloadAsSource() { return this.delegate.readPayloadAsSource(); }
  
  public SOAPMessage readAsSOAPMessage() throws SOAPException { return this.delegate.readAsSOAPMessage(); }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean) throws SOAPException { return this.delegate.readAsSOAPMessage(paramPacket, paramBoolean); }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException { return (T)this.delegate.readPayloadAsJAXB(paramUnmarshaller); }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)this.delegate.readPayloadAsJAXB(paramBridge); }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException { return (T)this.delegate.readPayloadAsJAXB(paramXMLBridge); }
  
  public XMLStreamReader readPayload() throws XMLStreamException { return this.delegate.readPayload(); }
  
  public void consume() { this.delegate.consume(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.delegate.writePayloadTo(paramXMLStreamWriter); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.delegate.writeTo(paramXMLStreamWriter); }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.delegate.writeTo(paramContentHandler, paramErrorHandler); }
  
  public Message copy() { return this.delegate.copy(); }
  
  @NotNull
  public String getID(@NotNull WSBinding paramWSBinding) { return this.delegate.getID(paramWSBinding); }
  
  @NotNull
  public String getID(AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion) { return this.delegate.getID(paramAddressingVersion, paramSOAPVersion); }
  
  public SOAPVersion getSOAPVersion() { return this.delegate.getSOAPVersion(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\FilterMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */