package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParser extends AbstractSAXParser {
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  
  protected static final String REPORT_WHITESPACE = "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://java.sun.com/xml/schema/features/report-ignored-element-content-whitespace" };
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
  
  public SAXParser(XMLParserConfiguration paramXMLParserConfiguration) { super(paramXMLParserConfiguration); }
  
  public SAXParser() { this(null, null); }
  
  public SAXParser(SymbolTable paramSymbolTable) { this(paramSymbolTable, null); }
  
  public SAXParser(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) {
    super(new XIncludeAwareParserConfiguration());
    this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
    this.fConfiguration.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    if (paramSymbolTable != null)
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable); 
    if (paramXMLGrammarPool != null)
      this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", paramXMLGrammarPool); 
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      this.securityManager = XMLSecurityManager.convert(paramObject, this.securityManager);
      super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
      return;
    } 
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      if (paramObject == null) {
        this.securityPropertyManager = new XMLSecurityPropertyManager();
      } else {
        this.securityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      } 
      super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
      return;
    } 
    if (this.securityManager == null) {
      this.securityManager = new XMLSecurityManager(true);
      super.setProperty("http://apache.org/xml/properties/security-manager", this.securityManager);
    } 
    if (this.securityPropertyManager == null) {
      this.securityPropertyManager = new XMLSecurityPropertyManager();
      super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.securityPropertyManager);
    } 
    int i = this.securityPropertyManager.getIndex(paramString);
    if (i > -1) {
      this.securityPropertyManager.setValue(i, XMLSecurityPropertyManager.State.APIPROPERTY, (String)paramObject);
    } else if (!this.securityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) {
      super.setProperty(paramString, paramObject);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\SAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */