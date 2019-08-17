package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class ContiguousCharArrayArray extends ValueArray {
  public static final int INITIAL_CHARACTER_SIZE = 512;
  
  public static final int MAXIMUM_CHARACTER_SIZE = 2147483647;
  
  protected int _maximumCharacterSize;
  
  public int[] _offset;
  
  public int[] _length;
  
  public char[] _array;
  
  public int _arrayIndex;
  
  public int _readOnlyArrayIndex;
  
  private String[] _cachedStrings;
  
  public int _cachedIndex;
  
  private ContiguousCharArrayArray _readOnlyArray;
  
  public ContiguousCharArrayArray(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this._offset = new int[paramInt1];
    this._length = new int[paramInt1];
    this._array = new char[paramInt3];
    this._maximumCapacity = paramInt2;
    this._maximumCharacterSize = paramInt4;
  }
  
  public ContiguousCharArrayArray() { this(10, 2147483647, 512, 2147483647); }
  
  public final void clear() {
    this._arrayIndex = this._readOnlyArrayIndex;
    this._size = this._readOnlyArraySize;
    if (this._cachedStrings != null)
      for (int i = this._readOnlyArraySize; i < this._cachedStrings.length; i++)
        this._cachedStrings[i] = null;  
  }
  
  public final int getArrayIndex() { return this._arrayIndex; }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean) {
    if (!(paramValueArray instanceof ContiguousCharArrayArray))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray })); 
    setReadOnlyArray((ContiguousCharArrayArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(ContiguousCharArrayArray paramContiguousCharArrayArray, boolean paramBoolean) {
    if (paramContiguousCharArrayArray != null) {
      this._readOnlyArray = paramContiguousCharArrayArray;
      this._readOnlyArraySize = paramContiguousCharArrayArray.getSize();
      this._readOnlyArrayIndex = paramContiguousCharArrayArray.getArrayIndex();
      if (paramBoolean)
        clear(); 
      this._array = getCompleteCharArray();
      this._offset = getCompleteOffsetArray();
      this._length = getCompleteLengthArray();
      this._size = this._readOnlyArraySize;
      this._arrayIndex = this._readOnlyArrayIndex;
    } 
  }
  
  public final char[] getCompleteCharArray() {
    if (this._readOnlyArray == null) {
      if (this._array == null)
        return null; 
      char[] arrayOfChar = new char[this._array.length];
      System.arraycopy(this._array, 0, arrayOfChar, 0, this._array.length);
      return arrayOfChar;
    } 
    char[] arrayOfChar1 = this._readOnlyArray.getCompleteCharArray();
    char[] arrayOfChar2 = new char[this._readOnlyArrayIndex + this._array.length];
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, this._readOnlyArrayIndex);
    return arrayOfChar2;
  }
  
  public final int[] getCompleteOffsetArray() {
    if (this._readOnlyArray == null) {
      if (this._offset == null)
        return null; 
      int[] arrayOfInt = new int[this._offset.length];
      System.arraycopy(this._offset, 0, arrayOfInt, 0, this._offset.length);
      return arrayOfInt;
    } 
    int[] arrayOfInt1 = this._readOnlyArray.getCompleteOffsetArray();
    int[] arrayOfInt2 = new int[this._readOnlyArraySize + this._offset.length];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, this._readOnlyArraySize);
    return arrayOfInt2;
  }
  
  public final int[] getCompleteLengthArray() {
    if (this._readOnlyArray == null) {
      if (this._length == null)
        return null; 
      int[] arrayOfInt = new int[this._length.length];
      System.arraycopy(this._length, 0, arrayOfInt, 0, this._length.length);
      return arrayOfInt;
    } 
    int[] arrayOfInt1 = this._readOnlyArray.getCompleteLengthArray();
    int[] arrayOfInt2 = new int[this._readOnlyArraySize + this._length.length];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, this._readOnlyArraySize);
    return arrayOfInt2;
  }
  
  public final String getString(int paramInt) {
    if (this._cachedStrings != null && paramInt < this._cachedStrings.length) {
      String str = this._cachedStrings[paramInt];
      return (str != null) ? str : (this._cachedStrings[paramInt] = new String(this._array, this._offset[paramInt], this._length[paramInt]));
    } 
    String[] arrayOfString = new String[this._offset.length];
    if (this._cachedStrings != null && paramInt >= this._cachedStrings.length)
      System.arraycopy(this._cachedStrings, 0, arrayOfString, 0, this._cachedStrings.length); 
    this._cachedStrings = arrayOfString;
    this._cachedStrings[paramInt] = new String(this._array, this._offset[paramInt], this._length[paramInt]);
    return new String(this._array, this._offset[paramInt], this._length[paramInt]);
  }
  
  public final void ensureSize(int paramInt) {
    if (this._arrayIndex + paramInt >= this._array.length)
      resizeArray(this._arrayIndex + paramInt); 
  }
  
  public final void add(int paramInt) {
    if (this._size == this._offset.length)
      resize(); 
    this._cachedIndex = this._size;
    this._offset[this._size] = this._arrayIndex;
    this._length[this._size++] = paramInt;
    this._arrayIndex += paramInt;
  }
  
  public final int add(char[] paramArrayOfChar, int paramInt) {
    if (this._size == this._offset.length)
      resize(); 
    int i = this._arrayIndex;
    int j = i + paramInt;
    this._cachedIndex = this._size;
    this._offset[this._size] = i;
    this._length[this._size++] = paramInt;
    if (j >= this._array.length)
      resizeArray(j); 
    System.arraycopy(paramArrayOfChar, 0, this._array, i, paramInt);
    this._arrayIndex = j;
    return i;
  }
  
  protected final void resize() {
    if (this._size == this._maximumCapacity)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity")); 
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity)
      i = this._maximumCapacity; 
    int[] arrayOfInt1 = new int[i];
    System.arraycopy(this._offset, 0, arrayOfInt1, 0, this._size);
    this._offset = arrayOfInt1;
    int[] arrayOfInt2 = new int[i];
    System.arraycopy(this._length, 0, arrayOfInt2, 0, this._size);
    this._length = arrayOfInt2;
  }
  
  protected final void resizeArray(int paramInt) {
    if (this._arrayIndex == this._maximumCharacterSize)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.maxNumberOfCharacters")); 
    int i = paramInt * 3 / 2 + 1;
    if (i > this._maximumCharacterSize)
      i = this._maximumCharacterSize; 
    char[] arrayOfChar = new char[i];
    System.arraycopy(this._array, 0, arrayOfChar, 0, this._arrayIndex);
    this._array = arrayOfChar;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\ContiguousCharArrayArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */