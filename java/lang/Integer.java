package java.lang;

import sun.misc.VM;

public final class Integer extends Number implements Comparable<Integer> {
  public static final int MIN_VALUE = -2147483648;
  
  public static final int MAX_VALUE = 2147483647;
  
  public static final Class<Integer> TYPE = Class.getPrimitiveClass("int");
  
  static final char[] digits = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
      'u', 'v', 'w', 'x', 'y', 'z' };
  
  static final char[] DigitTens = { 
      '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 
      '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', 
      '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', 
      '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', 
      '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', 
      '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', 
      '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', 
      '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', 
      '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', 
      '9', '9', '9', '9', '9', '9', '9', '9', '9', '9' };
  
  static final char[] DigitOnes = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  
  static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
  
  private final int value;
  
  public static final int SIZE = 32;
  
  public static final int BYTES = 4;
  
  private static final long serialVersionUID = 1360826667806852920L;
  
  public static String toString(int paramInt1, int paramInt2) {
    if (paramInt2 < 2 || paramInt2 > 36)
      paramInt2 = 10; 
    if (paramInt2 == 10)
      return toString(paramInt1); 
    char[] arrayOfChar = new char[33];
    boolean bool = (paramInt1 < 0) ? 1 : 0;
    byte b = 32;
    if (!bool)
      paramInt1 = -paramInt1; 
    while (paramInt1 <= -paramInt2) {
      arrayOfChar[b--] = digits[-(paramInt1 % paramInt2)];
      paramInt1 /= paramInt2;
    } 
    arrayOfChar[b] = digits[-paramInt1];
    if (bool)
      arrayOfChar[--b] = '-'; 
    return new String(arrayOfChar, b, 33 - b);
  }
  
  public static String toUnsignedString(int paramInt1, int paramInt2) { return Long.toUnsignedString(toUnsignedLong(paramInt1), paramInt2); }
  
  public static String toHexString(int paramInt) { return toUnsignedString0(paramInt, 4); }
  
  public static String toOctalString(int paramInt) { return toUnsignedString0(paramInt, 3); }
  
  public static String toBinaryString(int paramInt) { return toUnsignedString0(paramInt, 1); }
  
  private static String toUnsignedString0(int paramInt1, int paramInt2) {
    int i = 32 - numberOfLeadingZeros(paramInt1);
    int j = Math.max((i + paramInt2 - 1) / paramInt2, 1);
    char[] arrayOfChar = new char[j];
    formatUnsignedInt(paramInt1, paramInt2, arrayOfChar, 0, j);
    return new String(arrayOfChar, true);
  }
  
  static int formatUnsignedInt(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4) {
    int i = paramInt4;
    int j = 1 << paramInt2;
    int k = j - 1;
    do {
      paramArrayOfChar[paramInt3 + --i] = digits[paramInt1 & k];
      paramInt1 >>>= paramInt2;
    } while (paramInt1 != 0 && i > 0);
    return i;
  }
  
  public static String toString(int paramInt) {
    if (paramInt == Integer.MIN_VALUE)
      return "-2147483648"; 
    int i = (paramInt < 0) ? (stringSize(-paramInt) + 1) : stringSize(paramInt);
    char[] arrayOfChar = new char[i];
    getChars(paramInt, i, arrayOfChar);
    return new String(arrayOfChar, true);
  }
  
  public static String toUnsignedString(int paramInt) { return Long.toString(toUnsignedLong(paramInt)); }
  
  static void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar) {
    int i = paramInt2;
    byte b = 0;
    if (paramInt1 < 0) {
      b = 45;
      paramInt1 = -paramInt1;
    } 
    while (paramInt1 >= 65536) {
      int j = paramInt1 / 100;
      int k = paramInt1 - (j << 6) + (j << 5) + (j << 2);
      paramInt1 = j;
      paramArrayOfChar[--i] = DigitOnes[k];
      paramArrayOfChar[--i] = DigitTens[k];
    } 
    do {
      int j = paramInt1 * 52429 >>> 19;
      int k = paramInt1 - (j << 3) + (j << 1);
      paramArrayOfChar[--i] = digits[k];
      paramInt1 = j;
    } while (paramInt1 != 0);
    if (b != 0)
      paramArrayOfChar[--i] = b; 
  }
  
  static int stringSize(int paramInt) {
    for (byte b = 0;; b++) {
      if (paramInt <= sizeTable[b])
        return b + true; 
    } 
  }
  
  public static int parseInt(String paramString, int paramInt) throws NumberFormatException {
    if (paramString == null)
      throw new NumberFormatException("null"); 
    if (paramInt < 2)
      throw new NumberFormatException("radix " + paramInt + " less than Character.MIN_RADIX"); 
    if (paramInt > 36)
      throw new NumberFormatException("radix " + paramInt + " greater than Character.MAX_RADIX"); 
    int i = 0;
    boolean bool = false;
    byte b = 0;
    int j = paramString.length();
    int k = -2147483647;
    if (j > 0) {
      char c = paramString.charAt(0);
      if (c < '0') {
        if (c == '-') {
          bool = true;
          k = Integer.MIN_VALUE;
        } else if (c != '+') {
          throw NumberFormatException.forInputString(paramString);
        } 
        if (j == 1)
          throw NumberFormatException.forInputString(paramString); 
        b++;
      } 
      int m = k / paramInt;
      while (b < j) {
        int n = Character.digit(paramString.charAt(b++), paramInt);
        if (n < 0)
          throw NumberFormatException.forInputString(paramString); 
        if (i < m)
          throw NumberFormatException.forInputString(paramString); 
        i *= paramInt;
        if (i < k + n)
          throw NumberFormatException.forInputString(paramString); 
        i -= n;
      } 
    } else {
      throw NumberFormatException.forInputString(paramString);
    } 
    return bool ? i : -i;
  }
  
  public static int parseInt(String paramString) throws NumberFormatException { return parseInt(paramString, 10); }
  
  public static int parseUnsignedInt(String paramString, int paramInt) throws NumberFormatException {
    if (paramString == null)
      throw new NumberFormatException("null"); 
    int i = paramString.length();
    if (i > 0) {
      char c = paramString.charAt(0);
      if (c == '-')
        throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", new Object[] { paramString })); 
      if (i <= 5 || (paramInt == 10 && i <= 9))
        return parseInt(paramString, paramInt); 
      long l = Long.parseLong(paramString, paramInt);
      if ((l & 0xFFFFFFFF00000000L) == 0L)
        return (int)l; 
      throw new NumberFormatException(String.format("String value %s exceeds range of unsigned int.", new Object[] { paramString }));
    } 
    throw NumberFormatException.forInputString(paramString);
  }
  
  public static int parseUnsignedInt(String paramString) throws NumberFormatException { return parseUnsignedInt(paramString, 10); }
  
  public static Integer valueOf(String paramString, int paramInt) throws NumberFormatException { return valueOf(parseInt(paramString, paramInt)); }
  
  public static Integer valueOf(String paramString) throws NumberFormatException { return valueOf(parseInt(paramString, 10)); }
  
  public static Integer valueOf(int paramInt) { return (paramInt >= -128 && paramInt <= IntegerCache.high) ? IntegerCache.cache[paramInt + 128] : new Integer(paramInt); }
  
  public Integer(int paramInt) { this.value = paramInt; }
  
  public Integer(String paramString) throws NumberFormatException { this.value = parseInt(paramString, 10); }
  
  public byte byteValue() { return (byte)this.value; }
  
  public short shortValue() { return (short)this.value; }
  
  public int intValue() { return this.value; }
  
  public long longValue() { return this.value; }
  
  public float floatValue() { return this.value; }
  
  public double doubleValue() { return this.value; }
  
  public String toString() { return toString(this.value); }
  
  public int hashCode() { return hashCode(this.value); }
  
  public static int hashCode(int paramInt) { return paramInt; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Integer) ? ((this.value == ((Integer)paramObject).intValue())) : false; }
  
  public static Integer getInteger(String paramString) throws NumberFormatException { return getInteger(paramString, null); }
  
  public static Integer getInteger(String paramString, int paramInt) throws NumberFormatException {
    Integer integer;
    return (integer == null) ? (integer = getInteger(paramString, null)).valueOf(paramInt) : integer;
  }
  
  public static Integer getInteger(String paramString, Integer paramInteger) {
    String str = null;
    try {
      str = System.getProperty(paramString);
    } catch (IllegalArgumentException|NullPointerException illegalArgumentException) {}
    if (str != null)
      try {
        return decode(str);
      } catch (NumberFormatException numberFormatException) {} 
    return paramInteger;
  }
  
  public static Integer decode(String paramString) throws NumberFormatException {
    Integer integer;
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
      integer = bool ? (integer = valueOf(paramString.substring(b2), b1)).valueOf(-integer.intValue()) : integer;
    } catch (NumberFormatException numberFormatException) {
      String str = bool ? ("-" + paramString.substring(b2)) : paramString.substring(b2);
      integer = valueOf(str, b1);
    } 
    return integer;
  }
  
  public int compareTo(Integer paramInteger) { return compare(this.value, paramInteger.value); }
  
  public static int compare(int paramInt1, int paramInt2) { return (paramInt1 < paramInt2) ? -1 : ((paramInt1 == paramInt2) ? 0 : 1); }
  
  public static int compareUnsigned(int paramInt1, int paramInt2) { return compare(paramInt1 + Integer.MIN_VALUE, paramInt2 + Integer.MIN_VALUE); }
  
  public static long toUnsignedLong(int paramInt) { return paramInt & 0xFFFFFFFFL; }
  
  public static int divideUnsigned(int paramInt1, int paramInt2) { return (int)(toUnsignedLong(paramInt1) / toUnsignedLong(paramInt2)); }
  
  public static int remainderUnsigned(int paramInt1, int paramInt2) { return (int)(toUnsignedLong(paramInt1) % toUnsignedLong(paramInt2)); }
  
  public static int highestOneBit(int paramInt) {
    paramInt |= paramInt >> 1;
    paramInt |= paramInt >> 2;
    paramInt |= paramInt >> 4;
    paramInt |= paramInt >> 8;
    paramInt |= paramInt >> 16;
    return paramInt - (paramInt >>> 1);
  }
  
  public static int lowestOneBit(int paramInt) { return paramInt & -paramInt; }
  
  public static int numberOfLeadingZeros(int paramInt) {
    if (paramInt == 0)
      return 32; 
    null = 1;
    if (paramInt >>> 16 == 0) {
      null += true;
      paramInt <<= 16;
    } 
    if (paramInt >>> 24 == 0) {
      null += true;
      paramInt <<= 8;
    } 
    if (paramInt >>> 28 == 0) {
      null += true;
      paramInt <<= 4;
    } 
    if (paramInt >>> 30 == 0) {
      null += true;
      paramInt <<= 2;
    } 
    return paramInt >>> 31;
  }
  
  public static int numberOfTrailingZeros(int paramInt) {
    if (paramInt == 0)
      return 32; 
    int j = 31;
    int i = paramInt << 16;
    if (i != 0) {
      j -= 16;
      paramInt = i;
    } 
    i = paramInt << 8;
    if (i != 0) {
      j -= 8;
      paramInt = i;
    } 
    i = paramInt << 4;
    if (i != 0) {
      j -= 4;
      paramInt = i;
    } 
    i = paramInt << 2;
    if (i != 0) {
      j -= 2;
      paramInt = i;
    } 
    return j - (paramInt << 1 >>> 31);
  }
  
  public static int bitCount(int paramInt) {
    paramInt -= (paramInt >>> 1 & 0x55555555);
    paramInt = (paramInt & 0x33333333) + (paramInt >>> 2 & 0x33333333);
    paramInt = paramInt + (paramInt >>> 4) & 0xF0F0F0F;
    paramInt += (paramInt >>> 8);
    paramInt += (paramInt >>> 16);
    return paramInt & 0x3F;
  }
  
  public static int rotateLeft(int paramInt1, int paramInt2) { return paramInt1 << paramInt2 | paramInt1 >>> -paramInt2; }
  
  public static int rotateRight(int paramInt1, int paramInt2) { return paramInt1 >>> paramInt2 | paramInt1 << -paramInt2; }
  
  public static int reverse(int paramInt) {
    paramInt = (paramInt & 0x55555555) << 1 | paramInt >>> 1 & 0x55555555;
    paramInt = (paramInt & 0x33333333) << 2 | paramInt >>> 2 & 0x33333333;
    paramInt = (paramInt & 0xF0F0F0F) << 4 | paramInt >>> 4 & 0xF0F0F0F;
    return paramInt << 24 | (paramInt & 0xFF00) << 8 | paramInt >>> 8 & 0xFF00 | paramInt >>> 24;
  }
  
  public static int signum(int paramInt) { return paramInt >> 31 | -paramInt >>> 31; }
  
  public static int reverseBytes(int paramInt) { return paramInt >>> 24 | paramInt >> 8 & 0xFF00 | paramInt << 8 & 0xFF0000 | paramInt << 24; }
  
  public static int sum(int paramInt1, int paramInt2) { return paramInt1 + paramInt2; }
  
  public static int max(int paramInt1, int paramInt2) { return Math.max(paramInt1, paramInt2); }
  
  public static int min(int paramInt1, int paramInt2) { return Math.min(paramInt1, paramInt2); }
  
  private static class IntegerCache {
    static final int low = -128;
    
    static final int high;
    
    static final Integer[] cache;
    
    static  {
      int i = 127;
      String str = VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
      if (str != null)
        try {
          int j = Integer.parseInt(str);
          j = Math.max(j, 127);
          i = Math.min(j, 2147483518);
        } catch (NumberFormatException numberFormatException) {} 
      high = i;
      cache = new Integer[high - -128 + 1];
      byte b = -128;
      for (byte b1 = 0; b1 < cache.length; b1++)
        cache[b1] = new Integer(b++); 
      assert high >= 127;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Integer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */