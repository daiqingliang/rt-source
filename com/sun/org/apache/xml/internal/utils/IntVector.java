package com.sun.org.apache.xml.internal.utils;

public class IntVector implements Cloneable {
  protected int m_blocksize;
  
  protected int[] m_map;
  
  protected int m_firstFree = 0;
  
  protected int m_mapSize;
  
  public IntVector() {
    this.m_blocksize = 32;
    this.m_mapSize = this.m_blocksize;
    this.m_map = new int[this.m_blocksize];
  }
  
  public IntVector(int paramInt) {
    this.m_blocksize = paramInt;
    this.m_mapSize = paramInt;
    this.m_map = new int[paramInt];
  }
  
  public IntVector(int paramInt1, int paramInt2) {
    this.m_blocksize = paramInt2;
    this.m_mapSize = paramInt1;
    this.m_map = new int[paramInt1];
  }
  
  public IntVector(IntVector paramIntVector) {
    this.m_map = new int[paramIntVector.m_mapSize];
    this.m_mapSize = paramIntVector.m_mapSize;
    this.m_firstFree = paramIntVector.m_firstFree;
    this.m_blocksize = paramIntVector.m_blocksize;
    System.arraycopy(paramIntVector.m_map, 0, this.m_map, 0, this.m_firstFree);
  }
  
  public final int size() { return this.m_firstFree; }
  
  public final void setSize(int paramInt) { this.m_firstFree = paramInt; }
  
  public final void addElement(int paramInt) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    this.m_map[this.m_firstFree] = paramInt;
    this.m_firstFree++;
  }
  
  public final void addElements(int paramInt1, int paramInt2) {
    if (this.m_firstFree + paramInt2 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize + paramInt2;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    for (byte b = 0; b < paramInt2; b++) {
      this.m_map[this.m_firstFree] = paramInt1;
      this.m_firstFree++;
    } 
  }
  
  public final void addElements(int paramInt) {
    if (this.m_firstFree + paramInt >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize + paramInt;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    this.m_firstFree += paramInt;
  }
  
  public final void insertElementAt(int paramInt1, int paramInt2) {
    if (this.m_firstFree + 1 >= this.m_mapSize) {
      this.m_mapSize += this.m_blocksize;
      int[] arrayOfInt = new int[this.m_mapSize];
      System.arraycopy(this.m_map, 0, arrayOfInt, 0, this.m_firstFree + 1);
      this.m_map = arrayOfInt;
    } 
    if (paramInt2 <= this.m_firstFree - 1)
      System.arraycopy(this.m_map, paramInt2, this.m_map, paramInt2 + 1, this.m_firstFree - paramInt2); 
    this.m_map[paramInt2] = paramInt1;
    this.m_firstFree++;
  }
  
  public final void removeAllElements() {
    for (byte b = 0; b < this.m_firstFree; b++)
      this.m_map[b] = Integer.MIN_VALUE; 
    this.m_firstFree = 0;
  }
  
  public final boolean removeElement(int paramInt) {
    for (int i = 0; i < this.m_firstFree; i++) {
      if (this.m_map[i] == paramInt) {
        if (i + true < this.m_firstFree) {
          System.arraycopy(this.m_map, i + true, this.m_map, i - true, this.m_firstFree - i);
        } else {
          this.m_map[i] = Integer.MIN_VALUE;
        } 
        this.m_firstFree--;
        return true;
      } 
    } 
    return false;
  }
  
  public final void removeElementAt(int paramInt) {
    if (paramInt > this.m_firstFree) {
      System.arraycopy(this.m_map, paramInt + 1, this.m_map, paramInt, this.m_firstFree);
    } else {
      this.m_map[paramInt] = Integer.MIN_VALUE;
    } 
    this.m_firstFree--;
  }
  
  public final void setElementAt(int paramInt1, int paramInt2) { this.m_map[paramInt2] = paramInt1; }
  
  public final int elementAt(int paramInt) { return this.m_map[paramInt]; }
  
  public final boolean contains(int paramInt) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  public final int indexOf(int paramInt1, int paramInt2) {
    for (int i = paramInt2; i < this.m_firstFree; i++) {
      if (this.m_map[i] == paramInt1)
        return i; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public final int indexOf(int paramInt) {
    for (byte b = 0; b < this.m_firstFree; b++) {
      if (this.m_map[b] == paramInt)
        return b; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public final int lastIndexOf(int paramInt) {
    for (int i = this.m_firstFree - 1; i >= 0; i--) {
      if (this.m_map[i] == paramInt)
        return i; 
    } 
    return Integer.MIN_VALUE;
  }
  
  public Object clone() throws CloneNotSupportedException { return new IntVector(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\IntVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */