package com.sun.org.apache.xerces.internal.impl.dtd.models;

public abstract class CMNode {
  private int fType;
  
  private CMStateSet fFirstPos = null;
  
  private CMStateSet fFollowPos = null;
  
  private CMStateSet fLastPos = null;
  
  private int fMaxStates = -1;
  
  private Object fUserData = null;
  
  public CMNode(int paramInt) { this.fType = paramInt; }
  
  public abstract boolean isNullable();
  
  public final int type() { return this.fType; }
  
  public final CMStateSet firstPos() {
    if (this.fFirstPos == null) {
      this.fFirstPos = new CMStateSet(this.fMaxStates);
      calcFirstPos(this.fFirstPos);
    } 
    return this.fFirstPos;
  }
  
  public final CMStateSet lastPos() {
    if (this.fLastPos == null) {
      this.fLastPos = new CMStateSet(this.fMaxStates);
      calcLastPos(this.fLastPos);
    } 
    return this.fLastPos;
  }
  
  final void setFollowPos(CMStateSet paramCMStateSet) { this.fFollowPos = paramCMStateSet; }
  
  public final void setMaxStates(int paramInt) { this.fMaxStates = paramInt; }
  
  public void setUserData(Object paramObject) { this.fUserData = paramObject; }
  
  public Object getUserData() { return this.fUserData; }
  
  protected abstract void calcFirstPos(CMStateSet paramCMStateSet);
  
  protected abstract void calcLastPos(CMStateSet paramCMStateSet);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\models\CMNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */