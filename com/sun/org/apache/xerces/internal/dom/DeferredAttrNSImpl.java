package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrNSImpl extends AttrNSImpl implements DeferredNode {
  static final long serialVersionUID = 6074924934945957154L;
  
  protected int fNodeIndex;
  
  DeferredAttrNSImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
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
    int i = this.name.indexOf(':');
    if (i < 0) {
      this.localName = this.name;
    } else {
      this.localName = this.name.substring(i + 1);
    } 
    int j = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
    isSpecified(((j & 0x20) != 0));
    isIdAttribute(((j & 0x200) != 0));
    this.namespaceURI = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
    int k = deferredDocumentImpl.getLastChild(this.fNodeIndex);
    this.type = deferredDocumentImpl.getTypeInfo(k);
  }
  
  protected void synchronizeChildren() {
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredAttrNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */