package sun.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import sun.util.spi.XmlPropertiesProvider;

public class PlatformXmlPropertiesProvider extends XmlPropertiesProvider {
  private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
  
  private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
  
  private static final String EXTERNAL_XML_VERSION = "1.0";
  
  public void load(Properties paramProperties, InputStream paramInputStream) throws IOException, InvalidPropertiesFormatException {
    Document document = null;
    try {
      document = getLoadingDoc(paramInputStream);
    } catch (SAXException sAXException) {
      throw new InvalidPropertiesFormatException(sAXException);
    } 
    Element element = document.getDocumentElement();
    String str = element.getAttribute("version");
    if (str.compareTo("1.0") > 0)
      throw new InvalidPropertiesFormatException("Exported Properties file format version " + str + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK."); 
    importProperties(paramProperties, element);
  }
  
  static Document getLoadingDoc(InputStream paramInputStream) throws SAXException, IOException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setIgnoringElementContentWhitespace(true);
    documentBuilderFactory.setValidating(true);
    documentBuilderFactory.setCoalescing(true);
    documentBuilderFactory.setIgnoringComments(true);
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      documentBuilder.setEntityResolver(new Resolver(null));
      documentBuilder.setErrorHandler(new EH(null));
      InputSource inputSource = new InputSource(paramInputStream);
      return documentBuilder.parse(inputSource);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new Error(parserConfigurationException);
    } 
  }
  
  static void importProperties(Properties paramProperties, Element paramElement) {
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    boolean bool = (i > 0 && nodeList.item(0).getNodeName().equals("comment")) ? 1 : 0;
    for (byte b = bool; b < i; b++) {
      Element element = (Element)nodeList.item(b);
      if (element.hasAttribute("key")) {
        Node node = element.getFirstChild();
        String str = (node == null) ? "" : node.getNodeValue();
        paramProperties.setProperty(element.getAttribute("key"), str);
      } 
    } 
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2) throws IOException {
    try {
      Charset.forName(paramString2);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString2);
    } 
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException parserConfigurationException) {
      assert false;
    } 
    Document document = documentBuilder.newDocument();
    Element element = (Element)document.appendChild(document.createElement("properties"));
    if (paramString1 != null) {
      Element element1 = (Element)element.appendChild(document.createElement("comment"));
      element1.appendChild(document.createTextNode(paramString1));
    } 
    synchronized (paramProperties) {
      for (Map.Entry entry : paramProperties.entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        if (object1 instanceof String && object2 instanceof String) {
          Element element1 = (Element)element.appendChild(document.createElement("entry"));
          element1.setAttribute("key", (String)object1);
          element1.appendChild(document.createTextNode((String)object2));
        } 
      } 
    } 
    emitDocument(document, paramOutputStream, paramString2);
  }
  
  static void emitDocument(Document paramDocument, OutputStream paramOutputStream, String paramString) throws IOException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = null;
    try {
      transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("method", "xml");
      transformer.setOutputProperty("encoding", paramString);
    } catch (TransformerConfigurationException transformerConfigurationException) {
      assert false;
    } 
    DOMSource dOMSource = new DOMSource(paramDocument);
    StreamResult streamResult = new StreamResult(paramOutputStream);
    try {
      transformer.transform(dOMSource, streamResult);
    } catch (TransformerException transformerException) {
      throw new IOException(transformerException);
    } 
  }
  
  private static class EH implements ErrorHandler {
    private EH() {}
    
    public void error(SAXParseException param1SAXParseException) throws SAXException { throw param1SAXParseException; }
    
    public void fatalError(SAXParseException param1SAXParseException) throws SAXException { throw param1SAXParseException; }
    
    public void warning(SAXParseException param1SAXParseException) throws SAXException { throw param1SAXParseException; }
  }
  
  private static class Resolver implements EntityResolver {
    private Resolver() {}
    
    public InputSource resolveEntity(String param1String1, String param1String2) throws SAXException {
      if (param1String2.equals("http://java.sun.com/dtd/properties.dtd")) {
        InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
        inputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
        return inputSource;
      } 
      throw new SAXException("Invalid system identifier: " + param1String2);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\xml\PlatformXmlPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */