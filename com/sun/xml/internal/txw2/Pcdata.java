package com.sun.xml.internal.txw2;

final class Pcdata extends Text {
  Pcdata(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject) { super(paramDocument, paramNamespaceResolver, paramObject); }
  
  void accept(ContentVisitor paramContentVisitor) { paramContentVisitor.onPcdata(this.buffer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Pcdata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */