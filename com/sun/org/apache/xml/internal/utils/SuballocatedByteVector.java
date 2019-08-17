package com.sun.org.apache.xml.internal.utils;

public class SuballocatedByteVector {
  protected int m_blocksize;
  
  protected int m_numblocks = 32;
  
  protected byte[][] m_map;
  
  protected int m_firstFree = 0;
  
  protected byte[] m_map0;
  
  public SuballocatedByteVector() { this(2048); }
  
  public SuballocatedByteVector(int paramInt) {
    this.m_blocksize = paramInt;
    this.m_map0 = new byte[paramInt];
    this.m_map = new byte[this.m_numblocks][];
    this.m_map[0] = this.m_map0;
  }
  
  public SuballocatedByteVector(int paramInt1, int paramInt2) { this(paramInt1); }
  
  public int size() { return this.m_firstFree; }
  
  private void setSize(int paramInt) {
    if (this.m_firstFree < paramInt)
      this.m_firstFree = paramInt; 
  }
  
  public void addElement(byte paramByte) {
    if (this.m_firstFree < this.m_blocksize) {
      this.m_map0[this.m_firstFree++] = paramByte;
    } else {
      int i = this.m_firstFree / this.m_blocksize;
      int j = this.m_firstFree % this.m_blocksize;
      this.m_firstFree++;
      if (i >= this.m_map.length) {
        int k = i + this.m_numblocks;
        byte[][] arrayOfByte1 = new byte[k][];
        System.arraycopy(this.m_map, 0, arrayOfByte1, 0, this.m_map.length);
        this.m_map = arrayOfByte1;
      } 
      byte[] arrayOfByte = this.m_map[i];
      if (null == arrayOfByte)
        arrayOfByte = this.m_map[i] = new byte[this.m_blocksize]; 
      arrayOfByte[j] = paramByte;
    } 
  }
  
  private void addElements(byte paramByte, int paramInt) {
    if (this.m_firstFree + paramInt < this.m_blocksize) {
      for (byte b = 0; b < paramInt; b++)
        this.m_map0[this.m_firstFree++] = paramByte; 
    } else {
      int i = this.m_firstFree / this.m_blocksize;
      int j = this.m_firstFree % this.m_blocksize;
      this.m_firstFree += paramInt;
      while (paramInt > 0) {
        if (i >= this.m_map.length) {
          int m = i + this.m_numblocks;
          byte[][] arrayOfByte1 = new byte[m][];
          System.arraycopy(this.m_map, 0, arrayOfByte1, 0, this.m_map.length);
          this.m_map = arrayOfByte1;
        } 
        byte[] arrayOfByte = this.m_map[i];
        if (null == arrayOfByte)
          arrayOfByte = this.m_map[i] = new byte[this.m_blocksize]; 
        int k = (this.m_blocksize - j < paramInt) ? (this.m_blocksize - j) : paramInt;
        paramInt -= k;
        while (k-- > 0)
          arrayOfByte[j++] = paramByte; 
        i++;
        j = 0;
      } 
    } 
  }
  
  private void addElements(int paramInt) {
    int i = this.m_firstFree + paramInt;
    if (i > this.m_blocksize) {
      int j = this.m_firstFree % this.m_blocksize;
      int k = (this.m_firstFree + paramInt) % this.m_blocksize;
      for (int m = j + 1; m <= k; m++)
        this.m_map[m] = new byte[this.m_blocksize]; 
    } 
    this.m_firstFree = i;
  }
  
  private void insertElementAt(byte paramByte, int paramInt) {
    if (paramInt == this.m_firstFree) {
      addElement(paramByte);
    } else if (paramInt > this.m_firstFree) {
      int i = paramInt / this.m_blocksize;
      if (i >= this.m_map.length) {
        int k = i + this.m_numblocks;
        byte[][] arrayOfByte1 = new byte[k][];
        System.arraycopy(this.m_map, 0, arrayOfByte1, 0, this.m_map.length);
        this.m_map = arrayOfByte1;
      } 
      byte[] arrayOfByte = this.m_map[i];
      if (null == arrayOfByte)
        arrayOfByte = this.m_map[i] = new byte[this.m_blocksize]; 
      int j = paramInt % this.m_blocksize;
      arrayOfByte[j] = paramByte;
      this.m_firstFree = j + 1;
    } else {
      int i = paramInt / this.m_blocksize;
      int j = this.m_firstFree + 1 / this.m_blocksize;
      this.m_firstFree++;
      int k = paramInt % this.m_blocksize;
      while (i <= j) {
        byte b;
        int m = this.m_blocksize - k - 1;
        byte[] arrayOfByte = this.m_map[i];
        if (null == arrayOfByte) {
          b = 0;
          arrayOfByte = this.m_map[i] = new byte[this.m_blocksize];
        } else {
          b = arrayOfByte[this.m_blocksize - 1];
          System.arraycopy(arrayOfByte, k, arrayOfByte, k + 1, m);
        } 
        arrayOfByte[k] = paramByte;
        paramByte = b;
        k = 0;
        i++;
      } 
    } 
  }
  
  public void removeAllElements() { this.m_firstFree = 0; }
  
  private boolean removeElement(byte paramByte) {
    int i = indexOf(paramByte, 0);
    if (i < 0)
      return false; 
    removeElementAt(i);
    return true;
  }
  
  private void removeElementAt(int paramInt) {
    if (paramInt < this.m_firstFree) {
      int i = paramInt / this.m_blocksize;
      int j = this.m_firstFree / this.m_blocksize;
      int k = paramInt % this.m_blocksize;
      while (i <= j) {
        int m = this.m_blocksize - k - 1;
        byte[] arrayOfByte = this.m_map[i];
        if (null == arrayOfByte) {
          arrayOfByte = this.m_map[i] = new byte[this.m_blocksize];
        } else {
          System.arraycopy(arrayOfByte, k + 1, arrayOfByte, k, m);
        } 
        if (i < j) {
          byte[] arrayOfByte1 = this.m_map[i + 1];
          if (arrayOfByte1 != null)
            arrayOfByte[this.m_blocksize - 1] = (arrayOfByte1 != null) ? arrayOfByte1[0] : 0; 
        } else {
          arrayOfByte[this.m_blocksize - 1] = 0;
        } 
        k = 0;
        i++;
      } 
    } 
    this.m_firstFree--;
  }
  
  public void setElementAt(byte paramByte, int paramInt) {
    if (paramInt < this.m_blocksize) {
      this.m_map0[paramInt] = paramByte;
      return;
    } 
    int i = paramInt / this.m_blocksize;
    int j = paramInt % this.m_blocksize;
    if (i >= this.m_map.length) {
      int k = i + this.m_numblocks;
      byte[][] arrayOfByte1 = new byte[k][];
      System.arraycopy(this.m_map, 0, arrayOfByte1, 0, this.m_map.length);
      this.m_map = arrayOfByte1;
    } 
    byte[] arrayOfByte = this.m_map[i];
    if (null == arrayOfByte)
      arrayOfByte = this.m_map[i] = new byte[this.m_blocksize]; 
    arrayOfByte[j] = paramByte;
    if (paramInt >= this.m_firstFree)
      this.m_firstFree = paramInt + 1; 
  }
  
  public byte elementAt(int paramInt) { return (paramInt < this.m_blocksize) ? this.m_map0[paramInt] : this.m_map[paramInt / this.m_blocksize][paramInt % this.m_blocksize]; }
  
  private boolean contains(byte paramByte) { return (indexOf(paramByte, 0) >= 0); }
  
  public int indexOf(byte paramByte, int paramInt) {
    if (paramInt >= this.m_firstFree)
      return -1; 
    int i = paramInt / this.m_blocksize;
    int j = paramInt % this.m_blocksize;
    int k = this.m_firstFree / this.m_blocksize;
    while (i < k) {
      byte[] arrayOfByte1 = this.m_map[i];
      if (arrayOfByte1 != null)
        for (int i1 = j; i1 < this.m_blocksize; i1++) {
          if (arrayOfByte1[i1] == paramByte)
            return i1 + i * this.m_blocksize; 
        }  
      j = 0;
      i++;
    } 
    int m = this.m_firstFree % this.m_blocksize;
    byte[] arrayOfByte = this.m_map[k];
    for (int n = j; n < m; n++) {
      if (arrayOfByte[n] == paramByte)
        return n + k * this.m_blocksize; 
    } 
    return -1;
  }
  
  public int indexOf(byte paramByte) { return indexOf(paramByte, 0); }
  
  private int lastIndexOf(byte paramByte) {
    int i = this.m_firstFree % this.m_blocksize;
    for (int j = this.m_firstFree / this.m_blocksize; j >= 0; j--) {
      byte[] arrayOfByte = this.m_map[j];
      if (arrayOfByte != null)
        for (int k = i; k >= 0; k--) {
          if (arrayOfByte[k] == paramByte)
            return k + j * this.m_blocksize; 
        }  
      i = 0;
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\SuballocatedByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */