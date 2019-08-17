package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

public class XMLDocumentParser extends AbstractXMLDocumentParser {
  public XMLDocumentParser() { super(new XIncludeAwareParserConfiguration()); }
  
  public XMLDocumentParser(XMLParserConfiguration paramXMLParserConfiguration) { super(paramXMLParserConfiguration); }
  
  public XMLDocumentParser(SymbolTable paramSymbolTable) {
    super(new XIncludeAwareParserConfiguration());
    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
  }
  
  public XMLDocumentParser(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) {
    super(new XIncludeAwareParserConfiguration());
    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", paramXMLGrammarPool);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */