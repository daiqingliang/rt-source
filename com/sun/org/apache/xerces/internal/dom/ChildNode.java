package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

public abstract class ChildNode extends NodeImpl {
  static final long serialVersionUID = -6112455738802414002L;
  
  StringBuffer fBufferStr = null;
  
  protected ChildNode previousSibling;
  
  protected ChildNode nextSibling;
  
  protected ChildNode(CoreDocumentImpl paramCoreDocumentImpl) { super(paramCoreDocumentImpl); }
  
  public ChildNode() {}
  
  public Node cloneNode(boolean paramBoolean) {
    ChildNode childNode = (ChildNode)super.cloneNode(paramBoolean);
    childNode.previousSibling = null;
    childNode.nextSibling = null;
    childNode.isFirstChild(false);
    return childNode;
  }
  
  public Node getParentNode() { return isOwned() ? this.ownerNode : null; }
  
  final NodeImpl parentNode() { return isOwned() ? this.ownerNode : null; }
  
  public Node getNextSibling() { return this.nextSibling; }
  
  public Node getPreviousSibling() { return isFirstChild() ? null : this.previousSibling; }
  
  final ChildNode previousSibling() { return isFirstChild() ? null : this.previousSibling; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ChildNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */