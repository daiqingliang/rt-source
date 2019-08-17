package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.reflect.Array;
import java.util.AbstractList;

public final class ObjectListImpl extends AbstractList implements ObjectList {
  public static final ObjectListImpl EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
  
  private final Object[] fArray;
  
  private final int fLength;
  
  public ObjectListImpl(Object[] paramArrayOfObject, int paramInt) {
    this.fArray = paramArrayOfObject;
    this.fLength = paramInt;
  }
  
  public int getLength() { return this.fLength; }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null) {
      for (byte b = 0; b < this.fLength; b++) {
        if (this.fArray[b] == null)
          return true; 
      } 
    } else {
      for (byte b = 0; b < this.fLength; b++) {
        if (paramObject.equals(this.fArray[b]))
          return true; 
      } 
    } 
    return false;
  }
  
  public Object item(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : this.fArray[paramInt]; }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\ObjectListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */