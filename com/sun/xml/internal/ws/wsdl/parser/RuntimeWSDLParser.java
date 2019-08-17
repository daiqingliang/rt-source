package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.BindingIDFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSDLLocator;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLFaultImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLInputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLMessageImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOperationImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLOutputImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartDescriptorImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortTypeImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLServiceImpl;
import com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLParserExtension;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.WsdlmodelMessages;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class RuntimeWSDLParser {
  private final EditableWSDLModel wsdlDoc;
  
  private String targetNamespace;
  
  private final Set<String> importedWSDLs = new HashSet();
  
  private final XMLEntityResolver resolver;
  
  private final PolicyResolver policyResolver;
  
  private final WSDLParserExtension extensionFacade;
  
  private final WSDLParserExtensionContextImpl context;
  
  List<WSDLParserExtension> extensions;
  
  Map<String, String> wsdldef_nsdecl = new HashMap();
  
  Map<String, String> service_nsdecl = new HashMap();
  
  Map<String, String> port_nsdecl = new HashMap();
  
  private static final Logger LOGGER = Logger.getLogger(RuntimeWSDLParser.class.getName());
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException { return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, Service.class, PolicyResolverFactory.create(), paramVarArgs); }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException { return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, paramClass, PolicyResolverFactory.create(), paramVarArgs); }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, @NotNull PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException { return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, Service.class, paramPolicyResolver, paramVarArgs); }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, @NotNull PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException { return parse(paramURL, paramSource, paramEntityResolver, paramBoolean, paramContainer, paramClass, paramPolicyResolver, false, paramVarArgs); }
  
  public static WSDLModel parse(@Nullable URL paramURL, @NotNull Source paramSource, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean1, Container paramContainer, Class paramClass, @NotNull PolicyResolver paramPolicyResolver, boolean paramBoolean2, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException {
    XMLEntityResolver.Parser parser;
    assert paramEntityResolver != null;
    RuntimeWSDLParser runtimeWSDLParser = new RuntimeWSDLParser(paramSource.getSystemId(), new EntityResolverWrapper(paramEntityResolver, paramBoolean2), paramBoolean1, paramContainer, paramPolicyResolver, paramVarArgs);
    try {
      parser = runtimeWSDLParser.resolveWSDL(paramURL, paramSource, paramClass);
      if (!hasWSDLDefinitions(parser.parser))
        throw new XMLStreamException(ClientMessages.RUNTIME_WSDLPARSER_INVALID_WSDL(parser.systemId, WSDLConstants.QNAME_DEFINITIONS, parser.parser.getName(), parser.parser.getLocation())); 
    } catch (XMLStreamException xMLStreamException) {
      if (paramURL == null)
        throw xMLStreamException; 
      return tryWithMex(runtimeWSDLParser, paramURL, paramEntityResolver, paramBoolean1, paramContainer, xMLStreamException, paramClass, paramPolicyResolver, paramVarArgs);
    } catch (IOException iOException) {
      if (paramURL == null)
        throw iOException; 
      return tryWithMex(runtimeWSDLParser, paramURL, paramEntityResolver, paramBoolean1, paramContainer, iOException, paramClass, paramPolicyResolver, paramVarArgs);
    } 
    runtimeWSDLParser.extensionFacade.start(runtimeWSDLParser.context);
    runtimeWSDLParser.parseWSDL(parser, false);
    runtimeWSDLParser.wsdlDoc.freeze();
    runtimeWSDLParser.extensionFacade.finished(runtimeWSDLParser.context);
    runtimeWSDLParser.extensionFacade.postFinished(runtimeWSDLParser.context);
    if (runtimeWSDLParser.wsdlDoc.getServices().isEmpty())
      throw new WebServiceException(ClientMessages.WSDL_CONTAINS_NO_SERVICE(paramURL)); 
    return runtimeWSDLParser.wsdlDoc;
  }
  
  private static WSDLModel tryWithMex(@NotNull RuntimeWSDLParser paramRuntimeWSDLParser, @NotNull URL paramURL, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Throwable paramThrowable, Class paramClass, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs) throws SAXException, XMLStreamException {
    ArrayList arrayList = new ArrayList();
    try {
      WSDLModel wSDLModel = paramRuntimeWSDLParser.parseUsingMex(paramURL, paramEntityResolver, paramBoolean, paramContainer, paramClass, paramPolicyResolver, paramVarArgs);
      if (wSDLModel == null)
        throw new WebServiceException(ClientMessages.FAILED_TO_PARSE(paramURL.toExternalForm(), paramThrowable.getMessage()), paramThrowable); 
      return wSDLModel;
    } catch (URISyntaxException uRISyntaxException) {
      arrayList.add(paramThrowable);
      arrayList.add(uRISyntaxException);
    } catch (IOException iOException) {
      arrayList.add(paramThrowable);
      arrayList.add(iOException);
    } 
    throw new InaccessibleWSDLException(arrayList);
  }
  
  private WSDLModel parseUsingMex(@NotNull URL paramURL, @NotNull EntityResolver paramEntityResolver, boolean paramBoolean, Container paramContainer, Class paramClass, PolicyResolver paramPolicyResolver, WSDLParserExtension[] paramArrayOfWSDLParserExtension) throws IOException, SAXException, XMLStreamException, URISyntaxException {
    MetaDataResolver metaDataResolver = null;
    ServiceDescriptor serviceDescriptor = null;
    RuntimeWSDLParser runtimeWSDLParser = null;
    for (MetadataResolverFactory metadataResolverFactory : ServiceFinder.find(MetadataResolverFactory.class)) {
      metaDataResolver = metadataResolverFactory.metadataResolver(paramEntityResolver);
      serviceDescriptor = metaDataResolver.resolve(paramURL.toURI());
      if (serviceDescriptor != null)
        break; 
    } 
    if (serviceDescriptor != null) {
      List list = serviceDescriptor.getWSDLs();
      runtimeWSDLParser = new RuntimeWSDLParser(paramURL.toExternalForm(), new MexEntityResolver(list), paramBoolean, paramContainer, paramPolicyResolver, paramArrayOfWSDLParserExtension);
      runtimeWSDLParser.extensionFacade.start(runtimeWSDLParser.context);
      for (Source source : list) {
        String str = source.getSystemId();
        XMLEntityResolver.Parser parser = runtimeWSDLParser.resolver.resolveEntity(null, str);
        runtimeWSDLParser.parseWSDL(parser, false);
      } 
    } 
    if ((metaDataResolver == null || serviceDescriptor == null) && (paramURL.getProtocol().equals("http") || paramURL.getProtocol().equals("https")) && paramURL.getQuery() == null) {
      String str = paramURL.toExternalForm();
      str = str + "?wsdl";
      paramURL = new URL(str);
      runtimeWSDLParser = new RuntimeWSDLParser(paramURL.toExternalForm(), new EntityResolverWrapper(paramEntityResolver), paramBoolean, paramContainer, paramPolicyResolver, paramArrayOfWSDLParserExtension);
      runtimeWSDLParser.extensionFacade.start(runtimeWSDLParser.context);
      XMLEntityResolver.Parser parser = resolveWSDL(paramURL, new StreamSource(paramURL.toExternalForm()), paramClass);
      runtimeWSDLParser.parseWSDL(parser, false);
    } 
    if (runtimeWSDLParser == null)
      return null; 
    runtimeWSDLParser.wsdlDoc.freeze();
    runtimeWSDLParser.extensionFacade.finished(runtimeWSDLParser.context);
    runtimeWSDLParser.extensionFacade.postFinished(runtimeWSDLParser.context);
    return runtimeWSDLParser.wsdlDoc;
  }
  
  private static boolean hasWSDLDefinitions(XMLStreamReader paramXMLStreamReader) {
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    return paramXMLStreamReader.getName().equals(WSDLConstants.QNAME_DEFINITIONS);
  }
  
  public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException {
    assert paramXMLEntityResolver != null;
    RuntimeWSDLParser runtimeWSDLParser = new RuntimeWSDLParser(paramParser.systemId.toExternalForm(), paramXMLEntityResolver, paramBoolean, paramContainer, paramPolicyResolver, paramVarArgs);
    runtimeWSDLParser.extensionFacade.start(runtimeWSDLParser.context);
    runtimeWSDLParser.parseWSDL(paramParser, false);
    runtimeWSDLParser.wsdlDoc.freeze();
    runtimeWSDLParser.extensionFacade.finished(runtimeWSDLParser.context);
    runtimeWSDLParser.extensionFacade.postFinished(runtimeWSDLParser.context);
    return runtimeWSDLParser.wsdlDoc;
  }
  
  public static WSDLModel parse(XMLEntityResolver.Parser paramParser, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, WSDLParserExtension... paramVarArgs) throws IOException, XMLStreamException, SAXException {
    assert paramXMLEntityResolver != null;
    RuntimeWSDLParser runtimeWSDLParser = new RuntimeWSDLParser(paramParser.systemId.toExternalForm(), paramXMLEntityResolver, paramBoolean, paramContainer, PolicyResolverFactory.create(), paramVarArgs);
    runtimeWSDLParser.extensionFacade.start(runtimeWSDLParser.context);
    runtimeWSDLParser.parseWSDL(paramParser, false);
    runtimeWSDLParser.wsdlDoc.freeze();
    runtimeWSDLParser.extensionFacade.finished(runtimeWSDLParser.context);
    runtimeWSDLParser.extensionFacade.postFinished(runtimeWSDLParser.context);
    return runtimeWSDLParser.wsdlDoc;
  }
  
  private RuntimeWSDLParser(@NotNull String paramString, XMLEntityResolver paramXMLEntityResolver, boolean paramBoolean, Container paramContainer, PolicyResolver paramPolicyResolver, WSDLParserExtension... paramVarArgs) {
    this.wsdlDoc = (paramString != null) ? new WSDLModelImpl(paramString) : new WSDLModelImpl();
    this.resolver = paramXMLEntityResolver;
    this.policyResolver = paramPolicyResolver;
    this.extensions = new ArrayList();
    this.context = new WSDLParserExtensionContextImpl(this.wsdlDoc, paramBoolean, paramContainer, paramPolicyResolver);
    boolean bool = false;
    for (WSDLParserExtension wSDLParserExtension : paramVarArgs) {
      if (wSDLParserExtension instanceof com.sun.xml.internal.ws.api.wsdl.parser.PolicyWSDLParserExtension)
        bool = true; 
      register(wSDLParserExtension);
    } 
    if (!bool)
      register(new PolicyWSDLParserExtension()); 
    register(new MemberSubmissionAddressingWSDLParserExtension());
    register(new W3CAddressingWSDLParserExtension());
    register(new W3CAddressingMetadataWSDLParserExtension());
    this.extensionFacade = new WSDLParserExtensionFacade((WSDLParserExtension[])this.extensions.toArray(new WSDLParserExtension[0]));
  }
  
  private XMLEntityResolver.Parser resolveWSDL(@Nullable URL paramURL, @NotNull Source paramSource, Class paramClass) throws IOException, SAXException, XMLStreamException {
    String str = paramSource.getSystemId();
    XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, str);
    if (parser == null && paramURL != null) {
      String str1 = paramURL.toExternalForm();
      parser = this.resolver.resolveEntity(null, str1);
      if (parser == null && paramClass != null) {
        URL uRL = paramClass.getResource(".");
        if (uRL != null) {
          String str2 = uRL.toExternalForm();
          if (str1.startsWith(str2))
            parser = this.resolver.resolveEntity(null, str1.substring(str2.length())); 
        } 
      } 
    } 
    if (parser == null) {
      if (isKnownReadableSource(paramSource)) {
        parser = new XMLEntityResolver.Parser(paramURL, createReader(paramSource));
      } else if (paramURL != null) {
        parser = new XMLEntityResolver.Parser(paramURL, createReader(paramURL, paramClass));
      } 
      if (parser == null)
        parser = new XMLEntityResolver.Parser(paramURL, createReader(paramSource)); 
    } 
    return parser;
  }
  
  private boolean isKnownReadableSource(Source paramSource) { return (paramSource instanceof StreamSource) ? ((((StreamSource)paramSource).getInputStream() != null || ((StreamSource)paramSource).getReader() != null)) : false; }
  
  private XMLStreamReader createReader(@NotNull Source paramSource) throws XMLStreamException { return new TidyXMLStreamReader(SourceReaderFactory.createSourceReader(paramSource, true), null); }
  
  private void parseImport(@NotNull URL paramURL) throws XMLStreamException, IOException, SAXException {
    String str = paramURL.toExternalForm();
    XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, str);
    if (parser == null)
      parser = new XMLEntityResolver.Parser(paramURL, createReader(paramURL)); 
    parseWSDL(parser, true);
  }
  
  private void parseWSDL(XMLEntityResolver.Parser paramParser, boolean paramBoolean) throws XMLStreamException, IOException, SAXException {
    xMLStreamReader = paramParser.parser;
    try {
      if (paramParser.systemId != null && !this.importedWSDLs.add(paramParser.systemId.toExternalForm()))
        return; 
      if (xMLStreamReader.getEventType() == 7)
        XMLStreamReaderUtil.nextElementContent(xMLStreamReader); 
      if (WSDLConstants.QNAME_DEFINITIONS.equals(xMLStreamReader.getName()))
        readNSDecl(this.wsdldef_nsdecl, xMLStreamReader); 
      if (xMLStreamReader.getEventType() != 8 && xMLStreamReader.getName().equals(WSDLConstants.QNAME_SCHEMA) && paramBoolean) {
        LOGGER.warning(WsdlmodelMessages.WSDL_IMPORT_SHOULD_BE_WSDL(paramParser.systemId));
        return;
      } 
      String str1 = ParserUtil.getMandatoryNonEmptyAttribute(xMLStreamReader, "targetNamespace");
      String str2 = this.targetNamespace;
      this.targetNamespace = str1;
      while (XMLStreamReaderUtil.nextElementContent(xMLStreamReader) != 2 && xMLStreamReader.getEventType() != 8) {
        QName qName = xMLStreamReader.getName();
        if (WSDLConstants.QNAME_IMPORT.equals(qName)) {
          parseImport(paramParser.systemId, xMLStreamReader);
          continue;
        } 
        if (WSDLConstants.QNAME_MESSAGE.equals(qName)) {
          parseMessage(xMLStreamReader);
          continue;
        } 
        if (WSDLConstants.QNAME_PORT_TYPE.equals(qName)) {
          parsePortType(xMLStreamReader);
          continue;
        } 
        if (WSDLConstants.QNAME_BINDING.equals(qName)) {
          parseBinding(xMLStreamReader);
          continue;
        } 
        if (WSDLConstants.QNAME_SERVICE.equals(qName)) {
          parseService(xMLStreamReader);
          continue;
        } 
        this.extensionFacade.definitionsElements(xMLStreamReader);
      } 
      this.targetNamespace = str2;
    } finally {
      this.wsdldef_nsdecl = new HashMap();
      xMLStreamReader.close();
    } 
  }
  
  private void parseService(XMLStreamReader paramXMLStreamReader) {
    this.service_nsdecl.putAll(this.wsdldef_nsdecl);
    readNSDecl(this.service_nsdecl, paramXMLStreamReader);
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLServiceImpl wSDLServiceImpl = new WSDLServiceImpl(paramXMLStreamReader, this.wsdlDoc, new QName(this.targetNamespace, str));
    this.extensionFacade.serviceAttributes(wSDLServiceImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_PORT.equals(qName)) {
        parsePort(paramXMLStreamReader, wSDLServiceImpl);
        if (paramXMLStreamReader.getEventType() != 2)
          XMLStreamReaderUtil.next(paramXMLStreamReader); 
        continue;
      } 
      this.extensionFacade.serviceElements(wSDLServiceImpl, paramXMLStreamReader);
    } 
    this.wsdlDoc.addService(wSDLServiceImpl);
    this.service_nsdecl = new HashMap();
  }
  
  private void parsePort(XMLStreamReader paramXMLStreamReader, EditableWSDLService paramEditableWSDLService) {
    this.port_nsdecl.putAll(this.service_nsdecl);
    readNSDecl(this.port_nsdecl, paramXMLStreamReader);
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "binding");
    QName qName1 = ParserUtil.getQName(paramXMLStreamReader, str2);
    QName qName2 = new QName(paramEditableWSDLService.getName().getNamespaceURI(), str1);
    WSDLPortImpl wSDLPortImpl = new WSDLPortImpl(paramXMLStreamReader, paramEditableWSDLService, qName2, qName1);
    this.extensionFacade.portAttributes(wSDLPortImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (SOAPConstants.QNAME_ADDRESS.equals(qName) || SOAPConstants.QNAME_SOAP12ADDRESS.equals(qName)) {
        String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "location");
        if (str != null)
          try {
            wSDLPortImpl.setAddress(new EndpointAddress(str));
          } catch (URISyntaxException uRISyntaxException) {} 
        XMLStreamReaderUtil.next(paramXMLStreamReader);
        continue;
      } 
      if (AddressingVersion.W3C.nsUri.equals(qName.getNamespaceURI()) && "EndpointReference".equals(qName.getLocalPart()))
        try {
          StreamReaderBufferCreator streamReaderBufferCreator = new StreamReaderBufferCreator(new MutableXMLStreamBuffer());
          XMLStreamBufferMark xMLStreamBufferMark = new XMLStreamBufferMark(this.port_nsdecl, streamReaderBufferCreator);
          streamReaderBufferCreator.createElementFragment(paramXMLStreamReader, false);
          WSEndpointReference wSEndpointReference = new WSEndpointReference(xMLStreamBufferMark, AddressingVersion.W3C);
          wSDLPortImpl.setEPR(wSEndpointReference);
          if (paramXMLStreamReader.getEventType() == 2 && paramXMLStreamReader.getName().equals(WSDLConstants.QNAME_PORT))
            break; 
          continue;
        } catch (XMLStreamException xMLStreamException) {
          throw new WebServiceException(xMLStreamException);
        }  
      this.extensionFacade.portElements(wSDLPortImpl, paramXMLStreamReader);
    } 
    if (wSDLPortImpl.getAddress() == null)
      try {
        wSDLPortImpl.setAddress(new EndpointAddress(""));
      } catch (URISyntaxException uRISyntaxException) {} 
    paramEditableWSDLService.put(qName2, wSDLPortImpl);
    this.port_nsdecl = new HashMap();
  }
  
  private void parseBinding(XMLStreamReader paramXMLStreamReader) {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "type");
    if (str1 == null || str2 == null) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    } 
    WSDLBoundPortTypeImpl wSDLBoundPortTypeImpl = new WSDLBoundPortTypeImpl(paramXMLStreamReader, this.wsdlDoc, new QName(this.targetNamespace, str1), ParserUtil.getQName(paramXMLStreamReader, str2));
    this.extensionFacade.bindingAttributes(wSDLBoundPortTypeImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (WSDLConstants.NS_SOAP_BINDING.equals(qName)) {
        String str3 = paramXMLStreamReader.getAttributeValue(null, "transport");
        wSDLBoundPortTypeImpl.setBindingId(createBindingId(str3, SOAPVersion.SOAP_11));
        String str4 = paramXMLStreamReader.getAttributeValue(null, "style");
        if (str4 != null && str4.equals("rpc")) {
          wSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.RPC);
        } else {
          wSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        } 
        goToEnd(paramXMLStreamReader);
        continue;
      } 
      if (WSDLConstants.NS_SOAP12_BINDING.equals(qName)) {
        String str3 = paramXMLStreamReader.getAttributeValue(null, "transport");
        wSDLBoundPortTypeImpl.setBindingId(createBindingId(str3, SOAPVersion.SOAP_12));
        String str4 = paramXMLStreamReader.getAttributeValue(null, "style");
        if (str4 != null && str4.equals("rpc")) {
          wSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.RPC);
        } else {
          wSDLBoundPortTypeImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        } 
        goToEnd(paramXMLStreamReader);
        continue;
      } 
      if (WSDLConstants.QNAME_OPERATION.equals(qName)) {
        parseBindingOperation(paramXMLStreamReader, wSDLBoundPortTypeImpl);
        continue;
      } 
      this.extensionFacade.bindingElements(wSDLBoundPortTypeImpl, paramXMLStreamReader);
    } 
  }
  
  private static BindingID createBindingId(String paramString, SOAPVersion paramSOAPVersion) {
    if (!paramString.equals("http://schemas.xmlsoap.org/soap/http"))
      for (BindingIDFactory bindingIDFactory : ServiceFinder.find(BindingIDFactory.class)) {
        BindingID bindingID = bindingIDFactory.create(paramString, paramSOAPVersion);
        if (bindingID != null)
          return bindingID; 
      }  
    return paramSOAPVersion.equals(SOAPVersion.SOAP_11) ? BindingID.SOAP11_HTTP : BindingID.SOAP12_HTTP;
  }
  
  private void parseBindingOperation(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundPortType paramEditableWSDLBoundPortType) {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str == null) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    } 
    QName qName = new QName(paramEditableWSDLBoundPortType.getPortTypeName().getNamespaceURI(), str);
    WSDLBoundOperationImpl wSDLBoundOperationImpl = new WSDLBoundOperationImpl(paramXMLStreamReader, paramEditableWSDLBoundPortType, qName);
    paramEditableWSDLBoundPortType.put(qName, wSDLBoundOperationImpl);
    this.extensionFacade.bindingOperationAttributes(wSDLBoundOperationImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName1 = paramXMLStreamReader.getName();
      String str1 = null;
      if (WSDLConstants.QNAME_INPUT.equals(qName1)) {
        parseInputBinding(paramXMLStreamReader, wSDLBoundOperationImpl);
      } else if (WSDLConstants.QNAME_OUTPUT.equals(qName1)) {
        parseOutputBinding(paramXMLStreamReader, wSDLBoundOperationImpl);
      } else if (WSDLConstants.QNAME_FAULT.equals(qName1)) {
        parseFaultBinding(paramXMLStreamReader, wSDLBoundOperationImpl);
      } else if (SOAPConstants.QNAME_OPERATION.equals(qName1) || SOAPConstants.QNAME_SOAP12OPERATION.equals(qName1)) {
        str1 = paramXMLStreamReader.getAttributeValue(null, "style");
        String str2 = paramXMLStreamReader.getAttributeValue(null, "soapAction");
        if (str2 != null)
          wSDLBoundOperationImpl.setSoapAction(str2); 
        goToEnd(paramXMLStreamReader);
      } else {
        this.extensionFacade.bindingOperationElements(wSDLBoundOperationImpl, paramXMLStreamReader);
      } 
      if (str1 != null) {
        if (str1.equals("rpc")) {
          wSDLBoundOperationImpl.setStyle(SOAPBinding.Style.RPC);
          continue;
        } 
        wSDLBoundOperationImpl.setStyle(SOAPBinding.Style.DOCUMENT);
        continue;
      } 
      wSDLBoundOperationImpl.setStyle(paramEditableWSDLBoundPortType.getStyle());
    } 
  }
  
  private void parseInputBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation) {
    boolean bool = false;
    this.extensionFacade.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if ((SOAPConstants.QNAME_BODY.equals(qName) || SOAPConstants.QNAME_SOAP12BODY.equals(qName)) && !bool) {
        bool = true;
        paramEditableWSDLBoundOperation.setInputExplicitBodyParts(parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.INPUT));
        goToEnd(paramXMLStreamReader);
        continue;
      } 
      if (SOAPConstants.QNAME_HEADER.equals(qName) || SOAPConstants.QNAME_SOAP12HEADER.equals(qName)) {
        parseSOAPHeaderBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getInputParts());
        continue;
      } 
      if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(qName)) {
        parseMimeMultipartBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.INPUT);
        continue;
      } 
      this.extensionFacade.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    } 
  }
  
  private void parseOutputBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation) {
    boolean bool = false;
    this.extensionFacade.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if ((SOAPConstants.QNAME_BODY.equals(qName) || SOAPConstants.QNAME_SOAP12BODY.equals(qName)) && !bool) {
        bool = true;
        paramEditableWSDLBoundOperation.setOutputExplicitBodyParts(parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.OUTPUT));
        goToEnd(paramXMLStreamReader);
        continue;
      } 
      if (SOAPConstants.QNAME_HEADER.equals(qName) || SOAPConstants.QNAME_SOAP12HEADER.equals(qName)) {
        parseSOAPHeaderBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getOutputParts());
        continue;
      } 
      if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(qName)) {
        parseMimeMultipartBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, BindingMode.OUTPUT);
        continue;
      } 
      this.extensionFacade.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    } 
  }
  
  private void parseFaultBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation) {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLBoundFaultImpl wSDLBoundFaultImpl = new WSDLBoundFaultImpl(paramXMLStreamReader, str, paramEditableWSDLBoundOperation);
    paramEditableWSDLBoundOperation.addFault(wSDLBoundFaultImpl);
    this.extensionFacade.bindingOperationFaultAttributes(wSDLBoundFaultImpl, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      this.extensionFacade.bindingOperationFaultElements(wSDLBoundFaultImpl, paramXMLStreamReader); 
  }
  
  private static boolean parseSOAPBodyBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode) {
    String str = paramXMLStreamReader.getAttributeValue(null, "namespace");
    if (paramBindingMode == BindingMode.INPUT) {
      paramEditableWSDLBoundOperation.setRequestNamespace(str);
      return parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getInputParts());
    } 
    paramEditableWSDLBoundOperation.setResponseNamespace(str);
    return parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation.getOutputParts());
  }
  
  private static boolean parseSOAPBodyBinding(XMLStreamReader paramXMLStreamReader, Map<String, ParameterBinding> paramMap) {
    String str = paramXMLStreamReader.getAttributeValue(null, "parts");
    if (str != null) {
      List list = XmlUtil.parseTokenList(str);
      if (list.isEmpty()) {
        paramMap.put(" ", ParameterBinding.BODY);
      } else {
        for (String str1 : list)
          paramMap.put(str1, ParameterBinding.BODY); 
      } 
      return true;
    } 
    return false;
  }
  
  private static void parseSOAPHeaderBinding(XMLStreamReader paramXMLStreamReader, Map<String, ParameterBinding> paramMap) {
    String str = paramXMLStreamReader.getAttributeValue(null, "part");
    if (str == null || str.equals(""))
      return; 
    paramMap.put(str, ParameterBinding.HEADER);
    goToEnd(paramXMLStreamReader);
  }
  
  private static void parseMimeMultipartBinding(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode) {
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (MIMEConstants.QNAME_PART.equals(qName)) {
        parseMIMEPart(paramXMLStreamReader, paramEditableWSDLBoundOperation, paramBindingMode);
        continue;
      } 
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    } 
  }
  
  private static void parseMIMEPart(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundOperation paramEditableWSDLBoundOperation, BindingMode paramBindingMode) {
    boolean bool = false;
    Map map = null;
    if (paramBindingMode == BindingMode.INPUT) {
      map = paramEditableWSDLBoundOperation.getInputParts();
    } else if (paramBindingMode == BindingMode.OUTPUT) {
      map = paramEditableWSDLBoundOperation.getOutputParts();
    } else if (paramBindingMode == BindingMode.FAULT) {
      map = paramEditableWSDLBoundOperation.getFaultParts();
    } 
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (SOAPConstants.QNAME_BODY.equals(qName) && !bool) {
        bool = true;
        parseSOAPBodyBinding(paramXMLStreamReader, paramEditableWSDLBoundOperation, paramBindingMode);
        XMLStreamReaderUtil.next(paramXMLStreamReader);
        continue;
      } 
      if (SOAPConstants.QNAME_HEADER.equals(qName)) {
        bool = true;
        parseSOAPHeaderBinding(paramXMLStreamReader, map);
        XMLStreamReaderUtil.next(paramXMLStreamReader);
        continue;
      } 
      if (MIMEConstants.QNAME_CONTENT.equals(qName)) {
        String str1 = paramXMLStreamReader.getAttributeValue(null, "part");
        String str2 = paramXMLStreamReader.getAttributeValue(null, "type");
        if (str1 == null || str2 == null) {
          XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
          continue;
        } 
        ParameterBinding parameterBinding = ParameterBinding.createAttachment(str2);
        if (map != null && parameterBinding != null && str1 != null)
          map.put(str1, parameterBinding); 
        XMLStreamReaderUtil.next(paramXMLStreamReader);
        continue;
      } 
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    } 
  }
  
  protected void parseImport(@Nullable URL paramURL, XMLStreamReader paramXMLStreamReader) throws IOException, SAXException, XMLStreamException {
    URL uRL;
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "location");
    if (paramURL != null) {
      uRL = new URL(paramURL, str);
    } else {
      uRL = new URL(str);
    } 
    parseImport(uRL);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader); 
  }
  
  private void parsePortType(XMLStreamReader paramXMLStreamReader) {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str == null) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    } 
    WSDLPortTypeImpl wSDLPortTypeImpl = new WSDLPortTypeImpl(paramXMLStreamReader, this.wsdlDoc, new QName(this.targetNamespace, str));
    this.extensionFacade.portTypeAttributes(wSDLPortTypeImpl, paramXMLStreamReader);
    this.wsdlDoc.addPortType(wSDLPortTypeImpl);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_OPERATION.equals(qName)) {
        parsePortTypeOperation(paramXMLStreamReader, wSDLPortTypeImpl);
        continue;
      } 
      this.extensionFacade.portTypeElements(wSDLPortTypeImpl, paramXMLStreamReader);
    } 
  }
  
  private void parsePortTypeOperation(XMLStreamReader paramXMLStreamReader, EditableWSDLPortType paramEditableWSDLPortType) {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    if (str1 == null) {
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
      return;
    } 
    QName qName = new QName(paramEditableWSDLPortType.getName().getNamespaceURI(), str1);
    WSDLOperationImpl wSDLOperationImpl = new WSDLOperationImpl(paramXMLStreamReader, paramEditableWSDLPortType, qName);
    this.extensionFacade.portTypeOperationAttributes(wSDLOperationImpl, paramXMLStreamReader);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "parameterOrder");
    wSDLOperationImpl.setParameterOrder(str2);
    paramEditableWSDLPortType.put(str1, wSDLOperationImpl);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName1 = paramXMLStreamReader.getName();
      if (qName1.equals(WSDLConstants.QNAME_INPUT)) {
        parsePortTypeOperationInput(paramXMLStreamReader, wSDLOperationImpl);
        continue;
      } 
      if (qName1.equals(WSDLConstants.QNAME_OUTPUT)) {
        parsePortTypeOperationOutput(paramXMLStreamReader, wSDLOperationImpl);
        continue;
      } 
      if (qName1.equals(WSDLConstants.QNAME_FAULT)) {
        parsePortTypeOperationFault(paramXMLStreamReader, wSDLOperationImpl);
        continue;
      } 
      this.extensionFacade.portTypeOperationElements(wSDLOperationImpl, paramXMLStreamReader);
    } 
  }
  
  private void parsePortTypeOperationFault(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation) {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "message");
    QName qName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLFaultImpl wSDLFaultImpl = new WSDLFaultImpl(paramXMLStreamReader, str2, qName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.addFault(wSDLFaultImpl);
    this.extensionFacade.portTypeOperationFaultAttributes(wSDLFaultImpl, paramXMLStreamReader);
    this.extensionFacade.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      this.extensionFacade.portTypeOperationFaultElements(wSDLFaultImpl, paramXMLStreamReader); 
  }
  
  private void parsePortTypeOperationInput(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation) {
    String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "message");
    QName qName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "name");
    WSDLInputImpl wSDLInputImpl = new WSDLInputImpl(paramXMLStreamReader, str2, qName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.setInput(wSDLInputImpl);
    this.extensionFacade.portTypeOperationInputAttributes(wSDLInputImpl, paramXMLStreamReader);
    this.extensionFacade.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      this.extensionFacade.portTypeOperationInputElements(wSDLInputImpl, paramXMLStreamReader); 
  }
  
  private void parsePortTypeOperationOutput(XMLStreamReader paramXMLStreamReader, EditableWSDLOperation paramEditableWSDLOperation) {
    String str1 = ParserUtil.getAttribute(paramXMLStreamReader, "message");
    QName qName = ParserUtil.getQName(paramXMLStreamReader, str1);
    String str2 = ParserUtil.getAttribute(paramXMLStreamReader, "name");
    WSDLOutputImpl wSDLOutputImpl = new WSDLOutputImpl(paramXMLStreamReader, str2, qName, paramEditableWSDLOperation);
    paramEditableWSDLOperation.setOutput(wSDLOutputImpl);
    this.extensionFacade.portTypeOperationOutputAttributes(wSDLOutputImpl, paramXMLStreamReader);
    this.extensionFacade.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader);
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      this.extensionFacade.portTypeOperationOutputElements(wSDLOutputImpl, paramXMLStreamReader); 
  }
  
  private void parseMessage(XMLStreamReader paramXMLStreamReader) {
    String str = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
    WSDLMessageImpl wSDLMessageImpl = new WSDLMessageImpl(paramXMLStreamReader, new QName(this.targetNamespace, str));
    this.extensionFacade.messageAttributes(wSDLMessageImpl, paramXMLStreamReader);
    byte b = 0;
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      QName qName = paramXMLStreamReader.getName();
      if (WSDLConstants.QNAME_PART.equals(qName)) {
        String str1 = ParserUtil.getMandatoryNonEmptyAttribute(paramXMLStreamReader, "name");
        String str2 = null;
        int i = paramXMLStreamReader.getAttributeCount();
        WSDLDescriptorKind wSDLDescriptorKind = WSDLDescriptorKind.ELEMENT;
        for (byte b1 = 0; b1 < i; b1++) {
          QName qName1 = paramXMLStreamReader.getAttributeName(b1);
          if (qName1.getLocalPart().equals("element")) {
            wSDLDescriptorKind = WSDLDescriptorKind.ELEMENT;
          } else if (qName1.getLocalPart().equals("type")) {
            wSDLDescriptorKind = WSDLDescriptorKind.TYPE;
          } 
          if (qName1.getLocalPart().equals("element") || qName1.getLocalPart().equals("type")) {
            str2 = paramXMLStreamReader.getAttributeValue(b1);
            break;
          } 
        } 
        if (str2 != null) {
          WSDLPartImpl wSDLPartImpl = new WSDLPartImpl(paramXMLStreamReader, str1, b, new WSDLPartDescriptorImpl(paramXMLStreamReader, ParserUtil.getQName(paramXMLStreamReader, str2), wSDLDescriptorKind));
          wSDLMessageImpl.add(wSDLPartImpl);
        } 
        if (paramXMLStreamReader.getEventType() != 2)
          goToEnd(paramXMLStreamReader); 
        continue;
      } 
      this.extensionFacade.messageElements(wSDLMessageImpl, paramXMLStreamReader);
    } 
    this.wsdlDoc.addMessage(wSDLMessageImpl);
    if (paramXMLStreamReader.getEventType() != 2)
      goToEnd(paramXMLStreamReader); 
  }
  
  private static void goToEnd(XMLStreamReader paramXMLStreamReader) {
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2)
      XMLStreamReaderUtil.skipElement(paramXMLStreamReader); 
  }
  
  private static XMLStreamReader createReader(URL paramURL) throws IOException, XMLStreamException { return createReader(paramURL, null); }
  
  private static XMLStreamReader createReader(URL paramURL, Class<Service> paramClass) throws IOException, XMLStreamException {
    FilterInputStream filterInputStream;
    try {
      filterInputStream = paramURL.openStream();
    } catch (IOException iOException) {
      if (paramClass != null) {
        WSDLLocator wSDLLocator = (WSDLLocator)ContainerResolver.getInstance().getContainer().getSPI(WSDLLocator.class);
        if (wSDLLocator != null) {
          String str1 = paramURL.toExternalForm();
          URL uRL = paramClass.getResource(".");
          String str2 = paramURL.getPath();
          if (uRL != null) {
            String str = uRL.toExternalForm();
            if (str1.startsWith(str))
              str2 = str1.substring(str.length()); 
          } 
          paramURL = wSDLLocator.locateWSDL(paramClass, str2);
          if (paramURL != null) {
            filterInputStream = new FilterInputStream(paramURL.openStream()) {
                boolean closed;
                
                public void close() throws IOException {
                  if (!this.closed) {
                    this.closed = true;
                    byte[] arrayOfByte = new byte[8192];
                    while (read(arrayOfByte) != -1);
                    super.close();
                  } 
                }
              };
          } else {
            throw iOException;
          } 
        } else {
          throw iOException;
        } 
      } else {
        throw iOException;
      } 
    } 
    return new TidyXMLStreamReader(XMLStreamReaderFactory.create(paramURL.toExternalForm(), filterInputStream, false), filterInputStream);
  }
  
  private void register(WSDLParserExtension paramWSDLParserExtension) { this.extensions.add(new FoolProofParserExtension(paramWSDLParserExtension)); }
  
  private static void readNSDecl(Map<String, String> paramMap, XMLStreamReader paramXMLStreamReader) {
    if (paramXMLStreamReader.getNamespaceCount() > 0)
      for (byte b = 0; b < paramXMLStreamReader.getNamespaceCount(); b++)
        paramMap.put(paramXMLStreamReader.getNamespacePrefix(b), paramXMLStreamReader.getNamespaceURI(b));  
  }
  
  private enum BindingMode {
    INPUT, OUTPUT, FAULT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\RuntimeWSDLParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */