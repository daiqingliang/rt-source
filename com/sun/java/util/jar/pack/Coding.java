package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class Coding extends Object implements Comparable<Coding>, CodingMethod, Histogram.BitMetric {
  public static final int B_MAX = 5;
  
  public static final int H_MAX = 256;
  
  public static final int S_MAX = 2;
  
  private final int B;
  
  private final int H;
  
  private final int L;
  
  private final int S;
  
  private final int del;
  
  private final int min;
  
  private final int max;
  
  private final int umin;
  
  private final int umax;
  
  private final int[] byteMin;
  
  private final int[] byteMax;
  
  private static Map<Coding, Coding> codeMap;
  
  private static final byte[] byteBitWidths = new byte[256];
  
  static boolean verboseStringForDebug;
  
  private static int saturate32(long paramLong) { return (paramLong > 2147483647L) ? Integer.MAX_VALUE : ((paramLong < -2147483648L) ? Integer.MIN_VALUE : (int)paramLong); }
  
  private static long codeRangeLong(int paramInt1, int paramInt2) { return codeRangeLong(paramInt1, paramInt2, paramInt1); }
  
  private static long codeRangeLong(int paramInt1, int paramInt2, int paramInt3) {
    assert paramInt3 >= 0 && paramInt3 <= paramInt1;
    assert paramInt1 >= 1 && paramInt1 <= 5;
    assert paramInt2 >= 1 && paramInt2 <= 256;
    if (paramInt3 == 0)
      return 0L; 
    if (paramInt1 == 1)
      return paramInt2; 
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    for (byte b = 1; b <= paramInt3; b++) {
      l1 += l2;
      l2 *= paramInt2;
    } 
    l1 *= i;
    if (paramInt3 == paramInt1)
      l1 += l2; 
    return l1;
  }
  
  public static int codeMax(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l1 = codeRangeLong(paramInt1, paramInt2, paramInt4);
    if (l1 == 0L)
      return -1; 
    if (paramInt3 == 0 || l1 >= 4294967296L)
      return saturate32(l1 - 1L); 
    long l2;
    for (l2 = l1 - 1L; isNegativeCode(l2, paramInt3); l2--);
    if (l2 < 0L)
      return -1; 
    int i = decodeSign32(l2, paramInt3);
    return (i < 0) ? Integer.MAX_VALUE : i;
  }
  
  public static int codeMin(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l1 = codeRangeLong(paramInt1, paramInt2, paramInt4);
    if (l1 >= 4294967296L && paramInt4 == paramInt1)
      return Integer.MIN_VALUE; 
    if (paramInt3 == 0)
      return 0; 
    long l2;
    for (l2 = l1 - 1L; !isNegativeCode(l2, paramInt3); l2--);
    return (l2 < 0L) ? 0 : decodeSign32(l2, paramInt3);
  }
  
  private static long toUnsigned32(int paramInt) { return paramInt << 32 >>> 32; }
  
  private static boolean isNegativeCode(long paramLong, int paramInt) {
    assert paramInt > 0;
    assert paramLong >= -1L;
    int i = (1 << paramInt) - 1;
    return (((int)paramLong + 1 & i) == 0);
  }
  
  private static boolean hasNegativeCode(int paramInt1, int paramInt2) {
    assert paramInt2 > 0;
    return (0 > paramInt1 && paramInt1 >= (-1 >>> paramInt2 ^ 0xFFFFFFFF));
  }
  
  private static int decodeSign32(long paramLong, int paramInt) {
    int i;
    assert paramLong == toUnsigned32((int)paramLong) : Long.toHexString(paramLong);
    if (paramInt == 0)
      return (int)paramLong; 
    if (isNegativeCode(paramLong, paramInt)) {
      i = (int)paramLong >>> paramInt ^ 0xFFFFFFFF;
    } else {
      i = (int)paramLong - ((int)paramLong >>> paramInt);
    } 
    assert paramInt != 1 || i == ((int)paramLong >>> 1 ^ -((int)paramLong & true));
    return i;
  }
  
  private static long encodeSign32(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return toUnsigned32(paramInt1); 
    int i = (1 << paramInt2) - 1;
    if (!hasNegativeCode(paramInt1, paramInt2)) {
      l = paramInt1 + toUnsigned32(paramInt1) / i;
    } else {
      l = ((-paramInt1 << paramInt2) - 1);
    } 
    long l = toUnsigned32((int)l);
    assert paramInt1 == decodeSign32(l, paramInt2) : Long.toHexString(l) + " -> " + Integer.toHexString(paramInt1) + " != " + Integer.toHexString(decodeSign32(l, paramInt2));
    return l;
  }
  
  public static void writeInt(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    long l1 = encodeSign32(paramInt1, paramInt4);
    assert l1 == toUnsigned32((int)l1);
    assert l1 < codeRangeLong(paramInt2, paramInt3) : Long.toHexString(l1);
    int i = 256 - paramInt3;
    long l2 = l1;
    int j = paramArrayOfInt[0];
    for (byte b = 0; b < paramInt2 - 1 && l2 >= i; b++) {
      l2 -= i;
      int k = (int)(i + l2 % paramInt3);
      l2 /= paramInt3;
      paramArrayOfByte[j++] = (byte)k;
    } 
    paramArrayOfByte[j++] = (byte)(int)l2;
    paramArrayOfInt[0] = j;
  }
  
  public static int readInt(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    int j = paramArrayOfInt[0];
    for (byte b = 0; b < paramInt1; b++) {
      byte b1 = paramArrayOfByte[j++] & 0xFF;
      l1 += b1 * l2;
      l2 *= paramInt2;
      if (b1 < i)
        break; 
    } 
    paramArrayOfInt[0] = j;
    return decodeSign32(l1, paramInt3);
  }
  
  public static int readIntFrom(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    int i = 256 - paramInt2;
    long l1 = 0L;
    long l2 = 1L;
    for (byte b = 0; b < paramInt1; b++) {
      int j = paramInputStream.read();
      if (j < 0)
        throw new RuntimeException("unexpected EOF"); 
      l1 += j * l2;
      l2 *= paramInt2;
      if (j < i)
        break; 
    } 
    assert l1 >= 0L && l1 < codeRangeLong(paramInt1, paramInt2);
    return decodeSign32(l1, paramInt3);
  }
  
  private Coding(int paramInt1, int paramInt2, int paramInt3) { this(paramInt1, paramInt2, paramInt3, 0); }
  
  private Coding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.B = paramInt1;
    this.H = paramInt2;
    this.L = 256 - paramInt2;
    this.S = paramInt3;
    this.del = paramInt4;
    this.min = codeMin(paramInt1, paramInt2, paramInt3, paramInt1);
    this.max = codeMax(paramInt1, paramInt2, paramInt3, paramInt1);
    this.umin = codeMin(paramInt1, paramInt2, 0, paramInt1);
    this.umax = codeMax(paramInt1, paramInt2, 0, paramInt1);
    this.byteMin = new int[paramInt1];
    this.byteMax = new int[paramInt1];
    for (byte b = 1; b <= paramInt1; b++) {
      this.byteMin[b - true] = codeMin(paramInt1, paramInt2, paramInt3, b);
      this.byteMax[b - 1] = codeMax(paramInt1, paramInt2, paramInt3, b);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Coding))
      return false; 
    Coding coding = (Coding)paramObject;
    return (this.B != coding.B) ? false : ((this.H != coding.H) ? false : ((this.S != coding.S) ? false : (!(this.del != coding.del))));
  }
  
  public int hashCode() { return (this.del << 14) + (this.S << 11) + (this.B << 8) + (this.H << 0); }
  
  private static Coding of(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (codeMap == null)
      codeMap = new HashMap(); 
    Coding coding1 = new Coding(paramInt1, paramInt2, paramInt3, paramInt4);
    Coding coding2 = (Coding)codeMap.get(coding1);
    if (coding2 == null)
      codeMap.put(coding1, coding2 = coding1); 
    return coding2;
  }
  
  public static Coding of(int paramInt1, int paramInt2) { return of(paramInt1, paramInt2, 0, 0); }
  
  public static Coding of(int paramInt1, int paramInt2, int paramInt3) { return of(paramInt1, paramInt2, paramInt3, 0); }
  
  public boolean canRepresentValue(int paramInt) { return isSubrange() ? canRepresentUnsigned(paramInt) : canRepresentSigned(paramInt); }
  
  public boolean canRepresentSigned(int paramInt) { return (paramInt >= this.min && paramInt <= this.max); }
  
  public boolean canRepresentUnsigned(int paramInt) { return (paramInt >= this.umin && paramInt <= this.umax); }
  
  public int readFrom(byte[] paramArrayOfByte, int[] paramArrayOfInt) { return readInt(paramArrayOfByte, paramArrayOfInt, this.B, this.H, this.S); }
  
  public void writeTo(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt) { writeInt(paramArrayOfByte, paramArrayOfInt, paramInt, this.B, this.H, this.S); }
  
  public int readFrom(InputStream paramInputStream) throws IOException { return readIntFrom(paramInputStream, this.B, this.H, this.S); }
  
  public void writeTo(OutputStream paramOutputStream, int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[this.B];
    int[] arrayOfInt = new int[1];
    writeInt(arrayOfByte, arrayOfInt, paramInt, this.B, this.H, this.S);
    paramOutputStream.write(arrayOfByte, 0, arrayOfInt[0]);
  }
  
  public void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    int i;
    for (i = paramInt1; i < paramInt2; i++)
      paramArrayOfInt[i] = readFrom(paramInputStream); 
    for (i = 0; i < this.del; i++) {
      long l = 0L;
      for (int j = paramInt1; j < paramInt2; j++) {
        l += paramArrayOfInt[j];
        if (isSubrange())
          l = reduceToUnsignedRange(l); 
        paramArrayOfInt[j] = (int)l;
      } 
    } 
  }
  
  public void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 <= paramInt1)
      return; 
    for (byte b = 0; b < this.del; b++) {
      int[] arrayOfInt1;
      if (!isSubrange()) {
        arrayOfInt1 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, 0, 0);
      } else {
        arrayOfInt1 = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, this.min, this.max);
      } 
      paramArrayOfInt = arrayOfInt1;
      paramInt1 = 0;
      paramInt2 = arrayOfInt1.length;
    } 
    byte[] arrayOfByte = new byte[256];
    int i = arrayOfByte.length - this.B;
    int[] arrayOfInt = { 0 };
    int j = paramInt1;
    while (j < paramInt2) {
      while (arrayOfInt[0] <= i) {
        writeTo(arrayOfByte, arrayOfInt, paramArrayOfInt[j++]);
        if (j >= paramInt2)
          break; 
      } 
      paramOutputStream.write(arrayOfByte, 0, arrayOfInt[0]);
      arrayOfInt[0] = 0;
    } 
  }
  
  boolean isSubrange() { return (this.max < Integer.MAX_VALUE && this.max - this.min + 1L <= 2147483647L); }
  
  boolean isFullRange() { return (this.max == Integer.MAX_VALUE && this.min == Integer.MIN_VALUE); }
  
  int getRange() {
    assert isSubrange();
    return this.max - this.min + 1;
  }
  
  Coding setB(int paramInt) { return of(paramInt, this.H, this.S, this.del); }
  
  Coding setH(int paramInt) { return of(this.B, paramInt, this.S, this.del); }
  
  Coding setS(int paramInt) { return of(this.B, this.H, paramInt, this.del); }
  
  Coding setL(int paramInt) { return setH(256 - paramInt); }
  
  Coding setD(int paramInt) { return of(this.B, this.H, this.S, paramInt); }
  
  Coding getDeltaCoding() { return setD(this.del + 1); }
  
  Coding getValueCoding() { return isDelta() ? of(this.B, this.H, 0, this.del - 1) : this; }
  
  int reduceToUnsignedRange(long paramLong) {
    if (paramLong == (int)paramLong && canRepresentUnsigned((int)paramLong))
      return (int)paramLong; 
    int i = getRange();
    assert i > 0;
    paramLong %= i;
    if (paramLong < 0L)
      paramLong += i; 
    assert canRepresentUnsigned((int)paramLong);
    return (int)paramLong;
  }
  
  int reduceToSignedRange(int paramInt) { return canRepresentSigned(paramInt) ? paramInt : reduceToSignedRange(paramInt, this.min, this.max); }
  
  static int reduceToSignedRange(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt3 - paramInt2 + 1;
    assert i > 0;
    int j = paramInt1;
    paramInt1 -= paramInt2;
    if (paramInt1 < 0 && j >= 0) {
      paramInt1 -= i;
      assert paramInt1 >= 0;
    } 
    paramInt1 %= i;
    if (paramInt1 < 0)
      paramInt1 += i; 
    paramInt1 += paramInt2;
    assert paramInt2 <= paramInt1 && paramInt1 <= paramInt3;
    return paramInt1;
  }
  
  boolean isSigned() { return (this.min < 0); }
  
  boolean isDelta() { return (this.del != 0); }
  
  public int B() { return this.B; }
  
  public int H() { return this.H; }
  
  public int L() { return this.L; }
  
  public int S() { return this.S; }
  
  public int del() { return this.del; }
  
  public int min() { return this.min; }
  
  public int max() { return this.max; }
  
  public int umin() { return this.umin; }
  
  public int umax() { return this.umax; }
  
  public int byteMin(int paramInt) { return this.byteMin[paramInt - 1]; }
  
  public int byteMax(int paramInt) { return this.byteMax[paramInt - 1]; }
  
  public int compareTo(Coding paramCoding) {
    int i = this.del - paramCoding.del;
    if (i == 0)
      i = this.B - paramCoding.B; 
    if (i == 0)
      i = this.H - paramCoding.H; 
    if (i == 0)
      i = this.S - paramCoding.S; 
    return i;
  }
  
  public int distanceFrom(Coding paramCoding) {
    int m;
    int i = this.del - paramCoding.del;
    if (i < 0)
      i = -i; 
    int j = this.S - paramCoding.S;
    if (j < 0)
      j = -j; 
    int k = this.B - paramCoding.B;
    if (k < 0)
      k = -k; 
    if (this.H == paramCoding.H) {
      m = 0;
    } else {
      int i1 = getHL();
      int i2 = paramCoding.getHL();
      i1 *= i1;
      i2 *= i2;
      if (i1 > i2) {
        m = ceil_lg2(1 + (i1 - 1) / i2);
      } else {
        m = ceil_lg2(1 + (i2 - 1) / i1);
      } 
    } 
    int n = 5 * (i + j + k) + m;
    assert n != 0 || compareTo(paramCoding) == 0;
    return n;
  }
  
  private int getHL() { return (this.H <= 128) ? this.H : ((this.L >= 1) ? (16384 / this.L) : 32768); }
  
  static int ceil_lg2(int paramInt) {
    assert paramInt - 1 >= 0;
    paramInt--;
    byte b = 0;
    while (paramInt != 0) {
      b++;
      paramInt >>= 1;
    } 
    return b;
  }
  
  static int bitWidth(int paramInt) {
    if (paramInt < 0)
      paramInt ^= 0xFFFFFFFF; 
    null = 0;
    int i = paramInt;
    if (i < byteBitWidths.length)
      return byteBitWidths[i]; 
    int j = i >>> 16;
    if (j != 0) {
      i = j;
      null += true;
    } 
    j = i >>> 8;
    if (j != 0) {
      i = j;
      null += true;
    } 
    return byteBitWidths[i];
  }
  
  static int[] makeDeltas(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt4 >= paramInt3;
    int i = paramInt2 - paramInt1;
    int[] arrayOfInt = new int[i];
    int j = 0;
    if (paramInt3 == paramInt4) {
      for (int k = 0; k < i; k++) {
        int m = paramArrayOfInt[paramInt1 + k];
        arrayOfInt[k] = m - j;
        j = m;
      } 
    } else {
      for (int k = 0; k < i; k++) {
        int m = paramArrayOfInt[paramInt1 + k];
        assert m >= 0 && m + paramInt3 <= paramInt4;
        int n = m - j;
        assert n == m - j;
        j = m;
        n = reduceToSignedRange(n, paramInt3, paramInt4);
        arrayOfInt[k] = n;
      } 
    } 
    return arrayOfInt;
  }
  
  boolean canRepresent(int paramInt1, int paramInt2) {
    assert paramInt1 <= paramInt2;
    return (this.del > 0) ? (isSubrange() ? ((canRepresentUnsigned(paramInt2) && canRepresentUnsigned(paramInt1))) : isFullRange()) : ((canRepresentSigned(paramInt2) && canRepresentSigned(paramInt1)));
  }
  
  boolean canRepresent(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i == 0)
      return true; 
    if (isFullRange())
      return true; 
    int j = paramArrayOfInt[paramInt1];
    int k = j;
    for (int m = 1; m < i; m++) {
      int n = paramArrayOfInt[paramInt1 + m];
      if (j < n)
        j = n; 
      if (k > n)
        k = n; 
    } 
    return canRepresent(k, j);
  }
  
  public double getBitLength(int paramInt) { return getLength(paramInt) * 8.0D; }
  
  public int getLength(int paramInt) {
    if (isDelta() && isSubrange()) {
      if (!canRepresentUnsigned(paramInt))
        return Integer.MAX_VALUE; 
      paramInt = reduceToSignedRange(paramInt);
    } 
    if (paramInt >= 0) {
      for (byte b = 0; b < this.B; b++) {
        if (paramInt <= this.byteMax[b])
          return b + true; 
      } 
    } else {
      for (byte b = 0; b < this.B; b++) {
        if (paramInt >= this.byteMin[b])
          return b + true; 
      } 
    } 
    return Integer.MAX_VALUE;
  }
  
  public int getLength(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (this.B == 1)
      return i; 
    if (this.L == 0)
      return i * this.B; 
    if (isDelta()) {
      int[] arrayOfInt;
      if (!isSubrange()) {
        arrayOfInt = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, 0, 0);
      } else {
        arrayOfInt = makeDeltas(paramArrayOfInt, paramInt1, paramInt2, this.min, this.max);
      } 
      paramArrayOfInt = arrayOfInt;
      paramInt1 = 0;
    } 
    int j = i;
    for (byte b = 1; b <= this.B; b++) {
      int k = this.byteMax[b - true];
      int m = this.byteMin[b - true];
      int n = 0;
      for (int i1 = 0; i1 < i; i1++) {
        int i2 = paramArrayOfInt[paramInt1 + i1];
        if (i2 >= 0) {
          if (i2 > k)
            n++; 
        } else if (i2 < m) {
          n++;
        } 
      } 
      if (n == 0)
        break; 
      if (b == this.B)
        return Integer.MAX_VALUE; 
      j += n;
    } 
    return j;
  }
  
  public byte[] getMetaCoding(Coding paramCoding) {
    if (paramCoding == this)
      return new byte[] { 0 }; 
    int i = BandStructure.indexOf(this);
    return (i > 0) ? new byte[] { (byte)i } : new byte[] { 116, (byte)(this.del + 2 * this.S + 8 * (this.B - 1)), (byte)(this.H - 1) };
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod) {
    byte b = paramArrayOfByte[paramInt++] & 0xFF;
    if (1 <= b && b <= 115) {
      Coding coding = BandStructure.codingForIndex(b);
      assert coding != null;
      paramArrayOfCodingMethod[0] = coding;
      return paramInt;
    } 
    if (b == 116) {
      byte b1 = paramArrayOfByte[paramInt++] & 0xFF;
      byte b2 = paramArrayOfByte[paramInt++] & 0xFF;
      byte b3 = b1 % 2;
      byte b4 = b1 / 2 % 4;
      byte b5 = b1 / 8 + 1;
      byte b6 = b2 + 1;
      if (1 > b5 || b5 > 5 || 0 > b4 || b4 > 2 || 1 > b6 || b6 > 256 || 0 > b3 || b3 > 1 || (b5 == 1 && b6 != 256) || (b5 == 5 && b6 == 256))
        throw new RuntimeException("Bad arb. coding: (" + b5 + "," + b6 + "," + b4 + "," + b3); 
      paramArrayOfCodingMethod[0] = of(b5, b6, b4, b3);
      return paramInt;
    } 
    return paramInt - 1;
  }
  
  public String keyString() { return "(" + this.B + "," + this.H + "," + this.S + "," + this.del + ")"; }
  
  public String toString() { return "Coding" + keyString(); }
  
  String stringForDebug() {
    String str1 = (this.min == Integer.MIN_VALUE) ? "min" : ("" + this.min);
    String str2 = (this.max == Integer.MAX_VALUE) ? "max" : ("" + this.max);
    String str3 = keyString() + " L=" + this.L + " r=[" + str1 + "," + str2 + "]";
    if (isSubrange()) {
      str3 = str3 + " subrange";
    } else if (!isFullRange()) {
      str3 = str3 + " MIDRANGE";
    } 
    if (verboseStringForDebug) {
      str3 = str3 + " {";
      int i = 0;
      for (byte b = 1; b <= this.B; b++) {
        int j = saturate32(this.byteMax[b - true] - this.byteMin[b - true] + 1L);
        assert j == saturate32(codeRangeLong(this.B, this.H, b));
        j -= i;
        i = j;
        String str = (j == Integer.MAX_VALUE) ? "max" : ("" + j);
        str3 = str3 + " #" + b + "=" + str;
      } 
      str3 = str3 + " }";
    } 
    return str3;
  }
  
  static  {
    byte b;
    for (b = 0; b < byteBitWidths.length; b++)
      byteBitWidths[b] = (byte)ceil_lg2(b + true); 
    for (b = 10; b >= 0; b = (b << 1) - (b >> 3))
      assert bitWidth(b) == ceil_lg2(b + 1); 
    verboseStringForDebug = false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Coding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */