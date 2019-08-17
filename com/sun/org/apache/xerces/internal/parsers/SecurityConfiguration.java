package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class SecurityConfiguration extends XIncludeAwareParserConfiguration {
  protected static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
  
  public SecurityConfiguration() { this(null, null, null); }
  
  public SecurityConfiguration(SymbolTable paramSymbolTable) { this(paramSymbolTable, null, null); }
  
  public SecurityConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { this(paramSymbolTable, paramXMLGrammarPool, null); }
  
  public SecurityConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager) {
    super(paramSymbolTable, paramXMLGrammarPool, paramXMLComponentManager);
    setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\SecurityConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */