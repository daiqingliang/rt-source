package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool implements XMLGrammarPool {
  private final XMLGrammarPool core;
  
  public ReadOnlyGrammarPool(XMLGrammarPool paramXMLGrammarPool) { this.core = paramXMLGrammarPool; }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
  
  public void clear() {}
  
  public void lockPool() {}
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription) { return this.core.retrieveGrammar(paramXMLGrammarDescription); }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString) { return this.core.retrieveInitialGrammarSet(paramString); }
  
  public void unlockPool() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ReadOnlyGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */