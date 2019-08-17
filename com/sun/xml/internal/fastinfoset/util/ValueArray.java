package com.sun.xml.internal.fastinfoset.util;

public abstract class ValueArray {
  public static final int DEFAULT_CAPACITY = 10;
  
  public static final int MAXIMUM_CAPACITY = 2147483647;
  
  protected int _size;
  
  protected int _readOnlyArraySize;
  
  protected int _maximumCapacity;
  
  public int getSize() { return this._size; }
  
  public int getMaximumCapacity() { return this._maximumCapacity; }
  
  public void setMaximumCapacity(int paramInt) { this._maximumCapacity = paramInt; }
  
  public abstract void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean);
  
  public abstract void clear();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\ValueArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */