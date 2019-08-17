package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xs.XSModel;

public interface XSGrammar extends Grammar {
  XSModel toXSModel();
  
  XSModel toXSModel(XSGrammar[] paramArrayOfXSGrammar);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XSGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */