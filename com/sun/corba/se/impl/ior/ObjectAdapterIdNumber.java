package com.sun.corba.se.impl.ior;

public class ObjectAdapterIdNumber extends ObjectAdapterIdArray {
  private int poaid;
  
  public ObjectAdapterIdNumber(int paramInt) {
    super("OldRootPOA", Integer.toString(paramInt));
    this.poaid = paramInt;
  }
  
  public int getOldPOAId() { return this.poaid; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectAdapterIdNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */