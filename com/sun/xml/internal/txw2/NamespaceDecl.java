package com.sun.xml.internal.txw2;

final class NamespaceDecl {
  final String uri;
  
  boolean requirePrefix;
  
  final String dummyPrefix = 2.toString();
  
  final char uniqueId;
  
  String prefix;
  
  boolean declared;
  
  NamespaceDecl next;
  
  NamespaceDecl(char paramChar, String paramString1, String paramString2, boolean paramBoolean) {
    this.uri = paramString1;
    this.prefix = paramString2;
    this.requirePrefix = paramBoolean;
    this.uniqueId = paramChar;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\NamespaceDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */