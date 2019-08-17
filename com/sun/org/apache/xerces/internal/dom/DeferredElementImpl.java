package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;

public class DeferredElementImpl extends ElementImpl implements DeferredNode {
  static final long serialVersionUID = -7670981133940934842L;
  
  protected int fNodeIndex;
  
  DeferredElementImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncChildren(true);
  }
  
  public final int getNodeIndex() { return this.fNodeIndex; }
  
  protected final void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
    boolean bool = deferredDocumentImpl.mutationEvents;
    deferredDocumentImpl.mutationEvents = false;
    this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
    setupDefaultAttributes();
    int i = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
    if (i != -1) {
      NamedNodeMap namedNodeMap = getAttributes();
      do {
        NodeImpl nodeImpl = (NodeImpl)deferredDocumentImpl.getNodeObject(i);
        namedNodeMap.setNamedItem(nodeImpl);
        i = deferredDocumentImpl.getPrevSibling(i);
      } while (i != -1);
    } 
    deferredDocumentImpl.mutationEvents = bool;
  }
  
  protected final void synchronizeChildren() {
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */