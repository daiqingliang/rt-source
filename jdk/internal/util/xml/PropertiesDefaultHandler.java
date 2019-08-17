package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import jdk.internal.org.xml.sax.Attributes;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.impl.SAXParserImpl;
import jdk.internal.util.xml.impl.XMLStreamWriterImpl;

public class PropertiesDefaultHandler extends DefaultHandler {
  private static final String ELEMENT_ROOT = "properties";
  
  private static final String ELEMENT_COMMENT = "comment";
  
  private static final String ELEMENT_ENTRY = "entry";
  
  private static final String ATTR_KEY = "key";
  
  private static final String PROPS_DTD_DECL = "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">";
  
  private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
  
  private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
  
  private static final String EXTERNAL_XML_VERSION = "1.0";
  
  private Properties properties;
  
  static final String ALLOWED_ELEMENTS = "properties, comment, entry";
  
  static final String ALLOWED_COMMENT = "comment";
  
  StringBuffer buf = new StringBuffer();
  
  boolean sawComment = false;
  
  boolean validEntry = false;
  
  int rootElem = 0;
  
  String key;
  
  String rootElm;
  
  public void load(Properties paramProperties, InputStream paramInputStream) throws IOException, InvalidPropertiesFormatException, UnsupportedEncodingException {
    this.properties = paramProperties;
    try {
      SAXParserImpl sAXParserImpl = new SAXParserImpl();
      sAXParserImpl.parse(paramInputStream, this);
    } catch (SAXException sAXException) {
      throw new InvalidPropertiesFormatException(sAXException);
    } 
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2) throws IOException {
    try {
      XMLStreamWriterImpl xMLStreamWriterImpl = new XMLStreamWriterImpl(paramOutputStream, paramString2);
      xMLStreamWriterImpl.writeStartDocument();
      xMLStreamWriterImpl.writeDTD("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
      xMLStreamWriterImpl.writeStartElement("properties");
      if (paramString1 != null && paramString1.length() > 0) {
        xMLStreamWriterImpl.writeStartElement("comment");
        xMLStreamWriterImpl.writeCharacters(paramString1);
        xMLStreamWriterImpl.writeEndElement();
      } 
      synchronized (paramProperties) {
        for (Map.Entry entry : paramProperties.entrySet()) {
          Object object1 = entry.getKey();
          Object object2 = entry.getValue();
          if (object1 instanceof String && object2 instanceof String) {
            xMLStreamWriterImpl.writeStartElement("entry");
            xMLStreamWriterImpl.writeAttribute("key", (String)object1);
            xMLStreamWriterImpl.writeCharacters((String)object2);
            xMLStreamWriterImpl.writeEndElement();
          } 
        } 
      } 
      xMLStreamWriterImpl.writeEndElement();
      xMLStreamWriterImpl.writeEndDocument();
      xMLStreamWriterImpl.close();
    } catch (XMLStreamException xMLStreamException) {
      if (xMLStreamException.getCause() instanceof UnsupportedEncodingException)
        throw (UnsupportedEncodingException)xMLStreamException.getCause(); 
      throw new IOException(xMLStreamException);
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.rootElem < 2)
      this.rootElem++; 
    if (this.rootElm == null)
      fatalError(new SAXParseException("An XML properties document must contain the DOCTYPE declaration as defined by java.util.Properties.", null)); 
    if (this.rootElem == 1 && !this.rootElm.equals(paramString3))
      fatalError(new SAXParseException("Document root element \"" + paramString3 + "\", must match DOCTYPE root \"" + this.rootElm + "\"", null)); 
    if (!"properties, comment, entry".contains(paramString3))
      fatalError(new SAXParseException("Element type \"" + paramString3 + "\" must be declared.", null)); 
    if (paramString3.equals("entry")) {
      this.validEntry = true;
      this.key = paramAttributes.getValue("key");
      if (this.key == null)
        fatalError(new SAXParseException("Attribute \"key\" is required and must be specified for element type \"entry\"", null)); 
    } else if (paramString3.equals("comment")) {
      if (this.sawComment)
        fatalError(new SAXParseException("Only one comment element may be allowed. The content of element type \"properties\" must match \"(comment?,entry*)\"", null)); 
      this.sawComment = true;
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.validEntry)
      this.buf.append(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (!"properties, comment, entry".contains(paramString3))
      fatalError(new SAXParseException("Element: " + paramString3 + " is invalid, must match  \"(comment?,entry*)\".", null)); 
    if (this.validEntry) {
      this.properties.setProperty(this.key, this.buf.toString());
      this.buf.delete(0, this.buf.length());
      this.validEntry = false;
    } 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException { this.rootElm = paramString1; }
  
  public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException, IOException {
    if (paramString2.equals("http://java.sun.com/dtd/properties.dtd")) {
      InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
      inputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
      return inputSource;
    } 
    throw new SAXException("Invalid system identifier: " + paramString2);
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\PropertiesDefaultHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */