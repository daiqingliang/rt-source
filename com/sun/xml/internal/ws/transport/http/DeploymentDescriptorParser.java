package com.sun.xml.internal.ws.transport.http;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.streaming.Attributes;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;

public class DeploymentDescriptorParser<A> extends Object {
  public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
  
  public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
  
  public static final QName QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
  
  public static final QName QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
  
  public static final QName QNAME_EXT_METADA = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "external-metadata");
  
  public static final String ATTR_FILE = "file";
  
  public static final String ATTR_RESOURCE = "resource";
  
  public static final String ATTR_VERSION = "version";
  
  public static final String ATTR_NAME = "name";
  
  public static final String ATTR_IMPLEMENTATION = "implementation";
  
  public static final String ATTR_WSDL = "wsdl";
  
  public static final String ATTR_SERVICE = "service";
  
  public static final String ATTR_PORT = "port";
  
  public static final String ATTR_URL_PATTERN = "url-pattern";
  
  public static final String ATTR_ENABLE_MTOM = "enable-mtom";
  
  public static final String ATTR_MTOM_THRESHOLD_VALUE = "mtom-threshold-value";
  
  public static final String ATTR_BINDING = "binding";
  
  public static final String ATTR_DATABINDING = "databinding";
  
  public static final List<String> ATTRVALUE_SUPPORTED_VERSIONS = Arrays.asList(new String[] { "2.0", "2.1" });
  
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.http");
  
  private final Container container;
  
  private final ClassLoader classLoader;
  
  private final ResourceLoader loader;
  
  private final AdapterFactory<A> adapterFactory;
  
  private final Set<String> names = new HashSet();
  
  private final Map<String, SDDocumentSource> docs = new HashMap();
  
  public DeploymentDescriptorParser(ClassLoader paramClassLoader, ResourceLoader paramResourceLoader, Container paramContainer, AdapterFactory<A> paramAdapterFactory) throws MalformedURLException {
    this.classLoader = paramClassLoader;
    this.loader = paramResourceLoader;
    this.container = paramContainer;
    this.adapterFactory = paramAdapterFactory;
    collectDocs("/WEB-INF/wsdl/");
    logger.log(Level.FINE, "war metadata={0}", this.docs);
  }
  
  @NotNull
  public List<A> parse(String paramString, InputStream paramInputStream) {
    tidyXMLStreamReader = null;
    try {
      tidyXMLStreamReader = new TidyXMLStreamReader(XMLStreamReaderFactory.create(paramString, paramInputStream, true), paramInputStream);
      XMLStreamReaderUtil.nextElementContent(tidyXMLStreamReader);
      return parseAdapters(tidyXMLStreamReader);
    } finally {
      if (tidyXMLStreamReader != null)
        try {
          tidyXMLStreamReader.close();
        } catch (XMLStreamException xMLStreamException) {
          throw new ServerRtException("runtime.parser.xmlReader", new Object[] { xMLStreamException });
        }  
      try {
        paramInputStream.close();
      } catch (IOException iOException) {}
    } 
  }
  
  @NotNull
  public List<A> parse(File paramFile) throws IOException {
    fileInputStream = new FileInputStream(paramFile);
    try {
      return parse(paramFile.getPath(), fileInputStream);
    } finally {
      fileInputStream.close();
    } 
  }
  
  private void collectDocs(String paramString) throws MalformedURLException {
    Set set = this.loader.getResourcePaths(paramString);
    if (set != null)
      for (String str : set) {
        if (str.endsWith("/")) {
          if (str.endsWith("/CVS/") || str.endsWith("/.svn/"))
            continue; 
          collectDocs(str);
          continue;
        } 
        URL uRL = this.loader.getResource(str);
        this.docs.put(uRL.toString(), SDDocumentSource.create(uRL));
      }  
  }
  
  private List<A> parseAdapters(XMLStreamReader paramXMLStreamReader) {
    if (!paramXMLStreamReader.getName().equals(QNAME_ENDPOINTS))
      failWithFullName("runtime.parser.invalidElement", paramXMLStreamReader); 
    ArrayList arrayList = new ArrayList();
    Attributes attributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
    String str = getMandatoryNonEmptyAttribute(paramXMLStreamReader, attributes, "version");
    if (!ATTRVALUE_SUPPORTED_VERSIONS.contains(str))
      failWithLocalName("runtime.parser.invalidVersionNumber", paramXMLStreamReader, str); 
    while (XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader) != 2) {
      if (paramXMLStreamReader.getName().equals(QNAME_ENDPOINT)) {
        attributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
        String str1 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, attributes, "name");
        if (!this.names.add(str1))
          logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_NAME()); 
        String str2 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, attributes, "implementation");
        Class clazz = getImplementorClass(str2, paramXMLStreamReader);
        MetadataReader metadataReader = null;
        ExternalMetadataFeature externalMetadataFeature = null;
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getEventType() != 2) {
          externalMetadataFeature = configureExternalMetadataReader(paramXMLStreamReader);
          if (externalMetadataFeature != null)
            metadataReader = externalMetadataFeature.getMetadataReader(clazz.getClassLoader(), false); 
        } 
        QName qName1 = getQNameAttribute(attributes, "service");
        if (qName1 == null)
          qName1 = EndpointFactory.getDefaultServiceName(clazz, metadataReader); 
        QName qName2 = getQNameAttribute(attributes, "port");
        if (qName2 == null)
          qName2 = EndpointFactory.getDefaultPortName(qName1, clazz, metadataReader); 
        String str3 = getAttribute(attributes, "enable-mtom");
        String str4 = getAttribute(attributes, "mtom-threshold-value");
        String str5 = getAttribute(attributes, "databinding");
        String str6 = getAttribute(attributes, "binding");
        if (str6 != null)
          str6 = getBindingIdForToken(str6); 
        WSBinding wSBinding = createBinding(str6, clazz, str3, str4, str5);
        if (externalMetadataFeature != null)
          wSBinding.getFeatures().mergeFeatures(new WebServiceFeature[] { externalMetadataFeature }, true); 
        String str7 = getMandatoryNonEmptyAttribute(paramXMLStreamReader, attributes, "url-pattern");
        boolean bool = setHandlersAndRoles(wSBinding, paramXMLStreamReader, qName1, qName2);
        EndpointFactory.verifyImplementorClass(clazz, metadataReader);
        SDDocumentSource sDDocumentSource = getPrimaryWSDL(paramXMLStreamReader, attributes, clazz, metadataReader);
        WSEndpoint wSEndpoint = WSEndpoint.create(clazz, !bool, null, qName1, qName2, this.container, wSBinding, sDDocumentSource, this.docs.values(), createEntityResolver(), false);
        arrayList.add(this.adapterFactory.createAdapter(str1, str7, wSEndpoint));
        continue;
      } 
      failWithLocalName("runtime.parser.invalidElement", paramXMLStreamReader);
    } 
    return arrayList;
  }
  
  private static WSBinding createBinding(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4) {
    BindingID bindingID;
    WebServiceFeatureList webServiceFeatureList;
    MTOMFeature mTOMFeature = null;
    if (paramString2 != null)
      if (paramString3 != null) {
        mTOMFeature = new MTOMFeature(Boolean.valueOf(paramString2).booleanValue(), Integer.valueOf(paramString3).intValue());
      } else {
        mTOMFeature = new MTOMFeature(Boolean.valueOf(paramString2).booleanValue());
      }  
    if (paramString1 != null) {
      bindingID = BindingID.parse(paramString1);
      webServiceFeatureList = bindingID.createBuiltinFeatureList();
      if (checkMtomConflict((MTOMFeature)webServiceFeatureList.get(MTOMFeature.class), mTOMFeature))
        throw new ServerRtException(ServerMessages.DD_MTOM_CONFLICT(paramString1, paramString2), new Object[0]); 
    } else {
      bindingID = BindingID.parse(paramClass);
      webServiceFeatureList = new WebServiceFeatureList();
      if (mTOMFeature != null)
        webServiceFeatureList.add(mTOMFeature); 
      webServiceFeatureList.addAll(bindingID.createBuiltinFeatureList());
    } 
    if (paramString4 != null)
      webServiceFeatureList.add(new DatabindingModeFeature(paramString4)); 
    return bindingID.createBinding(webServiceFeatureList.toArray());
  }
  
  private static boolean checkMtomConflict(MTOMFeature paramMTOMFeature1, MTOMFeature paramMTOMFeature2) { return (paramMTOMFeature1 == null || paramMTOMFeature2 == null) ? false : (paramMTOMFeature1.isEnabled() ^ paramMTOMFeature2.isEnabled()); }
  
  @NotNull
  public static String getBindingIdForToken(@NotNull String paramString) { return paramString.equals("##SOAP11_HTTP") ? "http://schemas.xmlsoap.org/wsdl/soap/http" : (paramString.equals("##SOAP11_HTTP_MTOM") ? "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true" : (paramString.equals("##SOAP12_HTTP") ? "http://www.w3.org/2003/05/soap/bindings/HTTP/" : (paramString.equals("##SOAP12_HTTP_MTOM") ? "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true" : (paramString.equals("##XML_HTTP") ? "http://www.w3.org/2004/08/wsdl/http" : paramString)))); }
  
  private SDDocumentSource getPrimaryWSDL(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, Class<?> paramClass, MetadataReader paramMetadataReader) {
    String str = getAttribute(paramAttributes, "wsdl");
    if (str == null)
      str = EndpointFactory.getWsdlLocation(paramClass, paramMetadataReader); 
    if (str != null) {
      URL uRL;
      if (!str.startsWith("WEB-INF/wsdl")) {
        logger.log(Level.WARNING, "Ignoring wrong wsdl={0}. It should start with {1}. Going to generate and publish a new WSDL.", new Object[] { str, "WEB-INF/wsdl" });
        return null;
      } 
      try {
        uRL = this.loader.getResource('/' + str);
      } catch (MalformedURLException malformedURLException) {
        throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(str), malformedURLException, paramXMLStreamReader);
      } 
      if (uRL == null)
        throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(str), paramXMLStreamReader); 
      SDDocumentSource sDDocumentSource = (SDDocumentSource)this.docs.get(uRL.toExternalForm());
      assert sDDocumentSource != null;
      return sDDocumentSource;
    } 
    return null;
  }
  
  private EntityResolver createEntityResolver() {
    try {
      return XmlUtil.createEntityResolver(this.loader.getCatalogFile());
    } catch (MalformedURLException malformedURLException) {
      throw new WebServiceException(malformedURLException);
    } 
  }
  
  protected String getAttribute(Attributes paramAttributes, String paramString) {
    String str = paramAttributes.getValue(paramString);
    if (str != null)
      str = str.trim(); 
    return str;
  }
  
  protected QName getQNameAttribute(Attributes paramAttributes, String paramString) {
    String str = getAttribute(paramAttributes, paramString);
    return (str == null || str.equals("")) ? null : QName.valueOf(str);
  }
  
  protected String getNonEmptyAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString) {
    String str = getAttribute(paramAttributes, paramString);
    if (str != null && str.equals(""))
      failWithLocalName("runtime.parser.invalidAttributeValue", paramXMLStreamReader, paramString); 
    return str;
  }
  
  protected String getMandatoryAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString) {
    String str = getAttribute(paramAttributes, paramString);
    if (str == null)
      failWithLocalName("runtime.parser.missing.attribute", paramXMLStreamReader, paramString); 
    return str;
  }
  
  protected String getMandatoryNonEmptyAttribute(XMLStreamReader paramXMLStreamReader, Attributes paramAttributes, String paramString) {
    String str = getAttribute(paramAttributes, paramString);
    if (str == null) {
      failWithLocalName("runtime.parser.missing.attribute", paramXMLStreamReader, paramString);
    } else if (str.equals("")) {
      failWithLocalName("runtime.parser.invalidAttributeValue", paramXMLStreamReader, paramString);
    } 
    return str;
  }
  
  protected boolean setHandlersAndRoles(WSBinding paramWSBinding, XMLStreamReader paramXMLStreamReader, QName paramQName1, QName paramQName2) {
    if (paramXMLStreamReader.getEventType() == 2 || !paramXMLStreamReader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAINS))
      return false; 
    HandlerAnnotationInfo handlerAnnotationInfo = HandlerChainsModel.parseHandlerFile(paramXMLStreamReader, this.classLoader, paramQName1, paramQName2, paramWSBinding);
    paramWSBinding.setHandlerChain(handlerAnnotationInfo.getHandlers());
    if (paramWSBinding instanceof SOAPBinding)
      ((SOAPBinding)paramWSBinding).setRoles(handlerAnnotationInfo.getRoles()); 
    XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    return true;
  }
  
  protected ExternalMetadataFeature configureExternalMetadataReader(XMLStreamReader paramXMLStreamReader) {
    ExternalMetadataFeature.Builder builder = null;
    while (QNAME_EXT_METADA.equals(paramXMLStreamReader.getName())) {
      if (paramXMLStreamReader.getEventType() == 1) {
        Attributes attributes = XMLStreamReaderUtil.getAttributes(paramXMLStreamReader);
        String str1 = getAttribute(attributes, "file");
        if (str1 != null) {
          if (builder == null)
            builder = ExternalMetadataFeature.builder(); 
          builder.addFiles(new File[] { new File(str1) });
        } 
        String str2 = getAttribute(attributes, "resource");
        if (str2 != null) {
          if (builder == null)
            builder = ExternalMetadataFeature.builder(); 
          builder.addResources(new String[] { str2 });
        } 
      } 
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    } 
    return buildFeature(builder);
  }
  
  private ExternalMetadataFeature buildFeature(ExternalMetadataFeature.Builder paramBuilder) { return (paramBuilder != null) ? paramBuilder.build() : null; }
  
  protected static void fail(String paramString, XMLStreamReader paramXMLStreamReader) {
    logger.log(Level.SEVERE, "{0}{1}", new Object[] { paramString, Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()) });
    throw new ServerRtException(paramString, new Object[] { Integer.toString(paramXMLStreamReader.getLocation().getLineNumber()) });
  }
  
  protected static void failWithFullName(String paramString, XMLStreamReader paramXMLStreamReader) { throw new ServerRtException(paramString, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getName() }); }
  
  protected static void failWithLocalName(String paramString, XMLStreamReader paramXMLStreamReader) { throw new ServerRtException(paramString, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName() }); }
  
  protected static void failWithLocalName(String paramString1, XMLStreamReader paramXMLStreamReader, String paramString2) { throw new ServerRtException(paramString1, new Object[] { Integer.valueOf(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName(), paramString2 }); }
  
  protected Class loadClass(String paramString) {
    try {
      return Class.forName(paramString, true, this.classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      logger.log(Level.SEVERE, classNotFoundException.getMessage(), classNotFoundException);
      throw new ServerRtException("runtime.parser.classNotFound", new Object[] { paramString });
    } 
  }
  
  private Class getImplementorClass(String paramString, XMLStreamReader paramXMLStreamReader) {
    try {
      return Class.forName(paramString, true, this.classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      logger.log(Level.SEVERE, classNotFoundException.getMessage(), classNotFoundException);
      throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_CLASS_NOT_FOUND(paramString), classNotFoundException, paramXMLStreamReader);
    } 
  }
  
  public static interface AdapterFactory<A> {
    A createAdapter(String param1String1, String param1String2, WSEndpoint<?> param1WSEndpoint);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\DeploymentDescriptorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */