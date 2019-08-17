package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.HashMap;
import java.util.Map;

public class DTDGrammarBucket {
  protected Map<XMLDTDDescription, DTDGrammar> fGrammars = new HashMap();
  
  protected DTDGrammar fActiveGrammar;
  
  protected boolean fIsStandalone;
  
  public void putGrammar(DTDGrammar paramDTDGrammar) {
    XMLDTDDescription xMLDTDDescription = (XMLDTDDescription)paramDTDGrammar.getGrammarDescription();
    this.fGrammars.put(xMLDTDDescription, paramDTDGrammar);
  }
  
  public DTDGrammar getGrammar(XMLGrammarDescription paramXMLGrammarDescription) { return (DTDGrammar)this.fGrammars.get((XMLDTDDescription)paramXMLGrammarDescription); }
  
  public void clear() {
    this.fGrammars.clear();
    this.fActiveGrammar = null;
    this.fIsStandalone = false;
  }
  
  void setStandalone(boolean paramBoolean) { this.fIsStandalone = paramBoolean; }
  
  boolean getStandalone() { return this.fIsStandalone; }
  
  void setActiveGrammar(DTDGrammar paramDTDGrammar) { this.fActiveGrammar = paramDTDGrammar; }
  
  DTDGrammar getActiveGrammar() { return this.fActiveGrammar; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\DTDGrammarBucket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */