package com.sun.xml.internal.txw2;

final class StartDocument extends Content {
  boolean concludesPendingStartTag() { return true; }
  
  void accept(ContentVisitor paramContentVisitor) { paramContentVisitor.onStartDocument(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\StartDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */