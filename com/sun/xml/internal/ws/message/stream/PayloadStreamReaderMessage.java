package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class PayloadStreamReaderMessage extends AbstractMessageImpl {
  private final StreamMessage message;
  
  public PayloadStreamReaderMessage(XMLStreamReader paramXMLStreamReader, SOAPVersion paramSOAPVersion) { this(null, paramXMLStreamReader, new AttachmentSetImpl(), paramSOAPVersion); }
  
  public PayloadStreamReaderMessage(@Nullable MessageHeaders paramMessageHeaders, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet, @NotNull SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.message = new StreamMessage(paramMessageHeaders, paramAttachmentSet, paramXMLStreamReader, paramSOAPVersion);
  }
  
  public boolean hasHeaders() { return this.message.hasHeaders(); }
  
  public AttachmentSet getAttachments() { return this.message.getAttachments(); }
  
  public String getPayloadLocalPart() { return this.message.getPayloadLocalPart(); }
  
  public String getPayloadNamespaceURI() { return this.message.getPayloadNamespaceURI(); }
  
  public boolean hasPayload() { return true; }
  
  public Source readPayloadAsSource() { return this.message.readPayloadAsSource(); }
  
  public XMLStreamReader readPayload() throws XMLStreamException { return this.message.readPayload(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.message.writePayloadTo(paramXMLStreamWriter); }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException { return (T)this.message.readPayloadAsJAXB(paramUnmarshaller); }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { this.message.writeTo(paramContentHandler, paramErrorHandler); }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException { this.message.writePayloadTo(paramContentHandler, paramErrorHandler, paramBoolean); }
  
  public Message copy() { return this.message.copy(); }
  
  @NotNull
  public MessageHeaders getHeaders() { return this.message.getHeaders(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\stream\PayloadStreamReaderMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */