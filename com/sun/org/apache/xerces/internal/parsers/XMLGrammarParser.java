package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

public abstract class XMLGrammarParser extends XMLParser {
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLGrammarParser(SymbolTable paramSymbolTable) {
    super(new XIncludeAwareParserConfiguration());
    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLGrammarParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */