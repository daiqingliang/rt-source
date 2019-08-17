package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.FragmentContentHandler;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import com.sun.xml.internal.ws.util.ASCIIUtility;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public class SAAJMessage extends Message {
  private boolean parsedMessage;
  
  private boolean accessedMessage;
  
  private final SOAPMessage sm;
  
  private MessageHeaders headers;
  
  private List<Element> bodyParts;
  
  private Element payload;
  
  private String payloadLocalName;
  
  private String payloadNamespace;
  
  private SOAPVersion soapVersion;
  
  private NamedNodeMap bodyAttrs;
  
  private NamedNodeMap headerAttrs;
  
  private NamedNodeMap envelopeAttrs;
  
  private static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
  
  private static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
  
  private XMLStreamReader soapBodyFirstChildReader;
  
  private SOAPElement soapBodyFirstChild;
  
  public SAAJMessage(SOAPMessage paramSOAPMessage) { this.sm = paramSOAPMessage; }
  
  private SAAJMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, SOAPMessage paramSOAPMessage, SOAPVersion paramSOAPVersion) {
    this.sm = paramSOAPMessage;
    parse();
    if (paramMessageHeaders == null)
      paramMessageHeaders = new HeaderList(paramSOAPVersion); 
    this.headers = paramMessageHeaders;
    this.attachmentSet = paramAttachmentSet;
  }
  
  private void parse() {
    if (!this.parsedMessage)
      try {
        access();
        if (this.headers == null)
          this.headers = new HeaderList(getSOAPVersion()); 
        SOAPHeader sOAPHeader = this.sm.getSOAPHeader();
        if (sOAPHeader != null) {
          this.headerAttrs = sOAPHeader.getAttributes();
          Iterator iterator = sOAPHeader.examineAllHeaderElements();
          while (iterator.hasNext())
            this.headers.add(new SAAJHeader((SOAPHeaderElement)iterator.next())); 
        } 
        this.attachmentSet = new SAAJAttachmentSet(this.sm);
        this.parsedMessage = true;
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      }  
  }
  
  protected void access() {
    if (!this.accessedMessage)
      try {
        this.envelopeAttrs = this.sm.getSOAPPart().getEnvelope().getAttributes();
        SOAPBody sOAPBody = this.sm.getSOAPBody();
        this.bodyAttrs = sOAPBody.getAttributes();
        this.soapVersion = SOAPVersion.fromNsUri(sOAPBody.getNamespaceURI());
        this.bodyParts = DOMUtil.getChildElements(sOAPBody);
        this.payload = (this.bodyParts.size() > 0) ? (Element)this.bodyParts.get(0) : null;
        if (this.payload != null) {
          this.payloadLocalName = this.payload.getLocalName();
          this.payloadNamespace = this.payload.getNamespaceURI();
        } 
        this.accessedMessage = true;
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      }  
  }
  
  public boolean hasHeaders() {
    parse();
    return this.headers.hasHeaders();
  }
  
  @NotNull
  public MessageHeaders getHeaders() {
    parse();
    return this.headers;
  }
  
  @NotNull
  public AttachmentSet getAttachments() {
    if (this.attachmentSet == null)
      this.attachmentSet = new SAAJAttachmentSet(this.sm); 
    return this.attachmentSet;
  }
  
  protected boolean hasAttachments() { return !getAttachments().isEmpty(); }
  
  @Nullable
  public String getPayloadLocalPart() {
    soapBodyFirstChild();
    return this.payloadLocalName;
  }
  
  public String getPayloadNamespaceURI() {
    soapBodyFirstChild();
    return this.payloadNamespace;
  }
  
  public boolean hasPayload() { return (soapBodyFirstChild() != null); }
  
  private void addAttributes(Element paramElement, NamedNodeMap paramNamedNodeMap) {
    if (paramNamedNodeMap == null)
      return; 
    String str = paramElement.getPrefix();
    for (byte b = 0; b < paramNamedNodeMap.getLength(); b++) {
      Attr attr = (Attr)paramNamedNodeMap.item(b);
      if ("xmlns".equals(attr.getPrefix()) || "xmlns".equals(attr.getLocalName())) {
        if ((str != null || !attr.getLocalName().equals("xmlns")) && (str == null || !"xmlns".equals(attr.getPrefix()) || !str.equals(attr.getLocalName())))
          paramElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue()); 
      } else {
        paramElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
      } 
    } 
  }
  
  public Source readEnvelopeAsSource() {
    try {
      if (!this.parsedMessage) {
        SOAPEnvelope sOAPEnvelope1 = this.sm.getSOAPPart().getEnvelope();
        return new DOMSource(sOAPEnvelope1);
      } 
      SOAPMessage sOAPMessage = this.soapVersion.getMessageFactory().createMessage();
      addAttributes(sOAPMessage.getSOAPPart().getEnvelope(), this.envelopeAttrs);
      SOAPBody sOAPBody = sOAPMessage.getSOAPPart().getEnvelope().getBody();
      addAttributes(sOAPBody, this.bodyAttrs);
      for (Element element : this.bodyParts) {
        Node node = sOAPBody.getOwnerDocument().importNode(element, true);
        sOAPBody.appendChild(node);
      } 
      addAttributes(sOAPMessage.getSOAPHeader(), this.headerAttrs);
      for (Header header : this.headers.asList())
        header.writeTo(sOAPMessage); 
      SOAPEnvelope sOAPEnvelope = sOAPMessage.getSOAPPart().getEnvelope();
      return new DOMSource(sOAPEnvelope);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  public SOAPMessage readAsSOAPMessage() throws SOAPException {
    if (!this.parsedMessage)
      return this.sm; 
    SOAPMessage sOAPMessage = this.soapVersion.getMessageFactory().createMessage();
    addAttributes(sOAPMessage.getSOAPPart().getEnvelope(), this.envelopeAttrs);
    SOAPBody sOAPBody = sOAPMessage.getSOAPPart().getEnvelope().getBody();
    addAttributes(sOAPBody, this.bodyAttrs);
    for (Element element : this.bodyParts) {
      Node node = sOAPBody.getOwnerDocument().importNode(element, true);
      sOAPBody.appendChild(node);
    } 
    addAttributes(sOAPMessage.getSOAPHeader(), this.headerAttrs);
    for (Header header : this.headers.asList())
      header.writeTo(sOAPMessage); 
    for (Attachment attachment : getAttachments()) {
      AttachmentPart attachmentPart = sOAPMessage.createAttachmentPart();
      attachmentPart.setDataHandler(attachment.asDataHandler());
      attachmentPart.setContentId('<' + attachment.getContentId() + '>');
      addCustomMimeHeaders(attachment, attachmentPart);
      sOAPMessage.addAttachmentPart(attachmentPart);
    } 
    sOAPMessage.saveChanges();
    return sOAPMessage;
  }
  
  private void addCustomMimeHeaders(Attachment paramAttachment, AttachmentPart paramAttachmentPart) {
    if (paramAttachment instanceof AttachmentEx) {
      Iterator iterator = ((AttachmentEx)paramAttachment).getMimeHeaders();
      while (iterator.hasNext()) {
        AttachmentEx.MimeHeader mimeHeader = (AttachmentEx.MimeHeader)iterator.next();
        String str = mimeHeader.getName();
        if (!"Content-Type".equalsIgnoreCase(str) && !"Content-Id".equalsIgnoreCase(str))
          paramAttachmentPart.addMimeHeader(str, mimeHeader.getValue()); 
      } 
    } 
  }
  
  public Source readPayloadAsSource() {
    access();
    return (this.payload != null) ? new DOMSource(this.payload) : null;
  }
  
  public <T> T readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    access();
    if (this.payload != null) {
      if (hasAttachments())
        paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments())); 
      return (T)paramUnmarshaller.unmarshal(this.payload);
    } 
    return null;
  }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException {
    access();
    return (this.payload != null) ? (T)paramBridge.unmarshal(this.payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null) : null;
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException {
    access();
    return (this.payload != null) ? (T)paramXMLBridge.unmarshal(this.payload, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null) : null;
  }
  
  public XMLStreamReader readPayload() throws XMLStreamException { return soapBodyFirstChildReader(); }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    access();
    try {
      for (Element element : this.bodyParts)
        DOMUtil.serializeNode(element, paramXMLStreamWriter); 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    try {
      paramXMLStreamWriter.writeStartDocument();
      if (!this.parsedMessage) {
        DOMUtil.serializeNode(this.sm.getSOAPPart().getEnvelope(), paramXMLStreamWriter);
      } else {
        SOAPEnvelope sOAPEnvelope = this.sm.getSOAPPart().getEnvelope();
        DOMUtil.writeTagWithAttributes(sOAPEnvelope, paramXMLStreamWriter);
        if (hasHeaders()) {
          if (sOAPEnvelope.getHeader() != null) {
            DOMUtil.writeTagWithAttributes(sOAPEnvelope.getHeader(), paramXMLStreamWriter);
          } else {
            paramXMLStreamWriter.writeStartElement(sOAPEnvelope.getPrefix(), "Header", sOAPEnvelope.getNamespaceURI());
          } 
          for (Header header : this.headers.asList())
            header.writeTo(paramXMLStreamWriter); 
          paramXMLStreamWriter.writeEndElement();
        } 
        DOMUtil.serializeNode(this.sm.getSOAPBody(), paramXMLStreamWriter);
        paramXMLStreamWriter.writeEndElement();
      } 
      paramXMLStreamWriter.writeEndDocument();
      paramXMLStreamWriter.flush();
    } catch (SOAPException sOAPException) {
      throw new XMLStreamException2(sOAPException);
    } 
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    String str = this.soapVersion.nsUri;
    if (!this.parsedMessage) {
      DOMScanner dOMScanner = new DOMScanner();
      dOMScanner.setContentHandler(paramContentHandler);
      dOMScanner.scan(this.sm.getSOAPPart());
    } else {
      paramContentHandler.setDocumentLocator(NULL_LOCATOR);
      paramContentHandler.startDocument();
      paramContentHandler.startPrefixMapping("S", str);
      startPrefixMapping(paramContentHandler, this.envelopeAttrs, "S");
      paramContentHandler.startElement(str, "Envelope", "S:Envelope", getAttributes(this.envelopeAttrs));
      if (hasHeaders()) {
        startPrefixMapping(paramContentHandler, this.headerAttrs, "S");
        paramContentHandler.startElement(str, "Header", "S:Header", getAttributes(this.headerAttrs));
        MessageHeaders messageHeaders = getHeaders();
        for (Header header : messageHeaders.asList())
          header.writeTo(paramContentHandler, paramErrorHandler); 
        endPrefixMapping(paramContentHandler, this.headerAttrs, "S");
        paramContentHandler.endElement(str, "Header", "S:Header");
      } 
      startPrefixMapping(paramContentHandler, this.bodyAttrs, "S");
      paramContentHandler.startElement(str, "Body", "S:Body", getAttributes(this.bodyAttrs));
      writePayloadTo(paramContentHandler, paramErrorHandler, true);
      endPrefixMapping(paramContentHandler, this.bodyAttrs, "S");
      paramContentHandler.endElement(str, "Body", "S:Body");
      endPrefixMapping(paramContentHandler, this.envelopeAttrs, "S");
      paramContentHandler.endElement(str, "Envelope", "S:Envelope");
    } 
  }
  
  private AttributesImpl getAttributes(NamedNodeMap paramNamedNodeMap) {
    AttributesImpl attributesImpl = new AttributesImpl();
    if (paramNamedNodeMap == null)
      return EMPTY_ATTS; 
    for (byte b = 0; b < paramNamedNodeMap.getLength(); b++) {
      Attr attr = (Attr)paramNamedNodeMap.item(b);
      if (!"xmlns".equals(attr.getPrefix()) && !"xmlns".equals(attr.getLocalName()))
        attributesImpl.addAttribute(fixNull(attr.getNamespaceURI()), attr.getLocalName(), attr.getName(), attr.getSchemaTypeInfo().getTypeName(), attr.getValue()); 
    } 
    return attributesImpl;
  }
  
  private void startPrefixMapping(ContentHandler paramContentHandler, NamedNodeMap paramNamedNodeMap, String paramString) throws SAXException {
    if (paramNamedNodeMap == null)
      return; 
    for (byte b = 0; b < paramNamedNodeMap.getLength(); b++) {
      Attr attr = (Attr)paramNamedNodeMap.item(b);
      if (("xmlns".equals(attr.getPrefix()) || "xmlns".equals(attr.getLocalName())) && !fixNull(attr.getPrefix()).equals(paramString))
        paramContentHandler.startPrefixMapping(fixNull(attr.getPrefix()), attr.getNamespaceURI()); 
    } 
  }
  
  private void endPrefixMapping(ContentHandler paramContentHandler, NamedNodeMap paramNamedNodeMap, String paramString) throws SAXException {
    if (paramNamedNodeMap == null)
      return; 
    for (byte b = 0; b < paramNamedNodeMap.getLength(); b++) {
      Attr attr = (Attr)paramNamedNodeMap.item(b);
      if (("xmlns".equals(attr.getPrefix()) || "xmlns".equals(attr.getLocalName())) && !fixNull(attr.getPrefix()).equals(paramString))
        paramContentHandler.endPrefixMapping(fixNull(attr.getPrefix())); 
    } 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  private void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    if (paramBoolean)
      paramContentHandler = new FragmentContentHandler(paramContentHandler); 
    DOMScanner dOMScanner = new DOMScanner();
    dOMScanner.setContentHandler(paramContentHandler);
    dOMScanner.scan(this.payload);
  }
  
  public Message copy() {
    try {
      if (!this.parsedMessage)
        return new SAAJMessage(readAsSOAPMessage()); 
      SOAPMessage sOAPMessage = this.soapVersion.getMessageFactory().createMessage();
      SOAPBody sOAPBody = sOAPMessage.getSOAPPart().getEnvelope().getBody();
      for (Element element : this.bodyParts) {
        Node node = sOAPBody.getOwnerDocument().importNode(element, true);
        sOAPBody.appendChild(node);
      } 
      addAttributes(sOAPBody, this.bodyAttrs);
      return new SAAJMessage(getHeaders(), getAttachments(), sOAPMessage, this.soapVersion);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  public SOAPVersion getSOAPVersion() { return this.soapVersion; }
  
  protected XMLStreamReader getXMLStreamReader(SOAPElement paramSOAPElement) { return null; }
  
  protected XMLStreamReader createXMLStreamReader(SOAPElement paramSOAPElement) {
    DOMStreamReader dOMStreamReader = new DOMStreamReader();
    dOMStreamReader.setCurrentNode(paramSOAPElement);
    return dOMStreamReader;
  }
  
  protected XMLStreamReader soapBodyFirstChildReader() throws XMLStreamException {
    if (this.soapBodyFirstChildReader != null)
      return this.soapBodyFirstChildReader; 
    soapBodyFirstChild();
    if (this.soapBodyFirstChild != null) {
      this.soapBodyFirstChildReader = getXMLStreamReader(this.soapBodyFirstChild);
      if (this.soapBodyFirstChildReader == null)
        this.soapBodyFirstChildReader = createXMLStreamReader(this.soapBodyFirstChild); 
      if (this.soapBodyFirstChildReader.getEventType() == 7)
        try {
          while (this.soapBodyFirstChildReader.getEventType() != 1)
            this.soapBodyFirstChildReader.next(); 
        } catch (XMLStreamException xMLStreamException) {
          throw new RuntimeException(xMLStreamException);
        }  
      return this.soapBodyFirstChildReader;
    } 
    this.payloadLocalName = null;
    this.payloadNamespace = null;
    return null;
  }
  
  SOAPElement soapBodyFirstChild() {
    if (this.soapBodyFirstChild != null)
      return this.soapBodyFirstChild; 
    try {
      boolean bool = false;
      for (Node node = this.sm.getSOAPBody().getFirstChild(); node != null && !bool; node = node.getNextSibling()) {
        if (node.getNodeType() == 1) {
          bool = true;
          if (node instanceof SOAPElement) {
            this.soapBodyFirstChild = (SOAPElement)node;
            this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
            this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
            return this.soapBodyFirstChild;
          } 
        } 
      } 
      if (bool) {
        Iterator iterator = this.sm.getSOAPBody().getChildElements();
        while (iterator.hasNext()) {
          Object object = iterator.next();
          if (object instanceof SOAPElement) {
            this.soapBodyFirstChild = (SOAPElement)object;
            this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
            this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
            return this.soapBodyFirstChild;
          } 
        } 
      } 
    } catch (SOAPException sOAPException) {
      throw new RuntimeException(sOAPException);
    } 
    return this.soapBodyFirstChild;
  }
  
  protected static class SAAJAttachment implements AttachmentEx {
    final AttachmentPart ap;
    
    String contentIdNoAngleBracket;
    
    public SAAJAttachment(AttachmentPart param1AttachmentPart) { this.ap = param1AttachmentPart; }
    
    public String getContentId() {
      if (this.contentIdNoAngleBracket == null) {
        this.contentIdNoAngleBracket = this.ap.getContentId();
        if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<')
          this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1); 
      } 
      return this.contentIdNoAngleBracket;
    }
    
    public String getContentType() { return this.ap.getContentType(); }
    
    public byte[] asByteArray() {
      try {
        return this.ap.getRawContentBytes();
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public DataHandler asDataHandler() {
      try {
        return this.ap.getDataHandler();
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public Source asSource() {
      try {
        return new StreamSource(this.ap.getRawContent());
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public InputStream asInputStream() {
      try {
        return this.ap.getRawContent();
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public void writeTo(OutputStream param1OutputStream) throws IOException {
      try {
        ASCIIUtility.copyStream(this.ap.getRawContent(), param1OutputStream);
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public void writeTo(SOAPMessage param1SOAPMessage) { param1SOAPMessage.addAttachmentPart(this.ap); }
    
    AttachmentPart asAttachmentPart() { return this.ap; }
    
    public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
      final Iterator it = this.ap.getAllMimeHeaders();
      return new Iterator<AttachmentEx.MimeHeader>() {
          public boolean hasNext() { return it.hasNext(); }
          
          public AttachmentEx.MimeHeader next() {
            final MimeHeader mh = (MimeHeader)it.next();
            return new AttachmentEx.MimeHeader() {
                public String getName() { return mh.getName(); }
                
                public String getValue() { return mh.getValue(); }
              };
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        };
    }
  }
  
  protected static class SAAJAttachmentSet implements AttachmentSet {
    private Map<String, Attachment> attMap;
    
    private Iterator attIter;
    
    public SAAJAttachmentSet(SOAPMessage param1SOAPMessage) { this.attIter = param1SOAPMessage.getAttachments(); }
    
    public Attachment get(String param1String) {
      if (this.attMap == null) {
        if (!this.attIter.hasNext())
          return null; 
        this.attMap = createAttachmentMap();
      } 
      return (param1String.charAt(0) != '<') ? (Attachment)this.attMap.get('<' + param1String + '>') : (Attachment)this.attMap.get(param1String);
    }
    
    public boolean isEmpty() { return (this.attMap != null) ? this.attMap.isEmpty() : (!this.attIter.hasNext() ? 1 : 0); }
    
    public Iterator<Attachment> iterator() {
      if (this.attMap == null)
        this.attMap = createAttachmentMap(); 
      return this.attMap.values().iterator();
    }
    
    private Map<String, Attachment> createAttachmentMap() {
      HashMap hashMap = new HashMap();
      while (this.attIter.hasNext()) {
        AttachmentPart attachmentPart = (AttachmentPart)this.attIter.next();
        hashMap.put(attachmentPart.getContentId(), new SAAJMessage.SAAJAttachment(attachmentPart));
      } 
      return hashMap;
    }
    
    public void add(Attachment param1Attachment) { this.attMap.put('<' + param1Attachment.getContentId() + '>', param1Attachment); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\saaj\SAAJMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */