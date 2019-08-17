package java.math;

import java.util.Arrays;

class MutableBigInteger {
  int[] value;
  
  int intLen;
  
  int offset = 0;
  
  static final MutableBigInteger ONE = new MutableBigInteger(1);
  
  static final int KNUTH_POW2_THRESH_LEN = 6;
  
  static final int KNUTH_POW2_THRESH_ZEROS = 3;
  
  MutableBigInteger() {
    this.value = new int[1];
    this.intLen = 0;
  }
  
  MutableBigInteger(int paramInt) {
    this.value = new int[1];
    this.intLen = 1;
    this.value[0] = paramInt;
  }
  
  MutableBigInteger(int[] paramArrayOfInt) {
    this.value = paramArrayOfInt;
    this.intLen = paramArrayOfInt.length;
  }
  
  MutableBigInteger(BigInteger paramBigInteger) {
    this.intLen = paramBigInteger.mag.length;
    this.value = Arrays.copyOf(paramBigInteger.mag, this.intLen);
  }
  
  MutableBigInteger(MutableBigInteger paramMutableBigInteger) {
    this.intLen = paramMutableBigInteger.intLen;
    this.value = Arrays.copyOfRange(paramMutableBigInteger.value, paramMutableBigInteger.offset, paramMutableBigInteger.offset + this.intLen);
  }
  
  private void ones(int paramInt) {
    if (paramInt > this.value.length)
      this.value = new int[paramInt]; 
    Arrays.fill(this.value, -1);
    this.offset = 0;
    this.intLen = paramInt;
  }
  
  private int[] getMagnitudeArray() { return (this.offset > 0 || this.value.length != this.intLen) ? Arrays.copyOfRange(this.value, this.offset, this.offset + this.intLen) : this.value; }
  
  private long toLong() {
    assert this.intLen <= 2 : "this MutableBigInteger exceeds the range of long";
    if (this.intLen == 0)
      return 0L; 
    long l = this.value[this.offset] & 0xFFFFFFFFL;
    return (this.intLen == 2) ? (l << 32 | this.value[this.offset + 1] & 0xFFFFFFFFL) : l;
  }
  
  BigInteger toBigInteger(int paramInt) { return (this.intLen == 0 || paramInt == 0) ? BigInteger.ZERO : new BigInteger(getMagnitudeArray(), paramInt); }
  
  BigInteger toBigInteger() {
    normalize();
    return toBigInteger(isZero() ? 0 : 1);
  }
  
  BigDecimal toBigDecimal(int paramInt1, int paramInt2) {
    if (this.intLen == 0 || paramInt1 == 0)
      return BigDecimal.zeroValueOf(paramInt2); 
    int[] arrayOfInt = getMagnitudeArray();
    int i = arrayOfInt.length;
    int j = arrayOfInt[0];
    if (i > 2 || (j < 0 && i == 2))
      return new BigDecimal(new BigInteger(arrayOfInt, paramInt1), Float.MIN_VALUE, paramInt2, 0); 
    long l = (i == 2) ? (arrayOfInt[1] & 0xFFFFFFFFL | (j & 0xFFFFFFFFL) << 32) : (j & 0xFFFFFFFFL);
    return BigDecimal.valueOf((paramInt1 == -1) ? -l : l, paramInt2);
  }
  
  long toCompactValue(int paramInt) {
    if (this.intLen == 0 || paramInt == 0)
      return 0L; 
    int[] arrayOfInt = getMagnitudeArray();
    int i = arrayOfInt.length;
    int j = arrayOfInt[0];
    if (i > 2 || (j < 0 && i == 2))
      return Float.MIN_VALUE; 
    long l = (i == 2) ? (arrayOfInt[1] & 0xFFFFFFFFL | (j & 0xFFFFFFFFL) << 32) : (j & 0xFFFFFFFFL);
    return (paramInt == -1) ? -l : l;
  }
  
  void clear() {
    this.offset = this.intLen = 0;
    byte b = 0;
    int i = this.value.length;
    while (b < i) {
      this.value[b] = 0;
      b++;
    } 
  }
  
  void reset() { this.offset = this.intLen = 0; }
  
  final int compare(MutableBigInteger paramMutableBigInteger) {
    int i = paramMutableBigInteger.intLen;
    if (this.intLen < i)
      return -1; 
    if (this.intLen > i)
      return 1; 
    int[] arrayOfInt = paramMutableBigInteger.value;
    int j = this.offset;
    for (int k = paramMutableBigInteger.offset; j < this.intLen + this.offset; k++) {
      int m = this.value[j] + Integer.MIN_VALUE;
      int n = arrayOfInt[k] + Integer.MIN_VALUE;
      if (m < n)
        return -1; 
      if (m > n)
        return 1; 
      j++;
    } 
    return 0;
  }
  
  private int compareShifted(MutableBigInteger paramMutableBigInteger, int paramInt) {
    int i = paramMutableBigInteger.intLen;
    int j = this.intLen - paramInt;
    if (j < i)
      return -1; 
    if (j > i)
      return 1; 
    int[] arrayOfInt = paramMutableBigInteger.value;
    int k = this.offset;
    for (int m = paramMutableBigInteger.offset; k < j + this.offset; m++) {
      int n = this.value[k] + Integer.MIN_VALUE;
      int i1 = arrayOfInt[m] + Integer.MIN_VALUE;
      if (n < i1)
        return -1; 
      if (n > i1)
        return 1; 
      k++;
    } 
    return 0;
  }
  
  final int compareHalf(MutableBigInteger paramMutableBigInteger) {
    int i = paramMutableBigInteger.intLen;
    int j = this.intLen;
    if (j <= 0)
      return (i <= 0) ? 0 : -1; 
    if (j > i)
      return 1; 
    if (j < i - 1)
      return -1; 
    int[] arrayOfInt1 = paramMutableBigInteger.value;
    byte b1 = 0;
    int k = 0;
    if (j != i)
      if (arrayOfInt1[b1] == 1) {
        b1++;
        k = Integer.MIN_VALUE;
      } else {
        return -1;
      }  
    int[] arrayOfInt2 = this.value;
    int m = this.offset;
    byte b2 = b1;
    while (m < j + this.offset) {
      int n = arrayOfInt1[b2++];
      long l1 = ((n >>> 1) + k) & 0xFFFFFFFFL;
      long l2 = arrayOfInt2[m++] & 0xFFFFFFFFL;
      if (l2 != l1)
        return (l2 < l1) ? -1 : 1; 
      k = (n & true) << 31;
    } 
    return (k == 0) ? 0 : -1;
  }
  
  private final int getLowestSetBit() {
    if (this.intLen == 0)
      return -1; 
    int i;
    for (i = this.intLen - 1; i > 0 && this.value[i + this.offset] == 0; i--);
    int j = this.value[i + this.offset];
    return (j == 0) ? -1 : ((this.intLen - 1 - i << 5) + Integer.numberOfTrailingZeros(j));
  }
  
  private final int getInt(int paramInt) { return this.value[this.offset + paramInt]; }
  
  private final long getLong(int paramInt) { return this.value[this.offset + paramInt] & 0xFFFFFFFFL; }
  
  final void normalize() {
    if (this.intLen == 0) {
      this.offset = 0;
      return;
    } 
    int i = this.offset;
    if (this.value[i] != 0)
      return; 
    int j = i + this.intLen;
    do {
    
    } while (++i < j && this.value[i] == 0);
    int k = i - this.offset;
    this.intLen -= k;
    this.offset = (this.intLen == 0) ? 0 : (this.offset + k);
  }
  
  private final void ensureCapacity(int paramInt) {
    if (this.value.length < paramInt) {
      this.value = new int[paramInt];
      this.offset = 0;
      this.intLen = paramInt;
    } 
  }
  
  int[] toIntArray() {
    int[] arrayOfInt = new int[this.intLen];
    for (int i = 0; i < this.intLen; i++)
      arrayOfInt[i] = this.value[this.offset + i]; 
    return arrayOfInt;
  }
  
  void setInt(int paramInt1, int paramInt2) { this.value[this.offset + paramInt1] = paramInt2; }
  
  void setValue(int[] paramArrayOfInt, int paramInt) {
    this.value = paramArrayOfInt;
    this.intLen = paramInt;
    this.offset = 0;
  }
  
  void copyValue(MutableBigInteger paramMutableBigInteger) {
    int i = paramMutableBigInteger.intLen;
    if (this.value.length < i)
      this.value = new int[i]; 
    System.arraycopy(paramMutableBigInteger.value, paramMutableBigInteger.offset, this.value, 0, i);
    this.intLen = i;
    this.offset = 0;
  }
  
  void copyValue(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    if (this.value.length < i)
      this.value = new int[i]; 
    System.arraycopy(paramArrayOfInt, 0, this.value, 0, i);
    this.intLen = i;
    this.offset = 0;
  }
  
  boolean isOne() { return (this.intLen == 1 && this.value[this.offset] == 1); }
  
  boolean isZero() { return (this.intLen == 0); }
  
  boolean isEven() { return (this.intLen == 0 || (this.value[this.offset + this.intLen - 1] & true) == 0); }
  
  boolean isOdd() { return isZero() ? false : (((this.value[this.offset + this.intLen - 1] & true) == 1)); }
  
  boolean isNormal() { return (this.intLen + this.offset > this.value.length) ? false : ((this.intLen == 0) ? true : ((this.value[this.offset] != 0))); }
  
  public String toString() {
    BigInteger bigInteger = toBigInteger(1);
    return bigInteger.toString();
  }
  
  void safeRightShift(int paramInt) {
    if (paramInt / 32 >= this.intLen) {
      reset();
    } else {
      rightShift(paramInt);
    } 
  }
  
  void rightShift(int paramInt) {
    if (this.intLen == 0)
      return; 
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    this.intLen -= i;
    if (j == 0)
      return; 
    int k = BigInteger.bitLengthForInt(this.value[this.offset]);
    if (j >= k) {
      primitiveLeftShift(32 - j);
      this.intLen--;
    } else {
      primitiveRightShift(j);
    } 
  }
  
  void safeLeftShift(int paramInt) {
    if (paramInt > 0)
      leftShift(paramInt); 
  }
  
  void leftShift(int paramInt) {
    if (this.intLen == 0)
      return; 
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = BigInteger.bitLengthForInt(this.value[this.offset]);
    if (paramInt <= 32 - k) {
      primitiveLeftShift(j);
      return;
    } 
    int m = this.intLen + i + 1;
    if (j <= 32 - k)
      m--; 
    if (this.value.length < m) {
      int[] arrayOfInt = new int[m];
      for (int n = 0; n < this.intLen; n++)
        arrayOfInt[n] = this.value[this.offset + n]; 
      setValue(arrayOfInt, m);
    } else if (this.value.length - this.offset >= m) {
      for (int n = 0; n < m - this.intLen; n++)
        this.value[this.offset + this.intLen + n] = 0; 
    } else {
      int n;
      for (n = 0; n < this.intLen; n++)
        this.value[n] = this.value[this.offset + n]; 
      for (n = this.intLen; n < m; n++)
        this.value[n] = 0; 
      this.offset = 0;
    } 
    this.intLen = m;
    if (j == 0)
      return; 
    if (j <= 32 - k) {
      primitiveLeftShift(j);
    } else {
      primitiveRightShift(32 - j);
    } 
  }
  
  private int divadd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    long l = 0L;
    for (int i = paramArrayOfInt1.length - 1; i >= 0; i--) {
      long l1 = (paramArrayOfInt1[i] & 0xFFFFFFFFL) + (paramArrayOfInt2[i + paramInt] & 0xFFFFFFFFL) + l;
      paramArrayOfInt2[i + paramInt] = (int)l1;
      l = l1 >>> 32;
    } 
    return (int)l;
  }
  
  private int mulsub(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3) {
    long l1 = paramInt1 & 0xFFFFFFFFL;
    long l2 = 0L;
    paramInt3 += paramInt2;
    for (int i = paramInt2 - 1; i >= 0; i--) {
      long l3 = (paramArrayOfInt2[i] & 0xFFFFFFFFL) * l1 + l2;
      long l4 = paramArrayOfInt1[paramInt3] - l3;
      paramArrayOfInt1[paramInt3--] = (int)l4;
      l2 = (l3 >>> 32) + (((l4 & 0xFFFFFFFFL) > (((int)l3 ^ 0xFFFFFFFF) & 0xFFFFFFFFL)) ? true : false);
    } 
    return (int)l2;
  }
  
  private int mulsubBorrow(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3) {
    long l1 = paramInt1 & 0xFFFFFFFFL;
    long l2 = 0L;
    paramInt3 += paramInt2;
    for (int i = paramInt2 - 1; i >= 0; i--) {
      long l3 = (paramArrayOfInt2[i] & 0xFFFFFFFFL) * l1 + l2;
      long l4 = paramArrayOfInt1[paramInt3--] - l3;
      l2 = (l3 >>> 32) + (((l4 & 0xFFFFFFFFL) > (((int)l3 ^ 0xFFFFFFFF) & 0xFFFFFFFFL)) ? true : false);
    } 
    return (int)l2;
  }
  
  private final void primitiveRightShift(int paramInt) {
    int[] arrayOfInt = this.value;
    int i = 32 - paramInt;
    int j = this.offset + this.intLen - 1;
    int k = arrayOfInt[j];
    while (j > this.offset) {
      int m = k;
      k = arrayOfInt[j - 1];
      arrayOfInt[j] = k << i | m >>> paramInt;
      j--;
    } 
    arrayOfInt[this.offset] = arrayOfInt[this.offset] >>> paramInt;
  }
  
  private final void primitiveLeftShift(int paramInt) {
    int[] arrayOfInt = this.value;
    int i = 32 - paramInt;
    int j = this.offset;
    int k = arrayOfInt[j];
    int m = j + this.intLen - 1;
    while (j < m) {
      int n = k;
      k = arrayOfInt[j + 1];
      arrayOfInt[j] = n << paramInt | k >>> i;
      j++;
    } 
    arrayOfInt[this.offset + this.intLen - 1] = arrayOfInt[this.offset + this.intLen - 1] << paramInt;
  }
  
  private BigInteger getLower(int paramInt) {
    if (isZero())
      return BigInteger.ZERO; 
    if (this.intLen < paramInt)
      return toBigInteger(1); 
    int i;
    for (i = paramInt; i > 0 && this.value[this.offset + this.intLen - i] == 0; i--);
    byte b = (i > 0) ? 1 : 0;
    return new BigInteger(Arrays.copyOfRange(this.value, this.offset + this.intLen - i, this.offset + this.intLen), b);
  }
  
  private void keepLower(int paramInt) {
    if (this.intLen >= paramInt) {
      this.offset += this.intLen - paramInt;
      this.intLen = paramInt;
    } 
  }
  
  void add(MutableBigInteger paramMutableBigInteger) {
    int i = this.intLen;
    int j = paramMutableBigInteger.intLen;
    int k = (this.intLen > paramMutableBigInteger.intLen) ? this.intLen : paramMutableBigInteger.intLen;
    int[] arrayOfInt = (this.value.length < k) ? new int[k] : this.value;
    int m = arrayOfInt.length - 1;
    long l;
    for (l = 0L; i > 0 && j > 0; l = l1 >>> 32) {
      i--;
      long l1 = (this.value[i + this.offset] & 0xFFFFFFFFL) + (paramMutableBigInteger.value[--j + paramMutableBigInteger.offset] & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
    } 
    while (i > 0) {
      if (l == 0L && arrayOfInt == this.value && m == --i + this.offset)
        return; 
      long l1 = (this.value[i + this.offset] & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
      l = l1 >>> 32;
    } 
    while (j > 0) {
      long l1 = (paramMutableBigInteger.value[--j + paramMutableBigInteger.offset] & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
      l = l1 >>> 32;
    } 
    if (l > 0L)
      if (arrayOfInt.length < ++k) {
        int[] arrayOfInt1 = new int[k];
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 1, arrayOfInt.length);
        arrayOfInt1[0] = 1;
        arrayOfInt = arrayOfInt1;
      } else {
        arrayOfInt[m--] = 1;
      }  
    this.value = arrayOfInt;
    this.intLen = k;
    this.offset = arrayOfInt.length - k;
  }
  
  void addShifted(MutableBigInteger paramMutableBigInteger, int paramInt) {
    if (paramMutableBigInteger.isZero())
      return; 
    int i = this.intLen;
    int j = paramMutableBigInteger.intLen + paramInt;
    int k = (this.intLen > j) ? this.intLen : j;
    int[] arrayOfInt = (this.value.length < k) ? new int[k] : this.value;
    int m = arrayOfInt.length - 1;
    long l;
    for (l = 0L; i > 0 && j > 0; l = l1 >>> 32) {
      i--;
      int n = (--j + paramMutableBigInteger.offset < paramMutableBigInteger.value.length) ? paramMutableBigInteger.value[j + paramMutableBigInteger.offset] : 0;
      long l1 = (this.value[i + this.offset] & 0xFFFFFFFFL) + (n & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
    } 
    while (i > 0) {
      if (l == 0L && arrayOfInt == this.value && m == --i + this.offset)
        return; 
      long l1 = (this.value[i + this.offset] & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
      l = l1 >>> 32;
    } 
    while (j > 0) {
      int n = (--j + paramMutableBigInteger.offset < paramMutableBigInteger.value.length) ? paramMutableBigInteger.value[j + paramMutableBigInteger.offset] : 0;
      long l1 = (n & 0xFFFFFFFFL) + l;
      arrayOfInt[m--] = (int)l1;
      l = l1 >>> 32;
    } 
    if (l > 0L)
      if (arrayOfInt.length < ++k) {
        int[] arrayOfInt1 = new int[k];
        System.arraycopy(arrayOfInt, 0, arrayOfInt1, 1, arrayOfInt.length);
        arrayOfInt1[0] = 1;
        arrayOfInt = arrayOfInt1;
      } else {
        arrayOfInt[m--] = 1;
      }  
    this.value = arrayOfInt;
    this.intLen = k;
    this.offset = arrayOfInt.length - k;
  }
  
  void addDisjoint(MutableBigInteger paramMutableBigInteger, int paramInt) {
    int[] arrayOfInt;
    if (paramMutableBigInteger.isZero())
      return; 
    int i = this.intLen;
    int j = paramMutableBigInteger.intLen + paramInt;
    int k = (this.intLen > j) ? this.intLen : j;
    if (this.value.length < k) {
      arrayOfInt = new int[k];
    } else {
      arrayOfInt = this.value;
      Arrays.fill(this.value, this.offset + this.intLen, this.value.length, 0);
    } 
    int m = arrayOfInt.length - 1;
    System.arraycopy(this.value, this.offset, arrayOfInt, m + 1 - i, i);
    j -= i;
    m -= i;
    int n = Math.min(j, paramMutableBigInteger.value.length - paramMutableBigInteger.offset);
    System.arraycopy(paramMutableBigInteger.value, paramMutableBigInteger.offset, arrayOfInt, m + 1 - j, n);
    for (int i1 = m + 1 - j + n; i1 < m + 1; i1++)
      arrayOfInt[i1] = 0; 
    this.value = arrayOfInt;
    this.intLen = k;
    this.offset = arrayOfInt.length - k;
  }
  
  void addLower(MutableBigInteger paramMutableBigInteger, int paramInt) {
    MutableBigInteger mutableBigInteger = new MutableBigInteger(paramMutableBigInteger);
    if (mutableBigInteger.offset + mutableBigInteger.intLen >= paramInt) {
      mutableBigInteger.offset = mutableBigInteger.offset + mutableBigInteger.intLen - paramInt;
      mutableBigInteger.intLen = paramInt;
    } 
    mutableBigInteger.normalize();
    add(mutableBigInteger);
  }
  
  int subtract(MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger = this;
    int[] arrayOfInt = this.value;
    int i = mutableBigInteger.compare(paramMutableBigInteger);
    if (i == 0) {
      reset();
      return 0;
    } 
    if (i < 0) {
      MutableBigInteger mutableBigInteger1 = mutableBigInteger;
      mutableBigInteger = paramMutableBigInteger;
      paramMutableBigInteger = mutableBigInteger1;
    } 
    int j = mutableBigInteger.intLen;
    if (arrayOfInt.length < j)
      arrayOfInt = new int[j]; 
    long l = 0L;
    int k = mutableBigInteger.intLen;
    int m = paramMutableBigInteger.intLen;
    int n = arrayOfInt.length - 1;
    while (m > 0) {
      k--;
      l = (mutableBigInteger.value[k + mutableBigInteger.offset] & 0xFFFFFFFFL) - (paramMutableBigInteger.value[--m + paramMutableBigInteger.offset] & 0xFFFFFFFFL) - (int)-(l >> 32);
      arrayOfInt[n--] = (int)l;
    } 
    while (k > 0) {
      l = (mutableBigInteger.value[--k + mutableBigInteger.offset] & 0xFFFFFFFFL) - (int)-(l >> 32);
      arrayOfInt[n--] = (int)l;
    } 
    this.value = arrayOfInt;
    this.intLen = j;
    this.offset = this.value.length - j;
    normalize();
    return i;
  }
  
  private int difference(MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger = this;
    int i = mutableBigInteger.compare(paramMutableBigInteger);
    if (i == 0)
      return 0; 
    if (i < 0) {
      MutableBigInteger mutableBigInteger1 = mutableBigInteger;
      mutableBigInteger = paramMutableBigInteger;
      paramMutableBigInteger = mutableBigInteger1;
    } 
    long l = 0L;
    int j = mutableBigInteger.intLen;
    int k = paramMutableBigInteger.intLen;
    while (k > 0) {
      j--;
      l = (mutableBigInteger.value[mutableBigInteger.offset + j] & 0xFFFFFFFFL) - (paramMutableBigInteger.value[paramMutableBigInteger.offset + --k] & 0xFFFFFFFFL) - (int)-(l >> 32);
      mutableBigInteger.value[mutableBigInteger.offset + j] = (int)l;
    } 
    while (j > 0) {
      l = (mutableBigInteger.value[mutableBigInteger.offset + --j] & 0xFFFFFFFFL) - (int)-(l >> 32);
      mutableBigInteger.value[mutableBigInteger.offset + j] = (int)l;
    } 
    mutableBigInteger.normalize();
    return i;
  }
  
  void multiply(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) {
    int i = this.intLen;
    int j = paramMutableBigInteger1.intLen;
    int k = i + j;
    if (paramMutableBigInteger2.value.length < k)
      paramMutableBigInteger2.value = new int[k]; 
    paramMutableBigInteger2.offset = 0;
    paramMutableBigInteger2.intLen = k;
    long l = 0L;
    int m = j - 1;
    int n;
    for (n = j + i - 1; m >= 0; n--) {
      long l1 = (paramMutableBigInteger1.value[m + paramMutableBigInteger1.offset] & 0xFFFFFFFFL) * (this.value[i - 1 + this.offset] & 0xFFFFFFFFL) + l;
      paramMutableBigInteger2.value[n] = (int)l1;
      l = l1 >>> 32;
      m--;
    } 
    paramMutableBigInteger2.value[i - 1] = (int)l;
    for (m = i - 2; m >= 0; m--) {
      l = 0L;
      n = j - 1;
      for (int i1 = j + m; n >= 0; i1--) {
        long l1 = (paramMutableBigInteger1.value[n + paramMutableBigInteger1.offset] & 0xFFFFFFFFL) * (this.value[m + this.offset] & 0xFFFFFFFFL) + (paramMutableBigInteger2.value[i1] & 0xFFFFFFFFL) + l;
        paramMutableBigInteger2.value[i1] = (int)l1;
        l = l1 >>> 32;
        n--;
      } 
      paramMutableBigInteger2.value[m] = (int)l;
    } 
    paramMutableBigInteger2.normalize();
  }
  
  void mul(int paramInt, MutableBigInteger paramMutableBigInteger) {
    if (paramInt == 1) {
      paramMutableBigInteger.copyValue(this);
      return;
    } 
    if (paramInt == 0) {
      paramMutableBigInteger.clear();
      return;
    } 
    long l1 = paramInt & 0xFFFFFFFFL;
    int[] arrayOfInt = (paramMutableBigInteger.value.length < this.intLen + 1) ? new int[this.intLen + 1] : paramMutableBigInteger.value;
    long l2 = 0L;
    for (int i = this.intLen - 1; i >= 0; i--) {
      long l = l1 * (this.value[i + this.offset] & 0xFFFFFFFFL) + l2;
      arrayOfInt[i + 1] = (int)l;
      l2 = l >>> 32;
    } 
    if (l2 == 0L) {
      paramMutableBigInteger.offset = 1;
      paramMutableBigInteger.intLen = this.intLen;
    } else {
      paramMutableBigInteger.offset = 0;
      this.intLen++;
      arrayOfInt[0] = (int)l2;
    } 
    paramMutableBigInteger.value = arrayOfInt;
  }
  
  int divideOneWord(int paramInt, MutableBigInteger paramMutableBigInteger) {
    long l1 = paramInt & 0xFFFFFFFFL;
    if (this.intLen == 1) {
      long l = this.value[this.offset] & 0xFFFFFFFFL;
      int m = (int)(l / l1);
      int n = (int)(l - m * l1);
      paramMutableBigInteger.value[0] = m;
      paramMutableBigInteger.intLen = (m == 0) ? 0 : 1;
      paramMutableBigInteger.offset = 0;
      return n;
    } 
    if (paramMutableBigInteger.value.length < this.intLen)
      paramMutableBigInteger.value = new int[this.intLen]; 
    paramMutableBigInteger.offset = 0;
    paramMutableBigInteger.intLen = this.intLen;
    int i = Integer.numberOfLeadingZeros(paramInt);
    int j = this.value[this.offset];
    long l2 = j & 0xFFFFFFFFL;
    if (l2 < l1) {
      paramMutableBigInteger.value[0] = 0;
    } else {
      paramMutableBigInteger.value[0] = (int)(l2 / l1);
      j = (int)(l2 - paramMutableBigInteger.value[0] * l1);
      l2 = j & 0xFFFFFFFFL;
    } 
    int k = this.intLen;
    while (--k > 0) {
      int m;
      long l = l2 << 32 | this.value[this.offset + this.intLen - k] & 0xFFFFFFFFL;
      if (l >= 0L) {
        m = (int)(l / l1);
        j = (int)(l - m * l1);
      } else {
        long l3 = divWord(l, paramInt);
        m = (int)(l3 & 0xFFFFFFFFL);
        j = (int)(l3 >>> 32);
      } 
      paramMutableBigInteger.value[this.intLen - k] = m;
      l2 = j & 0xFFFFFFFFL;
    } 
    paramMutableBigInteger.normalize();
    return (i > 0) ? (j % paramInt) : j;
  }
  
  MutableBigInteger divide(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) { return divide(paramMutableBigInteger1, paramMutableBigInteger2, true); }
  
  MutableBigInteger divide(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean) { return (paramMutableBigInteger1.intLen < 80 || this.intLen - paramMutableBigInteger1.intLen < 40) ? divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2, paramBoolean) : divideAndRemainderBurnikelZiegler(paramMutableBigInteger1, paramMutableBigInteger2); }
  
  MutableBigInteger divideKnuth(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) { return divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2, true); }
  
  MutableBigInteger divideKnuth(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean) {
    if (paramMutableBigInteger1.intLen == 0)
      throw new ArithmeticException("BigInteger divide by zero"); 
    if (this.intLen == 0) {
      paramMutableBigInteger2.intLen = paramMutableBigInteger2.offset = 0;
      return paramBoolean ? new MutableBigInteger() : null;
    } 
    int i = compare(paramMutableBigInteger1);
    if (i < 0) {
      paramMutableBigInteger2.intLen = paramMutableBigInteger2.offset = 0;
      return paramBoolean ? new MutableBigInteger(this) : null;
    } 
    if (i == 0) {
      paramMutableBigInteger2.value[0] = paramMutableBigInteger2.intLen = 1;
      paramMutableBigInteger2.offset = 0;
      return paramBoolean ? new MutableBigInteger() : null;
    } 
    paramMutableBigInteger2.clear();
    if (paramMutableBigInteger1.intLen == 1) {
      int j = divideOneWord(paramMutableBigInteger1.value[paramMutableBigInteger1.offset], paramMutableBigInteger2);
      return paramBoolean ? ((j == 0) ? new MutableBigInteger() : new MutableBigInteger(j)) : null;
    } 
    if (this.intLen >= 6) {
      int j = Math.min(getLowestSetBit(), paramMutableBigInteger1.getLowestSetBit());
      if (j >= 96) {
        MutableBigInteger mutableBigInteger1 = new MutableBigInteger(this);
        paramMutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger1);
        mutableBigInteger1.rightShift(j);
        paramMutableBigInteger1.rightShift(j);
        MutableBigInteger mutableBigInteger2 = mutableBigInteger1.divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2);
        mutableBigInteger2.leftShift(j);
        return mutableBigInteger2;
      } 
    } 
    return divideMagnitude(paramMutableBigInteger1, paramMutableBigInteger2, paramBoolean);
  }
  
  MutableBigInteger divideAndRemainderBurnikelZiegler(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) {
    int i = this.intLen;
    int j = paramMutableBigInteger1.intLen;
    paramMutableBigInteger2.offset = paramMutableBigInteger2.intLen = 0;
    if (i < j)
      return this; 
    int k = 1 << 32 - Integer.numberOfLeadingZeros(j / 80);
    int m = (j + k - 1) / k;
    int n = m * k;
    long l = 32L * n;
    int i1 = (int)Math.max(0L, l - paramMutableBigInteger1.bitLength());
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger1);
    mutableBigInteger1.safeLeftShift(i1);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(this);
    mutableBigInteger2.safeLeftShift(i1);
    int i2 = (int)((mutableBigInteger2.bitLength() + l) / l);
    if (i2 < 2)
      i2 = 2; 
    MutableBigInteger mutableBigInteger3 = mutableBigInteger2.getBlock(i2 - 1, i2, n);
    MutableBigInteger mutableBigInteger4 = mutableBigInteger2.getBlock(i2 - 2, i2, n);
    mutableBigInteger4.addDisjoint(mutableBigInteger3, n);
    MutableBigInteger mutableBigInteger5 = new MutableBigInteger();
    for (int i3 = i2 - 2; i3 > 0; i3--) {
      MutableBigInteger mutableBigInteger = mutableBigInteger4.divide2n1n(mutableBigInteger1, mutableBigInteger5);
      mutableBigInteger4 = mutableBigInteger2.getBlock(i3 - 1, i2, n);
      mutableBigInteger4.addDisjoint(mutableBigInteger, n);
      paramMutableBigInteger2.addShifted(mutableBigInteger5, i3 * n);
    } 
    MutableBigInteger mutableBigInteger6 = mutableBigInteger4.divide2n1n(mutableBigInteger1, mutableBigInteger5);
    paramMutableBigInteger2.add(mutableBigInteger5);
    mutableBigInteger6.rightShift(i1);
    return mutableBigInteger6;
  }
  
  private MutableBigInteger divide2n1n(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) {
    int i = paramMutableBigInteger1.intLen;
    if (i % 2 != 0 || i < 80)
      return divideKnuth(paramMutableBigInteger1, paramMutableBigInteger2); 
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(this);
    mutableBigInteger1.safeRightShift(32 * i / 2);
    keepLower(i / 2);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger3 = mutableBigInteger1.divide3n2n(paramMutableBigInteger1, mutableBigInteger2);
    addDisjoint(mutableBigInteger3, i / 2);
    MutableBigInteger mutableBigInteger4 = divide3n2n(paramMutableBigInteger1, paramMutableBigInteger2);
    paramMutableBigInteger2.addDisjoint(mutableBigInteger2, i / 2);
    return mutableBigInteger4;
  }
  
  private MutableBigInteger divide3n2n(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2) {
    MutableBigInteger mutableBigInteger4;
    MutableBigInteger mutableBigInteger3;
    int i = paramMutableBigInteger1.intLen / 2;
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(this);
    mutableBigInteger1.safeRightShift(32 * i);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(paramMutableBigInteger1);
    mutableBigInteger2.safeRightShift(i * 32);
    BigInteger bigInteger = paramMutableBigInteger1.getLower(i);
    if (compareShifted(paramMutableBigInteger1, i) < 0) {
      mutableBigInteger3 = mutableBigInteger1.divide2n1n(mutableBigInteger2, paramMutableBigInteger2);
      mutableBigInteger4 = new MutableBigInteger(paramMutableBigInteger2.toBigInteger().multiply(bigInteger));
    } else {
      paramMutableBigInteger2.ones(i);
      mutableBigInteger1.add(mutableBigInteger2);
      mutableBigInteger2.leftShift(32 * i);
      mutableBigInteger1.subtract(mutableBigInteger2);
      mutableBigInteger3 = mutableBigInteger1;
      mutableBigInteger4 = new MutableBigInteger(bigInteger);
      mutableBigInteger4.leftShift(32 * i);
      mutableBigInteger4.subtract(new MutableBigInteger(bigInteger));
    } 
    mutableBigInteger3.leftShift(32 * i);
    mutableBigInteger3.addLower(this, i);
    while (mutableBigInteger3.compare(mutableBigInteger4) < 0) {
      mutableBigInteger3.add(paramMutableBigInteger1);
      paramMutableBigInteger2.subtract(ONE);
    } 
    mutableBigInteger3.subtract(mutableBigInteger4);
    return mutableBigInteger3;
  }
  
  private MutableBigInteger getBlock(int paramInt1, int paramInt2, int paramInt3) {
    int j;
    int i = paramInt1 * paramInt3;
    if (i >= this.intLen)
      return new MutableBigInteger(); 
    if (paramInt1 == paramInt2 - 1) {
      j = this.intLen;
    } else {
      j = (paramInt1 + 1) * paramInt3;
    } 
    if (j > this.intLen)
      return new MutableBigInteger(); 
    int[] arrayOfInt = Arrays.copyOfRange(this.value, this.offset + this.intLen - j, this.offset + this.intLen - i);
    return new MutableBigInteger(arrayOfInt);
  }
  
  long bitLength() { return (this.intLen == 0) ? 0L : (this.intLen * 32L - Integer.numberOfLeadingZeros(this.value[this.offset])); }
  
  long divide(long paramLong, MutableBigInteger paramMutableBigInteger) {
    if (paramLong == 0L)
      throw new ArithmeticException("BigInteger divide by zero"); 
    if (this.intLen == 0) {
      paramMutableBigInteger.intLen = paramMutableBigInteger.offset = 0;
      return 0L;
    } 
    if (paramLong < 0L)
      paramLong = -paramLong; 
    int i = (int)(paramLong >>> 32);
    paramMutableBigInteger.clear();
    return (i == 0) ? (divideOneWord((int)paramLong, paramMutableBigInteger) & 0xFFFFFFFFL) : divideLongMagnitude(paramLong, paramMutableBigInteger).toLong();
  }
  
  private static void copyAndShift(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4) {
    int i = 32 - paramInt4;
    int j = paramArrayOfInt1[paramInt1];
    for (int k = 0; k < paramInt2 - 1; k++) {
      int m = j;
      j = paramArrayOfInt1[++paramInt1];
      paramArrayOfInt2[paramInt3 + k] = m << paramInt4 | j >>> i;
    } 
    paramArrayOfInt2[paramInt3 + paramInt2 - 1] = j << paramInt4;
  }
  
  private MutableBigInteger divideMagnitude(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, boolean paramBoolean) {
    MutableBigInteger mutableBigInteger;
    int[] arrayOfInt1;
    int i = Integer.numberOfLeadingZeros(paramMutableBigInteger1.value[paramMutableBigInteger1.offset]);
    int j = paramMutableBigInteger1.intLen;
    if (i > 0) {
      arrayOfInt1 = new int[j];
      copyAndShift(paramMutableBigInteger1.value, paramMutableBigInteger1.offset, j, arrayOfInt1, 0, i);
      if (Integer.numberOfLeadingZeros(this.value[this.offset]) >= i) {
        int[] arrayOfInt = new int[this.intLen + 1];
        mutableBigInteger = new MutableBigInteger(arrayOfInt);
        mutableBigInteger.intLen = this.intLen;
        mutableBigInteger.offset = 1;
        copyAndShift(this.value, this.offset, this.intLen, arrayOfInt, 1, i);
      } else {
        int[] arrayOfInt = new int[this.intLen + 2];
        mutableBigInteger = new MutableBigInteger(arrayOfInt);
        this.intLen++;
        mutableBigInteger.offset = 1;
        int i7 = this.offset;
        int i8 = 0;
        int i9 = 32 - i;
        byte b = 1;
        while (b < this.intLen + 1) {
          int i10 = i8;
          i8 = this.value[i7];
          arrayOfInt[b] = i10 << i | i8 >>> i9;
          b++;
          i7++;
        } 
        arrayOfInt[this.intLen + 1] = i8 << i;
      } 
    } else {
      arrayOfInt1 = Arrays.copyOfRange(paramMutableBigInteger1.value, paramMutableBigInteger1.offset, paramMutableBigInteger1.offset + paramMutableBigInteger1.intLen);
      mutableBigInteger = new MutableBigInteger(new int[this.intLen + 1]);
      System.arraycopy(this.value, this.offset, mutableBigInteger.value, 1, this.intLen);
      mutableBigInteger.intLen = this.intLen;
      mutableBigInteger.offset = 1;
    } 
    int k = mutableBigInteger.intLen;
    int m = k - j + 1;
    if (paramMutableBigInteger2.value.length < m) {
      paramMutableBigInteger2.value = new int[m];
      paramMutableBigInteger2.offset = 0;
    } 
    paramMutableBigInteger2.intLen = m;
    int[] arrayOfInt2 = paramMutableBigInteger2.value;
    if (mutableBigInteger.intLen == k) {
      mutableBigInteger.offset = 0;
      mutableBigInteger.value[0] = 0;
      mutableBigInteger.intLen++;
    } 
    int n = arrayOfInt1[0];
    long l = n & 0xFFFFFFFFL;
    int i1 = arrayOfInt1[1];
    int i2;
    for (i2 = 0; i2 < m - 1; i2++) {
      int i7 = 0;
      int i8 = 0;
      boolean bool1 = false;
      int i9 = mutableBigInteger.value[i2 + mutableBigInteger.offset];
      int i10 = i9 + Integer.MIN_VALUE;
      int i11 = mutableBigInteger.value[i2 + 1 + mutableBigInteger.offset];
      if (i9 == n) {
        i7 = -1;
        i8 = i9 + i11;
        bool1 = (i8 + Integer.MIN_VALUE < i10) ? 1 : 0;
      } else {
        long l1 = i9 << 32 | i11 & 0xFFFFFFFFL;
        if (l1 >= 0L) {
          i7 = (int)(l1 / l);
          i8 = (int)(l1 - i7 * l);
        } else {
          long l2 = divWord(l1, n);
          i7 = (int)(l2 & 0xFFFFFFFFL);
          i8 = (int)(l2 >>> 32);
        } 
      } 
      if (i7 != 0) {
        if (!bool1) {
          long l1 = mutableBigInteger.value[i2 + 2 + mutableBigInteger.offset] & 0xFFFFFFFFL;
          long l2 = (i8 & 0xFFFFFFFFL) << 32 | l1;
          long l3 = (i1 & 0xFFFFFFFFL) * (i7 & 0xFFFFFFFFL);
          if (unsignedLongCompare(l3, l2)) {
            i7--;
            i8 = (int)((i8 & 0xFFFFFFFFL) + l);
            if ((i8 & 0xFFFFFFFFL) >= l) {
              l3 -= (i1 & 0xFFFFFFFFL);
              l2 = (i8 & 0xFFFFFFFFL) << 32 | l1;
              if (unsignedLongCompare(l3, l2))
                i7--; 
            } 
          } 
        } 
        mutableBigInteger.value[i2 + mutableBigInteger.offset] = 0;
        int i12 = mulsub(mutableBigInteger.value, arrayOfInt1, i7, j, i2 + mutableBigInteger.offset);
        if (i12 + Integer.MIN_VALUE > i10) {
          divadd(arrayOfInt1, mutableBigInteger.value, i2 + 1 + mutableBigInteger.offset);
          i7--;
        } 
        arrayOfInt2[i2] = i7;
      } 
    } 
    i2 = 0;
    int i3 = 0;
    boolean bool = false;
    int i4 = mutableBigInteger.value[m - 1 + mutableBigInteger.offset];
    int i5 = i4 + Integer.MIN_VALUE;
    int i6 = mutableBigInteger.value[m + mutableBigInteger.offset];
    if (i4 == n) {
      i2 = -1;
      i3 = i4 + i6;
      bool = (i3 + Integer.MIN_VALUE < i5) ? 1 : 0;
    } else {
      long l1 = i4 << 32 | i6 & 0xFFFFFFFFL;
      if (l1 >= 0L) {
        i2 = (int)(l1 / l);
        i3 = (int)(l1 - i2 * l);
      } else {
        long l2 = divWord(l1, n);
        i2 = (int)(l2 & 0xFFFFFFFFL);
        i3 = (int)(l2 >>> 32);
      } 
    } 
    if (i2 != 0) {
      int i7;
      if (!bool) {
        i7 = mutableBigInteger.value[m + 1 + mutableBigInteger.offset] & 0xFFFFFFFFL;
        long l1 = (i3 & 0xFFFFFFFFL) << 32 | i7;
        long l2 = (i1 & 0xFFFFFFFFL) * (i2 & 0xFFFFFFFFL);
        if (unsignedLongCompare(l2, l1)) {
          i2--;
          i3 = (int)((i3 & 0xFFFFFFFFL) + l);
          if ((i3 & 0xFFFFFFFFL) >= l) {
            l2 -= (i1 & 0xFFFFFFFFL);
            l1 = (i3 & 0xFFFFFFFFL) << 32 | i7;
            if (unsignedLongCompare(l2, l1))
              i2--; 
          } 
        } 
      } 
      mutableBigInteger.value[m - 1 + mutableBigInteger.offset] = 0;
      if (paramBoolean) {
        i7 = mulsub(mutableBigInteger.value, arrayOfInt1, i2, j, m - 1 + mutableBigInteger.offset);
      } else {
        i7 = mulsubBorrow(mutableBigInteger.value, arrayOfInt1, i2, j, m - 1 + mutableBigInteger.offset);
      } 
      if (i7 + Integer.MIN_VALUE > i5) {
        if (paramBoolean)
          divadd(arrayOfInt1, mutableBigInteger.value, m - 1 + 1 + mutableBigInteger.offset); 
        i2--;
      } 
      arrayOfInt2[m - 1] = i2;
    } 
    if (paramBoolean) {
      if (i > 0)
        mutableBigInteger.rightShift(i); 
      mutableBigInteger.normalize();
    } 
    paramMutableBigInteger2.normalize();
    return paramBoolean ? mutableBigInteger : null;
  }
  
  private MutableBigInteger divideLongMagnitude(long paramLong, MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger = new MutableBigInteger(new int[this.intLen + 1]);
    System.arraycopy(this.value, this.offset, mutableBigInteger.value, 1, this.intLen);
    mutableBigInteger.intLen = this.intLen;
    mutableBigInteger.offset = 1;
    int i = mutableBigInteger.intLen;
    int j = i - 2 + 1;
    if (paramMutableBigInteger.value.length < j) {
      paramMutableBigInteger.value = new int[j];
      paramMutableBigInteger.offset = 0;
    } 
    paramMutableBigInteger.intLen = j;
    int[] arrayOfInt = paramMutableBigInteger.value;
    int k = Long.numberOfLeadingZeros(paramLong);
    if (k > 0) {
      paramLong <<= k;
      mutableBigInteger.leftShift(k);
    } 
    if (mutableBigInteger.intLen == i) {
      mutableBigInteger.offset = 0;
      mutableBigInteger.value[0] = 0;
      mutableBigInteger.intLen++;
    } 
    int m = (int)(paramLong >>> 32);
    long l = m & 0xFFFFFFFFL;
    int n = (int)(paramLong & 0xFFFFFFFFL);
    for (int i1 = 0; i1 < j; i1++) {
      int i2 = 0;
      int i3 = 0;
      boolean bool = false;
      int i4 = mutableBigInteger.value[i1 + mutableBigInteger.offset];
      int i5 = i4 + Integer.MIN_VALUE;
      int i6 = mutableBigInteger.value[i1 + 1 + mutableBigInteger.offset];
      if (i4 == m) {
        i2 = -1;
        i3 = i4 + i6;
        bool = (i3 + Integer.MIN_VALUE < i5) ? 1 : 0;
      } else {
        long l1 = i4 << 32 | i6 & 0xFFFFFFFFL;
        if (l1 >= 0L) {
          i2 = (int)(l1 / l);
          i3 = (int)(l1 - i2 * l);
        } else {
          long l2 = divWord(l1, m);
          i2 = (int)(l2 & 0xFFFFFFFFL);
          i3 = (int)(l2 >>> 32);
        } 
      } 
      if (i2 != 0) {
        if (!bool) {
          long l1 = mutableBigInteger.value[i1 + 2 + mutableBigInteger.offset] & 0xFFFFFFFFL;
          long l2 = (i3 & 0xFFFFFFFFL) << 32 | l1;
          long l3 = (n & 0xFFFFFFFFL) * (i2 & 0xFFFFFFFFL);
          if (unsignedLongCompare(l3, l2)) {
            i2--;
            i3 = (int)((i3 & 0xFFFFFFFFL) + l);
            if ((i3 & 0xFFFFFFFFL) >= l) {
              l3 -= (n & 0xFFFFFFFFL);
              l2 = (i3 & 0xFFFFFFFFL) << 32 | l1;
              if (unsignedLongCompare(l3, l2))
                i2--; 
            } 
          } 
        } 
        mutableBigInteger.value[i1 + mutableBigInteger.offset] = 0;
        int i7 = mulsubLong(mutableBigInteger.value, m, n, i2, i1 + mutableBigInteger.offset);
        if (i7 + Integer.MIN_VALUE > i5) {
          divaddLong(m, n, mutableBigInteger.value, i1 + 1 + mutableBigInteger.offset);
          i2--;
        } 
        arrayOfInt[i1] = i2;
      } 
    } 
    if (k > 0)
      mutableBigInteger.rightShift(k); 
    paramMutableBigInteger.normalize();
    mutableBigInteger.normalize();
    return mutableBigInteger;
  }
  
  private int divaddLong(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3) {
    long l1 = 0L;
    long l2 = (paramInt2 & 0xFFFFFFFFL) + (paramArrayOfInt[1 + paramInt3] & 0xFFFFFFFFL);
    paramArrayOfInt[1 + paramInt3] = (int)l2;
    l2 = (paramInt1 & 0xFFFFFFFFL) + (paramArrayOfInt[paramInt3] & 0xFFFFFFFFL) + l1;
    paramArrayOfInt[paramInt3] = (int)l2;
    l1 = l2 >>> 32;
    return (int)l1;
  }
  
  private int mulsubLong(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l1 = paramInt3 & 0xFFFFFFFFL;
    paramInt4 += 2;
    long l2 = (paramInt2 & 0xFFFFFFFFL) * l1;
    long l3 = paramArrayOfInt[paramInt4] - l2;
    paramArrayOfInt[paramInt4--] = (int)l3;
    long l4 = (l2 >>> 32) + (((l3 & 0xFFFFFFFFL) > (((int)l2 ^ 0xFFFFFFFF) & 0xFFFFFFFFL)) ? true : false);
    l2 = (paramInt1 & 0xFFFFFFFFL) * l1 + l4;
    l3 = paramArrayOfInt[paramInt4] - l2;
    paramArrayOfInt[paramInt4--] = (int)l3;
    l4 = (l2 >>> 32) + (((l3 & 0xFFFFFFFFL) > (((int)l2 ^ 0xFFFFFFFF) & 0xFFFFFFFFL)) ? true : false);
    return (int)l4;
  }
  
  private boolean unsignedLongCompare(long paramLong1, long paramLong2) { return (paramLong1 + Float.MIN_VALUE > paramLong2 + Float.MIN_VALUE); }
  
  static long divWord(long paramLong, int paramInt) {
    long l1 = paramInt & 0xFFFFFFFFL;
    if (l1 == 1L) {
      long l5 = (int)paramLong;
      long l4 = 0L;
      return l4 << 32 | l5 & 0xFFFFFFFFL;
    } 
    long l3 = (paramLong >>> true) / (l1 >>> true);
    long l2 = paramLong - l3 * l1;
    while (l2 < 0L) {
      l2 += l1;
      l3--;
    } 
    while (l2 >= l1) {
      l2 -= l1;
      l3++;
    } 
    return l2 << 32 | l3 & 0xFFFFFFFFL;
  }
  
  MutableBigInteger hybridGCD(MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger1 = this;
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
    while (paramMutableBigInteger.intLen != 0) {
      if (Math.abs(mutableBigInteger1.intLen - paramMutableBigInteger.intLen) < 2)
        return mutableBigInteger1.binaryGCD(paramMutableBigInteger); 
      MutableBigInteger mutableBigInteger = mutableBigInteger1.divide(paramMutableBigInteger, mutableBigInteger2);
      mutableBigInteger1 = paramMutableBigInteger;
      paramMutableBigInteger = mutableBigInteger;
    } 
    return mutableBigInteger1;
  }
  
  private MutableBigInteger binaryGCD(MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger1 = this;
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
    int i = mutableBigInteger1.getLowestSetBit();
    int j = paramMutableBigInteger.getLowestSetBit();
    int k = (i < j) ? i : j;
    if (k != 0) {
      mutableBigInteger1.rightShift(k);
      paramMutableBigInteger.rightShift(k);
    } 
    boolean bool = (k == i) ? 1 : 0;
    MutableBigInteger mutableBigInteger3 = bool ? paramMutableBigInteger : mutableBigInteger1;
    int m = bool ? -1 : 1;
    int n;
    while ((n = mutableBigInteger3.getLowestSetBit()) >= 0) {
      mutableBigInteger3.rightShift(n);
      if (m > 0) {
        mutableBigInteger1 = mutableBigInteger3;
      } else {
        paramMutableBigInteger = mutableBigInteger3;
      } 
      if (mutableBigInteger1.intLen < 2 && paramMutableBigInteger.intLen < 2) {
        int i1 = mutableBigInteger1.value[mutableBigInteger1.offset];
        int i2 = paramMutableBigInteger.value[paramMutableBigInteger.offset];
        i1 = binaryGcd(i1, i2);
        mutableBigInteger2.value[0] = i1;
        mutableBigInteger2.intLen = 1;
        mutableBigInteger2.offset = 0;
        if (k > 0)
          mutableBigInteger2.leftShift(k); 
        return mutableBigInteger2;
      } 
      if ((m = mutableBigInteger1.difference(paramMutableBigInteger)) == 0)
        break; 
      mutableBigInteger3 = (m >= 0) ? mutableBigInteger1 : paramMutableBigInteger;
    } 
    if (k > 0)
      mutableBigInteger1.leftShift(k); 
    return mutableBigInteger1;
  }
  
  static int binaryGcd(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return paramInt1; 
    if (paramInt1 == 0)
      return paramInt2; 
    int i = Integer.numberOfTrailingZeros(paramInt1);
    int j = Integer.numberOfTrailingZeros(paramInt2);
    paramInt1 >>>= i;
    paramInt2 >>>= j;
    int k = (i < j) ? i : j;
    while (paramInt1 != paramInt2) {
      if (paramInt1 + Integer.MIN_VALUE > paramInt2 + Integer.MIN_VALUE) {
        paramInt1 -= paramInt2;
        paramInt1 >>>= Integer.numberOfTrailingZeros(paramInt1);
        continue;
      } 
      paramInt2 -= paramInt1;
      paramInt2 >>>= Integer.numberOfTrailingZeros(paramInt2);
    } 
    return paramInt1 << k;
  }
  
  MutableBigInteger mutableModInverse(MutableBigInteger paramMutableBigInteger) {
    if (paramMutableBigInteger.isOdd())
      return modInverse(paramMutableBigInteger); 
    if (isEven())
      throw new ArithmeticException("BigInteger not invertible."); 
    int i = paramMutableBigInteger.getLowestSetBit();
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger);
    mutableBigInteger1.rightShift(i);
    if (mutableBigInteger1.isOne())
      return modInverseMP2(i); 
    MutableBigInteger mutableBigInteger2 = modInverse(mutableBigInteger1);
    MutableBigInteger mutableBigInteger3;
    MutableBigInteger mutableBigInteger4 = (mutableBigInteger3 = modInverseMP2(i)).modInverseBP2(mutableBigInteger1, i);
    MutableBigInteger mutableBigInteger5 = mutableBigInteger1.modInverseMP2(i);
    MutableBigInteger mutableBigInteger6 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger7 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger8 = new MutableBigInteger();
    mutableBigInteger2.leftShift(i);
    mutableBigInteger2.multiply(mutableBigInteger4, mutableBigInteger8);
    mutableBigInteger3.multiply(mutableBigInteger1, mutableBigInteger6);
    mutableBigInteger6.multiply(mutableBigInteger5, mutableBigInteger7);
    mutableBigInteger8.add(mutableBigInteger7);
    return mutableBigInteger8.divide(paramMutableBigInteger, mutableBigInteger6);
  }
  
  MutableBigInteger modInverseMP2(int paramInt) {
    if (isEven())
      throw new ArithmeticException("Non-invertible. (GCD != 1)"); 
    if (paramInt > 64)
      return euclidModInverse(paramInt); 
    int i = inverseMod32(this.value[this.offset + this.intLen - 1]);
    if (paramInt < 33) {
      i = (paramInt == 32) ? i : (i & (1 << paramInt) - 1);
      return new MutableBigInteger(i);
    } 
    long l1 = this.value[this.offset + this.intLen - 1] & 0xFFFFFFFFL;
    if (this.intLen > 1)
      l1 |= this.value[this.offset + this.intLen - 2] << 32; 
    long l2 = i & 0xFFFFFFFFL;
    l2 *= (2L - l1 * l2);
    l2 = (paramInt == 64) ? l2 : (l2 & (1L << paramInt) - 1L);
    MutableBigInteger mutableBigInteger = new MutableBigInteger(new int[2]);
    mutableBigInteger.value[0] = (int)(l2 >>> 32);
    mutableBigInteger.value[1] = (int)l2;
    mutableBigInteger.intLen = 2;
    mutableBigInteger.normalize();
    return mutableBigInteger;
  }
  
  static int inverseMod32(int paramInt) {
    null = paramInt;
    null *= (2 - paramInt * null);
    null *= (2 - paramInt * null);
    null *= (2 - paramInt * null);
    return 2 - paramInt * null;
  }
  
  static long inverseMod64(long paramLong) {
    long l = paramLong;
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    l *= (2L - paramLong * l);
    assert l * paramLong == 1L;
    return l;
  }
  
  static MutableBigInteger modInverseBP2(MutableBigInteger paramMutableBigInteger, int paramInt) { return fixup(new MutableBigInteger(1), new MutableBigInteger(paramMutableBigInteger), paramInt); }
  
  private MutableBigInteger modInverse(MutableBigInteger paramMutableBigInteger) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(paramMutableBigInteger);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(this);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(mutableBigInteger1);
    SignedMutableBigInteger signedMutableBigInteger1 = new SignedMutableBigInteger(1);
    SignedMutableBigInteger signedMutableBigInteger2 = new SignedMutableBigInteger();
    MutableBigInteger mutableBigInteger4 = null;
    SignedMutableBigInteger signedMutableBigInteger3 = null;
    int i = 0;
    if (mutableBigInteger2.isEven()) {
      int j = mutableBigInteger2.getLowestSetBit();
      mutableBigInteger2.rightShift(j);
      signedMutableBigInteger2.leftShift(j);
      i = j;
    } 
    while (!mutableBigInteger2.isOne()) {
      if (mutableBigInteger2.isZero())
        throw new ArithmeticException("BigInteger not invertible."); 
      if (mutableBigInteger2.compare(mutableBigInteger3) < 0) {
        mutableBigInteger4 = mutableBigInteger2;
        mutableBigInteger2 = mutableBigInteger3;
        mutableBigInteger3 = mutableBigInteger4;
        signedMutableBigInteger3 = signedMutableBigInteger2;
        signedMutableBigInteger2 = signedMutableBigInteger1;
        signedMutableBigInteger1 = signedMutableBigInteger3;
      } 
      if (((mutableBigInteger2.value[mutableBigInteger2.offset + mutableBigInteger2.intLen - 1] ^ mutableBigInteger3.value[mutableBigInteger3.offset + mutableBigInteger3.intLen - 1]) & 0x3) == 0) {
        mutableBigInteger2.subtract(mutableBigInteger3);
        signedMutableBigInteger1.signedSubtract(signedMutableBigInteger2);
      } else {
        mutableBigInteger2.add(mutableBigInteger3);
        signedMutableBigInteger1.signedAdd(signedMutableBigInteger2);
      } 
      int j = mutableBigInteger2.getLowestSetBit();
      mutableBigInteger2.rightShift(j);
      signedMutableBigInteger2.leftShift(j);
      i += j;
    } 
    while (signedMutableBigInteger1.sign < 0)
      signedMutableBigInteger1.signedAdd(mutableBigInteger1); 
    return fixup(signedMutableBigInteger1, mutableBigInteger1, i);
  }
  
  static MutableBigInteger fixup(MutableBigInteger paramMutableBigInteger1, MutableBigInteger paramMutableBigInteger2, int paramInt) {
    MutableBigInteger mutableBigInteger;
    int i = -(mutableBigInteger = new MutableBigInteger()).inverseMod32(paramMutableBigInteger2.value[paramMutableBigInteger2.offset + paramMutableBigInteger2.intLen - 1]);
    int j = 0;
    int k = paramInt >> 5;
    while (j < k) {
      int m = i * paramMutableBigInteger1.value[paramMutableBigInteger1.offset + paramMutableBigInteger1.intLen - 1];
      paramMutableBigInteger2.mul(m, mutableBigInteger);
      paramMutableBigInteger1.add(mutableBigInteger);
      paramMutableBigInteger1.intLen--;
      j++;
    } 
    j = paramInt & 0x1F;
    if (j != 0) {
      k = i * paramMutableBigInteger1.value[paramMutableBigInteger1.offset + paramMutableBigInteger1.intLen - 1];
      k &= (1 << j) - 1;
      paramMutableBigInteger2.mul(k, mutableBigInteger);
      paramMutableBigInteger1.add(mutableBigInteger);
      paramMutableBigInteger1.rightShift(j);
    } 
    while (paramMutableBigInteger1.compare(paramMutableBigInteger2) >= 0)
      paramMutableBigInteger1.subtract(paramMutableBigInteger2); 
    return paramMutableBigInteger1;
  }
  
  MutableBigInteger euclidModInverse(int paramInt) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(1);
    mutableBigInteger1.leftShift(paramInt);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(mutableBigInteger1);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
    MutableBigInteger mutableBigInteger4 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger5 = mutableBigInteger1.divide(mutableBigInteger3, mutableBigInteger4);
    MutableBigInteger mutableBigInteger6 = mutableBigInteger1;
    mutableBigInteger1 = mutableBigInteger5;
    mutableBigInteger5 = mutableBigInteger6;
    MutableBigInteger mutableBigInteger7 = new MutableBigInteger(mutableBigInteger4);
    MutableBigInteger mutableBigInteger8 = new MutableBigInteger(1);
    MutableBigInteger mutableBigInteger9 = new MutableBigInteger();
    while (!mutableBigInteger1.isOne()) {
      mutableBigInteger5 = mutableBigInteger3.divide(mutableBigInteger1, mutableBigInteger4);
      if (mutableBigInteger5.intLen == 0)
        throw new ArithmeticException("BigInteger not invertible."); 
      mutableBigInteger6 = mutableBigInteger5;
      mutableBigInteger3 = mutableBigInteger6;
      if (mutableBigInteger4.intLen == 1) {
        mutableBigInteger7.mul(mutableBigInteger4.value[mutableBigInteger4.offset], mutableBigInteger9);
      } else {
        mutableBigInteger4.multiply(mutableBigInteger7, mutableBigInteger9);
      } 
      mutableBigInteger6 = mutableBigInteger4;
      mutableBigInteger4 = mutableBigInteger9;
      mutableBigInteger9 = mutableBigInteger6;
      mutableBigInteger8.add(mutableBigInteger4);
      if (mutableBigInteger3.isOne())
        return mutableBigInteger8; 
      mutableBigInteger5 = mutableBigInteger1.divide(mutableBigInteger3, mutableBigInteger4);
      if (mutableBigInteger5.intLen == 0)
        throw new ArithmeticException("BigInteger not invertible."); 
      mutableBigInteger6 = mutableBigInteger1;
      mutableBigInteger1 = mutableBigInteger5;
      if (mutableBigInteger4.intLen == 1) {
        mutableBigInteger8.mul(mutableBigInteger4.value[mutableBigInteger4.offset], mutableBigInteger9);
      } else {
        mutableBigInteger4.multiply(mutableBigInteger8, mutableBigInteger9);
      } 
      mutableBigInteger6 = mutableBigInteger4;
      mutableBigInteger4 = mutableBigInteger9;
      mutableBigInteger9 = mutableBigInteger6;
      mutableBigInteger7.add(mutableBigInteger4);
    } 
    mutableBigInteger2.subtract(mutableBigInteger7);
    return mutableBigInteger2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\math\MutableBigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */