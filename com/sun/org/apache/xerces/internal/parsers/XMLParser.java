package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class XMLParser {
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-handler" };
  
  protected XMLParserConfiguration fConfiguration;
  
  XMLSecurityManager securityManager;
  
  XMLSecurityPropertyManager securityPropertyManager;
  
  public boolean getFeature(String paramString) throws SAXNotSupportedException, SAXNotRecognizedException { return this.fConfiguration.getFeature(paramString); }
  
  protected XMLParser(XMLParserConfiguration paramXMLParserConfiguration) {
    this.fConfiguration = paramXMLParserConfiguration;
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
  }
  
  public void parse(XMLInputSource paramXMLInputSource) throws XNIException, IOException {
    if (this.securityManager == null) {
      this.securityManager = new XMLSecurityManager(true);
      this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
    } 
    if (this.securityPropertyManager == null) {
      this.securityPropertyManager = new XMLSecurityPropertyManager();
      this.fConfiguration.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
    } 
    reset();
    this.fConfiguration.parse(paramXMLInputSource);
  }
  
  protected void reset() throws XNIException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */