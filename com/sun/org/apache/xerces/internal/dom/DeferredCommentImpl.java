package com.sun.org.apache.xerces.internal.dom;

public class DeferredCommentImpl extends CommentImpl implements DeferredNode {
  static final long serialVersionUID = 6498796371083589338L;
  
  protected int fNodeIndex;
  
  DeferredCommentImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredCommentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */