package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMUniOp extends CMNode {
  private CMNode fChild;
  
  public CMUniOp(int paramInt, CMNode paramCMNode) {
    super(paramInt);
    if (type() != 1 && type() != 2 && type() != 3)
      throw new RuntimeException("ImplementationMessages.VAL_UST"); 
    this.fChild = paramCMNode;
  }
  
  final CMNode getChild() { return this.fChild; }
  
  public boolean isNullable() { return (type() == 3) ? this.fChild.isNullable() : 1; }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet) { paramCMStateSet.setTo(this.fChild.firstPos()); }
  
  protected void calcLastPos(CMStateSet paramCMStateSet) { paramCMStateSet.setTo(this.fChild.lastPos()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMUniOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */