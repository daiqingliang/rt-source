package com.sun.xml.internal.txw2;

final class Comment extends Content {
  private final StringBuilder buffer = new StringBuilder();
  
  public Comment(Document paramDocument, NamespaceResolver paramNamespaceResolver, Object paramObject) { paramDocument.writeValue(paramObject, paramNamespaceResolver, this.buffer); }
  
  boolean concludesPendingStartTag() { return false; }
  
  void accept(ContentVisitor paramContentVisitor) { paramContentVisitor.onComment(this.buffer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Comment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */