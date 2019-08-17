package com.sun.org.apache.xerces.internal.xs;

public interface ElementPSVI extends ItemPSVI {
  XSElementDeclaration getElementDeclaration();
  
  XSNotationDeclaration getNotation();
  
  boolean getNil();
  
  XSModel getSchemaInformation();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\ElementPSVI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */