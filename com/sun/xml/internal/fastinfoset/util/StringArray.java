package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringArray extends ValueArray {
  public String[] _array;
  
  private StringArray _readOnlyArray;
  
  private boolean _clear;
  
  public StringArray(int paramInt1, int paramInt2, boolean paramBoolean) {
    this._array = new String[paramInt1];
    this._maximumCapacity = paramInt2;
    this._clear = paramBoolean;
  }
  
  public StringArray() { this(10, 2147483647, false); }
  
  public final void clear() {
    if (this._clear)
      for (int i = this._readOnlyArraySize; i < this._size; i++)
        this._array[i] = null;  
    this._size = this._readOnlyArraySize;
  }
  
  public final String[] getArray() {
    if (this._array == null)
      return null; 
    String[] arrayOfString = new String[this._array.length];
    System.arraycopy(this._array, 0, arrayOfString, 0, this._array.length);
    return arrayOfString;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean) {
    if (!(paramValueArray instanceof StringArray))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray })); 
    setReadOnlyArray((StringArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(StringArray paramStringArray, boolean paramBoolean) {
    if (paramStringArray != null) {
      this._readOnlyArray = paramStringArray;
      this._readOnlyArraySize = paramStringArray.getSize();
      if (paramBoolean)
        clear(); 
      this._array = getCompleteArray();
      this._size = this._readOnlyArraySize;
    } 
  }
  
  public final String[] getCompleteArray() {
    if (this._readOnlyArray == null)
      return getArray(); 
    String[] arrayOfString1 = this._readOnlyArray.getCompleteArray();
    String[] arrayOfString2 = new String[this._readOnlyArraySize + this._array.length];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, this._readOnlyArraySize);
    return arrayOfString2;
  }
  
  public final String get(int paramInt) { return this._array[paramInt]; }
  
  public final int add(String paramString) {
    if (this._size == this._array.length)
      resize(); 
    this._array[this._size++] = paramString;
    return this._size;
  }
  
  protected final void resize() {
    if (this._size == this._maximumCapacity)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity")); 
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity)
      i = this._maximumCapacity; 
    String[] arrayOfString = new String[i];
    System.arraycopy(this._array, 0, arrayOfString, 0, this._size);
    this._array = arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\StringArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */