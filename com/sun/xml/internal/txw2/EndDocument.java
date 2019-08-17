package com.sun.xml.internal.txw2;

final class EndDocument extends Content {
  boolean concludesPendingStartTag() { return true; }
  
  void accept(ContentVisitor paramContentVisitor) { paramContentVisitor.onEndDocument(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\EndDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */