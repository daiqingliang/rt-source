package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.AbstractList;

public final class ShortListImpl extends AbstractList implements ShortList {
  public static final ShortListImpl EMPTY_LIST = new ShortListImpl(new short[0], 0);
  
  private final short[] fArray;
  
  private final int fLength;
  
  public ShortListImpl(short[] paramArrayOfShort, int paramInt) {
    this.fArray = paramArrayOfShort;
    this.fLength = paramInt;
  }
  
  public int getLength() { return this.fLength; }
  
  public boolean contains(short paramShort) {
    for (byte b = 0; b < this.fLength; b++) {
      if (this.fArray[b] == paramShort)
        return true; 
    } 
    return false;
  }
  
  public short item(int paramInt) throws XSException {
    if (paramInt < 0 || paramInt >= this.fLength)
      throw new XSException((short)2, null); 
    return this.fArray[paramInt];
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof ShortList))
      return false; 
    ShortList shortList = (ShortList)paramObject;
    if (this.fLength != shortList.getLength())
      return false; 
    for (byte b = 0; b < this.fLength; b++) {
      if (this.fArray[b] != shortList.item(b))
        return false; 
    } 
    return true;
  }
  
  public Object get(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fLength)
      return new Short(this.fArray[paramInt]); 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size() { return getLength(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\ShortListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */