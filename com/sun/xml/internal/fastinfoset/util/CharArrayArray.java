package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayArray extends ValueArray {
  private CharArray[] _array;
  
  private CharArrayArray _readOnlyArray;
  
  public CharArrayArray(int paramInt1, int paramInt2) {
    this._array = new CharArray[paramInt1];
    this._maximumCapacity = paramInt2;
  }
  
  public CharArrayArray() { this(10, 2147483647); }
  
  public final void clear() {
    for (byte b = 0; b < this._size; b++)
      this._array[b] = null; 
    this._size = 0;
  }
  
  public final CharArray[] getArray() {
    if (this._array == null)
      return null; 
    CharArray[] arrayOfCharArray = new CharArray[this._array.length];
    System.arraycopy(this._array, 0, arrayOfCharArray, 0, this._array.length);
    return arrayOfCharArray;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean) {
    if (!(paramValueArray instanceof CharArrayArray))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray })); 
    setReadOnlyArray((CharArrayArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(CharArrayArray paramCharArrayArray, boolean paramBoolean) {
    if (paramCharArrayArray != null) {
      this._readOnlyArray = paramCharArrayArray;
      this._readOnlyArraySize = paramCharArrayArray.getSize();
      if (paramBoolean)
        clear(); 
    } 
  }
  
  public final CharArray get(int paramInt) { return (this._readOnlyArray == null) ? this._array[paramInt] : ((paramInt < this._readOnlyArraySize) ? this._readOnlyArray.get(paramInt) : this._array[paramInt - this._readOnlyArraySize]); }
  
  public final void add(CharArray paramCharArray) {
    if (this._size == this._array.length)
      resize(); 
    this._array[this._size++] = paramCharArray;
  }
  
  protected final void resize() {
    if (this._size == this._maximumCapacity)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity")); 
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity)
      i = this._maximumCapacity; 
    CharArray[] arrayOfCharArray = new CharArray[i];
    System.arraycopy(this._array, 0, arrayOfCharArray, 0, this._size);
    this._array = arrayOfCharArray;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\CharArrayArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */