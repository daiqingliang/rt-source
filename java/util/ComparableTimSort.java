package java.util;

class ComparableTimSort {
  private static final int MIN_MERGE = 32;
  
  private final Object[] a;
  
  private static final int MIN_GALLOP = 7;
  
  private int minGallop = 7;
  
  private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
  
  private Object[] tmp;
  
  private int tmpBase;
  
  private int tmpLen;
  
  private int stackSize = 0;
  
  private final int[] runBase;
  
  private final int[] runLen;
  
  private ComparableTimSort(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt1, int paramInt2) {
    this.a = paramArrayOfObject1;
    int i = paramArrayOfObject1.length;
    int j = (i < 512) ? (i >>> 1) : 256;
    if (paramArrayOfObject2 == null || paramInt2 < j || paramInt1 + j > paramArrayOfObject2.length) {
      this.tmp = new Object[j];
      this.tmpBase = 0;
      this.tmpLen = j;
    } else {
      this.tmp = paramArrayOfObject2;
      this.tmpBase = paramInt1;
      this.tmpLen = paramInt2;
    } 
    byte b = (i < 120) ? 5 : ((i < 1542) ? 10 : ((i < 119151) ? 24 : 49));
    this.runBase = new int[b];
    this.runLen = new int[b];
  }
  
  static void sort(Object[] paramArrayOfObject1, int paramInt1, int paramInt2, Object[] paramArrayOfObject2, int paramInt3, int paramInt4) {
    assert paramArrayOfObject1 != null && paramInt1 >= 0 && paramInt1 <= paramInt2 && paramInt2 <= paramArrayOfObject1.length;
    int i = paramInt2 - paramInt1;
    if (i < 2)
      return; 
    if (i < 32) {
      int k = countRunAndMakeAscending(paramArrayOfObject1, paramInt1, paramInt2);
      binarySort(paramArrayOfObject1, paramInt1, paramInt2, paramInt1 + k);
      return;
    } 
    ComparableTimSort comparableTimSort;
    int j = (comparableTimSort = new ComparableTimSort(paramArrayOfObject1, paramArrayOfObject2, paramInt3, paramInt4)).minRunLength(i);
    do {
      int k = countRunAndMakeAscending(paramArrayOfObject1, paramInt1, paramInt2);
      if (k < j) {
        int m = (i <= j) ? i : j;
        binarySort(paramArrayOfObject1, paramInt1, paramInt1 + m, paramInt1 + k);
        k = m;
      } 
      comparableTimSort.pushRun(paramInt1, k);
      comparableTimSort.mergeCollapse();
      paramInt1 += k;
      i -= k;
    } while (i != 0);
    assert paramInt1 == paramInt2;
    comparableTimSort.mergeForceCollapse();
    assert comparableTimSort.stackSize == 1;
  }
  
  private static void binarySort(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3) {
    assert paramInt1 <= paramInt3 && paramInt3 <= paramInt2;
    if (paramInt3 == paramInt1)
      paramInt3++; 
    while (paramInt3 < paramInt2) {
      Comparable comparable = (Comparable)paramArrayOfObject[paramInt3];
      int i = paramInt1;
      int j = paramInt3;
      assert i <= j;
      while (i < j) {
        int m = i + j >>> 1;
        if (comparable.compareTo(paramArrayOfObject[m]) < 0) {
          j = m;
          continue;
        } 
        i = m + 1;
      } 
      assert i == j;
      int k = paramInt3 - i;
      switch (k) {
        case 2:
          paramArrayOfObject[i + 2] = paramArrayOfObject[i + 1];
        case 1:
          paramArrayOfObject[i + 1] = paramArrayOfObject[i];
          break;
        default:
          System.arraycopy(paramArrayOfObject, i, paramArrayOfObject, i + 1, k);
          break;
      } 
      paramArrayOfObject[i] = comparable;
      paramInt3++;
    } 
  }
  
  private static int countRunAndMakeAscending(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    assert paramInt1 < paramInt2;
    int i = paramInt1 + 1;
    if (i == paramInt2)
      return 1; 
    if (((Comparable)paramArrayOfObject[i++]).compareTo(paramArrayOfObject[paramInt1]) < 0) {
      while (i < paramInt2 && ((Comparable)paramArrayOfObject[i]).compareTo(paramArrayOfObject[i - 1]) < 0)
        i++; 
      reverseRange(paramArrayOfObject, paramInt1, i);
    } else {
      while (i < paramInt2 && ((Comparable)paramArrayOfObject[i]).compareTo(paramArrayOfObject[i - 1]) >= 0)
        i++; 
    } 
    return i - paramInt1;
  }
  
  private static void reverseRange(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    while (paramInt1 < --paramInt2) {
      Object object = paramArrayOfObject[paramInt1];
      paramArrayOfObject[paramInt1++] = paramArrayOfObject[paramInt2];
      paramArrayOfObject[paramInt2--] = object;
    } 
  }
  
  private static int minRunLength(int paramInt) {
    assert paramInt >= 0;
    int i = 0;
    while (paramInt >= 32) {
      i |= paramInt & true;
      paramInt >>= 1;
    } 
    return paramInt + i;
  }
  
  private void pushRun(int paramInt1, int paramInt2) {
    this.runBase[this.stackSize] = paramInt1;
    this.runLen[this.stackSize] = paramInt2;
    this.stackSize++;
  }
  
  private void mergeCollapse() {
    while (this.stackSize > 1) {
      int i = this.stackSize - 2;
      if (i > 0 && this.runLen[i - 1] <= this.runLen[i] + this.runLen[i + 1]) {
        if (this.runLen[i - 1] < this.runLen[i + 1])
          i--; 
        mergeAt(i);
        continue;
      } 
      if (this.runLen[i] <= this.runLen[i + 1])
        mergeAt(i); 
    } 
  }
  
  private void mergeForceCollapse() {
    while (this.stackSize > 1) {
      int i = this.stackSize - 2;
      if (i > 0 && this.runLen[i - 1] < this.runLen[i + 1])
        i--; 
      mergeAt(i);
    } 
  }
  
  private void mergeAt(int paramInt) {
    assert this.stackSize >= 2;
    assert paramInt >= 0;
    assert paramInt == this.stackSize - 2 || paramInt == this.stackSize - 3;
    int i = this.runBase[paramInt];
    int j = this.runLen[paramInt];
    int k = this.runBase[paramInt + 1];
    int m = this.runLen[paramInt + 1];
    assert j > 0 && m > 0;
    assert i + j == k;
    this.runLen[paramInt] = j + m;
    if (paramInt == this.stackSize - 3) {
      this.runBase[paramInt + 1] = this.runBase[paramInt + 2];
      this.runLen[paramInt + 1] = this.runLen[paramInt + 2];
    } 
    this.stackSize--;
    int n = gallopRight((Comparable)this.a[k], this.a, i, j, 0);
    assert n >= 0;
    i += n;
    j -= n;
    if (j == 0)
      return; 
    m = gallopLeft((Comparable)this.a[i + j - 1], this.a, k, m, m - 1);
    assert m >= 0;
    if (m == 0)
      return; 
    if (j <= m) {
      mergeLo(i, j, k, m);
    } else {
      mergeHi(i, j, k, m);
    } 
  }
  
  private static int gallopLeft(Comparable<Object> paramComparable, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3) {
    assert paramInt2 > 0 && paramInt3 >= 0 && paramInt3 < paramInt2;
    int i = 0;
    int j = 1;
    if (paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3]) > 0) {
      int k = paramInt2 - paramInt3;
      while (j < k && paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3 + j]) > 0) {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0)
          j = k; 
      } 
      if (j > k)
        j = k; 
      i += paramInt3;
      j += paramInt3;
    } else {
      int k = paramInt3 + 1;
      while (j < k && paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3 - j]) <= 0) {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0)
          j = k; 
      } 
      if (j > k)
        j = k; 
      int m = i;
      i = paramInt3 - j;
      j = paramInt3 - m;
    } 
    assert -1 <= i && i < j && j <= paramInt2;
    while (++i < j) {
      int k = i + (j - i >>> 1);
      if (paramComparable.compareTo(paramArrayOfObject[paramInt1 + k]) > 0) {
        i = k + 1;
        continue;
      } 
      j = k;
    } 
    assert i == j;
    return j;
  }
  
  private static int gallopRight(Comparable<Object> paramComparable, Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3) {
    assert paramInt2 > 0 && paramInt3 >= 0 && paramInt3 < paramInt2;
    int i = 1;
    int j = 0;
    if (paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3]) < 0) {
      int k = paramInt3 + 1;
      while (i < k && paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3 - i]) < 0) {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0)
          i = k; 
      } 
      if (i > k)
        i = k; 
      int m = j;
      j = paramInt3 - i;
      i = paramInt3 - m;
    } else {
      int k = paramInt2 - paramInt3;
      while (i < k && paramComparable.compareTo(paramArrayOfObject[paramInt1 + paramInt3 + i]) >= 0) {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0)
          i = k; 
      } 
      if (i > k)
        i = k; 
      j += paramInt3;
      i += paramInt3;
    } 
    assert -1 <= j && j < i && i <= paramInt2;
    while (++j < i) {
      int k = j + (i - j >>> 1);
      if (paramComparable.compareTo(paramArrayOfObject[paramInt1 + k]) < 0) {
        i = k;
        continue;
      } 
      j = k + 1;
    } 
    assert j == i;
    return i;
  }
  
  private void mergeLo(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt2 > 0 && paramInt4 > 0 && paramInt1 + paramInt2 == paramInt3;
    Object[] arrayOfObject1 = this.a;
    Object[] arrayOfObject2 = ensureCapacity(paramInt2);
    int i = this.tmpBase;
    int j = paramInt3;
    int k = paramInt1;
    System.arraycopy(arrayOfObject1, paramInt1, arrayOfObject2, i, paramInt2);
    arrayOfObject1[k++] = arrayOfObject1[j++];
    if (--paramInt4 == 0) {
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, paramInt2);
      return;
    } 
    if (paramInt2 == 1) {
      System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, paramInt4);
      arrayOfObject1[k + paramInt4] = arrayOfObject2[i];
      return;
    } 
    int m = this.minGallop;
    while (true) {
      int n = 0;
      int i1 = 0;
      while (true) {
        assert paramInt2 > 1 && paramInt4 > 0;
        if (((Comparable)arrayOfObject1[j]).compareTo(arrayOfObject2[i]) < 0) {
          arrayOfObject1[k++] = arrayOfObject1[j++];
          i1++;
          n = 0;
          if (--paramInt4 == 0)
            break; 
        } else {
          arrayOfObject1[k++] = arrayOfObject2[i++];
          n++;
          i1 = 0;
          if (--paramInt2 == 1)
            break; 
        } 
        if ((n | i1) >= m) {
          label79: while (true) {
            assert paramInt2 > 1 && paramInt4 > 0;
            n = gallopRight((Comparable)arrayOfObject1[j], arrayOfObject2, i, paramInt2, 0);
            if (n != 0) {
              System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, n);
              k += n;
              i += n;
              paramInt2 -= n;
              if (paramInt2 <= 1)
                break; 
            } 
            arrayOfObject1[k++] = arrayOfObject1[j++];
            if (--paramInt4 == 0)
              break; 
            i1 = gallopLeft((Comparable)arrayOfObject2[i], arrayOfObject1, j, paramInt4, 0);
            if (i1 != 0) {
              System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, i1);
              k += i1;
              j += i1;
              paramInt4 -= i1;
              if (paramInt4 == 0)
                break; 
            } 
            arrayOfObject1[k++] = arrayOfObject2[i++];
            if (--paramInt2 == 1)
              break; 
            m--;
            if (!(((n >= 7) ? 1 : 0) | ((i1 >= 7) ? 1 : 0))) {
              if (m < 0) {
                m = 0;
                m += 2;
                continue;
              } 
              break label79;
            } 
          } 
          break;
        } 
      } 
      break;
    } 
    this.minGallop = (m < 1) ? 1 : m;
    if (paramInt2 == 1) {
      assert paramInt4 > 0;
      System.arraycopy(arrayOfObject1, j, arrayOfObject1, k, paramInt4);
      arrayOfObject1[k + paramInt4] = arrayOfObject2[i];
    } else {
      if (paramInt2 == 0)
        throw new IllegalArgumentException("Comparison method violates its general contract!"); 
      assert paramInt4 == 0;
      assert paramInt2 > 1;
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, k, paramInt2);
    } 
  }
  
  private void mergeHi(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt2 > 0 && paramInt4 > 0 && paramInt1 + paramInt2 == paramInt3;
    Object[] arrayOfObject1 = this.a;
    Object[] arrayOfObject2 = ensureCapacity(paramInt4);
    int i = this.tmpBase;
    System.arraycopy(arrayOfObject1, paramInt3, arrayOfObject2, i, paramInt4);
    int j = paramInt1 + paramInt2 - 1;
    int k = i + paramInt4 - 1;
    int m = paramInt3 + paramInt4 - 1;
    arrayOfObject1[m--] = arrayOfObject1[j--];
    if (--paramInt2 == 0) {
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, m - paramInt4 - 1, paramInt4);
      return;
    } 
    if (paramInt4 == 1) {
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, paramInt2);
      arrayOfObject1[m] = arrayOfObject2[k];
      return;
    } 
    int n = this.minGallop;
    while (true) {
      int i1 = 0;
      int i2 = 0;
      while (true) {
        assert paramInt2 > 0 && paramInt4 > 1;
        if (((Comparable)arrayOfObject2[k]).compareTo(arrayOfObject1[j]) < 0) {
          arrayOfObject1[m--] = arrayOfObject1[j--];
          i1++;
          i2 = 0;
          if (--paramInt2 == 0)
            break; 
        } else {
          arrayOfObject1[m--] = arrayOfObject2[k--];
          i2++;
          i1 = 0;
          if (--paramInt4 == 1)
            break; 
        } 
        if ((i1 | i2) >= n) {
          label79: while (true) {
            assert paramInt2 > 0 && paramInt4 > 1;
            i1 = paramInt2 - gallopRight((Comparable)arrayOfObject2[k], arrayOfObject1, paramInt1, paramInt2, paramInt2 - 1);
            if (i1 != 0) {
              m -= i1;
              j -= i1;
              paramInt2 -= i1;
              System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, i1);
              if (paramInt2 == 0)
                break; 
            } 
            arrayOfObject1[m--] = arrayOfObject2[k--];
            if (--paramInt4 == 1)
              break; 
            i2 = paramInt4 - gallopLeft((Comparable)arrayOfObject1[j], arrayOfObject2, i, paramInt4, paramInt4 - 1);
            if (i2 != 0) {
              m -= i2;
              k -= i2;
              paramInt4 -= i2;
              System.arraycopy(arrayOfObject2, k + 1, arrayOfObject1, m + 1, i2);
              if (paramInt4 <= 1)
                break; 
            } 
            arrayOfObject1[m--] = arrayOfObject1[j--];
            if (--paramInt2 == 0)
              break; 
            n--;
            if (!(((i1 >= 7) ? 1 : 0) | ((i2 >= 7) ? 1 : 0))) {
              if (n < 0) {
                n = 0;
                n += 2;
                continue;
              } 
              break label79;
            } 
          } 
          break;
        } 
      } 
      break;
    } 
    this.minGallop = (n < 1) ? 1 : n;
    if (paramInt4 == 1) {
      assert paramInt2 > 0;
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfObject1, j + 1, arrayOfObject1, m + 1, paramInt2);
      arrayOfObject1[m] = arrayOfObject2[k];
    } else {
      if (paramInt4 == 0)
        throw new IllegalArgumentException("Comparison method violates its general contract!"); 
      assert paramInt2 == 0;
      assert paramInt4 > 0;
      System.arraycopy(arrayOfObject2, i, arrayOfObject1, m - paramInt4 - 1, paramInt4);
    } 
  }
  
  private Object[] ensureCapacity(int paramInt) {
    if (this.tmpLen < paramInt) {
      int i = paramInt;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      if (++i < 0) {
        i = paramInt;
      } else {
        i = Math.min(i, this.a.length >>> 1);
      } 
      Object[] arrayOfObject = new Object[i];
      this.tmp = arrayOfObject;
      this.tmpLen = i;
      this.tmpBase = 0;
    } 
    return this.tmp;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ComparableTimSort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */