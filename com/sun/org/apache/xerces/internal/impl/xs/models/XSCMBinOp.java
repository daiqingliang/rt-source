package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMBinOp extends CMNode {
  private CMNode fLeftChild;
  
  private CMNode fRightChild;
  
  public XSCMBinOp(int paramInt, CMNode paramCMNode1, CMNode paramCMNode2) {
    super(paramInt);
    if (type() != 101 && type() != 102)
      throw new RuntimeException("ImplementationMessages.VAL_BST"); 
    this.fLeftChild = paramCMNode1;
    this.fRightChild = paramCMNode2;
  }
  
  final CMNode getLeft() { return this.fLeftChild; }
  
  final CMNode getRight() { return this.fRightChild; }
  
  public boolean isNullable() {
    if (type() == 101)
      return (this.fLeftChild.isNullable() || this.fRightChild.isNullable()); 
    if (type() == 102)
      return (this.fLeftChild.isNullable() && this.fRightChild.isNullable()); 
    throw new RuntimeException("ImplementationMessages.VAL_BST");
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet) {
    if (type() == 101) {
      paramCMStateSet.setTo(this.fLeftChild.firstPos());
      paramCMStateSet.union(this.fRightChild.firstPos());
    } else if (type() == 102) {
      paramCMStateSet.setTo(this.fLeftChild.firstPos());
      if (this.fLeftChild.isNullable())
        paramCMStateSet.union(this.fRightChild.firstPos()); 
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_BST");
    } 
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet) {
    if (type() == 101) {
      paramCMStateSet.setTo(this.fLeftChild.lastPos());
      paramCMStateSet.union(this.fRightChild.lastPos());
    } else if (type() == 102) {
      paramCMStateSet.setTo(this.fRightChild.lastPos());
      if (this.fRightChild.isNullable())
        paramCMStateSet.union(this.fLeftChild.lastPos()); 
    } else {
      throw new RuntimeException("ImplementationMessages.VAL_BST");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\models\XSCMBinOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */