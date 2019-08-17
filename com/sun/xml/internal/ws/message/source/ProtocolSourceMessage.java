package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ProtocolSourceMessage extends Message {
  private final Message sm;
  
  public ProtocolSourceMessage(Source paramSource, SOAPVersion paramSOAPVersion) {
    XMLStreamReader xMLStreamReader = SourceReaderFactory.createSourceReader(paramSource, true);
    StreamSOAPCodec streamSOAPCodec = Codecs.createSOAPEnvelopeXmlCodec(paramSOAPVersion);
    this.sm = streamSOAPCodec.decode(xMLStreamReader);
  }
  
  public boolean hasHeaders() { return this.sm.hasHeaders(); }
  
  public String getPayloadLocalPart() { return this.sm.getPayloadLocalPart(); }
  
  public String getPayloadNamespaceURI() { return this.sm.getPayloadNamespaceURI(); }
  
  public boolean hasPayload() { return this.sm.hasPayload(); }
  
  public Source readPayloadAsSource() { return this.sm.readPayloadAsSource(); }
  
  public XMLStreamReader readPayload() throws XMLStreamException { return this.sm.readPayload(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.sm.writePayloadTo(paramXMLStreamWriter); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.sm.writeTo(paramXMLStreamWriter); }
  
  public Message copy() { return this.sm.copy(); }
  
  public Source readEnvelopeAsSource() { return this.sm.readEnvelopeAsSource(); }
  
  public SOAPMessage readAsSOAPMessage() throws SOAPException { return this.sm.readAsSOAPMessage(); }
  
  public SOAPMessage readAsSOAPMessage(Packet paramPacket, boolean paramBoolean) throws SOAPException { return this.sm.readAsSOAPMessage(paramPacket, paramBoolean); }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException { return (T)this.sm.readPayloadAsJAXB(paramUnmarshaller); }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException { return (T)this.sm.readPayloadAsJAXB(paramBridge); }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException { return (T)this.sm.readPayloadAsJAXB(paramXMLBridge); }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.sm.writeTo(paramContentHandler, paramErrorHandler); }
  
  public SOAPVersion getSOAPVersion() { return this.sm.getSOAPVersion(); }
  
  public MessageHeaders getHeaders() { return this.sm.getHeaders(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\source\ProtocolSourceMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */