package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.PayloadElementSniffer;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class JAXBDispatchMessage extends AbstractMessageImpl {
  private final Object jaxbObject;
  
  private final XMLBridge bridge;
  
  private final JAXBContext rawContext;
  
  private QName payloadQName;
  
  private JAXBDispatchMessage(JAXBDispatchMessage paramJAXBDispatchMessage) {
    super(paramJAXBDispatchMessage);
    this.jaxbObject = paramJAXBDispatchMessage.jaxbObject;
    this.rawContext = paramJAXBDispatchMessage.rawContext;
    this.bridge = paramJAXBDispatchMessage.bridge;
  }
  
  public JAXBDispatchMessage(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.bridge = null;
    this.rawContext = paramJAXBContext;
    this.jaxbObject = paramObject;
  }
  
  public JAXBDispatchMessage(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.bridge = paramBindingContext.createFragmentBridge();
    this.rawContext = null;
    this.jaxbObject = paramObject;
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException { throw new UnsupportedOperationException(); }
  
  public boolean hasHeaders() { return false; }
  
  public MessageHeaders getHeaders() { return null; }
  
  public String getPayloadLocalPart() {
    if (this.payloadQName == null)
      readPayloadElement(); 
    return this.payloadQName.getLocalPart();
  }
  
  public String getPayloadNamespaceURI() {
    if (this.payloadQName == null)
      readPayloadElement(); 
    return this.payloadQName.getNamespaceURI();
  }
  
  private void readPayloadElement() {
    PayloadElementSniffer payloadElementSniffer = new PayloadElementSniffer();
    try {
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.FALSE);
        marshaller.marshal(this.jaxbObject, payloadElementSniffer);
      } else {
        this.bridge.marshal(this.jaxbObject, payloadElementSniffer, null);
      } 
    } catch (JAXBException jAXBException) {
      this.payloadQName = payloadElementSniffer.getPayloadQName();
    } 
  }
  
  public boolean hasPayload() { return true; }
  
  public Source readPayloadAsSource() { throw new UnsupportedOperationException(); }
  
  public XMLStreamReader readPayload() throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public Message copy() { return new JAXBDispatchMessage(this); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    try {
      AttachmentMarshaller attachmentMarshaller = (paramXMLStreamWriter instanceof MtomStreamWriter) ? ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream outputStream = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.FALSE);
        marshaller.setAttachmentMarshaller(attachmentMarshaller);
        if (outputStream != null) {
          marshaller.marshal(this.jaxbObject, outputStream);
        } else {
          marshaller.marshal(this.jaxbObject, paramXMLStreamWriter);
        } 
      } else if (outputStream != null && str != null && str.equalsIgnoreCase("utf-8")) {
        this.bridge.marshal(this.jaxbObject, outputStream, paramXMLStreamWriter.getNamespaceContext(), attachmentMarshaller);
      } else {
        this.bridge.marshal(this.jaxbObject, paramXMLStreamWriter, attachmentMarshaller);
      } 
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBDispatchMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */