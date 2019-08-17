package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

public class XML11DTDProcessor extends XMLDTDLoader {
  public XML11DTDProcessor() {}
  
  public XML11DTDProcessor(SymbolTable paramSymbolTable) { super(paramSymbolTable); }
  
  public XML11DTDProcessor(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) { super(paramSymbolTable, paramXMLGrammarPool); }
  
  XML11DTDProcessor(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLErrorReporter paramXMLErrorReporter, XMLEntityResolver paramXMLEntityResolver) { super(paramSymbolTable, paramXMLGrammarPool, paramXMLErrorReporter, paramXMLEntityResolver); }
  
  protected boolean isValidNmtoken(String paramString) { return XML11Char.isXML11ValidNmtoken(paramString); }
  
  protected boolean isValidName(String paramString) { return XML11Char.isXML11ValidName(paramString); }
  
  protected XMLDTDScannerImpl createDTDScanner(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager) { return new XML11DTDScannerImpl(paramSymbolTable, paramXMLErrorReporter, paramXMLEntityManager); }
  
  protected short getScannerVersion() { return 2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XML11DTDProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */