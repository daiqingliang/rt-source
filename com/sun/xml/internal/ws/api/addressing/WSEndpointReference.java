package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.EndpointReferenceUtil;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.spi.ProviderImpl;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class WSEndpointReference implements WSDLExtension {
  private final XMLStreamBuffer infoset;
  
  private final AddressingVersion version;
  
  @NotNull
  private Header[] referenceParameters;
  
  @NotNull
  private String address;
  
  @NotNull
  private QName rootElement;
  
  private static final OutboundReferenceParameterHeader[] EMPTY_ARRAY = new OutboundReferenceParameterHeader[0];
  
  private Map<QName, EPRExtension> rootEprExtensions;
  
  public WSEndpointReference(EndpointReference paramEndpointReference, AddressingVersion paramAddressingVersion) {
    try {
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      paramEndpointReference.writeTo(new XMLStreamBufferResult(mutableXMLStreamBuffer));
      this.infoset = mutableXMLStreamBuffer;
      this.version = paramAddressingVersion;
      this.rootElement = new QName("EndpointReference", paramAddressingVersion.nsUri);
      parse();
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(ClientMessages.FAILED_TO_PARSE_EPR(paramEndpointReference), xMLStreamException);
    } 
  }
  
  public WSEndpointReference(EndpointReference paramEndpointReference) { this(paramEndpointReference, AddressingVersion.fromSpecClass(paramEndpointReference.getClass())); }
  
  public WSEndpointReference(XMLStreamBuffer paramXMLStreamBuffer, AddressingVersion paramAddressingVersion) {
    try {
      this.infoset = paramXMLStreamBuffer;
      this.version = paramAddressingVersion;
      this.rootElement = new QName("EndpointReference", paramAddressingVersion.nsUri);
      parse();
    } catch (XMLStreamException xMLStreamException) {
      throw new AssertionError(xMLStreamException);
    } 
  }
  
  public WSEndpointReference(InputStream paramInputStream, AddressingVersion paramAddressingVersion) throws XMLStreamException { this(XMLStreamReaderFactory.create(null, paramInputStream, false), paramAddressingVersion); }
  
  public WSEndpointReference(XMLStreamReader paramXMLStreamReader, AddressingVersion paramAddressingVersion) throws XMLStreamException { this(XMLStreamBuffer.createNewBufferFromXMLStreamReader(paramXMLStreamReader), paramAddressingVersion); }
  
  public WSEndpointReference(URL paramURL, AddressingVersion paramAddressingVersion) { this(paramURL.toExternalForm(), paramAddressingVersion); }
  
  public WSEndpointReference(URI paramURI, AddressingVersion paramAddressingVersion) { this(paramURI.toString(), paramAddressingVersion); }
  
  public WSEndpointReference(String paramString, AddressingVersion paramAddressingVersion) {
    this.infoset = createBufferFromAddress(paramString, paramAddressingVersion);
    this.version = paramAddressingVersion;
    this.address = paramString;
    this.rootElement = new QName("EndpointReference", paramAddressingVersion.nsUri);
    this.referenceParameters = EMPTY_ARRAY;
  }
  
  private static XMLStreamBuffer createBufferFromAddress(String paramString, AddressingVersion paramAddressingVersion) {
    try {
      MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
      StreamWriterBufferCreator streamWriterBufferCreator = new StreamWriterBufferCreator(mutableXMLStreamBuffer);
      streamWriterBufferCreator.writeStartDocument();
      streamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", paramAddressingVersion.nsUri);
      streamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), paramAddressingVersion.nsUri);
      streamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), paramAddressingVersion.eprType.address, paramAddressingVersion.nsUri);
      streamWriterBufferCreator.writeCharacters(paramString);
      streamWriterBufferCreator.writeEndElement();
      streamWriterBufferCreator.writeEndElement();
      streamWriterBufferCreator.writeEndDocument();
      streamWriterBufferCreator.close();
      return mutableXMLStreamBuffer;
    } catch (XMLStreamException xMLStreamException) {
      throw new AssertionError(xMLStreamException);
    } 
  }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable List<Element> paramList2) { this(paramAddressingVersion, paramString1, paramQName1, paramQName2, paramQName3, paramList1, paramString2, null, paramList2, null, null); }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable List<Element> paramList2, @Nullable Collection<EPRExtension> paramCollection, @Nullable Map<QName, String> paramMap) { this(createBufferFromData(paramAddressingVersion, paramString1, paramList2, paramQName1, paramQName2, paramQName3, paramList1, paramString2, null, paramCollection, paramMap), paramAddressingVersion); }
  
  public WSEndpointReference(@NotNull AddressingVersion paramAddressingVersion, @NotNull String paramString1, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable QName paramQName3, @Nullable List<Element> paramList1, @Nullable String paramString2, @Nullable String paramString3, @Nullable List<Element> paramList2, @Nullable List<Element> paramList3, @Nullable Map<QName, String> paramMap) { this(createBufferFromData(paramAddressingVersion, paramString1, paramList2, paramQName1, paramQName2, paramQName3, paramList1, paramString2, paramString3, paramList3, paramMap), paramAddressingVersion); }
  
  private static XMLStreamBuffer createBufferFromData(AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable List<Element> paramList3, @Nullable Map<QName, String> paramMap) {
    StreamWriterBufferCreator streamWriterBufferCreator = new StreamWriterBufferCreator();
    try {
      streamWriterBufferCreator.writeStartDocument();
      streamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", paramAddressingVersion.nsUri);
      streamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), paramAddressingVersion.nsUri);
      writePartialEPRInfoset(streamWriterBufferCreator, paramAddressingVersion, paramString1, paramList1, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3, paramMap);
      if (paramList3 != null)
        for (Element element : paramList3)
          DOMUtil.serializeNode(element, streamWriterBufferCreator);  
      streamWriterBufferCreator.writeEndElement();
      streamWriterBufferCreator.writeEndDocument();
      streamWriterBufferCreator.flush();
      return streamWriterBufferCreator.getXMLStreamBuffer();
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  private static XMLStreamBuffer createBufferFromData(AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable Collection<EPRExtension> paramCollection, @Nullable Map<QName, String> paramMap) {
    StreamWriterBufferCreator streamWriterBufferCreator = new StreamWriterBufferCreator();
    try {
      streamWriterBufferCreator.writeStartDocument();
      streamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), "EndpointReference", paramAddressingVersion.nsUri);
      streamWriterBufferCreator.writeNamespace(paramAddressingVersion.getPrefix(), paramAddressingVersion.nsUri);
      writePartialEPRInfoset(streamWriterBufferCreator, paramAddressingVersion, paramString1, paramList1, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3, paramMap);
      if (paramCollection != null)
        for (EPRExtension ePRExtension : paramCollection) {
          XMLStreamReaderToXMLStreamWriter xMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
          XMLStreamReader xMLStreamReader = ePRExtension.readAsXMLStreamReader();
          xMLStreamReaderToXMLStreamWriter.bridge(xMLStreamReader, streamWriterBufferCreator);
          XMLStreamReaderFactory.recycle(xMLStreamReader);
        }  
      streamWriterBufferCreator.writeEndElement();
      streamWriterBufferCreator.writeEndDocument();
      streamWriterBufferCreator.flush();
      return streamWriterBufferCreator.getXMLStreamBuffer();
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
  }
  
  private static void writePartialEPRInfoset(StreamWriterBufferCreator paramStreamWriterBufferCreator, AddressingVersion paramAddressingVersion, String paramString1, List<Element> paramList1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList2, String paramString2, String paramString3, @Nullable Map<QName, String> paramMap) throws XMLStreamException {
    if (paramMap != null)
      for (Map.Entry entry : paramMap.entrySet()) {
        QName qName = (QName)entry.getKey();
        paramStreamWriterBufferCreator.writeAttribute(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart(), (String)entry.getValue());
      }  
    paramStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), paramAddressingVersion.eprType.address, paramAddressingVersion.nsUri);
    paramStreamWriterBufferCreator.writeCharacters(paramString1);
    paramStreamWriterBufferCreator.writeEndElement();
    if (paramList1 != null && paramList1.size() > 0) {
      paramStreamWriterBufferCreator.writeStartElement(paramAddressingVersion.getPrefix(), paramAddressingVersion.eprType.referenceParameters, paramAddressingVersion.nsUri);
      for (Element element : paramList1)
        DOMUtil.serializeNode(element, paramStreamWriterBufferCreator); 
      paramStreamWriterBufferCreator.writeEndElement();
    } 
    switch (paramAddressingVersion) {
      case W3C:
        writeW3CMetaData(paramStreamWriterBufferCreator, paramQName1, paramQName2, paramQName3, paramList2, paramString2, paramString3);
        break;
      case MEMBER:
        writeMSMetaData(paramStreamWriterBufferCreator, paramQName1, paramQName2, paramQName3, paramList2);
        if (paramString2 != null) {
          paramStreamWriterBufferCreator.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI());
          paramStreamWriterBufferCreator.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI());
          paramStreamWriterBufferCreator.writeAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
          writeWsdl(paramStreamWriterBufferCreator, paramQName1, paramString2);
          paramStreamWriterBufferCreator.writeEndElement();
          paramStreamWriterBufferCreator.writeEndElement();
        } 
        break;
    } 
  }
  
  private static boolean isEmty(QName paramQName) { return (paramQName == null || paramQName.toString().trim().length() == 0); }
  
  private static void writeW3CMetaData(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList, String paramString1, String paramString2) throws XMLStreamException {
    if (isEmty(paramQName1) && isEmty(paramQName2) && isEmty(paramQName3) && paramList == null)
      return; 
    paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart(), AddressingVersion.W3C.nsUri);
    paramStreamWriterBufferCreator.writeNamespace(AddressingVersion.W3C.getWsdlPrefix(), AddressingVersion.W3C.wsdlNsUri);
    if (paramString1 != null)
      writeWsdliLocation(paramStreamWriterBufferCreator, paramQName1, paramString1, paramString2); 
    if (paramQName3 != null) {
      paramStreamWriterBufferCreator.writeStartElement("wsam", AddressingVersion.W3C.eprType.portTypeName, "http://www.w3.org/2007/05/addressing/metadata");
      paramStreamWriterBufferCreator.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
      String str = paramQName3.getPrefix();
      if (str == null || str.equals(""))
        str = "wsns"; 
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName3.getNamespaceURI());
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName3.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    } 
    if (paramQName1 != null && !paramQName1.getNamespaceURI().equals("") && !paramQName1.getLocalPart().equals("")) {
      paramStreamWriterBufferCreator.writeStartElement("wsam", AddressingVersion.W3C.eprType.serviceName, "http://www.w3.org/2007/05/addressing/metadata");
      paramStreamWriterBufferCreator.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
      String str = paramQName1.getPrefix();
      if (str == null || str.equals(""))
        str = "wsns"; 
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName1.getNamespaceURI());
      if (paramQName2 != null)
        paramStreamWriterBufferCreator.writeAttribute(AddressingVersion.W3C.eprType.portName, paramQName2.getLocalPart()); 
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName1.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    } 
    if (paramList != null)
      for (Element element : paramList)
        DOMUtil.serializeNode(element, paramStreamWriterBufferCreator);  
    paramStreamWriterBufferCreator.writeEndElement();
  }
  
  private static void writeWsdliLocation(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName, String paramString1, String paramString2) throws XMLStreamException {
    String str = "";
    if (paramString2 != null) {
      str = paramString2 + " ";
    } else if (paramQName != null) {
      str = paramQName.getNamespaceURI() + " ";
    } else {
      throw new WebServiceException("WSDL target Namespace cannot be resolved");
    } 
    str = str + paramString1;
    paramStreamWriterBufferCreator.writeNamespace("wsdli", "http://www.w3.org/ns/wsdl-instance");
    paramStreamWriterBufferCreator.writeAttribute("wsdli", "http://www.w3.org/ns/wsdl-instance", "wsdlLocation", str);
  }
  
  private static void writeMSMetaData(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList) throws XMLStreamException {
    if (paramQName3 != null) {
      paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.portTypeName, AddressingVersion.MEMBER.nsUri);
      String str = paramQName3.getPrefix();
      if (str == null || str.equals(""))
        str = "wsns"; 
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName3.getNamespaceURI());
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName3.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    } 
    if (paramQName1 != null && !paramQName1.getNamespaceURI().equals("") && !paramQName1.getLocalPart().equals("")) {
      paramStreamWriterBufferCreator.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.serviceName, AddressingVersion.MEMBER.nsUri);
      String str = paramQName1.getPrefix();
      if (str == null || str.equals(""))
        str = "wsns"; 
      paramStreamWriterBufferCreator.writeNamespace(str, paramQName1.getNamespaceURI());
      if (paramQName2 != null)
        paramStreamWriterBufferCreator.writeAttribute(AddressingVersion.MEMBER.eprType.portName, paramQName2.getLocalPart()); 
      paramStreamWriterBufferCreator.writeCharacters(str + ":" + paramQName1.getLocalPart());
      paramStreamWriterBufferCreator.writeEndElement();
    } 
  }
  
  private static void writeWsdl(StreamWriterBufferCreator paramStreamWriterBufferCreator, QName paramQName, String paramString) throws XMLStreamException {
    paramStreamWriterBufferCreator.writeStartElement("wsdl", WSDLConstants.QNAME_DEFINITIONS.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeStartElement("wsdl", WSDLConstants.QNAME_IMPORT.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
    paramStreamWriterBufferCreator.writeAttribute("namespace", paramQName.getNamespaceURI());
    paramStreamWriterBufferCreator.writeAttribute("location", paramString);
    paramStreamWriterBufferCreator.writeEndElement();
    paramStreamWriterBufferCreator.writeEndElement();
  }
  
  @Nullable
  public static WSEndpointReference create(@Nullable EndpointReference paramEndpointReference) { return (paramEndpointReference != null) ? new WSEndpointReference(paramEndpointReference) : null; }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull URI paramURI) { return createWithAddress(paramURI.toString()); }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull URL paramURL) { return createWithAddress(paramURL.toString()); }
  
  @NotNull
  public WSEndpointReference createWithAddress(@NotNull final String newAddress) {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    XMLFilterImpl xMLFilterImpl = new XMLFilterImpl() {
        private boolean inAddress = false;
        
        public void startElement(String param1String1, String param1String2, String param1String3, Attributes param1Attributes) throws SAXException {
          if (param1String2.equals("Address") && param1String1.equals(this.this$0.version.nsUri))
            this.inAddress = true; 
          super.startElement(param1String1, param1String2, param1String3, param1Attributes);
        }
        
        public void characters(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws SAXException {
          if (!this.inAddress)
            super.characters(param1ArrayOfChar, param1Int1, param1Int2); 
        }
        
        public void endElement(String param1String1, String param1String2, String param1String3) throws SAXException {
          if (this.inAddress)
            super.characters(newAddress.toCharArray(), 0, newAddress.length()); 
          this.inAddress = false;
          super.endElement(param1String1, param1String2, param1String3);
        }
      };
    xMLFilterImpl.setContentHandler(mutableXMLStreamBuffer.createFromSAXBufferCreator());
    try {
      this.infoset.writeTo(xMLFilterImpl, false);
    } catch (SAXException sAXException) {
      throw new AssertionError(sAXException);
    } 
    return new WSEndpointReference(mutableXMLStreamBuffer, this.version);
  }
  
  @NotNull
  public EndpointReference toSpec() { return ProviderImpl.INSTANCE.readEndpointReference(asSource("EndpointReference")); }
  
  @NotNull
  public <T extends EndpointReference> T toSpec(Class<T> paramClass) { return (T)EndpointReferenceUtil.transform(paramClass, toSpec()); }
  
  @NotNull
  public <T> T getPort(@NotNull Service paramService, @NotNull Class<T> paramClass, WebServiceFeature... paramVarArgs) { return (T)paramService.getPort(toSpec(), paramClass, paramVarArgs); }
  
  @NotNull
  public <T> Dispatch<T> createDispatch(@NotNull Service paramService, @NotNull Class<T> paramClass, @NotNull Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return paramService.createDispatch(toSpec(), paramClass, paramMode, paramVarArgs); }
  
  @NotNull
  public Dispatch<Object> createDispatch(@NotNull Service paramService, @NotNull JAXBContext paramJAXBContext, @NotNull Service.Mode paramMode, WebServiceFeature... paramVarArgs) { return paramService.createDispatch(toSpec(), paramJAXBContext, paramMode, paramVarArgs); }
  
  @NotNull
  public AddressingVersion getVersion() { return this.version; }
  
  @NotNull
  public String getAddress() { return this.address; }
  
  public boolean isAnonymous() { return this.address.equals(this.version.anonymousUri); }
  
  public boolean isNone() { return this.address.equals(this.version.noneUri); }
  
  private void parse() throws XMLStreamException {
    StreamReaderBufferProcessor streamReaderBufferProcessor = this.infoset.readAsXMLStreamReader();
    if (streamReaderBufferProcessor.getEventType() == 7)
      streamReaderBufferProcessor.nextTag(); 
    assert streamReaderBufferProcessor.getEventType() == 1;
    String str = streamReaderBufferProcessor.getLocalName();
    if (!streamReaderBufferProcessor.getNamespaceURI().equals(this.version.nsUri))
      throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, streamReaderBufferProcessor.getNamespaceURI())); 
    this.rootElement = new QName(streamReaderBufferProcessor.getNamespaceURI(), str);
    ArrayList arrayList = null;
    while (streamReaderBufferProcessor.nextTag() == 1) {
      String str1 = streamReaderBufferProcessor.getLocalName();
      if (this.version.isReferenceParameter(str1)) {
        XMLStreamBuffer xMLStreamBuffer;
        while ((xMLStreamBuffer = streamReaderBufferProcessor.nextTagAndMark()) != null) {
          if (arrayList == null)
            arrayList = new ArrayList(); 
          arrayList.add(this.version.createReferenceParameterHeader(xMLStreamBuffer, streamReaderBufferProcessor.getNamespaceURI(), streamReaderBufferProcessor.getLocalName()));
          XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
        } 
        continue;
      } 
      if (str1.equals("Address")) {
        if (this.address != null)
          throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, str), AddressingVersion.fault_duplicateAddressInEpr); 
        this.address = streamReaderBufferProcessor.getElementText().trim();
        continue;
      } 
      XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
    } 
    if (arrayList == null) {
      this.referenceParameters = EMPTY_ARRAY;
    } else {
      this.referenceParameters = (Header[])arrayList.toArray(new Header[arrayList.size()]);
    } 
    if (this.address == null)
      throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, str), this.version.fault_missingAddressInEpr); 
  }
  
  public XMLStreamReader read(@NotNull final String localName) throws XMLStreamException { return new StreamReaderBufferProcessor(this.infoset) {
        protected void processElement(String param1String1, String param1String2, String param1String3, boolean param1Boolean) {
          if (this._depth == 0)
            param1String3 = localName; 
          super.processElement(param1String1, param1String2, param1String3, WSEndpointReference.this.isInscope(WSEndpointReference.this.infoset, this._depth));
        }
      }; }
  
  private boolean isInscope(XMLStreamBuffer paramXMLStreamBuffer, int paramInt) { return (paramXMLStreamBuffer.getInscopeNamespaces().size() > 0 && paramInt == 0); }
  
  public Source asSource(@NotNull String paramString) { return new SAXSource(new SAXBufferProcessorImpl(this, paramString), new InputSource()); }
  
  public void writeTo(@NotNull String paramString, ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    SAXBufferProcessorImpl sAXBufferProcessorImpl = new SAXBufferProcessorImpl(this, paramString);
    sAXBufferProcessorImpl.setContentHandler(paramContentHandler);
    sAXBufferProcessorImpl.setErrorHandler(paramErrorHandler);
    sAXBufferProcessorImpl.process(this.infoset, paramBoolean);
  }
  
  public void writeTo(@NotNull final String localName, @NotNull XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(paramXMLStreamWriter) {
          private boolean root = true;
          
          public void writeStartDocument() throws XMLStreamException {}
          
          public void writeStartDocument(String param1String1, String param1String2) throws XMLStreamException {}
          
          public void writeStartDocument(String param1String) throws XMLStreamException {}
          
          public void writeEndDocument() throws XMLStreamException {}
          
          private String override(String param1String) {
            if (this.root) {
              this.root = false;
              return localName;
            } 
            return param1String;
          }
          
          public void writeStartElement(String param1String) throws XMLStreamException { super.writeStartElement(override(param1String)); }
          
          public void writeStartElement(String param1String1, String param1String2) throws XMLStreamException { super.writeStartElement(param1String1, override(param1String2)); }
          
          public void writeStartElement(String param1String1, String param1String2, String param1String3) throws SAXException { super.writeStartElement(param1String1, override(param1String2), param1String3); }
        }true); }
  
  public Header createHeader(QName paramQName) { return new EPRHeader(paramQName, this); }
  
  public void addReferenceParametersToList(HeaderList paramHeaderList) {
    for (Header header : this.referenceParameters)
      paramHeaderList.add(header); 
  }
  
  public void addReferenceParametersToList(MessageHeaders paramMessageHeaders) {
    for (Header header : this.referenceParameters)
      paramMessageHeaders.add(header); 
  }
  
  public void addReferenceParameters(HeaderList paramHeaderList) {
    if (paramHeaderList != null) {
      Header[] arrayOfHeader = new Header[this.referenceParameters.length + paramHeaderList.size()];
      System.arraycopy(this.referenceParameters, 0, arrayOfHeader, 0, this.referenceParameters.length);
      int i = this.referenceParameters.length;
      for (Header header : paramHeaderList)
        arrayOfHeader[i++] = header; 
      this.referenceParameters = arrayOfHeader;
    } 
  }
  
  public String toString() {
    try {
      StringWriter stringWriter = new StringWriter();
      XmlUtil.newTransformer().transform(asSource("EndpointReference"), new StreamResult(stringWriter));
      return stringWriter.toString();
    } catch (TransformerException transformerException) {
      return transformerException.toString();
    } 
  }
  
  public QName getName() { return this.rootElement; }
  
  @Nullable
  public EPRExtension getEPRExtension(QName paramQName) throws XMLStreamException {
    if (this.rootEprExtensions == null)
      parseEPRExtensions(); 
    return (EPRExtension)this.rootEprExtensions.get(paramQName);
  }
  
  @NotNull
  public Collection<EPRExtension> getEPRExtensions() throws XMLStreamException {
    if (this.rootEprExtensions == null)
      parseEPRExtensions(); 
    return this.rootEprExtensions.values();
  }
  
  private void parseEPRExtensions() throws XMLStreamException {
    this.rootEprExtensions = new HashMap();
    StreamReaderBufferProcessor streamReaderBufferProcessor = this.infoset.readAsXMLStreamReader();
    if (streamReaderBufferProcessor.getEventType() == 7)
      streamReaderBufferProcessor.nextTag(); 
    assert streamReaderBufferProcessor.getEventType() == 1;
    if (!streamReaderBufferProcessor.getNamespaceURI().equals(this.version.nsUri))
      throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, streamReaderBufferProcessor.getNamespaceURI())); 
    XMLStreamBuffer xMLStreamBuffer;
    while ((xMLStreamBuffer = streamReaderBufferProcessor.nextTagAndMark()) != null) {
      String str1 = streamReaderBufferProcessor.getLocalName();
      String str2 = streamReaderBufferProcessor.getNamespaceURI();
      if (this.version.nsUri.equals(str2)) {
        XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
        continue;
      } 
      QName qName = new QName(str2, str1);
      this.rootEprExtensions.put(qName, new WSEPRExtension(xMLStreamBuffer, qName));
      XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
    } 
  }
  
  @NotNull
  public Metadata getMetaData() { return new Metadata(null); }
  
  public static abstract class EPRExtension {
    public abstract XMLStreamReader readAsXMLStreamReader() throws XMLStreamException;
    
    public abstract QName getQName();
  }
  
  public class Metadata {
    @Nullable
    private QName serviceName;
    
    @Nullable
    private QName portName;
    
    @Nullable
    private QName portTypeName;
    
    @Nullable
    private Source wsdlSource;
    
    @Nullable
    private String wsdliLocation;
    
    @Nullable
    public QName getServiceName() { return this.serviceName; }
    
    @Nullable
    public QName getPortName() { return this.portName; }
    
    @Nullable
    public QName getPortTypeName() { return this.portTypeName; }
    
    @Nullable
    public Source getWsdlSource() { return this.wsdlSource; }
    
    @Nullable
    public String getWsdliLocation() { return this.wsdliLocation; }
    
    private Metadata() {
      try {
        parseMetaData();
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
    }
    
    private void parseMetaData() throws XMLStreamException {
      StreamReaderBufferProcessor streamReaderBufferProcessor = WSEndpointReference.this.infoset.readAsXMLStreamReader();
      if (streamReaderBufferProcessor.getEventType() == 7)
        streamReaderBufferProcessor.nextTag(); 
      assert streamReaderBufferProcessor.getEventType() == 1;
      String str = streamReaderBufferProcessor.getLocalName();
      if (!streamReaderBufferProcessor.getNamespaceURI().equals(this.this$0.version.nsUri))
        throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.this$0.version.nsUri, streamReaderBufferProcessor.getNamespaceURI())); 
      if (WSEndpointReference.this.version == AddressingVersion.W3C) {
        do {
          if (streamReaderBufferProcessor.getLocalName().equals(this.this$0.version.eprType.wsdlMetadata.getLocalPart())) {
            String str1 = streamReaderBufferProcessor.getAttributeValue("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
            if (str1 != null)
              this.wsdliLocation = str1.trim(); 
            XMLStreamBuffer xMLStreamBuffer;
            while ((xMLStreamBuffer = streamReaderBufferProcessor.nextTagAndMark()) != null) {
              String str2 = streamReaderBufferProcessor.getLocalName();
              String str3 = streamReaderBufferProcessor.getNamespaceURI();
              if (str2.equals(this.this$0.version.eprType.serviceName)) {
                String str4 = streamReaderBufferProcessor.getAttributeValue(null, this.this$0.version.eprType.portName);
                if (this.serviceName != null)
                  throw new RuntimeException("More than one " + this.this$0.version.eprType.serviceName + " element in EPR Metadata"); 
                this.serviceName = getElementTextAsQName(streamReaderBufferProcessor);
                if (this.serviceName != null && str4 != null)
                  this.portName = new QName(this.serviceName.getNamespaceURI(), str4); 
                continue;
              } 
              if (str2.equals(this.this$0.version.eprType.portTypeName)) {
                if (this.portTypeName != null)
                  throw new RuntimeException("More than one " + this.this$0.version.eprType.portTypeName + " element in EPR Metadata"); 
                this.portTypeName = getElementTextAsQName(streamReaderBufferProcessor);
                continue;
              } 
              if (str3.equals("http://schemas.xmlsoap.org/wsdl/") && str2.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                this.wsdlSource = new XMLStreamBufferSource(xMLStreamBuffer);
                continue;
              } 
              XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
            } 
          } else if (!streamReaderBufferProcessor.getLocalName().equals(str)) {
            XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
          } 
        } while (XMLStreamReaderUtil.nextElementContent(streamReaderBufferProcessor) == 1);
        if (this.wsdliLocation != null) {
          String str1 = this.wsdliLocation.trim();
          str1 = str1.substring(this.wsdliLocation.lastIndexOf(" "));
          this.wsdlSource = new StreamSource(str1);
        } 
      } else if (WSEndpointReference.this.version == AddressingVersion.MEMBER) {
        do {
          String str1 = streamReaderBufferProcessor.getLocalName();
          String str2 = streamReaderBufferProcessor.getNamespaceURI();
          if (str1.equals(this.this$0.version.eprType.wsdlMetadata.getLocalPart()) && str2.equals(this.this$0.version.eprType.wsdlMetadata.getNamespaceURI())) {
            while (streamReaderBufferProcessor.nextTag() == 1) {
              XMLStreamBuffer xMLStreamBuffer;
              while ((xMLStreamBuffer = streamReaderBufferProcessor.nextTagAndMark()) != null) {
                str1 = streamReaderBufferProcessor.getLocalName();
                str2 = streamReaderBufferProcessor.getNamespaceURI();
                if (str2.equals("http://schemas.xmlsoap.org/wsdl/") && str1.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                  this.wsdlSource = new XMLStreamBufferSource(xMLStreamBuffer);
                  continue;
                } 
                XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
              } 
            } 
          } else if (str1.equals(this.this$0.version.eprType.serviceName)) {
            String str3 = streamReaderBufferProcessor.getAttributeValue(null, this.this$0.version.eprType.portName);
            this.serviceName = getElementTextAsQName(streamReaderBufferProcessor);
            if (this.serviceName != null && str3 != null)
              this.portName = new QName(this.serviceName.getNamespaceURI(), str3); 
          } else if (str1.equals(this.this$0.version.eprType.portTypeName)) {
            this.portTypeName = getElementTextAsQName(streamReaderBufferProcessor);
          } else if (!streamReaderBufferProcessor.getLocalName().equals(str)) {
            XMLStreamReaderUtil.skipElement(streamReaderBufferProcessor);
          } 
        } while (XMLStreamReaderUtil.nextElementContent(streamReaderBufferProcessor) == 1);
      } 
    }
    
    private QName getElementTextAsQName(StreamReaderBufferProcessor param1StreamReaderBufferProcessor) throws XMLStreamException {
      String str1 = param1StreamReaderBufferProcessor.getElementText().trim();
      String str2 = XmlUtil.getPrefix(str1);
      String str3 = XmlUtil.getLocalPart(str1);
      if (str3 != null)
        if (str2 != null) {
          String str = param1StreamReaderBufferProcessor.getNamespaceURI(str2);
          if (str != null)
            return new QName(str, str3, str2); 
        } else {
          return new QName(null, str3);
        }  
      return null;
    }
  }
  
  class SAXBufferProcessorImpl extends SAXBufferProcessor {
    private final String rootLocalName;
    
    private boolean root = true;
    
    public SAXBufferProcessorImpl(WSEndpointReference this$0, String param1String) {
      super(this$0.infoset, false);
      this.rootLocalName = param1String;
    }
    
    protected void processElement(String param1String1, String param1String2, String param1String3, boolean param1Boolean) {
      if (this.root) {
        this.root = false;
        if (param1String3.equals(param1String2)) {
          param1String3 = param1String2 = this.rootLocalName;
        } else {
          param1String2 = this.rootLocalName;
          int i = param1String3.indexOf(':');
          param1String3 = param1String3.substring(0, i + 1) + this.rootLocalName;
        } 
      } 
      super.processElement(param1String1, param1String2, param1String3, param1Boolean);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\WSEndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */