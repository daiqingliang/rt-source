package com.sun.org.apache.xerces.internal.xs;

public interface XSImplementation {
  StringList getRecognizedVersions();
  
  XSLoader createXSLoader(StringList paramStringList) throws XSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */