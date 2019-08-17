package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class CMLeaf extends CMNode {
  private QName fElement = new QName();
  
  private int fPosition = -1;
  
  public CMLeaf(QName paramQName, int paramInt) {
    super(0);
    this.fElement.setValues(paramQName);
    this.fPosition = paramInt;
  }
  
  public CMLeaf(QName paramQName) {
    super(0);
    this.fElement.setValues(paramQName);
  }
  
  final QName getElement() { return this.fElement; }
  
  final int getPosition() { return this.fPosition; }
  
  final void setPosition(int paramInt) { this.fPosition = paramInt; }
  
  public boolean isNullable() { return (this.fPosition == -1); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.fElement.toString());
    stringBuffer.append(" (");
    stringBuffer.append(this.fElement.uri);
    stringBuffer.append(',');
    stringBuffer.append(this.fElement.localpart);
    stringBuffer.append(')');
    if (this.fPosition >= 0)
      stringBuffer.append(" (Pos:" + (new Integer(this.fPosition)).toString() + ")"); 
    return stringBuffer.toString();
  }
  
  protected void calcFirstPos(CMStateSet paramCMStateSet) {
    if (this.fPosition == -1) {
      paramCMStateSet.zeroBits();
    } else {
      paramCMStateSet.setBit(this.fPosition);
    } 
  }
  
  protected void calcLastPos(CMStateSet paramCMStateSet) {
    if (this.fPosition == -1) {
      paramCMStateSet.zeroBits();
    } else {
      paramCMStateSet.setBit(this.fPosition);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMLeaf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */