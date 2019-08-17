package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;

public final class TransientObjectManager {
  private ORB orb;
  
  private int maxSize = 128;
  
  private Element[] elementArray;
  
  private Element freeList;
  
  void dprint(String paramString) { ORBUtility.dprint(this, paramString); }
  
  public TransientObjectManager(ORB paramORB) {
    this.orb = paramORB;
    this.elementArray = new Element[this.maxSize];
    this.elementArray[this.maxSize - 1] = new Element(this.maxSize - 1, null);
    for (int i = this.maxSize - 2; i >= 0; i--)
      this.elementArray[i] = new Element(i, this.elementArray[i + 1]); 
    this.freeList = this.elementArray[0];
  }
  
  public byte[] storeServant(Object paramObject1, Object paramObject2) {
    if (this.freeList == null)
      doubleSize(); 
    Element element = this.freeList;
    this.freeList = (Element)this.freeList.servant;
    byte[] arrayOfByte = element.getKey(paramObject1, paramObject2);
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("storeServant returns key for element " + element); 
    return arrayOfByte;
  }
  
  public Object lookupServant(byte[] paramArrayOfByte) {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    int j = ORBUtility.bytesToInt(paramArrayOfByte, 4);
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("lookupServant called with index=" + i + ", counter=" + j); 
    if ((this.elementArray[i]).counter == j && (this.elementArray[i]).valid) {
      if (this.orb.transientObjectManagerDebugFlag)
        dprint("\tcounter is valid"); 
      return (this.elementArray[i]).servant;
    } 
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("\tcounter is invalid"); 
    return null;
  }
  
  public Object lookupServantData(byte[] paramArrayOfByte) {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    int j = ORBUtility.bytesToInt(paramArrayOfByte, 4);
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("lookupServantData called with index=" + i + ", counter=" + j); 
    if ((this.elementArray[i]).counter == j && (this.elementArray[i]).valid) {
      if (this.orb.transientObjectManagerDebugFlag)
        dprint("\tcounter is valid"); 
      return (this.elementArray[i]).servantData;
    } 
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("\tcounter is invalid"); 
    return null;
  }
  
  public void deleteServant(byte[] paramArrayOfByte) {
    int i = ORBUtility.bytesToInt(paramArrayOfByte, 0);
    if (this.orb.transientObjectManagerDebugFlag)
      dprint("deleting servant at index=" + i); 
    this.elementArray[i].delete(this.freeList);
    this.freeList = this.elementArray[i];
  }
  
  public byte[] getKey(Object paramObject) {
    for (byte b = 0; b < this.maxSize; b++) {
      if ((this.elementArray[b]).valid && (this.elementArray[b]).servant == paramObject)
        return this.elementArray[b].toBytes(); 
    } 
    return null;
  }
  
  private void doubleSize() {
    Element[] arrayOfElement = this.elementArray;
    int i = this.maxSize;
    this.maxSize *= 2;
    this.elementArray = new Element[this.maxSize];
    int j;
    for (j = 0; j < i; j++)
      this.elementArray[j] = arrayOfElement[j]; 
    this.elementArray[this.maxSize - 1] = new Element(this.maxSize - 1, null);
    for (j = this.maxSize - 2; j >= i; j--)
      this.elementArray[j] = new Element(j, this.elementArray[j + 1]); 
    this.freeList = this.elementArray[i];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\toa\TransientObjectManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */