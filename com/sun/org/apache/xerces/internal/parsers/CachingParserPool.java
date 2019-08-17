package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.ShadowedSymbolTable;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public class CachingParserPool {
  public static final boolean DEFAULT_SHADOW_SYMBOL_TABLE = false;
  
  public static final boolean DEFAULT_SHADOW_GRAMMAR_POOL = false;
  
  protected SymbolTable fSynchronizedSymbolTable;
  
  protected XMLGrammarPool fSynchronizedGrammarPool;
  
  protected boolean fShadowSymbolTable = false;
  
  protected boolean fShadowGrammarPool = false;
  
  public CachingParserPool() { this(new SymbolTable(), new XMLGrammarPoolImpl()); }
  
  public CachingParserPool(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool) {
    this.fSynchronizedSymbolTable = new SynchronizedSymbolTable(paramSymbolTable);
    this.fSynchronizedGrammarPool = new SynchronizedGrammarPool(paramXMLGrammarPool);
  }
  
  public SymbolTable getSymbolTable() { return this.fSynchronizedSymbolTable; }
  
  public XMLGrammarPool getXMLGrammarPool() { return this.fSynchronizedGrammarPool; }
  
  public void setShadowSymbolTable(boolean paramBoolean) { this.fShadowSymbolTable = paramBoolean; }
  
  public DOMParser createDOMParser() {
    ShadowedSymbolTable shadowedSymbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
    ShadowedGrammarPool shadowedGrammarPool = this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
    return new DOMParser(shadowedSymbolTable, shadowedGrammarPool);
  }
  
  public SAXParser createSAXParser() {
    ShadowedSymbolTable shadowedSymbolTable = this.fShadowSymbolTable ? new ShadowedSymbolTable(this.fSynchronizedSymbolTable) : this.fSynchronizedSymbolTable;
    ShadowedGrammarPool shadowedGrammarPool = this.fShadowGrammarPool ? new ShadowedGrammarPool(this.fSynchronizedGrammarPool) : this.fSynchronizedGrammarPool;
    return new SAXParser(shadowedSymbolTable, shadowedGrammarPool);
  }
  
  public static final class ShadowedGrammarPool extends XMLGrammarPoolImpl {
    private XMLGrammarPool fGrammarPool;
    
    public ShadowedGrammarPool(XMLGrammarPool param1XMLGrammarPool) { this.fGrammarPool = param1XMLGrammarPool; }
    
    public Grammar[] retrieveInitialGrammarSet(String param1String) {
      Grammar[] arrayOfGrammar = super.retrieveInitialGrammarSet(param1String);
      return (arrayOfGrammar != null) ? arrayOfGrammar : this.fGrammarPool.retrieveInitialGrammarSet(param1String);
    }
    
    public Grammar retrieveGrammar(XMLGrammarDescription param1XMLGrammarDescription) {
      Grammar grammar = super.retrieveGrammar(param1XMLGrammarDescription);
      return (grammar != null) ? grammar : this.fGrammarPool.retrieveGrammar(param1XMLGrammarDescription);
    }
    
    public void cacheGrammars(String param1String, Grammar[] param1ArrayOfGrammar) {
      super.cacheGrammars(param1String, param1ArrayOfGrammar);
      this.fGrammarPool.cacheGrammars(param1String, param1ArrayOfGrammar);
    }
    
    public Grammar getGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return super.containsGrammar(param1XMLGrammarDescription) ? super.getGrammar(param1XMLGrammarDescription) : null; }
    
    public boolean containsGrammar(XMLGrammarDescription param1XMLGrammarDescription) { return super.containsGrammar(param1XMLGrammarDescription); }
  }
  
  public static final class SynchronizedGrammarPool implements XMLGrammarPool {
    private XMLGrammarPool fGrammarPool;
    
    public SynchronizedGrammarPool(XMLGrammarPool param1XMLGrammarPool) { this.fGrammarPool = param1XMLGrammarPool; }
    
    public Grammar[] retrieveInitialGrammarSet(String param1String) {
      synchronized (this.fGrammarPool) {
        return this.fGrammarPool.retrieveInitialGrammarSet(param1String);
      } 
    }
    
    public Grammar retrieveGrammar(XMLGrammarDescription param1XMLGrammarDescription) {
      synchronized (this.fGrammarPool) {
        return this.fGrammarPool.retrieveGrammar(param1XMLGrammarDescription);
      } 
    }
    
    public void cacheGrammars(String param1String, Grammar[] param1ArrayOfGrammar) {
      synchronized (this.fGrammarPool) {
        this.fGrammarPool.cacheGrammars(param1String, param1ArrayOfGrammar);
      } 
    }
    
    public void lockPool() {
      synchronized (this.fGrammarPool) {
        this.fGrammarPool.lockPool();
      } 
    }
    
    public void clear() {
      synchronized (this.fGrammarPool) {
        this.fGrammarPool.clear();
      } 
    }
    
    public void unlockPool() {
      synchronized (this.fGrammarPool) {
        this.fGrammarPool.unlockPool();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\CachingParserPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */