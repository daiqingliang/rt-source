package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class EmptyMessageImpl extends AbstractMessageImpl {
  private final MessageHeaders headers;
  
  private final AttachmentSet attachmentSet;
  
  public EmptyMessageImpl(SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.headers = new HeaderList(paramSOAPVersion);
    this.attachmentSet = new AttachmentSetImpl();
  }
  
  public EmptyMessageImpl(MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    if (paramMessageHeaders == null)
      paramMessageHeaders = new HeaderList(paramSOAPVersion); 
    this.attachmentSet = paramAttachmentSet;
    this.headers = paramMessageHeaders;
  }
  
  private EmptyMessageImpl(EmptyMessageImpl paramEmptyMessageImpl) {
    super(paramEmptyMessageImpl);
    this.headers = new HeaderList(paramEmptyMessageImpl.headers);
    this.attachmentSet = paramEmptyMessageImpl.attachmentSet;
  }
  
  public boolean hasHeaders() { return this.headers.hasHeaders(); }
  
  public MessageHeaders getHeaders() { return this.headers; }
  
  public String getPayloadLocalPart() { return null; }
  
  public String getPayloadNamespaceURI() { return null; }
  
  public boolean hasPayload() { return false; }
  
  public Source readPayloadAsSource() { return null; }
  
  public XMLStreamReader readPayload() throws XMLStreamException { return null; }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {}
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {}
  
  public Message copy() { return new EmptyMessageImpl(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\EmptyMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */