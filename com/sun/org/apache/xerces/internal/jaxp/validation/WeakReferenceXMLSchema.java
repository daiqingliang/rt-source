package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import java.lang.ref.WeakReference;

final class WeakReferenceXMLSchema extends AbstractXMLSchema {
  private WeakReference fGrammarPool = new WeakReference(null);
  
  public XMLGrammarPool getGrammarPool() {
    XMLGrammarPool xMLGrammarPool = (XMLGrammarPool)this.fGrammarPool.get();
    if (xMLGrammarPool == null) {
      xMLGrammarPool = new SoftReferenceGrammarPool();
      this.fGrammarPool = new WeakReference(xMLGrammarPool);
    } 
    return xMLGrammarPool;
  }
  
  public boolean isFullyComposed() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\WeakReferenceXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */