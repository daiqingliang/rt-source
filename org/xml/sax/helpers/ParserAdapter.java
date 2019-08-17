package org.xml.sax.helpers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ParserAdapter implements XMLReader, DocumentHandler {
  private static SecuritySupport ss = new SecuritySupport();
  
  private static final String FEATURES = "http://xml.org/sax/features/";
  
  private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  
  private static final String XMLNS_URIs = "http://xml.org/sax/features/xmlns-uris";
  
  private NamespaceSupport nsSupport;
  
  private AttributeListAdapter attAdapter;
  
  private boolean parsing = false;
  
  private String[] nameParts = new String[3];
  
  private Parser parser = null;
  
  private AttributesImpl atts = null;
  
  private boolean namespaces = true;
  
  private boolean prefixes = false;
  
  private boolean uris = false;
  
  Locator locator;
  
  EntityResolver entityResolver = null;
  
  DTDHandler dtdHandler = null;
  
  ContentHandler contentHandler = null;
  
  ErrorHandler errorHandler = null;
  
  public ParserAdapter() throws SAXException {
    String str = ss.getSystemProperty("org.xml.sax.parser");
    try {
      setup(ParserFactory.makeParser());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SAXException("Cannot find SAX1 driver class " + str, classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new SAXException("SAX1 driver class " + str + " found but cannot be loaded", illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw new SAXException("SAX1 driver class " + str + " loaded but cannot be instantiated", instantiationException);
    } catch (ClassCastException classCastException) {
      throw new SAXException("SAX1 driver class " + str + " does not implement org.xml.sax.Parser");
    } catch (NullPointerException nullPointerException) {
      throw new SAXException("System property org.xml.sax.parser not specified");
    } 
  }
  
  public ParserAdapter(Parser paramParser) { setup(paramParser); }
  
  private void setup(Parser paramParser) {
    if (paramParser == null)
      throw new NullPointerException("Parser argument must not be null"); 
    this.parser = paramParser;
    this.atts = new AttributesImpl();
    this.nsSupport = new NamespaceSupport();
    this.attAdapter = new AttributeListAdapter();
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces")) {
      checkNotParsing("feature", paramString);
      this.namespaces = paramBoolean;
      if (!this.namespaces && !this.prefixes)
        this.prefixes = true; 
    } else if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      checkNotParsing("feature", paramString);
      this.prefixes = paramBoolean;
      if (!this.prefixes && !this.namespaces)
        this.namespaces = true; 
    } else if (paramString.equals("http://xml.org/sax/features/xmlns-uris")) {
      checkNotParsing("feature", paramString);
      this.uris = paramBoolean;
    } else {
      throw new SAXNotRecognizedException("Feature: " + paramString);
    } 
  }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://xml.org/sax/features/namespaces"))
      return this.namespaces; 
    if (paramString.equals("http://xml.org/sax/features/namespace-prefixes"))
      return this.prefixes; 
    if (paramString.equals("http://xml.org/sax/features/xmlns-uris"))
      return this.uris; 
    throw new SAXNotRecognizedException("Feature: " + paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException { throw new SAXNotRecognizedException("Property: " + paramString); }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { throw new SAXNotRecognizedException("Property: " + paramString); }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this.entityResolver = paramEntityResolver; }
  
  public EntityResolver getEntityResolver() { return this.entityResolver; }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this.dtdHandler = paramDTDHandler; }
  
  public DTDHandler getDTDHandler() { return this.dtdHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.contentHandler = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this.contentHandler; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.errorHandler = paramErrorHandler; }
  
  public ErrorHandler getErrorHandler() { return this.errorHandler; }
  
  public void parse(String paramString) throws IOException, SAXException { parse(new InputSource(paramString)); }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException {
    if (this.parsing)
      throw new SAXException("Parser is already in use"); 
    setupParser();
    this.parsing = true;
    try {
      this.parser.parse(paramInputSource);
    } finally {
      this.parsing = false;
    } 
    this.parsing = false;
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    this.locator = paramLocator;
    if (this.contentHandler != null)
      this.contentHandler.setDocumentLocator(paramLocator); 
  }
  
  public void startDocument() throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.startDocument(); 
  }
  
  public void endDocument() throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.endDocument(); 
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {
    Vector vector = null;
    if (!this.namespaces) {
      if (this.contentHandler != null) {
        this.attAdapter.setAttributeList(paramAttributeList);
        this.contentHandler.startElement("", "", paramString.intern(), this.attAdapter);
      } 
      return;
    } 
    this.nsSupport.pushContext();
    int i = paramAttributeList.getLength();
    byte b;
    for (b = 0; b < i; b++) {
      String str2;
      String str1 = paramAttributeList.getName(b);
      if (!str1.startsWith("xmlns"))
        continue; 
      int j = str1.indexOf(':');
      if (j == -1 && str1.length() == 5) {
        str2 = "";
      } else {
        if (j != 5)
          continue; 
        str2 = str1.substring(j + 1);
      } 
      String str3 = paramAttributeList.getValue(b);
      if (!this.nsSupport.declarePrefix(str2, str3)) {
        reportError("Illegal Namespace prefix: " + str2);
      } else if (this.contentHandler != null) {
        this.contentHandler.startPrefixMapping(str2, str3);
      } 
      continue;
    } 
    this.atts.clear();
    for (b = 0; b < i; b++) {
      String str1 = paramAttributeList.getName(b);
      String str2 = paramAttributeList.getType(b);
      String str3 = paramAttributeList.getValue(b);
      if (str1.startsWith("xmlns")) {
        String str;
        int j = str1.indexOf(':');
        if (j == -1 && str1.length() == 5) {
          str = "";
        } else if (j != 5) {
          str = null;
        } else {
          str = str1.substring(6);
        } 
        if (str != null) {
          if (this.prefixes)
            if (this.uris) {
              this.nsSupport;
              this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", str, str1.intern(), str2, str3);
            } else {
              this.atts.addAttribute("", "", str1.intern(), str2, str3);
            }  
          continue;
        } 
      } 
      try {
        String[] arrayOfString = processName(str1, true, true);
        this.atts.addAttribute(arrayOfString[0], arrayOfString[1], arrayOfString[2], str2, str3);
      } catch (SAXException sAXException) {
        if (vector == null)
          vector = new Vector(); 
        vector.addElement(sAXException);
        this.atts.addAttribute("", str1, str1, str2, str3);
      } 
      continue;
    } 
    if (vector != null && this.errorHandler != null)
      for (b = 0; b < vector.size(); b++)
        this.errorHandler.error((SAXParseException)vector.elementAt(b));  
    if (this.contentHandler != null) {
      String[] arrayOfString = processName(paramString, false, false);
      this.contentHandler.startElement(arrayOfString[0], arrayOfString[1], arrayOfString[2], this.atts);
    } 
  }
  
  public void endElement(String paramString) throws IOException, SAXException {
    if (!this.namespaces) {
      if (this.contentHandler != null)
        this.contentHandler.endElement("", "", paramString.intern()); 
      return;
    } 
    String[] arrayOfString = processName(paramString, false, false);
    if (this.contentHandler != null) {
      this.contentHandler.endElement(arrayOfString[0], arrayOfString[1], arrayOfString[2]);
      Enumeration enumeration = this.nsSupport.getDeclaredPrefixes();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        this.contentHandler.endPrefixMapping(str);
      } 
    } 
    this.nsSupport.popContext();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.characters(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (this.contentHandler != null)
      this.contentHandler.processingInstruction(paramString1, paramString2); 
  }
  
  private void setupParser() throws SAXException {
    if (!this.prefixes && !this.namespaces)
      throw new IllegalStateException(); 
    this.nsSupport.reset();
    if (this.uris)
      this.nsSupport.setNamespaceDeclUris(true); 
    if (this.entityResolver != null)
      this.parser.setEntityResolver(this.entityResolver); 
    if (this.dtdHandler != null)
      this.parser.setDTDHandler(this.dtdHandler); 
    if (this.errorHandler != null)
      this.parser.setErrorHandler(this.errorHandler); 
    this.parser.setDocumentHandler(this);
    this.locator = null;
  }
  
  private String[] processName(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws SAXException {
    String[] arrayOfString = this.nsSupport.processName(paramString, this.nameParts, paramBoolean1);
    if (arrayOfString == null) {
      if (paramBoolean2)
        throw makeException("Undeclared prefix: " + paramString); 
      reportError("Undeclared prefix: " + paramString);
      arrayOfString = new String[3];
      arrayOfString[1] = "";
      arrayOfString[0] = "";
      arrayOfString[2] = paramString.intern();
    } 
    return arrayOfString;
  }
  
  void reportError(String paramString) throws IOException, SAXException {
    if (this.errorHandler != null)
      this.errorHandler.error(makeException(paramString)); 
  }
  
  private SAXParseException makeException(String paramString) { return (this.locator != null) ? new SAXParseException(paramString, this.locator) : new SAXParseException(paramString, null, null, -1, -1); }
  
  private void checkNotParsing(String paramString1, String paramString2) throws SAXException {
    if (this.parsing)
      throw new SAXNotSupportedException("Cannot change " + paramString1 + ' ' + paramString2 + " while parsing"); 
  }
  
  final class AttributeListAdapter implements Attributes {
    private AttributeList qAtts;
    
    void setAttributeList(AttributeList param1AttributeList) { this.qAtts = param1AttributeList; }
    
    public int getLength() { return this.qAtts.getLength(); }
    
    public String getURI(int param1Int) { return ""; }
    
    public String getLocalName(int param1Int) { return ""; }
    
    public String getQName(int param1Int) { return this.qAtts.getName(param1Int).intern(); }
    
    public String getType(int param1Int) { return this.qAtts.getType(param1Int).intern(); }
    
    public String getValue(int param1Int) { return this.qAtts.getValue(param1Int); }
    
    public int getIndex(String param1String1, String param1String2) { return -1; }
    
    public int getIndex(String param1String) {
      int i = ParserAdapter.this.atts.getLength();
      for (byte b = 0; b < i; b++) {
        if (this.qAtts.getName(b).equals(param1String))
          return b; 
      } 
      return -1;
    }
    
    public String getType(String param1String1, String param1String2) { return null; }
    
    public String getType(String param1String) { return this.qAtts.getType(param1String).intern(); }
    
    public String getValue(String param1String1, String param1String2) { return null; }
    
    public String getValue(String param1String) { return this.qAtts.getValue(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\ParserAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */