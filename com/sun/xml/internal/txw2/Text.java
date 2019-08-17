package com.sun.xml.internal.txw2;

abstract class Text extends Content {
  protected final StringBuilder buffer = new StringBuilder();
  
  protected Text(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject) { paramDocument.writeValue(paramObject, paramNamespaceResolver, this.buffer); }
  
  boolean concludesPendingStartTag() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */