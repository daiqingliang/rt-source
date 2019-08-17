package com.sun.org.apache.xalan.internal.xsltc.util;

import java.io.PrintStream;

public final class IntegerArray {
  private static final int InitialSize = 32;
  
  private int[] _array;
  
  private int _size;
  
  private int _free = 0;
  
  public IntegerArray() { this(32); }
  
  public IntegerArray(int paramInt) { this._array = new int[this._size = paramInt]; }
  
  public IntegerArray(int[] paramArrayOfInt) {
    this(paramArrayOfInt.length);
    System.arraycopy(paramArrayOfInt, 0, this._array, 0, this._free = this._size);
  }
  
  public void clear() { this._free = 0; }
  
  public Object clone() {
    IntegerArray integerArray = new IntegerArray((this._free > 0) ? this._free : 1);
    System.arraycopy(this._array, 0, integerArray._array, 0, this._free);
    integerArray._free = this._free;
    return integerArray;
  }
  
  public int[] toIntArray() {
    int[] arrayOfInt = new int[cardinality()];
    System.arraycopy(this._array, 0, arrayOfInt, 0, cardinality());
    return arrayOfInt;
  }
  
  public final int at(int paramInt) { return this._array[paramInt]; }
  
  public final void set(int paramInt1, int paramInt2) { this._array[paramInt1] = paramInt2; }
  
  public int indexOf(int paramInt) {
    for (byte b = 0; b < this._free; b++) {
      if (paramInt == this._array[b])
        return b; 
    } 
    return -1;
  }
  
  public final void add(int paramInt) {
    if (this._free == this._size)
      growArray(this._size * 2); 
    this._array[this._free++] = paramInt;
  }
  
  public void addNew(int paramInt) {
    for (byte b = 0; b < this._free; b++) {
      if (this._array[b] == paramInt)
        return; 
    } 
    add(paramInt);
  }
  
  public void reverse() {
    byte b = 0;
    int i = this._free - 1;
    while (b < i) {
      int j = this._array[b];
      this._array[b++] = this._array[i];
      this._array[i--] = j;
    } 
  }
  
  public void merge(IntegerArray paramIntegerArray) {
    int i = this._free + paramIntegerArray._free;
    int[] arrayOfInt = new int[i];
    byte b1 = 0;
    byte b2 = 0;
    byte b3;
    for (b3 = 0; b1 < this._free && b2 < paramIntegerArray._free; b3++) {
      int j = this._array[b1];
      int k = paramIntegerArray._array[b2];
      if (j < k) {
        arrayOfInt[b3] = j;
        b1++;
      } else if (j > k) {
        arrayOfInt[b3] = k;
        b2++;
      } else {
        arrayOfInt[b3] = j;
        b1++;
        b2++;
      } 
    } 
    if (b1 >= this._free) {
      while (b2 < paramIntegerArray._free)
        arrayOfInt[b3++] = paramIntegerArray._array[b2++]; 
    } else {
      while (b1 < this._free)
        arrayOfInt[b3++] = this._array[b1++]; 
    } 
    this._array = arrayOfInt;
    this._free = this._size = i;
  }
  
  public void sort() { quicksort(this._array, 0, this._free - 1); }
  
  private static void quicksort(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      int i = partition(paramArrayOfInt, paramInt1, paramInt2);
      quicksort(paramArrayOfInt, paramInt1, i);
      quicksort(paramArrayOfInt, i + 1, paramInt2);
    } 
  }
  
  private static int partition(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramArrayOfInt[paramInt1 + paramInt2 >>> 1];
    int j = paramInt1 - 1;
    int k = paramInt2 + 1;
    while (true) {
      if (i < paramArrayOfInt[--k])
        continue; 
      while (i > paramArrayOfInt[++j]);
      if (j < k) {
        int m = paramArrayOfInt[j];
        paramArrayOfInt[j] = paramArrayOfInt[k];
        paramArrayOfInt[k] = m;
        continue;
      } 
      break;
    } 
    return k;
  }
  
  private void growArray(int paramInt) {
    int[] arrayOfInt = new int[this._size = paramInt];
    System.arraycopy(this._array, 0, arrayOfInt, 0, this._free);
    this._array = arrayOfInt;
  }
  
  public int popLast() { return this._array[--this._free]; }
  
  public int last() { return this._array[this._free - 1]; }
  
  public void setLast(int paramInt) { this._array[this._free - 1] = paramInt; }
  
  public void pop() { this._free--; }
  
  public void pop(int paramInt) { this._free -= paramInt; }
  
  public final int cardinality() { return this._free; }
  
  public void print(PrintStream paramPrintStream) {
    if (this._free > 0) {
      for (byte b = 0; b < this._free - 1; b++) {
        paramPrintStream.print(this._array[b]);
        paramPrintStream.print(' ');
      } 
      paramPrintStream.println(this._array[this._free - 1]);
    } else {
      paramPrintStream.println("IntegerArray: empty");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xslt\\util\IntegerArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */