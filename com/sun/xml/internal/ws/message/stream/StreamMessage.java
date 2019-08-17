package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.StreamingSOAP;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.DummyLocation;
import com.sun.xml.internal.ws.util.xml.StAXSource;
import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;

public class StreamMessage extends AbstractMessageImpl implements StreamingSOAP {
  @NotNull
  private XMLStreamReader reader;
  
  @Nullable
  private MessageHeaders headers;
  
  private String bodyPrologue = null;
  
  private String bodyEpilogue = null;
  
  private String payloadLocalName;
  
  private String payloadNamespaceURI;
  
  private Throwable consumedAt;
  
  private XMLStreamReader envelopeReader;
  
  private static final String SOAP_ENVELOPE = "Envelope";
  
  private static final String SOAP_HEADER = "Header";
  
  private static final String SOAP_BODY = "Body";
  
  static final StreamHeaderDecoder SOAP12StreamHeaderDecoder = new StreamHeaderDecoder() {
      public Header decodeHeader(XMLStreamReader param1XMLStreamReader, XMLStreamBuffer param1XMLStreamBuffer) { return new StreamHeader12(param1XMLStreamReader, param1XMLStreamBuffer); }
    };
  
  static final StreamHeaderDecoder SOAP11StreamHeaderDecoder = new StreamHeaderDecoder() {
      public Header decodeHeader(XMLStreamReader param1XMLStreamReader, XMLStreamBuffer param1XMLStreamBuffer) { return new StreamHeader11(param1XMLStreamReader, param1XMLStreamBuffer); }
    };
  
  public StreamMessage(SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    this.payloadLocalName = null;
    this.payloadNamespaceURI = null;
  }
  
  public StreamMessage(SOAPVersion paramSOAPVersion, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull AttachmentSet paramAttachmentSet) {
    super(paramSOAPVersion);
    this.envelopeReader = paramXMLStreamReader;
    this.attachmentSet = paramAttachmentSet;
  }
  
  public XMLStreamReader readEnvelope() {
    if (this.envelopeReader == null) {
      ArrayList arrayList = new ArrayList();
      XMLReaderComposite.ElemInfo elemInfo1 = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
      XMLReaderComposite.ElemInfo elemInfo2 = (this.headerTag != null) ? new XMLReaderComposite.ElemInfo(this.headerTag, elemInfo1) : null;
      XMLReaderComposite.ElemInfo elemInfo3 = new XMLReaderComposite.ElemInfo(this.bodyTag, elemInfo1);
      for (Header header : getHeaders().asList()) {
        try {
          arrayList.add(header.readHeader());
        } catch (XMLStreamException xMLStreamException) {
          throw new RuntimeException(xMLStreamException);
        } 
      } 
      XMLReaderComposite xMLReaderComposite1 = (elemInfo2 != null) ? new XMLReaderComposite(elemInfo2, (XMLStreamReader[])arrayList.toArray(new XMLStreamReader[arrayList.size()])) : null;
      XMLStreamReader[] arrayOfXMLStreamReader1 = { readPayload() };
      XMLReaderComposite xMLReaderComposite2 = new XMLReaderComposite(elemInfo3, arrayOfXMLStreamReader1);
      new XMLStreamReader[2][0] = xMLReaderComposite1;
      new XMLStreamReader[2][1] = xMLReaderComposite2;
      new XMLStreamReader[1][0] = xMLReaderComposite2;
      XMLStreamReader[] arrayOfXMLStreamReader2 = (xMLReaderComposite1 != null) ? new XMLStreamReader[2] : new XMLStreamReader[1];
      return new XMLReaderComposite(elemInfo1, arrayOfXMLStreamReader2);
    } 
    return this.envelopeReader;
  }
  
  public StreamMessage(@Nullable MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    init(paramMessageHeaders, paramAttachmentSet, paramXMLStreamReader, paramSOAPVersion);
  }
  
  private void init(@Nullable MessageHeaders paramMessageHeaders, @NotNull AttachmentSet paramAttachmentSet, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion) {
    this.headers = paramMessageHeaders;
    this.attachmentSet = paramAttachmentSet;
    this.reader = paramXMLStreamReader;
    if (paramXMLStreamReader.getEventType() == 7)
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader); 
    if (paramXMLStreamReader.getEventType() == 2) {
      String str1 = paramXMLStreamReader.getLocalName();
      String str2 = paramXMLStreamReader.getNamespaceURI();
      assert str1 != null;
      assert str2 != null;
      if (str1.equals("Body") && str2.equals(paramSOAPVersion.nsUri)) {
        this.payloadLocalName = null;
        this.payloadNamespaceURI = null;
      } else {
        throw new WebServiceException("Malformed stream: {" + str2 + "}" + str1);
      } 
    } else {
      this.payloadLocalName = paramXMLStreamReader.getLocalName();
      this.payloadNamespaceURI = paramXMLStreamReader.getNamespaceURI();
    } 
    int i = paramSOAPVersion.ordinal() * 3;
    this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(i);
    this.headerTag = (TagInfoset)DEFAULT_TAGS.get(i + 1);
    this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(i + 2);
  }
  
  public StreamMessage(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @NotNull TagInfoset paramTagInfoset3, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion) { this(paramTagInfoset1, paramTagInfoset2, paramAttachmentSet, paramMessageHeaders, null, paramTagInfoset3, null, paramXMLStreamReader, paramSOAPVersion); }
  
  public StreamMessage(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @Nullable String paramString1, @NotNull TagInfoset paramTagInfoset3, @Nullable String paramString2, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion) {
    super(paramSOAPVersion);
    init(paramTagInfoset1, paramTagInfoset2, paramAttachmentSet, paramMessageHeaders, paramString1, paramTagInfoset3, paramString2, paramXMLStreamReader, paramSOAPVersion);
  }
  
  private void init(@NotNull TagInfoset paramTagInfoset1, @Nullable TagInfoset paramTagInfoset2, @NotNull AttachmentSet paramAttachmentSet, @Nullable MessageHeaders paramMessageHeaders, @Nullable String paramString1, @NotNull TagInfoset paramTagInfoset3, @Nullable String paramString2, @NotNull XMLStreamReader paramXMLStreamReader, @NotNull SOAPVersion paramSOAPVersion) {
    init(paramMessageHeaders, paramAttachmentSet, paramXMLStreamReader, paramSOAPVersion);
    if (paramTagInfoset1 == null)
      throw new IllegalArgumentException("EnvelopeTag TagInfoset cannot be null"); 
    if (paramTagInfoset3 == null)
      throw new IllegalArgumentException("BodyTag TagInfoset cannot be null"); 
    this.envelopeTag = paramTagInfoset1;
    this.headerTag = paramTagInfoset2;
    this.bodyTag = paramTagInfoset3;
    this.bodyPrologue = paramString1;
    this.bodyEpilogue = paramString2;
  }
  
  public boolean hasHeaders() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return (this.headers != null && this.headers.hasHeaders());
  }
  
  public MessageHeaders getHeaders() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    if (this.headers == null)
      this.headers = new HeaderList(getSOAPVersion()); 
    return this.headers;
  }
  
  public String getPayloadLocalPart() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return this.payloadLocalName;
  }
  
  public String getPayloadNamespaceURI() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return this.payloadNamespaceURI;
  }
  
  public boolean hasPayload() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return (this.payloadLocalName != null);
  }
  
  public Source readPayloadAsSource() {
    if (hasPayload()) {
      assert unconsumed();
      return new StAXSource(this.reader, true, getInscopeNamespaces());
    } 
    return null;
  }
  
  private String[] getInscopeNamespaces() {
    NamespaceSupport namespaceSupport = new NamespaceSupport();
    namespaceSupport.pushContext();
    boolean bool;
    for (bool = false; bool < this.envelopeTag.ns.length; bool += true)
      namespaceSupport.declarePrefix(this.envelopeTag.ns[bool], this.envelopeTag.ns[bool + true]); 
    namespaceSupport.pushContext();
    for (bool = false; bool < this.bodyTag.ns.length; bool += true)
      namespaceSupport.declarePrefix(this.bodyTag.ns[bool], this.bodyTag.ns[bool + true]); 
    ArrayList arrayList = new ArrayList();
    Enumeration enumeration = namespaceSupport.getPrefixes();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      arrayList.add(str);
      arrayList.add(namespaceSupport.getURI(str));
    } 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  public Object readPayloadAsJAXB(Unmarshaller paramUnmarshaller) throws JAXBException {
    if (!hasPayload())
      return null; 
    assert unconsumed();
    if (hasAttachments())
      paramUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerImpl(getAttachments())); 
    try {
      return paramUnmarshaller.unmarshal(this.reader);
    } finally {
      paramUnmarshaller.setAttachmentUnmarshaller(null);
      XMLStreamReaderUtil.readRest(this.reader);
      XMLStreamReaderUtil.close(this.reader);
      XMLStreamReaderFactory.recycle(this.reader);
    } 
  }
  
  public <T> T readPayloadAsJAXB(Bridge<T> paramBridge) throws JAXBException {
    if (!hasPayload())
      return null; 
    assert unconsumed();
    Object object = paramBridge.unmarshal(this.reader, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    XMLStreamReaderUtil.readRest(this.reader);
    XMLStreamReaderUtil.close(this.reader);
    XMLStreamReaderFactory.recycle(this.reader);
    return (T)object;
  }
  
  public <T> T readPayloadAsJAXB(XMLBridge<T> paramXMLBridge) throws JAXBException {
    if (!hasPayload())
      return null; 
    assert unconsumed();
    Object object = paramXMLBridge.unmarshal(this.reader, hasAttachments() ? new AttachmentUnmarshallerImpl(getAttachments()) : null);
    XMLStreamReaderUtil.readRest(this.reader);
    XMLStreamReaderUtil.close(this.reader);
    XMLStreamReaderFactory.recycle(this.reader);
    return (T)object;
  }
  
  public void consume() {
    assert unconsumed();
    XMLStreamReaderUtil.readRest(this.reader);
    XMLStreamReaderUtil.close(this.reader);
    XMLStreamReaderFactory.recycle(this.reader);
  }
  
  public XMLStreamReader readPayload() {
    if (!hasPayload())
      return null; 
    assert unconsumed();
    return this.reader;
  }
  
  public void writePayloadTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    assert unconsumed();
    if (this.payloadLocalName == null)
      return; 
    if (this.bodyPrologue != null)
      paramXMLStreamWriter.writeCharacters(this.bodyPrologue); 
    XMLStreamReaderToXMLStreamWriter xMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
    while (this.reader.getEventType() != 8) {
      String str1 = this.reader.getLocalName();
      String str2 = this.reader.getNamespaceURI();
      if (this.reader.getEventType() == 2) {
        if (!isBodyElement(str1, str2)) {
          String str = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
          if (str != null) {
            this.bodyEpilogue = str;
            paramXMLStreamWriter.writeCharacters(str);
          } 
          continue;
        } 
        break;
      } 
      xMLStreamReaderToXMLStreamWriter.bridge(this.reader, paramXMLStreamWriter);
    } 
    XMLStreamReaderUtil.readRest(this.reader);
    XMLStreamReaderUtil.close(this.reader);
    XMLStreamReaderFactory.recycle(this.reader);
  }
  
  private boolean isBodyElement(String paramString1, String paramString2) { return (paramString1.equals("Body") && paramString2.equals(this.soapVersion.nsUri)); }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    writeEnvelope(paramXMLStreamWriter);
  }
  
  private void writeEnvelope(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    paramXMLStreamWriter.writeStartDocument();
    this.envelopeTag.writeStart(paramXMLStreamWriter);
    MessageHeaders messageHeaders = getHeaders();
    if (messageHeaders.hasHeaders() && this.headerTag == null)
      this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, EMPTY_ATTS, new String[0]); 
    if (this.headerTag != null) {
      this.headerTag.writeStart(paramXMLStreamWriter);
      if (messageHeaders.hasHeaders())
        for (Header header : messageHeaders.asList())
          header.writeTo(paramXMLStreamWriter);  
      paramXMLStreamWriter.writeEndElement();
    } 
    this.bodyTag.writeStart(paramXMLStreamWriter);
    if (hasPayload())
      writePayloadTo(paramXMLStreamWriter); 
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndElement();
    paramXMLStreamWriter.writeEndDocument();
  }
  
  public void writePayloadTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    assert unconsumed();
    try {
      if (this.payloadLocalName == null)
        return; 
      if (this.bodyPrologue != null) {
        char[] arrayOfChar = this.bodyPrologue.toCharArray();
        paramContentHandler.characters(arrayOfChar, 0, arrayOfChar.length);
      } 
      XMLStreamReaderToContentHandler xMLStreamReaderToContentHandler = new XMLStreamReaderToContentHandler(this.reader, paramContentHandler, true, paramBoolean, getInscopeNamespaces());
      while (this.reader.getEventType() != 8) {
        String str1 = this.reader.getLocalName();
        String str2 = this.reader.getNamespaceURI();
        if (this.reader.getEventType() == 2) {
          if (!isBodyElement(str1, str2)) {
            String str = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
            if (str != null) {
              this.bodyEpilogue = str;
              char[] arrayOfChar = str.toCharArray();
              paramContentHandler.characters(arrayOfChar, 0, arrayOfChar.length);
            } 
            continue;
          } 
          break;
        } 
        xMLStreamReaderToContentHandler.bridge();
      } 
      XMLStreamReaderUtil.readRest(this.reader);
      XMLStreamReaderUtil.close(this.reader);
      XMLStreamReaderFactory.recycle(this.reader);
    } catch (XMLStreamException xMLStreamException) {
      Location location = xMLStreamException.getLocation();
      if (location == null)
        location = DummyLocation.INSTANCE; 
      SAXParseException sAXParseException = new SAXParseException(xMLStreamException.getMessage(), location.getPublicId(), location.getSystemId(), location.getLineNumber(), location.getColumnNumber(), xMLStreamException);
      paramErrorHandler.error(sAXParseException);
    } 
  }
  
  public Message copy() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    try {
      assert unconsumed();
      this.consumedAt = null;
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      StreamReaderBufferCreator streamReaderBufferCreator = new StreamReaderBufferCreator(mutableXMLStreamBuffer);
      streamReaderBufferCreator.storeElement(this.envelopeTag.nsUri, this.envelopeTag.localName, this.envelopeTag.prefix, this.envelopeTag.ns);
      streamReaderBufferCreator.storeElement(this.bodyTag.nsUri, this.bodyTag.localName, this.bodyTag.prefix, this.bodyTag.ns);
      if (hasPayload())
        while (this.reader.getEventType() != 8) {
          String str1 = this.reader.getLocalName();
          String str2 = this.reader.getNamespaceURI();
          if (isBodyElement(str1, str2) || this.reader.getEventType() == 8)
            break; 
          streamReaderBufferCreator.create(this.reader);
          if (this.reader.isWhiteSpace()) {
            this.bodyEpilogue = XMLStreamReaderUtil.currentWhiteSpaceContent(this.reader);
            continue;
          } 
          this.bodyEpilogue = null;
        }  
      streamReaderBufferCreator.storeEndElement();
      streamReaderBufferCreator.storeEndElement();
      streamReaderBufferCreator.storeEndElement();
      XMLStreamReaderUtil.readRest(this.reader);
      XMLStreamReaderUtil.close(this.reader);
      XMLStreamReaderFactory.recycle(this.reader);
      this.reader = mutableXMLStreamBuffer.readAsXMLStreamReader();
      StreamReaderBufferProcessor streamReaderBufferProcessor = mutableXMLStreamBuffer.readAsXMLStreamReader();
      proceedToRootElement(this.reader);
      proceedToRootElement(streamReaderBufferProcessor);
      return new StreamMessage(this.envelopeTag, this.headerTag, this.attachmentSet, HeaderList.copy(this.headers), this.bodyPrologue, this.bodyTag, this.bodyEpilogue, streamReaderBufferProcessor, this.soapVersion);
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException("Failed to copy a message", xMLStreamException);
    } 
  }
  
  private void proceedToRootElement(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    assert paramXMLStreamReader.getEventType() == 7;
    paramXMLStreamReader.nextTag();
    paramXMLStreamReader.nextTag();
    paramXMLStreamReader.nextTag();
    assert paramXMLStreamReader.getEventType() == 1 || paramXMLStreamReader.getEventType() == 2;
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    paramContentHandler.setDocumentLocator(NULL_LOCATOR);
    paramContentHandler.startDocument();
    this.envelopeTag.writeStart(paramContentHandler);
    if (hasHeaders() && this.headerTag == null)
      this.headerTag = new TagInfoset(this.envelopeTag.nsUri, "Header", this.envelopeTag.prefix, EMPTY_ATTS, new String[0]); 
    if (this.headerTag != null) {
      this.headerTag.writeStart(paramContentHandler);
      if (hasHeaders()) {
        MessageHeaders messageHeaders = getHeaders();
        for (Header header : messageHeaders.asList())
          header.writeTo(paramContentHandler, paramErrorHandler); 
      } 
      this.headerTag.writeEnd(paramContentHandler);
    } 
    this.bodyTag.writeStart(paramContentHandler);
    writePayloadTo(paramContentHandler, paramErrorHandler, true);
    this.bodyTag.writeEnd(paramContentHandler);
    this.envelopeTag.writeEnd(paramContentHandler);
    paramContentHandler.endDocument();
  }
  
  private boolean unconsumed() {
    if (this.payloadLocalName == null)
      return true; 
    if (this.reader.getEventType() != 1) {
      AssertionError assertionError = new AssertionError("StreamMessage has been already consumed. See the nested exception for where it's consumed");
      assertionError.initCause(this.consumedAt);
      throw assertionError;
    } 
    this.consumedAt = (new Exception()).fillInStackTrace();
    return true;
  }
  
  public String getBodyPrologue() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return this.bodyPrologue;
  }
  
  public String getBodyEpilogue() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    return this.bodyEpilogue;
  }
  
  public XMLStreamReader getReader() {
    if (this.envelopeReader != null)
      readEnvelope(this); 
    assert unconsumed();
    return this.reader;
  }
  
  private static void readEnvelope(StreamMessage paramStreamMessage) {
    if (paramStreamMessage.envelopeReader == null)
      return; 
    XMLStreamReader xMLStreamReader = paramStreamMessage.envelopeReader;
    paramStreamMessage.envelopeReader = null;
    SOAPVersion sOAPVersion = paramStreamMessage.soapVersion;
    if (xMLStreamReader.getEventType() != 1)
      XMLStreamReaderUtil.nextElementContent(xMLStreamReader); 
    XMLStreamReaderUtil.verifyReaderState(xMLStreamReader, 1);
    if ("Envelope".equals(xMLStreamReader.getLocalName()) && !sOAPVersion.nsUri.equals(xMLStreamReader.getNamespaceURI()))
      throw new VersionMismatchException(sOAPVersion, new Object[] { sOAPVersion.nsUri, xMLStreamReader.getNamespaceURI() }); 
    XMLStreamReaderUtil.verifyTag(xMLStreamReader, sOAPVersion.nsUri, "Envelope");
    TagInfoset tagInfoset1 = new TagInfoset(xMLStreamReader);
    HashMap hashMap = new HashMap();
    for (byte b = 0; b < xMLStreamReader.getNamespaceCount(); b++)
      hashMap.put(xMLStreamReader.getNamespacePrefix(b), xMLStreamReader.getNamespaceURI(b)); 
    XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
    XMLStreamReaderUtil.verifyReaderState(xMLStreamReader, 1);
    HeaderList headerList = null;
    TagInfoset tagInfoset2 = null;
    if (xMLStreamReader.getLocalName().equals("Header") && xMLStreamReader.getNamespaceURI().equals(sOAPVersion.nsUri)) {
      tagInfoset2 = new TagInfoset(xMLStreamReader);
      for (b1 = 0; b1 < xMLStreamReader.getNamespaceCount(); b1++)
        hashMap.put(xMLStreamReader.getNamespacePrefix(b1), xMLStreamReader.getNamespaceURI(b1)); 
      XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
      if (xMLStreamReader.getEventType() == 1) {
        headerList = new HeaderList(sOAPVersion);
        try {
          StreamHeaderDecoder streamHeaderDecoder = SOAPVersion.SOAP_11.equals(sOAPVersion) ? SOAP11StreamHeaderDecoder : SOAP12StreamHeaderDecoder;
          cacheHeaders(xMLStreamReader, hashMap, headerList, streamHeaderDecoder);
        } catch (XMLStreamException b1) {
          XMLStreamException xMLStreamException;
          throw new WebServiceException(xMLStreamException);
        } 
      } 
      XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
    } 
    XMLStreamReaderUtil.verifyTag(xMLStreamReader, sOAPVersion.nsUri, "Body");
    TagInfoset tagInfoset3 = new TagInfoset(xMLStreamReader);
    String str = XMLStreamReaderUtil.nextWhiteSpaceContent(xMLStreamReader);
    paramStreamMessage.init(tagInfoset1, tagInfoset2, paramStreamMessage.attachmentSet, headerList, str, tagInfoset3, null, xMLStreamReader, sOAPVersion);
  }
  
  private static XMLStreamBuffer cacheHeaders(XMLStreamReader paramXMLStreamReader, Map<String, String> paramMap, HeaderList paramHeaderList, StreamHeaderDecoder paramStreamHeaderDecoder) throws XMLStreamException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = createXMLStreamBuffer();
    StreamReaderBufferCreator streamReaderBufferCreator = new StreamReaderBufferCreator();
    streamReaderBufferCreator.setXMLStreamBuffer(mutableXMLStreamBuffer);
    while (paramXMLStreamReader.getEventType() == 1) {
      Map<String, String> map = paramMap;
      if (paramXMLStreamReader.getNamespaceCount() > 0) {
        map = new HashMap<String, String>(paramMap);
        for (byte b = 0; b < paramXMLStreamReader.getNamespaceCount(); b++)
          map.put(paramXMLStreamReader.getNamespacePrefix(b), paramXMLStreamReader.getNamespaceURI(b)); 
      } 
      XMLStreamBufferMark xMLStreamBufferMark = new XMLStreamBufferMark(map, streamReaderBufferCreator);
      paramHeaderList.add(paramStreamHeaderDecoder.decodeHeader(paramXMLStreamReader, xMLStreamBufferMark));
      streamReaderBufferCreator.createElementFragment(paramXMLStreamReader, false);
      if (paramXMLStreamReader.getEventType() != 1 && paramXMLStreamReader.getEventType() != 2)
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader); 
    } 
    return mutableXMLStreamBuffer;
  }
  
  private static MutableXMLStreamBuffer createXMLStreamBuffer() { return new MutableXMLStreamBuffer(); }
  
  protected static interface StreamHeaderDecoder {
    Header decodeHeader(XMLStreamReader param1XMLStreamReader, XMLStreamBuffer param1XMLStreamBuffer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\stream\StreamMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */