package com.sun.org.apache.xerces.internal.xs;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSInput;

public interface XSLoader {
  DOMConfiguration getConfig();
  
  XSModel loadURIList(StringList paramStringList);
  
  XSModel loadInputList(LSInputList paramLSInputList);
  
  XSModel loadURI(String paramString);
  
  XSModel load(LSInput paramLSInput);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */