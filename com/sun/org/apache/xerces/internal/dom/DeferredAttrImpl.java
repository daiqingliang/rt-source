package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrImpl extends AttrImpl implements DeferredNode {
  static final long serialVersionUID = 6903232312469148636L;
  
  protected int fNodeIndex;
  
  DeferredAttrImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
    needsSyncChildren(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
    int i = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
    isSpecified(((i & 0x20) != 0));
    isIdAttribute(((i & 0x200) != 0));
    int j = deferredDocumentImpl.getLastChild(this.fNodeIndex);
    this.type = deferredDocumentImpl.getTypeInfo(j);
  }
  
  protected void synchronizeChildren() {
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredAttrImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */