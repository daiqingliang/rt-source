package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelImpl;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.util.ArrayList;

public class XSGrammarPool extends XMLGrammarPoolImpl {
  public XSModel toXSModel() { return toXSModel((short)1); }
  
  public XSModel toXSModel(short paramShort) {
    ArrayList arrayList = new ArrayList();
    int i;
    for (i = 0; i < this.fGrammars.length; i++) {
      for (XMLGrammarPoolImpl.Entry entry = this.fGrammars[i]; entry != null; entry = entry.next) {
        if (entry.desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema"))
          arrayList.add(entry.grammar); 
      } 
    } 
    i = arrayList.size();
    if (i == 0)
      return toXSModel(new SchemaGrammar[0], paramShort); 
    SchemaGrammar[] arrayOfSchemaGrammar = (SchemaGrammar[])arrayList.toArray(new SchemaGrammar[i]);
    return toXSModel(arrayOfSchemaGrammar, paramShort);
  }
  
  protected XSModel toXSModel(SchemaGrammar[] paramArrayOfSchemaGrammar, short paramShort) { return new XSModelImpl(paramArrayOfSchemaGrammar, paramShort); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\XSGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */