package com.sun.org.apache.xerces.internal.dom;

public class DeferredTextImpl extends TextImpl implements DeferredNode {
  static final long serialVersionUID = 2310613872100393425L;
  
  protected int fNodeIndex;
  
  DeferredTextImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
    isIgnorableWhitespace((deferredDocumentImpl.getNodeExtra(this.fNodeIndex) == 1));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredTextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */