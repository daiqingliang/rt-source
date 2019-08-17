package java.util.prefs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class XmlSupport {
  private static final String PREFS_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";
  
  private static final String PREFS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >";
  
  private static final String EXTERNAL_XML_VERSION = "1.0";
  
  private static final String MAP_XML_VERSION = "1.0";
  
  static void export(OutputStream paramOutputStream, Preferences paramPreferences, boolean paramBoolean) throws IOException, BackingStoreException {
    if (((AbstractPreferences)paramPreferences).isRemoved())
      throw new IllegalStateException("Node has been removed"); 
    Document document = createPrefsDoc("preferences");
    Element element1 = document.getDocumentElement();
    element1.setAttribute("EXTERNAL_XML_VERSION", "1.0");
    Element element2 = (Element)element1.appendChild(document.createElement("root"));
    element2.setAttribute("type", paramPreferences.isUserNode() ? "user" : "system");
    ArrayList arrayList = new ArrayList();
    Preferences preferences1 = paramPreferences;
    for (Preferences preferences2 = preferences1.parent(); preferences2 != null; preferences2 = preferences1.parent()) {
      arrayList.add(preferences1);
      preferences1 = preferences2;
    } 
    Element element3 = element2;
    for (int i = arrayList.size() - 1; i >= 0; i--) {
      element3.appendChild(document.createElement("map"));
      element3 = (Element)element3.appendChild(document.createElement("node"));
      element3.setAttribute("name", ((Preferences)arrayList.get(i)).name());
    } 
    putPreferencesInXml(element3, document, paramPreferences, paramBoolean);
    writeDoc(document, paramOutputStream);
  }
  
  private static void putPreferencesInXml(Element paramElement, Document paramDocument, Preferences paramPreferences, boolean paramBoolean) throws BackingStoreException {
    Preferences[] arrayOfPreferences = null;
    String[] arrayOfString = null;
    synchronized (((AbstractPreferences)paramPreferences).lock) {
      if (((AbstractPreferences)paramPreferences).isRemoved()) {
        paramElement.getParentNode().removeChild(paramElement);
        return;
      } 
      String[] arrayOfString1 = paramPreferences.keys();
      Element element = (Element)paramElement.appendChild(paramDocument.createElement("map"));
      byte b;
      for (b = 0; b < arrayOfString1.length; b++) {
        Element element1 = (Element)element.appendChild(paramDocument.createElement("entry"));
        element1.setAttribute("key", arrayOfString1[b]);
        element1.setAttribute("value", paramPreferences.get(arrayOfString1[b], null));
      } 
      if (paramBoolean) {
        arrayOfString = paramPreferences.childrenNames();
        arrayOfPreferences = new Preferences[arrayOfString.length];
        for (b = 0; b < arrayOfString.length; b++)
          arrayOfPreferences[b] = paramPreferences.node(arrayOfString[b]); 
      } 
    } 
    if (paramBoolean)
      for (byte b = 0; b < arrayOfString.length; b++) {
        Element element = (Element)paramElement.appendChild(paramDocument.createElement("node"));
        element.setAttribute("name", arrayOfString[b]);
        putPreferencesInXml(element, paramDocument, arrayOfPreferences[b], paramBoolean);
      }  
  }
  
  static void importPreferences(InputStream paramInputStream) throws IOException, InvalidPreferencesFormatException {
    try {
      Document document = loadPrefsDoc(paramInputStream);
      String str = document.getDocumentElement().getAttribute("EXTERNAL_XML_VERSION");
      if (str.compareTo("1.0") > 0)
        throw new InvalidPreferencesFormatException("Exported preferences file format version " + str + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK."); 
      Element element = (Element)document.getDocumentElement().getChildNodes().item(0);
      Preferences preferences = element.getAttribute("type").equals("user") ? Preferences.userRoot() : Preferences.systemRoot();
      ImportSubtree(preferences, element);
    } catch (SAXException sAXException) {
      throw new InvalidPreferencesFormatException(sAXException);
    } 
  }
  
  private static Document createPrefsDoc(String paramString) {
    try {
      DOMImplementation dOMImplementation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
      DocumentType documentType = dOMImplementation.createDocumentType(paramString, null, "http://java.sun.com/dtd/preferences.dtd");
      return dOMImplementation.createDocument(null, paramString, documentType);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new AssertionError(parserConfigurationException);
    } 
  }
  
  private static Document loadPrefsDoc(InputStream paramInputStream) throws SAXException, IOException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setIgnoringElementContentWhitespace(true);
    documentBuilderFactory.setValidating(true);
    documentBuilderFactory.setCoalescing(true);
    documentBuilderFactory.setIgnoringComments(true);
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      documentBuilder.setEntityResolver(new Resolver(null));
      documentBuilder.setErrorHandler(new EH(null));
      return documentBuilder.parse(new InputSource(paramInputStream));
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new AssertionError(parserConfigurationException);
    } 
  }
  
  private static final void writeDoc(Document paramDocument, OutputStream paramOutputStream) throws IOException {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      try {
        transformerFactory.setAttribute("indent-number", new Integer(2));
      } catch (IllegalArgumentException illegalArgumentException) {}
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty("doctype-system", paramDocument.getDoctype().getSystemId());
      transformer.setOutputProperty("indent", "yes");
      transformer.transform(new DOMSource(paramDocument), new StreamResult(new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF-8"))));
    } catch (TransformerException transformerException) {
      throw new AssertionError(transformerException);
    } 
  }
  
  private static void ImportSubtree(Preferences paramPreferences, Element paramElement) {
    Preferences[] arrayOfPreferences;
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    synchronized (((AbstractPreferences)paramPreferences).lock) {
      if (((AbstractPreferences)paramPreferences).isRemoved())
        return; 
      Element element = (Element)nodeList.item(0);
      ImportPrefs(paramPreferences, element);
      arrayOfPreferences = new Preferences[i - 1];
      for (byte b1 = 1; b1 < i; b1++) {
        Element element1 = (Element)nodeList.item(b1);
        arrayOfPreferences[b1 - 1] = paramPreferences.node(element1.getAttribute("name"));
      } 
    } 
    for (byte b = 1; b < i; b++)
      ImportSubtree(arrayOfPreferences[b - true], (Element)nodeList.item(b)); 
  }
  
  private static void ImportPrefs(Preferences paramPreferences, Element paramElement) {
    NodeList nodeList = paramElement.getChildNodes();
    byte b = 0;
    int i = nodeList.getLength();
    while (b < i) {
      Element element = (Element)nodeList.item(b);
      paramPreferences.put(element.getAttribute("key"), element.getAttribute("value"));
      b++;
    } 
  }
  
  static void exportMap(OutputStream paramOutputStream, Map<String, String> paramMap) throws IOException {
    Document document = createPrefsDoc("map");
    Element element = document.getDocumentElement();
    element.setAttribute("MAP_XML_VERSION", "1.0");
    for (Map.Entry entry : paramMap.entrySet()) {
      Element element1 = (Element)element.appendChild(document.createElement("entry"));
      element1.setAttribute("key", (String)entry.getKey());
      element1.setAttribute("value", (String)entry.getValue());
    } 
    writeDoc(document, paramOutputStream);
  }
  
  static void importMap(InputStream paramInputStream, Map<String, String> paramMap) throws IOException, InvalidPreferencesFormatException {
    try {
      Document document = loadPrefsDoc(paramInputStream);
      Element element = document.getDocumentElement();
      String str = element.getAttribute("MAP_XML_VERSION");
      if (str.compareTo("1.0") > 0)
        throw new InvalidPreferencesFormatException("Preferences map file format version " + str + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK."); 
      NodeList nodeList = element.getChildNodes();
      byte b = 0;
      int i = nodeList.getLength();
      while (b < i) {
        Element element1 = (Element)nodeList.item(b);
        paramMap.put(element1.getAttribute("key"), element1.getAttribute("value"));
        b++;
      } 
    } catch (SAXException sAXException) {
      throw new InvalidPreferencesFormatException(sAXException);
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
      if (param1String2.equals("http://java.sun.com/dtd/preferences.dtd")) {
        InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >"));
        inputSource.setSystemId("http://java.sun.com/dtd/preferences.dtd");
        return inputSource;
      } 
      throw new SAXException("Invalid system identifier: " + param1String2);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\XmlSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */