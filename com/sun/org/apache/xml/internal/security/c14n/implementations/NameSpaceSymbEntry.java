package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Attr;

class NameSpaceSymbEntry implements Cloneable {
  String prefix;
  
  String uri;
  
  String lastrendered = null;
  
  boolean rendered = false;
  
  Attr n;
  
  NameSpaceSymbEntry(String paramString1, Attr paramAttr, boolean paramBoolean, String paramString2) {
    this.uri = paramString1;
    this.rendered = paramBoolean;
    this.n = paramAttr;
    this.prefix = paramString2;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\NameSpaceSymbEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */