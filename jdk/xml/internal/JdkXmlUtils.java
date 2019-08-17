package jdk.xml.internal;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class JdkXmlUtils {
  private static final String DOM_FACTORY_ID = "javax.xml.parsers.DocumentBuilderFactory";
  
  private static final String SAX_FACTORY_ID = "javax.xml.parsers.SAXParserFactory";
  
  private static final String SAX_DRIVER = "org.xml.sax.driver";
  
  public static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  
  public static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  
  public static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
  
  public static final boolean OVERRIDE_PARSER_DEFAULT = ((Boolean)SecuritySupport.getJAXPSystemProperty(Boolean.class, "jdk.xml.overrideDefaultParser", "false")).booleanValue();
  
  public static final String FEATURE_TRUE = "true";
  
  public static final String FEATURE_FALSE = "false";
  
  private static final SAXParserFactory defaultSAXFactory = getSAXFactory(false);
  
  public static int getValue(Object paramObject, int paramInt) {
    if (paramObject == null)
      return paramInt; 
    if (paramObject instanceof Number)
      return ((Number)paramObject).intValue(); 
    if (paramObject instanceof String)
      return Integer.parseInt(String.valueOf(paramObject)); 
    throw new IllegalArgumentException("Unexpected class: " + paramObject.getClass());
  }
  
  public static void setXMLReaderPropertyIfSupport(XMLReader paramXMLReader, String paramString, Object paramObject, boolean paramBoolean) {
    try {
      paramXMLReader.setProperty(paramString, paramObject);
    } catch (SAXNotRecognizedException|org.xml.sax.SAXNotSupportedException sAXNotRecognizedException) {
      if (paramBoolean)
        XMLSecurityManager.printWarning(paramXMLReader.getClass().getName(), paramString, sAXNotRecognizedException); 
    } 
  }
  
  public static XMLReader getXMLReader(boolean paramBoolean1, boolean paramBoolean2) {
    XMLReader xMLReader = null;
    String str = SecuritySupport.getSystemProperty("org.xml.sax.driver");
    if (str != null) {
      xMLReader = getXMLReaderWXMLReaderFactory();
    } else if (paramBoolean1) {
      xMLReader = getXMLReaderWSAXFactory(paramBoolean1);
    } 
    if (xMLReader != null) {
      if (paramBoolean2)
        try {
          xMLReader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", paramBoolean2);
        } catch (SAXException sAXException) {
          XMLSecurityManager.printWarning(xMLReader.getClass().getName(), "http://javax.xml.XMLConstants/feature/secure-processing", sAXException);
        }  
      try {
        xMLReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      } catch (SAXException sAXException) {}
      return xMLReader;
    } 
    SAXParserFactory sAXParserFactory = defaultSAXFactory;
    try {
      xMLReader = sAXParserFactory.newSAXParser().getXMLReader();
    } catch (ParserConfigurationException|SAXException parserConfigurationException) {}
    return xMLReader;
  }
  
  public static Document getDOMDocument() {
    try {
      DocumentBuilderFactory documentBuilderFactory = getDOMFactory(false);
      return documentBuilderFactory.newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException parserConfigurationException) {
      return null;
    } 
  }
  
  public static DocumentBuilderFactory getDOMFactory(boolean paramBoolean) {
    boolean bool = paramBoolean;
    String str = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.DocumentBuilderFactory");
    if (str != null && System.getSecurityManager() == null)
      bool = true; 
    DocumentBuilderFactoryImpl documentBuilderFactoryImpl = !bool ? new DocumentBuilderFactoryImpl() : DocumentBuilderFactory.newInstance();
    documentBuilderFactoryImpl.setNamespaceAware(true);
    documentBuilderFactoryImpl.setValidating(false);
    return documentBuilderFactoryImpl;
  }
  
  public static SAXParserFactory getSAXFactory(boolean paramBoolean) {
    boolean bool = paramBoolean;
    String str = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.SAXParserFactory");
    if (str != null && System.getSecurityManager() == null)
      bool = true; 
    SAXParserFactoryImpl sAXParserFactoryImpl = !bool ? new SAXParserFactoryImpl() : SAXParserFactory.newInstance();
    sAXParserFactoryImpl.setNamespaceAware(true);
    return sAXParserFactoryImpl;
  }
  
  public static SAXTransformerFactory getSAXTransformFactory(boolean paramBoolean) {
    SAXTransformerFactory sAXTransformerFactory = paramBoolean ? (SAXTransformerFactory)SAXTransformerFactory.newInstance() : new TransformerFactoryImpl();
    try {
      sAXTransformerFactory.setFeature("jdk.xml.overrideDefaultParser", paramBoolean);
    } catch (TransformerConfigurationException transformerConfigurationException) {}
    return sAXTransformerFactory;
  }
  
  private static XMLReader getXMLReaderWSAXFactory(boolean paramBoolean) {
    SAXParserFactory sAXParserFactory = getSAXFactory(paramBoolean);
    try {
      return sAXParserFactory.newSAXParser().getXMLReader();
    } catch (ParserConfigurationException|SAXException parserConfigurationException) {
      return getXMLReaderWXMLReaderFactory();
    } 
  }
  
  private static XMLReader getXMLReaderWXMLReaderFactory() {
    try {
      return XMLReaderFactory.createXMLReader();
    } catch (SAXException sAXException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\xml\internal\JdkXmlUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */