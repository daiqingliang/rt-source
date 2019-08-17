package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class SimpleXMLSchema extends AbstractXMLSchema implements XMLGrammarPool {
  private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
  
  private Grammar fGrammar;
  
  private Grammar[] fGrammars;
  
  private XMLGrammarDescription fGrammarDescription;
  
  public SimpleXMLSchema(Grammar paramGrammar) {
    this.fGrammar = paramGrammar;
    this.fGrammars = new Grammar[] { paramGrammar };
    this.fGrammarDescription = paramGrammar.getGrammarDescription();
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString) { return "http://www.w3.org/2001/XMLSchema".equals(paramString) ? (Grammar[])this.fGrammars.clone() : ZERO_LENGTH_GRAMMAR_ARRAY; }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription) { return this.fGrammarDescription.equals(paramXMLGrammarDescription) ? this.fGrammar : null; }
  
  public void lockPool() {}
  
  public void unlockPool() {}
  
  public void clear() {}
  
  public XMLGrammarPool getGrammarPool() { return this; }
  
  public boolean isFullyComposed() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\SimpleXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */