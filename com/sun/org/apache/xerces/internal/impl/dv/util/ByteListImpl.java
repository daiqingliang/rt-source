package com.sun.org.apache.xerces.internal.impl.dv.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import java.util.AbstractList;

public class ByteListImpl extends AbstractList implements ByteList {
  protected final byte[] data;
  
  protected String canonical;
  
  public ByteListImpl(byte[] paramArrayOfByte) { this.data = paramArrayOfByte; }
  
  public int getLength() { return this.data.length; }
  
  public boolean contains(byte paramByte) {
    for (byte b = 0; b < this.data.length; b++) {
      if (this.data[b] == paramByte)
        return true; 
    } 
    return false;
  }
  
  public byte item(int paramInt) throws XSException {
    if (paramInt < 0 || paramInt > this.data.length - 1)
      throw new XSException((short)2, null); 
    return this.data[paramInt];
  }
  
  public Object get(int paramInt) {
    if (paramInt >= 0 && paramInt < this.data.length)
      return new Byte(this.data[paramInt]); 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size() { return getLength(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\d\\util\ByteListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */