package com.sun.org.apache.xml.internal.utils;

public class SuballocatedIntVector {
  protected int m_blocksize;
  
  protected int m_SHIFT = 0;
  
  protected int m_MASK;
  
  protected static final int NUMBLOCKS_DEFAULT = 32;
  
  protected int m_numblocks = 32;
  
  protected int[][] m_map;
  
  protected int m_firstFree = 0;
  
  protected int[] m_map0;
  
  protected int[] m_buildCache;
  
  protected int m_buildCacheStartIndex;
  
  public SuballocatedIntVector() { this(2048); }
  
  public SuballocatedIntVector(int paramInt1, int paramInt2) {
    while (0 != paramInt1 >>>= 1)
      this.m_SHIFT++; 
    this.m_blocksize = 1 << this.m_SHIFT;
    this.m_MASK = this.m_blocksize - 1;
    this.m_numblocks = paramInt2;
    this.m_map0 = new int[this.m_blocksize];
    this.m_map = new int[paramInt2][];
    this.m_map[0] = this.m_map0;
    this.m_buildCache = this.m_map0;
    this.m_buildCacheStartIndex = 0;
  }
  
  public SuballocatedIntVector(int paramInt) { this(paramInt, 32); }
  
  public int size() { return this.m_firstFree; }
  
  public void setSize(int paramInt) {
    if (this.m_firstFree > paramInt)
      this.m_firstFree = paramInt; 
  }
  
  public void addElement(int paramInt) {
    int i = this.m_firstFree - this.m_buildCacheStartIndex;
    if (i >= 0 && i < this.m_blocksize) {
      this.m_buildCache[i] = paramInt;
      this.m_firstFree++;
    } else {
      int j = this.m_firstFree >>> this.m_SHIFT;
      int k = this.m_firstFree & this.m_MASK;
      if (j >= this.m_map.length) {
        int m = j + this.m_numblocks;
        int[][] arrayOfInt1 = new int[m][];
        System.arraycopy(this.m_map, 0, arrayOfInt1, 0, this.m_map.length);
        this.m_map = arrayOfInt1;
      } 
      int[] arrayOfInt = this.m_map[j];
      if (null == arrayOfInt)
        arrayOfInt = this.m_map[j] = new int[this.m_blocksize]; 
      arrayOfInt[k] = paramInt;
      this.m_buildCache = arrayOfInt;
      this.m_buildCacheStartIndex = this.m_firstFree - k;
      this.m_firstFree++;
    } 
  }
  
  private void addElements(int paramInt1, int paramInt2) {
    if (this.m_firstFree + paramInt2 < this.m_blocksize) {
      for (byte b = 0; b < paramInt2; b++)
        this.m_map0[this.m_firstFree++] = paramInt1; 
    } else {
      int i = this.m_firstFree >>> this.m_SHIFT;
      int j = this.m_firstFree & this.m_MASK;
      this.m_firstFree += paramInt2;
      while (paramInt2 > 0) {
        if (i >= this.m_map.length) {
          int m = i + this.m_numblocks;
          int[][] arrayOfInt1 = new int[m][];
          System.arraycopy(this.m_map, 0, arrayOfInt1, 0, this.m_map.length);
          this.m_map = arrayOfInt1;
        } 
        int[] arrayOfInt = this.m_map[i];
        if (null == arrayOfInt)
          arrayOfInt = this.m_map[i] = new int[this.m_blocksize]; 
        int k = (this.m_blocksize - j < paramInt2) ? (this.m_blocksize - j) : paramInt2;
        paramInt2 -= k;
        while (k-- > 0)
          arrayOfInt[j++] = paramInt1; 
        i++;
        j = 0;
      } 
    } 
  }
  
  private void addElements(int paramInt) {
    int i = this.m_firstFree + paramInt;
    if (i > this.m_blocksize) {
      int j = this.m_firstFree >>> this.m_SHIFT;
      int k = this.m_firstFree + paramInt >>> this.m_SHIFT;
      for (int m = j + 1; m <= k; m++)
        this.m_map[m] = new int[this.m_blocksize]; 
    } 
    this.m_firstFree = i;
  }
  
  private void insertElementAt(int paramInt1, int paramInt2) {
    if (paramInt2 == this.m_firstFree) {
      addElement(paramInt1);
    } else if (paramInt2 > this.m_firstFree) {
      int i = paramInt2 >>> this.m_SHIFT;
      if (i >= this.m_map.length) {
        int k = i + this.m_numblocks;
        int[][] arrayOfInt1 = new int[k][];
        System.arraycopy(this.m_map, 0, arrayOfInt1, 0, this.m_map.length);
        this.m_map = arrayOfInt1;
      } 
      int[] arrayOfInt = this.m_map[i];
      if (null == arrayOfInt)
        arrayOfInt = this.m_map[i] = new int[this.m_blocksize]; 
      int j = paramInt2 & this.m_MASK;
      arrayOfInt[j] = paramInt1;
      this.m_firstFree = j + 1;
    } else {
      int i = paramInt2 >>> this.m_SHIFT;
      int j = this.m_firstFree >>> this.m_SHIFT;
      this.m_firstFree++;
      int k = paramInt2 & this.m_MASK;
      while (i <= j) {
        int m;
        int n = this.m_blocksize - k - 1;
        int[] arrayOfInt = this.m_map[i];
        if (null == arrayOfInt) {
          m = 0;
          arrayOfInt = this.m_map[i] = new int[this.m_blocksize];
        } else {
          m = arrayOfInt[this.m_blocksize - 1];
          System.arraycopy(arrayOfInt, k, arrayOfInt, k + 1, n);
        } 
        arrayOfInt[k] = paramInt1;
        paramInt1 = m;
        k = 0;
        i++;
      } 
    } 
  }
  
  public void removeAllElements() {
    this.m_firstFree = 0;
    this.m_buildCache = this.m_map0;
    this.m_buildCacheStartIndex = 0;
  }
  
  private boolean removeElement(int paramInt) {
    int i = indexOf(paramInt, 0);
    if (i < 0)
      return false; 
    removeElementAt(i);
    return true;
  }
  
  private void removeElementAt(int paramInt) {
    if (paramInt < this.m_firstFree) {
      int i = paramInt >>> this.m_SHIFT;
      int j = this.m_firstFree >>> this.m_SHIFT;
      int k = paramInt & this.m_MASK;
      while (i <= j) {
        int m = this.m_blocksize - k - 1;
        int[] arrayOfInt = this.m_map[i];
        if (null == arrayOfInt) {
          arrayOfInt = this.m_map[i] = new int[this.m_blocksize];
        } else {
          System.arraycopy(arrayOfInt, k + 1, arrayOfInt, k, m);
        } 
        if (i < j) {
          int[] arrayOfInt1 = this.m_map[i + 1];
          if (arrayOfInt1 != null)
            arrayOfInt[this.m_blocksize - 1] = (arrayOfInt1 != null) ? arrayOfInt1[0] : 0; 
        } else {
          arrayOfInt[this.m_blocksize - 1] = 0;
        } 
        k = 0;
        i++;
      } 
    } 
    this.m_firstFree--;
  }
  
  public void setElementAt(int paramInt1, int paramInt2) {
    if (paramInt2 < this.m_blocksize) {
      this.m_map0[paramInt2] = paramInt1;
    } else {
      int i = paramInt2 >>> this.m_SHIFT;
      int j = paramInt2 & this.m_MASK;
      if (i >= this.m_map.length) {
        int k = i + this.m_numblocks;
        int[][] arrayOfInt1 = new int[k][];
        System.arraycopy(this.m_map, 0, arrayOfInt1, 0, this.m_map.length);
        this.m_map = arrayOfInt1;
      } 
      int[] arrayOfInt = this.m_map[i];
      if (null == arrayOfInt)
        arrayOfInt = this.m_map[i] = new int[this.m_blocksize]; 
      arrayOfInt[j] = paramInt1;
    } 
    if (paramInt2 >= this.m_firstFree)
      this.m_firstFree = paramInt2 + 1; 
  }
  
  public int elementAt(int paramInt) { return (paramInt < this.m_blocksize) ? this.m_map0[paramInt] : this.m_map[paramInt >>> this.m_SHIFT][paramInt & this.m_MASK]; }
  
  private boolean contains(int paramInt) { return (indexOf(paramInt, 0) >= 0); }
  
  public int indexOf(int paramInt1, int paramInt2) {
    if (paramInt2 >= this.m_firstFree)
      return -1; 
    int i = paramInt2 >>> this.m_SHIFT;
    int j = paramInt2 & this.m_MASK;
    int k = this.m_firstFree >>> this.m_SHIFT;
    while (i < k) {
      int[] arrayOfInt1 = this.m_map[i];
      if (arrayOfInt1 != null)
        for (int i1 = j; i1 < this.m_blocksize; i1++) {
          if (arrayOfInt1[i1] == paramInt1)
            return i1 + i * this.m_blocksize; 
        }  
      j = 0;
      i++;
    } 
    int m = this.m_firstFree & this.m_MASK;
    int[] arrayOfInt = this.m_map[k];
    for (int n = j; n < m; n++) {
      if (arrayOfInt[n] == paramInt1)
        return n + k * this.m_blocksize; 
    } 
    return -1;
  }
  
  public int indexOf(int paramInt) { return indexOf(paramInt, 0); }
  
  private int lastIndexOf(int paramInt) {
    int i = this.m_firstFree & this.m_MASK;
    for (int j = this.m_firstFree >>> this.m_SHIFT; j >= 0; j--) {
      int[] arrayOfInt = this.m_map[j];
      if (arrayOfInt != null)
        for (int k = i; k >= 0; k--) {
          if (arrayOfInt[k] == paramInt)
            return k + j * this.m_blocksize; 
        }  
      i = 0;
    } 
    return -1;
  }
  
  public final int[] getMap0() { return this.m_map0; }
  
  public final int[][] getMap() { return this.m_map; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\SuballocatedIntVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */