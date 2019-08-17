package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.LSInputList;
import java.lang.reflect.Array;
import java.util.AbstractList;
import org.w3c.dom.ls.LSInput;

public final class LSInputListImpl extends AbstractList implements LSInputList {
  public static final LSInputListImpl EMPTY_LIST = new LSInputListImpl(new LSInput[0], 0);
  
  private final LSInput[] fArray;
  
  private final int fLength;
  
  public LSInputListImpl(LSInput[] paramArrayOfLSInput, int paramInt) {
    this.fArray = paramArrayOfLSInput;
    this.fLength = paramInt;
  }
  
  public int getLength() { return this.fLength; }
  
  public LSInput item(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : this.fArray[paramInt]; }
  
  public Object get(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fLength)
      return this.fArray[paramInt]; 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size() { return getLength(); }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.fLength];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject) {
    if (paramArrayOfObject.length < this.fLength) {
      Class clazz1 = paramArrayOfObject.getClass();
      Class clazz2 = clazz1.getComponentType();
      paramArrayOfObject = (Object[])Array.newInstance(clazz2, this.fLength);
    } 
    toArray0(paramArrayOfObject);
    if (paramArrayOfObject.length > this.fLength)
      paramArrayOfObject[this.fLength] = null; 
    return paramArrayOfObject;
  }
  
  private void toArray0(Object[] paramArrayOfObject) {
    if (this.fLength > 0)
      System.arraycopy(this.fArray, 0, paramArrayOfObject, 0, this.fLength); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\LSInputListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */