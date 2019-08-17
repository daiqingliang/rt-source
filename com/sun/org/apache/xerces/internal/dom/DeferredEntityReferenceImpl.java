package com.sun.org.apache.xerces.internal.dom;

public class DeferredEntityReferenceImpl extends EntityReferenceImpl implements DeferredNode {
  static final long serialVersionUID = 390319091370032223L;
  
  protected int fNodeIndex;
  
  DeferredEntityReferenceImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
    this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
    this.baseURI = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
  }
  
  protected void synchronizeChildren() {
    needsSyncChildren(false);
    isReadOnly(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
    setReadOnly(true, true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredEntityReferenceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */