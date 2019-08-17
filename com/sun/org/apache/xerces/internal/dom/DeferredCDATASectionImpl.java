package com.sun.org.apache.xerces.internal.dom;

public class DeferredCDATASectionImpl extends CDATASectionImpl implements DeferredNode {
  static final long serialVersionUID = 1983580632355645726L;
  
  protected int fNodeIndex;
  
  DeferredCDATASectionImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredCDATASectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */