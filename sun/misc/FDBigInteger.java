package sun.misc;

import java.math.BigInteger;
import java.util.Arrays;

public class FDBigInteger {
  static final int[] SMALL_5_POW = { 
      1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 
      9765625, 48828125, 244140625, 1220703125 };
  
  static final long[] LONG_5_POW = { 
      1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 
      9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 
      95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L };
  
  private static final int MAX_FIVE_POW = 340;
  
  private static final FDBigInteger[] POW_5_CACHE = new FDBigInteger[340];
  
  public static final FDBigInteger ZERO;
  
  private static final long LONG_MASK = 4294967295L;
  
  private int[] data;
  
  private int offset;
  
  private int nWords;
  
  private boolean isImmutable = false;
  
  private FDBigInteger(int[] paramArrayOfInt, int paramInt) {
    this.data = paramArrayOfInt;
    this.offset = paramInt;
    this.nWords = paramArrayOfInt.length;
    trimLeadingZeros();
  }
  
  public FDBigInteger(long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = Math.max((paramInt2 + 8) / 9, 2);
    this.data = new int[i];
    this.data[0] = (int)paramLong;
    this.data[1] = (int)(paramLong >>> 32);
    this.offset = 0;
    this.nWords = 2;
    int j = paramInt1;
    int k = paramInt2 - 5;
    while (j < k) {
      int m = j + 5;
      char c1;
      for (c1 = paramArrayOfChar[j++] - '0'; j < m; c1 = '\n' * c1 + paramArrayOfChar[j++] - '0');
      multAddMe(100000, c1);
    } 
    byte b = 1;
    char c = Character.MIN_VALUE;
    while (j < paramInt2) {
      c = 10 * c + paramArrayOfChar[j++] - '0';
      b *= 10;
    } 
    if (b != 1)
      multAddMe(b, c); 
    trimLeadingZeros();
  }
  
  public static FDBigInteger valueOfPow52(int paramInt1, int paramInt2) {
    if (paramInt1 != 0) {
      if (paramInt2 == 0)
        return big5pow(paramInt1); 
      if (paramInt1 < SMALL_5_POW.length) {
        int i = SMALL_5_POW[paramInt1];
        int j = paramInt2 >> 5;
        int k = paramInt2 & 0x1F;
        return (k == 0) ? new FDBigInteger(new int[] { i }, j) : new FDBigInteger(new int[] { i << k, i >>> 32 - k }, j);
      } 
      return big5pow(paramInt1).leftShift(paramInt2);
    } 
    return valueOfPow2(paramInt2);
  }
  
  public static FDBigInteger valueOfMulPow52(long paramLong, int paramInt1, int paramInt2) {
    assert paramInt1 >= 0 : paramInt1;
    assert paramInt2 >= 0 : paramInt2;
    int i = (int)paramLong;
    int j = (int)(paramLong >>> 32);
    int k = paramInt2 >> 5;
    int m = paramInt2 & 0x1F;
    if (paramInt1 != 0) {
      int[] arrayOfInt;
      if (paramInt1 < SMALL_5_POW.length) {
        long l1 = SMALL_5_POW[paramInt1] & 0xFFFFFFFFL;
        long l2 = (i & 0xFFFFFFFFL) * l1;
        i = (int)l2;
        l2 >>>= 32;
        l2 = (j & 0xFFFFFFFFL) * l1 + l2;
        j = (int)l2;
        int n = (int)(l2 >>> 32);
        return (m == 0) ? new FDBigInteger(new int[] { i, j, n }, k) : new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, n << m | j >>> 32 - m, n >>> 32 - m }, k);
      } 
      FDBigInteger fDBigInteger = big5pow(paramInt1);
      if (j == 0) {
        arrayOfInt = new int[fDBigInteger.nWords + 1 + ((paramInt2 != 0) ? 1 : 0)];
        mult(fDBigInteger.data, fDBigInteger.nWords, i, arrayOfInt);
      } else {
        arrayOfInt = new int[fDBigInteger.nWords + 2 + ((paramInt2 != 0) ? 1 : 0)];
        mult(fDBigInteger.data, fDBigInteger.nWords, i, j, arrayOfInt);
      } 
      return (new FDBigInteger(arrayOfInt, fDBigInteger.offset)).leftShift(paramInt2);
    } 
    return (paramInt2 != 0) ? ((m == 0) ? new FDBigInteger(new int[] { i, j }, k) : new FDBigInteger(new int[] { i << m, j << m | i >>> 32 - m, j >>> 32 - m }, k)) : new FDBigInteger(new int[] { i, j }, 0);
  }
  
  private static FDBigInteger valueOfPow2(int paramInt) {
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    return new FDBigInteger(new int[] { 1 << j }, i);
  }
  
  private void trimLeadingZeros() {
    int i = this.nWords;
    if (i > 0 && this.data[--i] == 0) {
      while (i > 0 && this.data[i - 1] == 0)
        i--; 
      this.nWords = i;
      if (i == 0)
        this.offset = 0; 
    } 
  }
  
  public int getNormalizationBias() {
    if (this.nWords == 0)
      throw new IllegalArgumentException("Zero value cannot be normalized"); 
    int i = Integer.numberOfLeadingZeros(this.data[this.nWords - 1]);
    return (i < 4) ? (28 + i) : (i - 4);
  }
  
  private static void leftShift(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int paramInt3, int paramInt4) {
    while (paramInt1 > 0) {
      int j = paramInt4 << paramInt2;
      paramInt4 = paramArrayOfInt1[paramInt1 - 1];
      j |= paramInt4 >>> paramInt3;
      paramArrayOfInt2[paramInt1] = j;
      paramInt1--;
    } 
    int i = paramInt4 << paramInt2;
    paramArrayOfInt2[0] = i;
  }
  
  public FDBigInteger leftShift(int paramInt) {
    if (paramInt == 0 || this.nWords == 0)
      return this; 
    int i = paramInt >> 5;
    int j = paramInt & 0x1F;
    if (this.isImmutable) {
      int[] arrayOfInt;
      if (j == 0)
        return new FDBigInteger(Arrays.copyOf(this.data, this.nWords), this.offset + i); 
      int k = 32 - j;
      int m = this.nWords - 1;
      int n = this.data[m];
      int i1 = n >>> k;
      if (i1 != 0) {
        arrayOfInt = new int[this.nWords + 1];
        arrayOfInt[this.nWords] = i1;
      } else {
        arrayOfInt = new int[this.nWords];
      } 
      leftShift(this.data, m, arrayOfInt, j, k, n);
      return new FDBigInteger(arrayOfInt, this.offset + i);
    } 
    if (j != 0) {
      int k = 32 - j;
      if (this.data[0] << j == 0) {
        byte b = 0;
        int m = this.data[b];
        while (b < this.nWords - 1) {
          int i1 = m >>> k;
          m = this.data[b + true];
          i1 |= m << j;
          this.data[b] = i1;
          b++;
        } 
        int n = m >>> k;
        this.data[b] = n;
        if (n == 0)
          this.nWords--; 
        this.offset++;
      } else {
        int m = this.nWords - 1;
        int n = this.data[m];
        int i1 = n >>> k;
        int[] arrayOfInt1 = this.data;
        int[] arrayOfInt2 = this.data;
        if (i1 != 0) {
          if (this.nWords == this.data.length)
            this.data = arrayOfInt1 = new int[this.nWords + 1]; 
          arrayOfInt1[this.nWords++] = i1;
        } 
        leftShift(arrayOfInt2, m, arrayOfInt1, j, k, n);
      } 
    } 
    this.offset += i;
    return this;
  }
  
  private int size() { return this.nWords + this.offset; }
  
  public int quoRemIteration(FDBigInteger paramFDBigInteger) throws IllegalArgumentException {
    assert !this.isImmutable : "cannot modify immutable value";
    int i = size();
    int j = paramFDBigInteger.size();
    if (i < j) {
      int m = multAndCarryBy10(this.data, this.nWords, this.data);
      if (m != 0) {
        this.data[this.nWords++] = m;
      } else {
        trimLeadingZeros();
      } 
      return 0;
    } 
    if (i > j)
      throw new IllegalArgumentException("disparate values"); 
    long l1 = (this.data[this.nWords - 1] & 0xFFFFFFFFL) / (paramFDBigInteger.data[paramFDBigInteger.nWords - 1] & 0xFFFFFFFFL);
    long l2 = multDiffMe(l1, paramFDBigInteger);
    if (l2 != 0L) {
      long l = 0L;
      int m = paramFDBigInteger.offset - this.offset;
      int[] arrayOfInt1 = paramFDBigInteger.data;
      int[] arrayOfInt2 = this.data;
      while (l == 0L) {
        byte b = 0;
        for (int n = m; n < this.nWords; n++) {
          l += (arrayOfInt2[n] & 0xFFFFFFFFL) + (arrayOfInt1[b] & 0xFFFFFFFFL);
          arrayOfInt2[n] = (int)l;
          l >>>= 32;
          b++;
        } 
        assert l == 0L || l == 1L : l;
        l1--;
      } 
    } 
    int k = multAndCarryBy10(this.data, this.nWords, this.data);
    assert k == 0 : k;
    trimLeadingZeros();
    return (int)l1;
  }
  
  public FDBigInteger multBy10() {
    if (this.nWords == 0)
      return this; 
    if (this.isImmutable) {
      int[] arrayOfInt = new int[this.nWords + 1];
      arrayOfInt[this.nWords] = multAndCarryBy10(this.data, this.nWords, arrayOfInt);
      return new FDBigInteger(arrayOfInt, this.offset);
    } 
    int i = multAndCarryBy10(this.data, this.nWords, this.data);
    if (i != 0) {
      if (this.nWords == this.data.length)
        if (this.data[0] == 0) {
          System.arraycopy(this.data, 1, this.data, 0, --this.nWords);
          this.offset++;
        } else {
          this.data = Arrays.copyOf(this.data, this.data.length + 1);
        }  
      this.data[this.nWords++] = i;
    } else {
      trimLeadingZeros();
    } 
    return this;
  }
  
  public FDBigInteger multByPow52(int paramInt1, int paramInt2) {
    if (this.nWords == 0)
      return this; 
    FDBigInteger fDBigInteger = this;
    if (paramInt1 != 0) {
      int i = (paramInt2 != 0) ? 1 : 0;
      if (paramInt1 < SMALL_5_POW.length) {
        int[] arrayOfInt = new int[this.nWords + 1 + i];
        mult(this.data, this.nWords, SMALL_5_POW[paramInt1], arrayOfInt);
        fDBigInteger = new FDBigInteger(arrayOfInt, this.offset);
      } else {
        FDBigInteger fDBigInteger1 = big5pow(paramInt1);
        int[] arrayOfInt = new int[this.nWords + fDBigInteger1.size() + i];
        mult(this.data, this.nWords, fDBigInteger1.data, fDBigInteger1.nWords, arrayOfInt);
        fDBigInteger = new FDBigInteger(arrayOfInt, this.offset + fDBigInteger1.offset);
      } 
    } 
    return fDBigInteger.leftShift(paramInt2);
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3) {
    for (int i = 0; i < paramInt1; i++) {
      long l1 = paramArrayOfInt1[i] & 0xFFFFFFFFL;
      long l2 = 0L;
      for (byte b = 0; b < paramInt2; b++) {
        l2 += (paramArrayOfInt3[i + b] & 0xFFFFFFFFL) + l1 * (paramArrayOfInt2[b] & 0xFFFFFFFFL);
        paramArrayOfInt3[i + b] = (int)l2;
        l2 >>>= 32;
      } 
      paramArrayOfInt3[i + paramInt2] = (int)l2;
    } 
  }
  
  public FDBigInteger leftInplaceSub(FDBigInteger paramFDBigInteger) {
    FDBigInteger fDBigInteger;
    assert size() >= paramFDBigInteger.size() : "result should be positive";
    if (this.isImmutable) {
      fDBigInteger = new FDBigInteger((int[])this.data.clone(), this.offset);
    } else {
      fDBigInteger = this;
    } 
    int i = paramFDBigInteger.offset - fDBigInteger.offset;
    int[] arrayOfInt1 = paramFDBigInteger.data;
    int[] arrayOfInt2 = fDBigInteger.data;
    int j = paramFDBigInteger.nWords;
    int k = fDBigInteger.nWords;
    if (i < 0) {
      int n = k - i;
      if (n < arrayOfInt2.length) {
        System.arraycopy(arrayOfInt2, 0, arrayOfInt2, -i, k);
        Arrays.fill(arrayOfInt2, 0, -i, 0);
      } else {
        int[] arrayOfInt = new int[n];
        System.arraycopy(arrayOfInt2, 0, arrayOfInt, -i, k);
        fDBigInteger.data = arrayOfInt2 = arrayOfInt;
      } 
      fDBigInteger.offset = paramFDBigInteger.offset;
      fDBigInteger.nWords = k = n;
      i = 0;
    } 
    long l = 0L;
    int m = i;
    byte b = 0;
    while (b < j && m < k) {
      long l1 = (arrayOfInt2[m] & 0xFFFFFFFFL) - (arrayOfInt1[b] & 0xFFFFFFFFL) + l;
      arrayOfInt2[m] = (int)l1;
      l = l1 >> 32;
      b++;
      m++;
    } 
    while (l != 0L && m < k) {
      long l1 = (arrayOfInt2[m] & 0xFFFFFFFFL) + l;
      arrayOfInt2[m] = (int)l1;
      l = l1 >> 32;
      m++;
    } 
    assert l == 0L : l;
    fDBigInteger.trimLeadingZeros();
    return fDBigInteger;
  }
  
  public FDBigInteger rightInplaceSub(FDBigInteger paramFDBigInteger) {
    assert size() >= paramFDBigInteger.size() : "result should be positive";
    FDBigInteger fDBigInteger = this;
    if (paramFDBigInteger.isImmutable)
      paramFDBigInteger = new FDBigInteger((int[])paramFDBigInteger.data.clone(), paramFDBigInteger.offset); 
    int i = fDBigInteger.offset - paramFDBigInteger.offset;
    int[] arrayOfInt1 = paramFDBigInteger.data;
    int[] arrayOfInt2 = fDBigInteger.data;
    int j = paramFDBigInteger.nWords;
    int k = fDBigInteger.nWords;
    if (i < 0) {
      int m = k;
      if (m < arrayOfInt1.length) {
        System.arraycopy(arrayOfInt1, 0, arrayOfInt1, -i, j);
        Arrays.fill(arrayOfInt1, 0, -i, 0);
      } else {
        int[] arrayOfInt = new int[m];
        System.arraycopy(arrayOfInt1, 0, arrayOfInt, -i, j);
        paramFDBigInteger.data = arrayOfInt1 = arrayOfInt;
      } 
      paramFDBigInteger.offset = fDBigInteger.offset;
      j -= i;
      i = 0;
    } else {
      int m = k + i;
      if (m >= arrayOfInt1.length)
        paramFDBigInteger.data = arrayOfInt1 = Arrays.copyOf(arrayOfInt1, m); 
    } 
    byte b1 = 0;
    long l = 0L;
    while (b1 < i) {
      long l1 = 0L - (arrayOfInt1[b1] & 0xFFFFFFFFL) + l;
      arrayOfInt1[b1] = (int)l1;
      l = l1 >> 32;
      b1++;
    } 
    for (byte b2 = 0; b2 < k; b2++) {
      long l1 = (arrayOfInt2[b2] & 0xFFFFFFFFL) - (arrayOfInt1[b1] & 0xFFFFFFFFL) + l;
      arrayOfInt1[b1] = (int)l1;
      l = l1 >> 32;
      b1++;
    } 
    assert l == 0L : l;
    paramFDBigInteger.nWords = b1;
    paramFDBigInteger.trimLeadingZeros();
    return paramFDBigInteger;
  }
  
  private static int checkZeroTail(int[] paramArrayOfInt, int paramInt) {
    while (paramInt > 0) {
      if (paramArrayOfInt[--paramInt] != 0)
        return 1; 
    } 
    return 0;
  }
  
  public int cmp(FDBigInteger paramFDBigInteger) throws IllegalArgumentException {
    int i = this.nWords + this.offset;
    int j = paramFDBigInteger.nWords + paramFDBigInteger.offset;
    if (i > j)
      return 1; 
    if (i < j)
      return -1; 
    int k = this.nWords;
    int m = paramFDBigInteger.nWords;
    while (k > 0 && m > 0) {
      int n = this.data[--k];
      int i1 = paramFDBigInteger.data[--m];
      if (n != i1)
        return ((n & 0xFFFFFFFFL) < (i1 & 0xFFFFFFFFL)) ? -1 : 1; 
    } 
    return (k > 0) ? checkZeroTail(this.data, k) : ((m > 0) ? -checkZeroTail(paramFDBigInteger.data, m) : 0);
  }
  
  public int cmpPow52(int paramInt1, int paramInt2) {
    if (paramInt1 == 0) {
      int i = paramInt2 >> 5;
      int j = paramInt2 & 0x1F;
      int k = this.nWords + this.offset;
      if (k > i + 1)
        return 1; 
      if (k < i + 1)
        return -1; 
      int m = this.data[this.nWords - 1];
      int n = 1 << j;
      return (m != n) ? (((m & 0xFFFFFFFFL) < (n & 0xFFFFFFFFL)) ? -1 : 1) : checkZeroTail(this.data, this.nWords - 1);
    } 
    return cmp(big5pow(paramInt1).leftShift(paramInt2));
  }
  
  public int addAndCmp(FDBigInteger paramFDBigInteger1, FDBigInteger paramFDBigInteger2) {
    int m;
    int k;
    FDBigInteger fDBigInteger2;
    FDBigInteger fDBigInteger1;
    int i = paramFDBigInteger1.size();
    int j = paramFDBigInteger2.size();
    if (i >= j) {
      fDBigInteger1 = paramFDBigInteger1;
      fDBigInteger2 = paramFDBigInteger2;
      k = i;
      m = j;
    } else {
      fDBigInteger1 = paramFDBigInteger2;
      fDBigInteger2 = paramFDBigInteger1;
      k = j;
      m = i;
    } 
    int n = size();
    if (k == 0)
      return (n == 0) ? 0 : 1; 
    if (m == 0)
      return cmp(fDBigInteger1); 
    if (k > n)
      return -1; 
    if (k + 1 < n)
      return 1; 
    long l = fDBigInteger1.data[fDBigInteger1.nWords - 1] & 0xFFFFFFFFL;
    if (m == k)
      l += (fDBigInteger2.data[fDBigInteger2.nWords - 1] & 0xFFFFFFFFL); 
    if (l >>> 32 == 0L) {
      if (l + 1L >>> 32 == 0L) {
        if (k < n)
          return 1; 
        long l1 = this.data[this.nWords - 1] & 0xFFFFFFFFL;
        if (l1 < l)
          return -1; 
        if (l1 > l + 1L)
          return 1; 
      } 
    } else {
      if (k + 1 > n)
        return -1; 
      l >>>= 32;
      long l1 = this.data[this.nWords - 1] & 0xFFFFFFFFL;
      if (l1 < l)
        return -1; 
      if (l1 > l + 1L)
        return 1; 
    } 
    return cmp(fDBigInteger1.add(fDBigInteger2));
  }
  
  public void makeImmutable() { this.isImmutable = true; }
  
  private FDBigInteger mult(int paramInt) {
    if (this.nWords == 0)
      return this; 
    int[] arrayOfInt = new int[this.nWords + 1];
    mult(this.data, this.nWords, paramInt, arrayOfInt);
    return new FDBigInteger(arrayOfInt, this.offset);
  }
  
  private FDBigInteger mult(FDBigInteger paramFDBigInteger) {
    if (this.nWords == 0)
      return this; 
    if (size() == 1)
      return paramFDBigInteger.mult(this.data[0]); 
    if (paramFDBigInteger.nWords == 0)
      return paramFDBigInteger; 
    if (paramFDBigInteger.size() == 1)
      return mult(paramFDBigInteger.data[0]); 
    int[] arrayOfInt = new int[this.nWords + paramFDBigInteger.nWords];
    mult(this.data, this.nWords, paramFDBigInteger.data, paramFDBigInteger.nWords, arrayOfInt);
    return new FDBigInteger(arrayOfInt, this.offset + paramFDBigInteger.offset);
  }
  
  private FDBigInteger add(FDBigInteger paramFDBigInteger) {
    int j;
    int i;
    FDBigInteger fDBigInteger2;
    FDBigInteger fDBigInteger1;
    int k = size();
    int m = paramFDBigInteger.size();
    if (k >= m) {
      fDBigInteger1 = this;
      i = k;
      fDBigInteger2 = paramFDBigInteger;
      j = m;
    } else {
      fDBigInteger1 = paramFDBigInteger;
      i = m;
      fDBigInteger2 = this;
      j = k;
    } 
    int[] arrayOfInt = new int[i + 1];
    int n = 0;
    long l = 0L;
    while (n < j) {
      l += ((n < fDBigInteger1.offset) ? 0L : (fDBigInteger1.data[n - fDBigInteger1.offset] & 0xFFFFFFFFL)) + ((n < fDBigInteger2.offset) ? 0L : (fDBigInteger2.data[n - fDBigInteger2.offset] & 0xFFFFFFFFL));
      arrayOfInt[n] = (int)l;
      l >>= 32;
      n++;
    } 
    while (n < i) {
      l += ((n < fDBigInteger1.offset) ? 0L : (fDBigInteger1.data[n - fDBigInteger1.offset] & 0xFFFFFFFFL));
      arrayOfInt[n] = (int)l;
      l >>= 32;
      n++;
    } 
    arrayOfInt[i] = (int)l;
    return new FDBigInteger(arrayOfInt, 0);
  }
  
  private void multAddMe(int paramInt1, int paramInt2) {
    long l1 = paramInt1 & 0xFFFFFFFFL;
    long l2 = l1 * (this.data[0] & 0xFFFFFFFFL) + (paramInt2 & 0xFFFFFFFFL);
    this.data[0] = (int)l2;
    l2 >>>= 32;
    for (byte b = 1; b < this.nWords; b++) {
      l2 += l1 * (this.data[b] & 0xFFFFFFFFL);
      this.data[b] = (int)l2;
      l2 >>>= 32;
    } 
    if (l2 != 0L)
      this.data[this.nWords++] = (int)l2; 
  }
  
  private long multDiffMe(long paramLong, FDBigInteger paramFDBigInteger) {
    long l = 0L;
    if (paramLong != 0L) {
      int i = paramFDBigInteger.offset - this.offset;
      if (i >= 0) {
        int[] arrayOfInt1 = paramFDBigInteger.data;
        int[] arrayOfInt2 = this.data;
        byte b = 0;
        for (int j = i; b < paramFDBigInteger.nWords; j++) {
          l += (arrayOfInt2[j] & 0xFFFFFFFFL) - paramLong * (arrayOfInt1[b] & 0xFFFFFFFFL);
          arrayOfInt2[j] = (int)l;
          l >>= 32;
          b++;
        } 
      } else {
        i = -i;
        int[] arrayOfInt1 = new int[this.nWords + i];
        byte b1 = 0;
        byte b2 = 0;
        int[] arrayOfInt2 = paramFDBigInteger.data;
        while (b2 < i && b1 < paramFDBigInteger.nWords) {
          l -= paramLong * (arrayOfInt2[b1] & 0xFFFFFFFFL);
          arrayOfInt1[b2] = (int)l;
          l >>= 32;
          b1++;
          b2++;
        } 
        byte b3 = 0;
        int[] arrayOfInt3 = this.data;
        while (b1 < paramFDBigInteger.nWords) {
          l += (arrayOfInt3[b3] & 0xFFFFFFFFL) - paramLong * (arrayOfInt2[b1] & 0xFFFFFFFFL);
          arrayOfInt1[b2] = (int)l;
          l >>= 32;
          b1++;
          b3++;
          b2++;
        } 
        this.nWords += i;
        this.offset -= i;
        this.data = arrayOfInt1;
      } 
    } 
    return l;
  }
  
  private static int multAndCarryBy10(int[] paramArrayOfInt1, int paramInt, int[] paramArrayOfInt2) {
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      long l1 = (paramArrayOfInt1[b] & 0xFFFFFFFFL) * 10L + l;
      paramArrayOfInt2[b] = (int)l1;
      l = l1 >>> 32;
    } 
    return (int)l;
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2) {
    long l1 = paramInt2 & 0xFFFFFFFFL;
    long l2 = 0L;
    for (byte b = 0; b < paramInt1; b++) {
      long l = (paramArrayOfInt1[b] & 0xFFFFFFFFL) * l1 + l2;
      paramArrayOfInt2[b] = (int)l;
      l2 = l >>> 32;
    } 
    paramArrayOfInt2[paramInt1] = (int)l2;
  }
  
  private static void mult(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2) {
    long l1 = paramInt2 & 0xFFFFFFFFL;
    long l2 = 0L;
    byte b;
    for (b = 0; b < paramInt1; b++) {
      long l = l1 * (paramArrayOfInt1[b] & 0xFFFFFFFFL) + l2;
      paramArrayOfInt2[b] = (int)l;
      l2 = l >>> 32;
    } 
    paramArrayOfInt2[paramInt1] = (int)l2;
    l1 = paramInt3 & 0xFFFFFFFFL;
    l2 = 0L;
    for (b = 0; b < paramInt1; b++) {
      long l = (paramArrayOfInt2[b + 1] & 0xFFFFFFFFL) + l1 * (paramArrayOfInt1[b] & 0xFFFFFFFFL) + l2;
      paramArrayOfInt2[b + 1] = (int)l;
      l2 = l >>> 32;
    } 
    paramArrayOfInt2[paramInt1 + 1] = (int)l2;
  }
  
  private static FDBigInteger big5pow(int paramInt) {
    assert paramInt >= 0 : paramInt;
    return (paramInt < 340) ? POW_5_CACHE[paramInt] : big5powRec(paramInt);
  }
  
  private static FDBigInteger big5powRec(int paramInt) {
    if (paramInt < 340)
      return POW_5_CACHE[paramInt]; 
    int i = paramInt >> 1;
    int j = paramInt - i;
    FDBigInteger fDBigInteger = big5powRec(i);
    return (j < SMALL_5_POW.length) ? fDBigInteger.mult(SMALL_5_POW[j]) : fDBigInteger.mult(big5powRec(j));
  }
  
  public String toHexString() {
    if (this.nWords == 0)
      return "0"; 
    StringBuilder stringBuilder = new StringBuilder((this.nWords + this.offset) * 8);
    int i;
    for (i = this.nWords - 1; i >= 0; i--) {
      String str = Integer.toHexString(this.data[i]);
      for (int j = str.length(); j < 8; j++)
        stringBuilder.append('0'); 
      stringBuilder.append(str);
    } 
    for (i = this.offset; i > 0; i--)
      stringBuilder.append("00000000"); 
    return stringBuilder.toString();
  }
  
  public BigInteger toBigInteger() {
    byte[] arrayOfByte = new byte[this.nWords * 4 + 1];
    for (byte b = 0; b < this.nWords; b++) {
      int i = this.data[b];
      arrayOfByte[arrayOfByte.length - 4 * b - 1] = (byte)i;
      arrayOfByte[arrayOfByte.length - 4 * b - 2] = (byte)(i >> 8);
      arrayOfByte[arrayOfByte.length - 4 * b - 3] = (byte)(i >> 16);
      arrayOfByte[arrayOfByte.length - 4 * b - 4] = (byte)(i >> 24);
    } 
    return (new BigInteger(arrayOfByte)).shiftLeft(this.offset * 32);
  }
  
  public String toString() { return toBigInteger().toString(); }
  
  static  {
    byte b;
    for (b = 0; b < SMALL_5_POW.length; b++) {
      FDBigInteger fDBigInteger1 = new FDBigInteger(new int[] { SMALL_5_POW[b] }, 0);
      fDBigInteger1.makeImmutable();
      POW_5_CACHE[b] = fDBigInteger1;
    } 
    FDBigInteger fDBigInteger = POW_5_CACHE[b - 1];
    while (b < 'Ŕ') {
      POW_5_CACHE[b] = fDBigInteger = fDBigInteger.mult(5);
      fDBigInteger.makeImmutable();
      b++;
    } 
    ZERO = new FDBigInteger(new int[0], 0);
    ZERO.makeImmutable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\FDBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */