package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class JAXBMessage extends AbstractMessageImpl implements StreamingSOAP {
  private MessageHeaders headers;
  
  private final Object jaxbObject;
  
  private final XMLBridge bridge;
  
  private final JAXBContext rawContext;
  
  private String nsUri;
  
  private String localName;
  
  private XMLStreamBuffer infoset;
  
  public static Message create(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet) {
    if (!paramBindingContext.hasSwaRef())
      return new JAXBMessage(paramBindingContext, paramObject, paramSOAPVersion, paramMessageHeaders, paramAttachmentSet); 
    try {
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      Marshaller marshaller = paramBindingContext.createMarshaller();
      AttachmentMarshallerImpl attachmentMarshallerImpl = new AttachmentMarshallerImpl(paramAttachmentSet);
      marshaller.setAttachmentMarshaller(attachmentMarshallerImpl);
      attachmentMarshallerImpl.cleanup();
      marshaller.marshal(paramObject, mutableXMLStreamBuffer.createFromXMLStreamWriter());
      return new StreamMessage(paramMessageHeaders, paramAttachmentSet, mutableXMLStreamBuffer.readAsXMLStreamReader(), paramSOAPVersion);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  public static Message create(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion) { return create(paramBindingContext, paramObject, paramSOAPVersion, null, null); }
  
  public static Message create(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion) { return create(BindingContextFactory.create(paramJAXBContext), paramObject, paramSOAPVersion, null, null); }
  
  public static Message createRaw(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion) { return new JAXBMessage(paramJAXBContext, paramObject, paramSOAPVersion, null, null); }
  
  private JAXBMessage(BindingContext paramBindingContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet) {
    super(paramSOAPVersion);
    this.bridge = paramBindingContext.createFragmentBridge();
    this.rawContext = null;
    this.jaxbObject = paramObject;
    this.headers = paramMessageHeaders;
    this.attachmentSet = paramAttachmentSet;
  }
  
  private JAXBMessage(JAXBContext paramJAXBContext, Object paramObject, SOAPVersion paramSOAPVersion, MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet) {
    super(paramSOAPVersion);
    this.rawContext = paramJAXBContext;
    this.bridge = null;
    this.jaxbObject = paramObject;
    this.headers = paramMessageHeaders;
    this.attachmentSet = paramAttachmentSet;
  }
  
  public static Message create(XMLBridge paramXMLBridge, Object paramObject, SOAPVersion paramSOAPVersion) {
    if (!paramXMLBridge.context().hasSwaRef())
      return new JAXBMessage(paramXMLBridge, paramObject, paramSOAPVersion); 
    try {
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      AttachmentSetImpl attachmentSetImpl = new AttachmentSetImpl();
      AttachmentMarshallerImpl attachmentMarshallerImpl = new AttachmentMarshallerImpl(attachmentSetImpl);
      paramXMLBridge.marshal(paramObject, mutableXMLStreamBuffer.createFromXMLStreamWriter(), attachmentMarshallerImpl);
      attachmentMarshallerImpl.cleanup();
      return new StreamMessage(null, attachmentSetImpl, mutableXMLStreamBuffer.readAsXMLStreamReader(), paramSOAPVersion);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  private JAXBMessage(XMLBridge paramXMLBridge, Object paramObject, SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.bridge = paramXMLBridge;
    this.rawContext = null;
    this.jaxbObject = paramObject;
    QName qName = (paramXMLBridge.getTypeInfo()).tagName;
    this.nsUri = qName.getNamespaceURI();
    this.localName = qName.getLocalPart();
    this.attachmentSet = new AttachmentSetImpl();
  }
  
  public JAXBMessage(JAXBMessage paramJAXBMessage) {
    super(paramJAXBMessage);
    this.headers = paramJAXBMessage.headers;
    if (this.headers != null)
      this.headers = new HeaderList(this.headers); 
    this.attachmentSet = paramJAXBMessage.attachmentSet;
    this.jaxbObject = paramJAXBMessage.jaxbObject;
    this.bridge = paramJAXBMessage.bridge;
    this.rawContext = paramJAXBMessage.rawContext;
  }
  
  public boolean hasHeaders() { return (this.headers != null && this.headers.hasHeaders()); }
  
  public MessageHeaders getHeaders() {
    if (this.headers == null)
      this.headers = new HeaderList(getSOAPVersion()); 
    return this.headers;
  }
  
  public String getPayloadLocalPart() {
    if (this.localName == null)
      sniff(); 
    return this.localName;
  }
  
  public String getPayloadNamespaceURI() {
    if (this.nsUri == null)
      sniff(); 
    return this.nsUri;
  }
  
  public boolean hasPayload() { return true; }
  
  private void sniff() {
    RootElementSniffer rootElementSniffer = new RootElementSniffer(false);
    try {
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        marshaller.marshal(this.jaxbObject, rootElementSniffer);
      } else {
        this.bridge.marshal(this.jaxbObject, rootElementSniffer, null);
      } 
    } catch (JAXBException jAXBException) {
      this.nsUri = rootElementSniffer.getNsUri();
      this.localName = rootElementSniffer.getLocalName();
    } 
  }
  
  public Source readPayloadAsSource() { return new JAXBBridgeSource(this.bridge, this.jaxbObject); }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    JAXBResult jAXBResult = new JAXBResult(paramUnmarshaller);
    try {
      jAXBResult.getHandler().startDocument();
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        marshaller.marshal(this.jaxbObject, jAXBResult);
      } else {
        this.bridge.marshal(this.jaxbObject, jAXBResult);
      } 
      jAXBResult.getHandler().endDocument();
    } catch (SAXException sAXException) {
      throw new JAXBException(sAXException);
    } 
    return (T)jAXBResult.getResult();
  }
  
  public XMLStreamReader readPayload() throws XMLStreamException {
    try {
      if (this.infoset == null)
        if (this.rawContext != null) {
          XMLStreamBufferResult xMLStreamBufferResult = new XMLStreamBufferResult();
          Marshaller marshaller = this.rawContext.createMarshaller();
          marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
          marshaller.marshal(this.jaxbObject, xMLStreamBufferResult);
          this.infoset = xMLStreamBufferResult.getXMLStreamBuffer();
        } else {
          MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
          writePayloadTo(mutableXMLStreamBuffer.createFromXMLStreamWriter());
          this.infoset = mutableXMLStreamBuffer;
        }  
      StreamReaderBufferProcessor streamReaderBufferProcessor = this.infoset.readAsXMLStreamReader();
      if (streamReaderBufferProcessor.getEventType() == 7)
        XMLStreamReaderUtil.nextElementContent(streamReaderBufferProcessor); 
      return streamReaderBufferProcessor;
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
  
  protected void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    try {
      if (paramBoolean)
        paramContentHandler = new FragmentContentHandler(paramContentHandler); 
      AttachmentMarshallerImpl attachmentMarshallerImpl = new AttachmentMarshallerImpl(this.attachmentSet);
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        marshaller.setAttachmentMarshaller(attachmentMarshallerImpl);
        marshaller.marshal(this.jaxbObject, paramContentHandler);
      } else {
        this.bridge.marshal(this.jaxbObject, paramContentHandler, attachmentMarshallerImpl);
      } 
      attachmentMarshallerImpl.cleanup();
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException.getMessage(), jAXBException);
    } 
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    try {
      AttachmentMarshaller attachmentMarshaller = (paramXMLStreamWriter instanceof MtomStreamWriter) ? ((MtomStreamWriter)paramXMLStreamWriter).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
      String str = XMLStreamWriterUtil.getEncoding(paramXMLStreamWriter);
      OutputStream outputStream = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(paramXMLStreamWriter) : null;
      if (this.rawContext != null) {
        Marshaller marshaller = this.rawContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
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
  
  public Message copy() { return new JAXBMessage(this); }
  
  public XMLStreamReader readEnvelope() throws XMLStreamException {
    int i = this.soapVersion.ordinal() * 3;
    this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(i);
    this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(i + 2);
    ArrayList arrayList = new ArrayList();
    XMLReaderComposite.ElemInfo elemInfo1 = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
    XMLReaderComposite.ElemInfo elemInfo2 = new XMLReaderComposite.ElemInfo(this.bodyTag, elemInfo1);
    for (Header header : getHeaders().asList()) {
      try {
        arrayList.add(header.readHeader());
      } catch (XMLStreamException xMLStreamException) {
        throw new RuntimeException(xMLStreamException);
      } 
    } 
    XMLReaderComposite xMLReaderComposite = null;
    if (arrayList.size() > 0) {
      this.headerTag = (TagInfoset)DEFAULT_TAGS.get(i + 1);
      XMLReaderComposite.ElemInfo elemInfo = new XMLReaderComposite.ElemInfo(this.headerTag, elemInfo1);
      xMLReaderComposite = new XMLReaderComposite(elemInfo, (XMLStreamReader[])arrayList.toArray(new XMLStreamReader[arrayList.size()]));
    } 
    try {
      XMLStreamReader xMLStreamReader = readPayload();
      XMLReaderComposite xMLReaderComposite1 = new XMLReaderComposite(elemInfo2, new XMLStreamReader[] { xMLStreamReader });
      new XMLStreamReader[2][0] = xMLReaderComposite;
      new XMLStreamReader[2][1] = xMLReaderComposite1;
      new XMLStreamReader[1][0] = xMLReaderComposite1;
      XMLStreamReader[] arrayOfXMLStreamReader = (xMLReaderComposite != null) ? new XMLStreamReader[2] : new XMLStreamReader[1];
      return new XMLReaderComposite(elemInfo1, arrayOfXMLStreamReader);
    } catch (XMLStreamException xMLStreamException) {
      throw new RuntimeException(xMLStreamException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */