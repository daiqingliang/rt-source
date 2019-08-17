package com.sun.org.apache.xerces.internal.dom;

public class DeferredProcessingInstructionImpl extends ProcessingInstructionImpl implements DeferredNode {
  static final long serialVersionUID = -4643577954293565388L;
  
  protected int fNodeIndex;
  
  DeferredProcessingInstructionImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt) {
    super(paramDeferredDocumentImpl, null, null);
    this.fNodeIndex = paramInt;
    needsSyncData(true);
  }
  
  public int getNodeIndex() { return this.fNodeIndex; }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    this.target = deferredDocumentImpl.getNodeName(this.fNodeIndex);
    this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredProcessingInstructionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */