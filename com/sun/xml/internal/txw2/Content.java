package com.sun.xml.internal.txw2;

abstract class Content {
  private Content next;
  
  final Content getNext() { return this.next; }
  
  final void setNext(Document paramDocument, Content paramContent) {
    assert paramContent != null;
    assert this.next == null : "next of " + this + " is already set to " + this.next;
    this.next = paramContent;
    paramDocument.run();
  }
  
  boolean isReadyToCommit() { return true; }
  
  abstract boolean concludesPendingStartTag();
  
  abstract void accept(ContentVisitor paramContentVisitor);
  
  public void written() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Content.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */