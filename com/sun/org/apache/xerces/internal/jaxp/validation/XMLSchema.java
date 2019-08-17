package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class XMLSchema extends AbstractXMLSchema {
  private final XMLGrammarPool fGrammarPool;
  
  public XMLSchema(XMLGrammarPool paramXMLGrammarPool) { this.fGrammarPool = paramXMLGrammarPool; }
  
  public XMLGrammarPool getGrammarPool() { return this.fGrammarPool; }
  
  public boolean isFullyComposed() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */