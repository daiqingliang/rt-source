package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMBinOp extends CMNode {
  private CMNode fLeftChild;
  
  private CMNode fRightChild;
  
  public CMBinOp(int paramInt, CMNode paramCMNode1, CMNode paramCMNode2) {
    super(paramInt);
    if (type() != 4 && type() != 5)
      throw new RuntimeException("ImplementationMessages.VAL_BST"); 
    this.fLeftChild = paramCMNode1;
    this.fRightChild = paramCMNode2;
  }
  
  final CMNode getLeft() { return this.fLeftChild; }
  
  final CMNode getRight() { return this.fRightChild; }
  
  public boolean isNullable() {
    if (type() == 4)
      return (this.fLeftChild.isNullable() || this.fRightChild.isNullable()); 
    if (type() == 5)
      return (this.fLeftChild.isNullable() && this.fRightChild.isNullable()); 
    throw new RuntimeException("ImplementationMessages.VAL_BST");
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet) {
    if (type() == 4) {
      paramCMStateSet.setTo(this.fLeftChild.firstPos());
      paramCMStateSet.union(this.fRightChild.firstPos());
    } else if (type() == 5) {
      paramCMStateSet.setTo(this.fLeftChild.firstPos());
      if (this.fLeftChild.isNullable())
        paramCMStateSet.union(this.fRightChild.firstPos()); 
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_BST");
    } 
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet) {
    if (type() == 4) {
      paramCMStateSet.setTo(this.fLeftChild.lastPos());
      paramCMStateSet.union(this.fRightChild.lastPos());
    } else if (type() == 5) {
      paramCMStateSet.setTo(this.fRightChild.lastPos());
      if (this.fRightChild.isNullable())
        paramCMStateSet.union(this.fLeftChild.lastPos()); 
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_BST");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMBinOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */