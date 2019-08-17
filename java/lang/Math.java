package java.lang;

import java.util.Random;

public final class Math {
  public static final double E = 2.718281828459045D;
  
  public static final double PI = 3.141592653589793D;
  
  private static long negativeZeroFloatBits = Float.floatToRawIntBits(-0.0F);
  
  private static long negativeZeroDoubleBits = Double.doubleToRawLongBits(-0.0D);
  
  static double twoToTheDoubleScaleUp = powerOfTwoD(512);
  
  static double twoToTheDoubleScaleDown = powerOfTwoD(-512);
  
  public static double sin(double paramDouble) { return StrictMath.sin(paramDouble); }
  
  public static double cos(double paramDouble) { return StrictMath.cos(paramDouble); }
  
  public static double tan(double paramDouble) { return StrictMath.tan(paramDouble); }
  
  public static double asin(double paramDouble) { return StrictMath.asin(paramDouble); }
  
  public static double acos(double paramDouble) { return StrictMath.acos(paramDouble); }
  
  public static double atan(double paramDouble) { return StrictMath.atan(paramDouble); }
  
  public static double toRadians(double paramDouble) { return paramDouble / 180.0D * Math.PI; }
  
  public static double toDegrees(double paramDouble) { return paramDouble * 180.0D / Math.PI; }
  
  public static double exp(double paramDouble) { return StrictMath.exp(paramDouble); }
  
  public static double log(double paramDouble) { return StrictMath.log(paramDouble); }
  
  public static double log10(double paramDouble) { return StrictMath.log10(paramDouble); }
  
  public static double sqrt(double paramDouble) { return StrictMath.sqrt(paramDouble); }
  
  public static double cbrt(double paramDouble) { return StrictMath.cbrt(paramDouble); }
  
  public static double IEEEremainder(double paramDouble1, double paramDouble2) { return StrictMath.IEEEremainder(paramDouble1, paramDouble2); }
  
  public static double ceil(double paramDouble) { return StrictMath.ceil(paramDouble); }
  
  public static double floor(double paramDouble) { return StrictMath.floor(paramDouble); }
  
  public static double rint(double paramDouble) { return StrictMath.rint(paramDouble); }
  
  public static double atan2(double paramDouble1, double paramDouble2) { return StrictMath.atan2(paramDouble1, paramDouble2); }
  
  public static double pow(double paramDouble1, double paramDouble2) { return StrictMath.pow(paramDouble1, paramDouble2); }
  
  public static int round(float paramFloat) {
    int i = Float.floatToRawIntBits(paramFloat);
    int j = (i & 0x7F800000) >> 23;
    int k = 149 - j;
    if ((k & 0xFFFFFFE0) == 0) {
      int m = i & 0x7FFFFF | 0x800000;
      if (i < 0)
        m = -m; 
      return (m >> k) + 1 >> 1;
    } 
    return (int)paramFloat;
  }
  
  public static long round(double paramDouble) {
    long l1 = Double.doubleToRawLongBits(paramDouble);
    long l2 = (l1 & 0x7FF0000000000000L) >> 52;
    long l3 = 1074L - l2;
    if ((l3 & 0xFFFFFFFFFFFFFFC0L) == 0L) {
      long l = l1 & 0xFFFFFFFFFFFFFL | 0x10000000000000L;
      if (l1 < 0L)
        l = -l; 
      return (l >> (int)l3) + 1L >> true;
    } 
    return (long)paramDouble;
  }
  
  public static double random() { return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble(); }
  
  public static int addExact(int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    if (((paramInt1 ^ i) & (paramInt2 ^ i)) < 0)
      throw new ArithmeticException("integer overflow"); 
    return i;
  }
  
  public static long addExact(long paramLong1, long paramLong2) {
    long l = paramLong1 + paramLong2;
    if (((paramLong1 ^ l) & (paramLong2 ^ l)) < 0L)
      throw new ArithmeticException("long overflow"); 
    return l;
  }
  
  public static int subtractExact(int paramInt1, int paramInt2) {
    int i = paramInt1 - paramInt2;
    if (((paramInt1 ^ paramInt2) & (paramInt1 ^ i)) < 0)
      throw new ArithmeticException("integer overflow"); 
    return i;
  }
  
  public static long subtractExact(long paramLong1, long paramLong2) {
    long l = paramLong1 - paramLong2;
    if (((paramLong1 ^ paramLong2) & (paramLong1 ^ l)) < 0L)
      throw new ArithmeticException("long overflow"); 
    return l;
  }
  
  public static int multiplyExact(int paramInt1, int paramInt2) {
    long l = paramInt1 * paramInt2;
    if ((int)l != l)
      throw new ArithmeticException("integer overflow"); 
    return (int)l;
  }
  
  public static long multiplyExact(long paramLong1, long paramLong2) {
    long l1 = paramLong1 * paramLong2;
    long l2 = abs(paramLong1);
    long l3 = abs(paramLong2);
    if ((l2 | l3) >>> 31 != 0L && ((paramLong2 != 0L && l1 / paramLong2 != paramLong1) || (paramLong1 == Float.MIN_VALUE && paramLong2 == -1L)))
      throw new ArithmeticException("long overflow"); 
    return l1;
  }
  
  public static int incrementExact(int paramInt) {
    if (paramInt == Integer.MAX_VALUE)
      throw new ArithmeticException("integer overflow"); 
    return paramInt + 1;
  }
  
  public static long incrementExact(long paramLong) {
    if (paramLong == Float.MAX_VALUE)
      throw new ArithmeticException("long overflow"); 
    return paramLong + 1L;
  }
  
  public static int decrementExact(int paramInt) {
    if (paramInt == Integer.MIN_VALUE)
      throw new ArithmeticException("integer overflow"); 
    return paramInt - 1;
  }
  
  public static long decrementExact(long paramLong) {
    if (paramLong == Float.MIN_VALUE)
      throw new ArithmeticException("long overflow"); 
    return paramLong - 1L;
  }
  
  public static int negateExact(int paramInt) {
    if (paramInt == Integer.MIN_VALUE)
      throw new ArithmeticException("integer overflow"); 
    return -paramInt;
  }
  
  public static long negateExact(long paramLong) {
    if (paramLong == Float.MIN_VALUE)
      throw new ArithmeticException("long overflow"); 
    return -paramLong;
  }
  
  public static int toIntExact(long paramLong) {
    if ((int)paramLong != paramLong)
      throw new ArithmeticException("integer overflow"); 
    return (int)paramLong;
  }
  
  public static int floorDiv(int paramInt1, int paramInt2) {
    int i = paramInt1 / paramInt2;
    if ((paramInt1 ^ paramInt2) < 0 && i * paramInt2 != paramInt1)
      i--; 
    return i;
  }
  
  public static long floorDiv(long paramLong1, long paramLong2) {
    long l = paramLong1 / paramLong2;
    if ((paramLong1 ^ paramLong2) < 0L && l * paramLong2 != paramLong1)
      l--; 
    return l;
  }
  
  public static int floorMod(int paramInt1, int paramInt2) { return paramInt1 - floorDiv(paramInt1, paramInt2) * paramInt2; }
  
  public static long floorMod(long paramLong1, long paramLong2) { return paramLong1 - floorDiv(paramLong1, paramLong2) * paramLong2; }
  
  public static int abs(int paramInt) { return (paramInt < 0) ? -paramInt : paramInt; }
  
  public static long abs(long paramLong) { return (paramLong < 0L) ? -paramLong : paramLong; }
  
  public static float abs(float paramFloat) { return (paramFloat <= 0.0F) ? (0.0F - paramFloat) : paramFloat; }
  
  public static double abs(double paramDouble) { return (paramDouble <= 0.0D) ? (0.0D - paramDouble) : paramDouble; }
  
  public static int max(int paramInt1, int paramInt2) { return (paramInt1 >= paramInt2) ? paramInt1 : paramInt2; }
  
  public static long max(long paramLong1, long paramLong2) { return (paramLong1 >= paramLong2) ? paramLong1 : paramLong2; }
  
  public static float max(float paramFloat1, float paramFloat2) { return (paramFloat1 != paramFloat1) ? paramFloat1 : ((paramFloat1 == 0.0F && paramFloat2 == 0.0F && Float.floatToRawIntBits(paramFloat1) == negativeZeroFloatBits) ? paramFloat2 : ((paramFloat1 >= paramFloat2) ? paramFloat1 : paramFloat2)); }
  
  public static double max(double paramDouble1, double paramDouble2) { return (paramDouble1 != paramDouble1) ? paramDouble1 : ((paramDouble1 == 0.0D && paramDouble2 == 0.0D && Double.doubleToRawLongBits(paramDouble1) == negativeZeroDoubleBits) ? paramDouble2 : ((paramDouble1 >= paramDouble2) ? paramDouble1 : paramDouble2)); }
  
  public static int min(int paramInt1, int paramInt2) { return (paramInt1 <= paramInt2) ? paramInt1 : paramInt2; }
  
  public static long min(long paramLong1, long paramLong2) { return (paramLong1 <= paramLong2) ? paramLong1 : paramLong2; }
  
  public static float min(float paramFloat1, float paramFloat2) { return (paramFloat1 != paramFloat1) ? paramFloat1 : ((paramFloat1 == 0.0F && paramFloat2 == 0.0F && Float.floatToRawIntBits(paramFloat2) == negativeZeroFloatBits) ? paramFloat2 : ((paramFloat1 <= paramFloat2) ? paramFloat1 : paramFloat2)); }
  
  public static double min(double paramDouble1, double paramDouble2) { return (paramDouble1 != paramDouble1) ? paramDouble1 : ((paramDouble1 == 0.0D && paramDouble2 == 0.0D && Double.doubleToRawLongBits(paramDouble2) == negativeZeroDoubleBits) ? paramDouble2 : ((paramDouble1 <= paramDouble2) ? paramDouble1 : paramDouble2)); }
  
  public static double ulp(double paramDouble) {
    int i = getExponent(paramDouble);
    switch (i) {
      case 1024:
        return abs(paramDouble);
      case -1023:
        return Double.MIN_VALUE;
    } 
    assert i <= 1023 && i >= -1022;
    i -= 52;
    return (i >= -1022) ? powerOfTwoD(i) : Double.longBitsToDouble(1L << i - -1074);
  }
  
  public static float ulp(float paramFloat) {
    int i = getExponent(paramFloat);
    switch (i) {
      case 128:
        return abs(paramFloat);
      case -127:
        return Float.MIN_VALUE;
    } 
    assert i <= 127 && i >= -126;
    i -= 23;
    return (i >= -126) ? powerOfTwoF(i) : Float.intBitsToFloat(1 << i - -149);
  }
  
  public static double signum(double paramDouble) { return (paramDouble == 0.0D || Double.isNaN(paramDouble)) ? paramDouble : copySign(1.0D, paramDouble); }
  
  public static float signum(float paramFloat) { return (paramFloat == 0.0F || Float.isNaN(paramFloat)) ? paramFloat : copySign(1.0F, paramFloat); }
  
  public static double sinh(double paramDouble) { return StrictMath.sinh(paramDouble); }
  
  public static double cosh(double paramDouble) { return StrictMath.cosh(paramDouble); }
  
  public static double tanh(double paramDouble) { return StrictMath.tanh(paramDouble); }
  
  public static double hypot(double paramDouble1, double paramDouble2) { return StrictMath.hypot(paramDouble1, paramDouble2); }
  
  public static double expm1(double paramDouble) { return StrictMath.expm1(paramDouble); }
  
  public static double log1p(double paramDouble) { return StrictMath.log1p(paramDouble); }
  
  public static double copySign(double paramDouble1, double paramDouble2) { return Double.longBitsToDouble(Double.doubleToRawLongBits(paramDouble2) & Float.MIN_VALUE | Double.doubleToRawLongBits(paramDouble1) & Float.MAX_VALUE); }
  
  public static float copySign(float paramFloat1, float paramFloat2) { return Float.intBitsToFloat(Float.floatToRawIntBits(paramFloat2) & 0x80000000 | Float.floatToRawIntBits(paramFloat1) & 0x7FFFFFFF); }
  
  public static int getExponent(float paramFloat) { return ((Float.floatToRawIntBits(paramFloat) & 0x7F800000) >> 23) - 127; }
  
  public static int getExponent(double paramDouble) { return (int)(((Double.doubleToRawLongBits(paramDouble) & 0x7FF0000000000000L) >> 52) - 1023L); }
  
  public static double nextAfter(double paramDouble1, double paramDouble2) {
    if (Double.isNaN(paramDouble1) || Double.isNaN(paramDouble2))
      return paramDouble1 + paramDouble2; 
    if (paramDouble1 == paramDouble2)
      return paramDouble2; 
    long l = Double.doubleToRawLongBits(paramDouble1 + 0.0D);
    if (paramDouble2 > paramDouble1) {
      l += ((l >= 0L) ? 1L : -1L);
    } else {
      assert paramDouble2 < paramDouble1;
      if (l > 0L) {
        l--;
      } else if (l < 0L) {
        l++;
      } else {
        l = -9223372036854775807L;
      } 
    } 
    return Double.longBitsToDouble(l);
  }
  
  public static float nextAfter(float paramFloat, double paramDouble) {
    if (Float.isNaN(paramFloat) || Double.isNaN(paramDouble))
      return paramFloat + (float)paramDouble; 
    if (paramFloat == paramDouble)
      return (float)paramDouble; 
    int i = Float.floatToRawIntBits(paramFloat + 0.0F);
    if (paramDouble > paramFloat) {
      i += ((i >= 0) ? 1 : -1);
    } else {
      assert paramDouble < paramFloat;
      if (i > 0) {
        i--;
      } else if (i < 0) {
        i++;
      } else {
        i = -2147483647;
      } 
    } 
    return Float.intBitsToFloat(i);
  }
  
  public static double nextUp(double paramDouble) {
    if (Double.isNaN(paramDouble) || paramDouble == Double.POSITIVE_INFINITY)
      return paramDouble; 
    paramDouble += 0.0D;
    return Double.longBitsToDouble(Double.doubleToRawLongBits(paramDouble) + ((paramDouble >= 0.0D) ? 1L : -1L));
  }
  
  public static float nextUp(float paramFloat) {
    if (Float.isNaN(paramFloat) || paramFloat == Float.POSITIVE_INFINITY)
      return paramFloat; 
    paramFloat += 0.0F;
    return Float.intBitsToFloat(Float.floatToRawIntBits(paramFloat) + ((paramFloat >= 0.0F) ? 1 : -1));
  }
  
  public static double nextDown(double paramDouble) { return (Double.isNaN(paramDouble) || paramDouble == Double.NEGATIVE_INFINITY) ? paramDouble : ((paramDouble == 0.0D) ? -4.9E-324D : Double.longBitsToDouble(Double.doubleToRawLongBits(paramDouble) + ((paramDouble > 0.0D) ? -1L : 1L))); }
  
  public static float nextDown(float paramFloat) { return (Float.isNaN(paramFloat) || paramFloat == Float.NEGATIVE_INFINITY) ? paramFloat : ((paramFloat == 0.0F) ? -1.4E-45F : Float.intBitsToFloat(Float.floatToRawIntBits(paramFloat) + ((paramFloat > 0.0F) ? -1 : 1))); }
  
  public static double scalb(double paramDouble, int paramInt) {
    int i = 0;
    int j = 0;
    double d = NaND;
    if (paramInt < 0) {
      paramInt = max(paramInt, -2099);
      j = -512;
      d = twoToTheDoubleScaleDown;
    } else {
      paramInt = min(paramInt, 2099);
      j = 512;
      d = twoToTheDoubleScaleUp;
    } 
    int k = paramInt >> 8 >>> 23;
    i = (paramInt + k & 0x1FF) - k;
    paramDouble *= powerOfTwoD(i);
    for (paramInt -= i; paramInt != 0; paramInt -= j)
      paramDouble *= d; 
    return paramDouble;
  }
  
  public static float scalb(float paramFloat, int paramInt) {
    paramInt = max(min(paramInt, 278), -278);
    return (float)(paramFloat * powerOfTwoD(paramInt));
  }
  
  static double powerOfTwoD(int paramInt) {
    assert paramInt >= -1022 && paramInt <= 1023;
    return Double.longBitsToDouble(paramInt + 1023L << 52 & 0x7FF0000000000000L);
  }
  
  static float powerOfTwoF(int paramInt) {
    assert paramInt >= -126 && paramInt <= 127;
    return Float.intBitsToFloat(paramInt + 127 << 23 & 0x7F800000);
  }
  
  private static final class RandomNumberGeneratorHolder {
    static final Random randomNumberGenerator = new Random();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Math.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */