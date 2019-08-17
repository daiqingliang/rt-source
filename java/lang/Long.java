package java.lang;

import java.math.BigInteger;

public final class Long extends Number implements Comparable<Long> {
  public static final long MIN_VALUE = -9223372036854775808L;
  
  public static final long MAX_VALUE = 9223372036854775807L;
  
  public static final Class<Long> TYPE = Class.getPrimitiveClass("long");
  
  private final long value;
  
  public static final int SIZE = 64;
  
  public static final int BYTES = 8;
  
  private static final long serialVersionUID = 4290774380558885855L;
  
  public static String toString(long paramLong, int paramInt) {
    if (paramInt < 2 || paramInt > 36)
      paramInt = 10; 
    if (paramInt == 10)
      return toString(paramLong); 
    char[] arrayOfChar = new char[65];
    byte b = 64;
    boolean bool = (paramLong < 0L) ? 1 : 0;
    if (!bool)
      paramLong = -paramLong; 
    while (paramLong <= -paramInt) {
      arrayOfChar[b--] = Integer.digits[(int)-(paramLong % paramInt)];
      paramLong /= paramInt;
    } 
    arrayOfChar[b] = Integer.digits[(int)-paramLong];
    if (bool)
      arrayOfChar[--b] = '-'; 
    return new String(arrayOfChar, b, 65 - b);
  }
  
  public static String toUnsignedString(long paramLong, int paramInt) {
    long l2;
    long l1;
    if (paramLong >= 0L)
      return toString(paramLong, paramInt); 
    switch (paramInt) {
      case 2:
        return toBinaryString(paramLong);
      case 4:
        return toUnsignedString0(paramLong, 2);
      case 8:
        return toOctalString(paramLong);
      case 10:
        l1 = (paramLong >>> true) / 5L;
        l2 = paramLong - l1 * 10L;
        return toString(l1) + l2;
      case 16:
        return toHexString(paramLong);
      case 32:
        return toUnsignedString0(paramLong, 5);
    } 
    return toUnsignedBigInteger(paramLong).toString(paramInt);
  }
  
  private static BigInteger toUnsignedBigInteger(long paramLong) {
    if (paramLong >= 0L)
      return BigInteger.valueOf(paramLong); 
    int i = (int)(paramLong >>> 32);
    int j = (int)paramLong;
    return BigInteger.valueOf(Integer.toUnsignedLong(i)).shiftLeft(32).add(BigInteger.valueOf(Integer.toUnsignedLong(j)));
  }
  
  public static String toHexString(long paramLong) { return toUnsignedString0(paramLong, 4); }
  
  public static String toOctalString(long paramLong) { return toUnsignedString0(paramLong, 3); }
  
  public static String toBinaryString(long paramLong) { return toUnsignedString0(paramLong, 1); }
  
  static String toUnsignedString0(long paramLong, int paramInt) {
    int i = 64 - numberOfLeadingZeros(paramLong);
    int j = Math.max((i + paramInt - 1) / paramInt, 1);
    char[] arrayOfChar = new char[j];
    formatUnsignedLong(paramLong, paramInt, arrayOfChar, 0, j);
    return new String(arrayOfChar, true);
  }
  
  static int formatUnsignedLong(long paramLong, int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    int i = paramInt3;
    int j = 1 << paramInt1;
    int k = j - 1;
    do {
      paramArrayOfChar[paramInt2 + --i] = Integer.digits[(int)paramLong & k];
      paramLong >>>= paramInt1;
    } while (paramLong != 0L && i > 0);
    return i;
  }
  
  public static String toString(long paramLong) {
    if (paramLong == Float.MIN_VALUE)
      return "-9223372036854775808"; 
    int i = (paramLong < 0L) ? (stringSize(-paramLong) + 1) : stringSize(paramLong);
    char[] arrayOfChar = new char[i];
    getChars(paramLong, i, arrayOfChar);
    return new String(arrayOfChar, true);
  }
  
  public static String toUnsignedString(long paramLong) { return toUnsignedString(paramLong, 10); }
  
  static void getChars(long paramLong, int paramInt, char[] paramArrayOfChar) {
    int i = paramInt;
    byte b = 0;
    if (paramLong < 0L) {
      b = 45;
      paramLong = -paramLong;
    } 
    while (paramLong > 2147483647L) {
      long l = paramLong / 100L;
      int k = (int)(paramLong - (l << 6) + (l << 5) + (l << 2));
      paramLong = l;
      paramArrayOfChar[--i] = Integer.DigitOnes[k];
      paramArrayOfChar[--i] = Integer.DigitTens[k];
    } 
    int j = (int)paramLong;
    while (j >= 65536) {
      int m = j / 100;
      int k = j - (m << 6) + (m << 5) + (m << 2);
      j = m;
      paramArrayOfChar[--i] = Integer.DigitOnes[k];
      paramArrayOfChar[--i] = Integer.DigitTens[k];
    } 
    do {
      int m = j * 52429 >>> 19;
      int k = j - (m << 3) + (m << 1);
      paramArrayOfChar[--i] = Integer.digits[k];
      j = m;
    } while (j != 0);
    if (b != 0)
      paramArrayOfChar[--i] = b; 
  }
  
  static int stringSize(long paramLong) {
    long l = 10L;
    for (byte b = 1; b < 19; b++) {
      if (paramLong < l)
        return b; 
      l = 10L * l;
    } 
    return 19;
  }
  
  public static long parseLong(String paramString, int paramInt) throws NumberFormatException {
    if (paramString == null)
      throw new NumberFormatException("null"); 
    if (paramInt < 2)
      throw new NumberFormatException("radix " + paramInt + " less than Character.MIN_RADIX"); 
    if (paramInt > 36)
      throw new NumberFormatException("radix " + paramInt + " greater than Character.MAX_RADIX"); 
    long l1 = 0L;
    boolean bool = false;
    byte b = 0;
    int i = paramString.length();
    long l2 = -9223372036854775807L;
    if (i > 0) {
      char c = paramString.charAt(0);
      if (c < '0') {
        if (c == '-') {
          bool = true;
          l2 = Float.MIN_VALUE;
        } else if (c != '+') {
          throw NumberFormatException.forInputString(paramString);
        } 
        if (i == 1)
          throw NumberFormatException.forInputString(paramString); 
        b++;
      } 
      long l = l2 / paramInt;
      while (b < i) {
        int j = Character.digit(paramString.charAt(b++), paramInt);
        if (j < 0)
          throw NumberFormatException.forInputString(paramString); 
        if (l1 < l)
          throw NumberFormatException.forInputString(paramString); 
        l1 *= paramInt;
        if (l1 < l2 + j)
          throw NumberFormatException.forInputString(paramString); 
        l1 -= j;
      } 
    } else {
      throw NumberFormatException.forInputString(paramString);
    } 
    return bool ? l1 : -l1;
  }
  
  public static long parseLong(String paramString) throws NumberFormatException { return parseLong(paramString, 10); }
  
  public static long parseUnsignedLong(String paramString, int paramInt) throws NumberFormatException {
    if (paramString == null)
      throw new NumberFormatException("null"); 
    int i = paramString.length();
    if (i > 0) {
      char c = paramString.charAt(0);
      if (c == '-')
        throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", new Object[] { paramString })); 
      if (i <= 12 || (paramInt == 10 && i <= 18))
        return parseLong(paramString, paramInt); 
      long l1 = parseLong(paramString.substring(0, i - 1), paramInt);
      int j = Character.digit(paramString.charAt(i - 1), paramInt);
      if (j < 0)
        throw new NumberFormatException("Bad digit at end of " + paramString); 
      long l2 = l1 * paramInt + j;
      if (compareUnsigned(l2, l1) < 0)
        throw new NumberFormatException(String.format("String value %s exceeds range of unsigned long.", new Object[] { paramString })); 
      return l2;
    } 
    throw NumberFormatException.forInputString(paramString);
  }
  
  public static long parseUnsignedLong(String paramString) throws NumberFormatException { return parseUnsignedLong(paramString, 10); }
  
  public static Long valueOf(String paramString, int paramInt) throws NumberFormatException { return valueOf(parseLong(paramString, paramInt)); }
  
  public static Long valueOf(String paramString) throws NumberFormatException { return valueOf(parseLong(paramString, 10)); }
  
  public static Long valueOf(long paramLong) { return (paramLong >= -128L && paramLong <= 127L) ? LongCache.cache[(int)paramLong + 128] : new Long(paramLong); }
  
  public static Long decode(String paramString) throws NumberFormatException {
    Long long;
    byte b1 = 10;
    byte b2 = 0;
    boolean bool = false;
    if (paramString.length() == 0)
      throw new NumberFormatException("Zero length string"); 
    char c = paramString.charAt(0);
    if (c == '-') {
      bool = true;
      b2++;
    } else if (c == '+') {
      b2++;
    } 
    if (paramString.startsWith("0x", b2) || paramString.startsWith("0X", b2)) {
      b2 += 2;
      b1 = 16;
    } else if (paramString.startsWith("#", b2)) {
      b2++;
      b1 = 16;
    } else if (paramString.startsWith("0", b2) && paramString.length() > 1 + b2) {
      b2++;
      b1 = 8;
    } 
    if (paramString.startsWith("-", b2) || paramString.startsWith("+", b2))
      throw new NumberFormatException("Sign character in wrong position"); 
    try {
      long = bool ? (long = valueOf(paramString.substring(b2), b1)).valueOf(-long.longValue()) : long;
    } catch (NumberFormatException numberFormatException) {
      String str = bool ? ("-" + paramString.substring(b2)) : paramString.substring(b2);
      long = valueOf(str, b1);
    } 
    return long;
  }
  
  public Long(long paramLong) { this.value = paramLong; }
  
  public Long(String paramString) throws NumberFormatException { this.value = parseLong(paramString, 10); }
  
  public byte byteValue() { return (byte)(int)this.value; }
  
  public short shortValue() { return (short)(int)this.value; }
  
  public int intValue() { return (int)this.value; }
  
  public long longValue() { return this.value; }
  
  public float floatValue() { return (float)this.value; }
  
  public double doubleValue() { return this.value; }
  
  public String toString() { return toString(this.value); }
  
  public int hashCode() { return hashCode(this.value); }
  
  public static int hashCode(long paramLong) { return (int)(paramLong ^ paramLong >>> 32); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Long) ? ((this.value == ((Long)paramObject).longValue())) : false; }
  
  public static Long getLong(String paramString) throws NumberFormatException { return getLong(paramString, null); }
  
  public static Long getLong(String paramString, long paramLong) {
    Long long;
    return (long == null) ? (long = getLong(paramString, null)).valueOf(paramLong) : long;
  }
  
  public static Long getLong(String paramString, Long paramLong) {
    String str = null;
    try {
      str = System.getProperty(paramString);
    } catch (IllegalArgumentException|NullPointerException illegalArgumentException) {}
    if (str != null)
      try {
        return decode(str);
      } catch (NumberFormatException numberFormatException) {} 
    return paramLong;
  }
  
  public int compareTo(Long paramLong) { return compare(this.value, paramLong.value); }
  
  public static int compare(long paramLong1, long paramLong2) { return (paramLong1 < paramLong2) ? -1 : ((paramLong1 == paramLong2) ? 0 : 1); }
  
  public static int compareUnsigned(long paramLong1, long paramLong2) { return compare(paramLong1 + Float.MIN_VALUE, paramLong2 + Float.MIN_VALUE); }
  
  public static long divideUnsigned(long paramLong1, long paramLong2) { return (paramLong2 < 0L) ? ((compareUnsigned(paramLong1, paramLong2) < 0) ? 0L : 1L) : ((paramLong1 > 0L) ? (paramLong1 / paramLong2) : toUnsignedBigInteger(paramLong1).divide(toUnsignedBigInteger(paramLong2)).longValue()); }
  
  public static long remainderUnsigned(long paramLong1, long paramLong2) { return (paramLong1 > 0L && paramLong2 > 0L) ? (paramLong1 % paramLong2) : ((compareUnsigned(paramLong1, paramLong2) < 0) ? paramLong1 : toUnsignedBigInteger(paramLong1).remainder(toUnsignedBigInteger(paramLong2)).longValue()); }
  
  public static long highestOneBit(long paramLong) {
    paramLong |= paramLong >> true;
    paramLong |= paramLong >> 2;
    paramLong |= paramLong >> 4;
    paramLong |= paramLong >> 8;
    paramLong |= paramLong >> 16;
    paramLong |= paramLong >> 32;
    return paramLong - (paramLong >>> true);
  }
  
  public static long lowestOneBit(long paramLong) { return paramLong & -paramLong; }
  
  public static int numberOfLeadingZeros(long paramLong) {
    if (paramLong == 0L)
      return 64; 
    null = 1;
    int i = (int)(paramLong >>> 32);
    if (i == 0) {
      null += true;
      i = (int)paramLong;
    } 
    if (i >>> 16 == 0) {
      null += true;
      i <<= 16;
    } 
    if (i >>> 24 == 0) {
      null += true;
      i <<= 8;
    } 
    if (i >>> 28 == 0) {
      null += true;
      i <<= 4;
    } 
    if (i >>> 30 == 0) {
      null += true;
      i <<= 2;
    } 
    return i >>> 31;
  }
  
  public static int numberOfTrailingZeros(long paramLong) {
    int i;
    if (paramLong == 0L)
      return 64; 
    int k = 63;
    int j = (int)paramLong;
    if (j != 0) {
      k -= 32;
      i = j;
    } else {
      i = (int)(paramLong >>> 32);
    } 
    j = i << 16;
    if (j != 0) {
      k -= 16;
      i = j;
    } 
    j = i << 8;
    if (j != 0) {
      k -= 8;
      i = j;
    } 
    j = i << 4;
    if (j != 0) {
      k -= 4;
      i = j;
    } 
    j = i << 2;
    if (j != 0) {
      k -= 2;
      i = j;
    } 
    return k - (i << 1 >>> 31);
  }
  
  public static int bitCount(long paramLong) {
    paramLong -= (paramLong >>> true & 0x5555555555555555L);
    paramLong = (paramLong & 0x3333333333333333L) + (paramLong >>> 2 & 0x3333333333333333L);
    paramLong = paramLong + (paramLong >>> 4) & 0xF0F0F0F0F0F0F0FL;
    paramLong += (paramLong >>> 8);
    paramLong += (paramLong >>> 16);
    paramLong += (paramLong >>> 32);
    return (int)paramLong & 0x7F;
  }
  
  public static long rotateLeft(long paramLong, int paramInt) { return paramLong << paramInt | paramLong >>> -paramInt; }
  
  public static long rotateRight(long paramLong, int paramInt) { return paramLong >>> paramInt | paramLong << -paramInt; }
  
  public static long reverse(long paramLong) {
    paramLong = (paramLong & 0x5555555555555555L) << true | paramLong >>> true & 0x5555555555555555L;
    paramLong = (paramLong & 0x3333333333333333L) << 2 | paramLong >>> 2 & 0x3333333333333333L;
    paramLong = (paramLong & 0xF0F0F0F0F0F0F0FL) << 4 | paramLong >>> 4 & 0xF0F0F0F0F0F0F0FL;
    paramLong = (paramLong & 0xFF00FF00FF00FFL) << 8 | paramLong >>> 8 & 0xFF00FF00FF00FFL;
    return paramLong << 48 | (paramLong & 0xFFFF0000L) << 16 | paramLong >>> 16 & 0xFFFF0000L | paramLong >>> 48;
  }
  
  public static int signum(long paramLong) { return (int)(paramLong >> 63 | -paramLong >>> 63); }
  
  public static long reverseBytes(long paramLong) {
    paramLong = (paramLong & 0xFF00FF00FF00FFL) << 8 | paramLong >>> 8 & 0xFF00FF00FF00FFL;
    return paramLong << 48 | (paramLong & 0xFFFF0000L) << 16 | paramLong >>> 16 & 0xFFFF0000L | paramLong >>> 48;
  }
  
  public static long sum(long paramLong1, long paramLong2) { return paramLong1 + paramLong2; }
  
  public static long max(long paramLong1, long paramLong2) { return Math.max(paramLong1, paramLong2); }
  
  public static long min(long paramLong1, long paramLong2) { return Math.min(paramLong1, paramLong2); }
  
  private static class LongCache {
    static final Long[] cache = new Long[256];
    
    static  {
      for (byte b = 0; b < cache.length; b++)
        cache[b] = new Long((b - '')); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Long.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */