package com.sun.org.apache.xerces.internal.dom;

public class DeferredElementDefinitionImpl extends ElementDefinitionImpl implements DeferredNode {
  static final long serialVersionUID = 6703238199538041591L;
  
  protected int fNodeIndex;
  
  DeferredElementDefinitionImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
    needsSyncChildren(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
    this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
  }
  
  protected void synchronizeChildren() {
    boolean bool = this.ownerDocument.getMutationEvents();
    this.ownerDocument.setMutationEvents(false);
    needsSyncChildren(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
    this.attributes = new NamedNodeMapImpl(deferredDocumentImpl);
    for (int i = deferredDocumentImpl.getLastChild(this.fNodeIndex); i != -1; i = deferredDocumentImpl.getPrevSibling(i)) {
      DeferredNode deferredNode = deferredDocumentImpl.getNodeObject(i);
      this.attributes.setNamedItem(deferredNode);
    } 
    deferredDocumentImpl.setMutationEvents(bool);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredElementDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */