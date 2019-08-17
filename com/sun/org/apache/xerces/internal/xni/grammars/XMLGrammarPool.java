package com.sun.org.apache.xerces.internal.xni.grammars;

public interface XMLGrammarPool {
  Grammar[] retrieveInitialGrammarSet(String paramString);
  
  void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar);
  
  Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription);
  
  void lockPool();
  
  void unlockPool();
  
  void clear();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */