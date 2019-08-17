package com.sun.xml.internal.ws.util.xml;

import com.sun.istack.internal.Nullable;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.WebServiceException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class XmlUtil {
  private static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  
  private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
  
  private static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
  
  private static final String EXTERNAL_GE = "http://xml.org/sax/features/external-general-entities";
  
  private static final String EXTERNAL_PE = "http://xml.org/sax/features/external-parameter-entities";
  
  private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());
  
  private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.ws.disableXmlSecurity";
  
  private static boolean XML_SECURITY_DISABLED = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("com.sun.xml.internal.ws.disableXmlSecurity")); }
      })).booleanValue();
  
  static final ContextClassloaderLocal<TransformerFactory> transformerFactory = new ContextClassloaderLocal<TransformerFactory>() {
      protected TransformerFactory initialValue() { return TransformerFactory.newInstance(); }
    };
  
  static final ContextClassloaderLocal<SAXParserFactory> saxParserFactory = new ContextClassloaderLocal<SAXParserFactory>() {
      protected SAXParserFactory initialValue() throws Exception {
        SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
        sAXParserFactory.setNamespaceAware(true);
        return sAXParserFactory;
      }
    };
  
  public static final ErrorHandler DRACONIAN_ERROR_HANDLER = new ErrorHandler() {
      public void warning(SAXParseException param1SAXParseException) {}
      
      public void error(SAXParseException param1SAXParseException) { throw param1SAXParseException; }
      
      public void fatalError(SAXParseException param1SAXParseException) { throw param1SAXParseException; }
    };
  
  public static String getPrefix(String paramString) {
    int i = paramString.indexOf(':');
    return (i == -1) ? null : paramString.substring(0, i);
  }
  
  public static String getLocalPart(String paramString) {
    int i = paramString.indexOf(':');
    return (i == -1) ? paramString : paramString.substring(i + 1);
  }
  
  public static String getAttributeOrNull(Element paramElement, String paramString) {
    Attr attr = paramElement.getAttributeNode(paramString);
    return (attr == null) ? null : attr.getValue();
  }
  
  public static String getAttributeNSOrNull(Element paramElement, String paramString1, String paramString2) {
    Attr attr = paramElement.getAttributeNodeNS(paramString2, paramString1);
    return (attr == null) ? null : attr.getValue();
  }
  
  public static String getAttributeNSOrNull(Element paramElement, QName paramQName) {
    Attr attr = paramElement.getAttributeNodeNS(paramQName.getNamespaceURI(), paramQName.getLocalPart());
    return (attr == null) ? null : attr.getValue();
  }
  
  public static Iterator getAllChildren(Element paramElement) { return new NodeListIterator(paramElement.getChildNodes()); }
  
  public static Iterator getAllAttributes(Element paramElement) { return new NamedNodeMapIterator(paramElement.getAttributes()); }
  
  public static List<String> parseTokenList(String paramString) {
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ");
    while (stringTokenizer.hasMoreTokens())
      arrayList.add(stringTokenizer.nextToken()); 
    return arrayList;
  }
  
  public static String getTextForNode(Node paramNode) {
    StringBuilder stringBuilder = new StringBuilder();
    NodeList nodeList = paramNode.getChildNodes();
    if (nodeList.getLength() == 0)
      return null; 
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      if (node instanceof org.w3c.dom.Text) {
        stringBuilder.append(node.getNodeValue());
      } else if (node instanceof org.w3c.dom.EntityReference) {
        String str = getTextForNode(node);
        if (str == null)
          return null; 
        stringBuilder.append(str);
      } else {
        return null;
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static InputStream getUTF8Stream(String paramString) {
    try {
      ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayBuffer, "utf-8");
      outputStreamWriter.write(paramString);
      outputStreamWriter.close();
      return byteArrayBuffer.newInputStream();
    } catch (IOException iOException) {
      throw new RuntimeException("should not happen");
    } 
  }
  
  public static Transformer newTransformer() {
    try {
      return ((TransformerFactory)transformerFactory.get()).newTransformer();
    } catch (TransformerConfigurationException transformerConfigurationException) {
      throw new IllegalStateException("Unable to create a JAXP transformer");
    } 
  }
  
  public static <T extends javax.xml.transform.Result> T identityTransform(Source paramSource, T paramT) throws TransformerException, SAXException, ParserConfigurationException, IOException {
    if (paramSource instanceof StreamSource) {
      StreamSource streamSource = (StreamSource)paramSource;
      TransformerHandler transformerHandler = ((SAXTransformerFactory)transformerFactory.get()).newTransformerHandler();
      transformerHandler.setResult(paramT);
      XMLReader xMLReader = ((SAXParserFactory)saxParserFactory.get()).newSAXParser().getXMLReader();
      xMLReader.setContentHandler(transformerHandler);
      xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", transformerHandler);
      xMLReader.parse(toInputSource(streamSource));
    } else {
      newTransformer().transform(paramSource, paramT);
    } 
    return paramT;
  }
  
  private static InputSource toInputSource(StreamSource paramStreamSource) {
    InputSource inputSource = new InputSource();
    inputSource.setByteStream(paramStreamSource.getInputStream());
    inputSource.setCharacterStream(paramStreamSource.getReader());
    inputSource.setPublicId(paramStreamSource.getPublicId());
    inputSource.setSystemId(paramStreamSource.getSystemId());
    return inputSource;
  }
  
  public static EntityResolver createEntityResolver(@Nullable URL paramURL) {
    CatalogManager catalogManager = new CatalogManager();
    catalogManager.setIgnoreMissingProperties(true);
    catalogManager.setUseStaticCatalog(false);
    Catalog catalog = catalogManager.getCatalog();
    try {
      if (paramURL != null)
        catalog.parseCatalog(paramURL); 
    } catch (IOException iOException) {
      throw new ServerRtException("server.rt.err", new Object[] { iOException });
    } 
    return workaroundCatalogResolver(catalog);
  }
  
  public static EntityResolver createDefaultCatalogResolver() {
    CatalogManager catalogManager = new CatalogManager();
    catalogManager.setIgnoreMissingProperties(true);
    catalogManager.setUseStaticCatalog(false);
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Catalog catalog = catalogManager.getCatalog();
    try {
      Enumeration enumeration;
      if (classLoader == null) {
        enumeration = ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml");
      } else {
        enumeration = classLoader.getResources("META-INF/jax-ws-catalog.xml");
      } 
      while (enumeration.hasMoreElements()) {
        URL uRL = (URL)enumeration.nextElement();
        catalog.parseCatalog(uRL);
      } 
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
    return workaroundCatalogResolver(catalog);
  }
  
  private static CatalogResolver workaroundCatalogResolver(final Catalog catalog) {
    CatalogManager catalogManager = new CatalogManager() {
        public Catalog getCatalog() { return catalog; }
      };
    catalogManager.setIgnoreMissingProperties(true);
    catalogManager.setUseStaticCatalog(false);
    return new CatalogResolver(catalogManager);
  }
  
  public static DocumentBuilderFactory newDocumentBuilderFactory() { return newDocumentBuilderFactory(false); }
  
  public static DocumentBuilderFactory newDocumentBuilderFactory(boolean paramBoolean) {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    String str = "http://javax.xml.XMLConstants/feature/secure-processing";
    try {
      boolean bool = !isXMLSecurityDisabled(paramBoolean);
      documentBuilderFactory.setFeature(str, bool);
      documentBuilderFactory.setNamespaceAware(true);
      if (bool) {
        documentBuilderFactory.setExpandEntityReferences(false);
        str = "http://apache.org/xml/features/disallow-doctype-decl";
        documentBuilderFactory.setFeature(str, true);
        str = "http://xml.org/sax/features/external-general-entities";
        documentBuilderFactory.setFeature(str, false);
        str = "http://xml.org/sax/features/external-parameter-entities";
        documentBuilderFactory.setFeature(str, false);
        str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        documentBuilderFactory.setFeature(str, false);
      } 
    } catch (ParserConfigurationException parserConfigurationException) {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + str + " feature!", new Object[] { documentBuilderFactory.getClass().getName() });
    } 
    return documentBuilderFactory;
  }
  
  public static TransformerFactory newTransformerFactory(boolean paramBoolean) {
    TransformerFactory transformerFactory1 = TransformerFactory.newInstance();
    try {
      transformerFactory1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(paramBoolean));
    } catch (TransformerConfigurationException transformerConfigurationException) {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { transformerFactory1.getClass().getName() });
    } 
    return transformerFactory1;
  }
  
  public static TransformerFactory newTransformerFactory() { return newTransformerFactory(true); }
  
  public static SAXParserFactory newSAXParserFactory(boolean paramBoolean) {
    SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
    String str = "http://javax.xml.XMLConstants/feature/secure-processing";
    try {
      boolean bool = !isXMLSecurityDisabled(paramBoolean);
      sAXParserFactory.setFeature(str, bool);
      sAXParserFactory.setNamespaceAware(true);
      if (bool) {
        str = "http://apache.org/xml/features/disallow-doctype-decl";
        sAXParserFactory.setFeature(str, true);
        str = "http://xml.org/sax/features/external-general-entities";
        sAXParserFactory.setFeature(str, false);
        str = "http://xml.org/sax/features/external-parameter-entities";
        sAXParserFactory.setFeature(str, false);
        str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        sAXParserFactory.setFeature(str, false);
      } 
    } catch (ParserConfigurationException|org.xml.sax.SAXNotRecognizedException|org.xml.sax.SAXNotSupportedException parserConfigurationException) {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + str + " feature!", new Object[] { sAXParserFactory.getClass().getName() });
    } 
    return sAXParserFactory;
  }
  
  public static XPathFactory newXPathFactory(boolean paramBoolean) {
    XPathFactory xPathFactory = XPathFactory.newInstance();
    try {
      xPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(paramBoolean));
    } catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
      LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { xPathFactory.getClass().getName() });
    } 
    return xPathFactory;
  }
  
  public static XMLInputFactory newXMLInputFactory(boolean paramBoolean) {
    XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();
    if (isXMLSecurityDisabled(paramBoolean)) {
      xMLInputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(false));
      xMLInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.valueOf(false));
    } 
    return xMLInputFactory;
  }
  
  private static boolean isXMLSecurityDisabled(boolean paramBoolean) { return (XML_SECURITY_DISABLED || paramBoolean); }
  
  public static SchemaFactory allowExternalAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean) {
    if (isXMLSecurityDisabled(paramBoolean)) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Xml Security disabled, no JAXP xsd external access configuration necessary."); 
      return paramSchemaFactory;
    } 
    if (System.getProperty("javax.xml.accessExternalSchema") != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Detected explicitly JAXP configuration, no JAXP xsd external access configuration necessary."); 
      return paramSchemaFactory;
    } 
    try {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", paramString);
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Property \"{0}\" is supported and has been successfully set by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" }); 
    } catch (SAXException sAXException) {
      if (LOGGER.isLoggable(Level.CONFIG))
        LOGGER.log(Level.CONFIG, "Property \"{0}\" is not supported by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" }); 
    } 
    return paramSchemaFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\XmlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */