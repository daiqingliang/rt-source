package java.lang;

import java.util.Random;

public final class StrictMath {
  public static final double E = 2.718281828459045D;
  
  public static final double PI = 3.141592653589793D;
  
  public static native double sin(double paramDouble);
  
  public static native double cos(double paramDouble);
  
  public static native double tan(double paramDouble);
  
  public static native double asin(double paramDouble);
  
  public static native double acos(double paramDouble);
  
  public static native double atan(double paramDouble);
  
  public static double toRadians(double paramDouble) { return paramDouble / 180.0D * Math.PI; }
  
  public static double toDegrees(double paramDouble) { return paramDouble * 180.0D / Math.PI; }
  
  public static native double exp(double paramDouble);
  
  public static native double log(double paramDouble);
  
  public static native double log10(double paramDouble);
  
  public static native double sqrt(double paramDouble);
  
  public static native double cbrt(double paramDouble);
  
  public static native double IEEEremainder(double paramDouble1, double paramDouble2);
  
  public static double ceil(double paramDouble) { return floorOrCeil(paramDouble, -0.0D, 1.0D, 1.0D); }
  
  public static double floor(double paramDouble) { return floorOrCeil(paramDouble, -1.0D, 0.0D, -1.0D); }
  
  private static double floorOrCeil(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    int i = Math.getExponent(paramDouble1);
    if (i < 0)
      return (paramDouble1 == 0.0D) ? paramDouble1 : ((paramDouble1 < 0.0D) ? paramDouble2 : paramDouble3); 
    if (i >= 52)
      return paramDouble1; 
    assert i >= 0 && i <= 51;
    long l1 = Double.doubleToRawLongBits(paramDouble1);
    long l2 = 4503599627370495L >> i;
    if ((l2 & l1) == 0L)
      return paramDouble1; 
    double d = Double.longBitsToDouble(l1 & (l2 ^ 0xFFFFFFFFFFFFFFFFL));
    if (paramDouble4 * paramDouble1 > 0.0D)
      d += paramDouble4; 
    return d;
  }
  
  public static double rint(double paramDouble) {
    double d1 = 4.503599627370496E15D;
    double d2 = Math.copySign(1.0D, paramDouble);
    paramDouble = Math.abs(paramDouble);
    if (paramDouble < d1)
      paramDouble = d1 + paramDouble - d1; 
    return d2 * paramDouble;
  }
  
  public static native double atan2(double paramDouble1, double paramDouble2);
  
  public static native double pow(double paramDouble1, double paramDouble2);
  
  public static int round(float paramFloat) { return Math.round(paramFloat); }
  
  public static long round(double paramDouble) { return Math.round(paramDouble); }
  
  public static double random() { return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble(); }
  
  public static int addExact(int paramInt1, int paramInt2) { return Math.addExact(paramInt1, paramInt2); }
  
  public static long addExact(long paramLong1, long paramLong2) { return Math.addExact(paramLong1, paramLong2); }
  
  public static int subtractExact(int paramInt1, int paramInt2) { return Math.subtractExact(paramInt1, paramInt2); }
  
  public static long subtractExact(long paramLong1, long paramLong2) { return Math.subtractExact(paramLong1, paramLong2); }
  
  public static int multiplyExact(int paramInt1, int paramInt2) { return Math.multiplyExact(paramInt1, paramInt2); }
  
  public static long multiplyExact(long paramLong1, long paramLong2) { return Math.multiplyExact(paramLong1, paramLong2); }
  
  public static int toIntExact(long paramLong) { return Math.toIntExact(paramLong); }
  
  public static int floorDiv(int paramInt1, int paramInt2) { return Math.floorDiv(paramInt1, paramInt2); }
  
  public static long floorDiv(long paramLong1, long paramLong2) { return Math.floorDiv(paramLong1, paramLong2); }
  
  public static int floorMod(int paramInt1, int paramInt2) { return Math.floorMod(paramInt1, paramInt2); }
  
  public static long floorMod(long paramLong1, long paramLong2) { return Math.floorMod(paramLong1, paramLong2); }
  
  public static int abs(int paramInt) { return Math.abs(paramInt); }
  
  public static long abs(long paramLong) { return Math.abs(paramLong); }
  
  public static float abs(float paramFloat) { return Math.abs(paramFloat); }
  
  public static double abs(double paramDouble) { return Math.abs(paramDouble); }
  
  public static int max(int paramInt1, int paramInt2) { return Math.max(paramInt1, paramInt2); }
  
  public static long max(long paramLong1, long paramLong2) { return Math.max(paramLong1, paramLong2); }
  
  public static float max(float paramFloat1, float paramFloat2) { return Math.max(paramFloat1, paramFloat2); }
  
  public static double max(double paramDouble1, double paramDouble2) { return Math.max(paramDouble1, paramDouble2); }
  
  public static int min(int paramInt1, int paramInt2) { return Math.min(paramInt1, paramInt2); }
  
  public static long min(long paramLong1, long paramLong2) { return Math.min(paramLong1, paramLong2); }
  
  public static float min(float paramFloat1, float paramFloat2) { return Math.min(paramFloat1, paramFloat2); }
  
  public static double min(double paramDouble1, double paramDouble2) { return Math.min(paramDouble1, paramDouble2); }
  
  public static double ulp(double paramDouble) { return Math.ulp(paramDouble); }
  
  public static float ulp(float paramFloat) { return Math.ulp(paramFloat); }
  
  public static double signum(double paramDouble) { return Math.signum(paramDouble); }
  
  public static float signum(float paramFloat) { return Math.signum(paramFloat); }
  
  public static native double sinh(double paramDouble);
  
  public static native double cosh(double paramDouble);
  
  public static native double tanh(double paramDouble);
  
  public static native double hypot(double paramDouble1, double paramDouble2);
  
  public static native double expm1(double paramDouble);
  
  public static native double log1p(double paramDouble);
  
  public static double copySign(double paramDouble1, double paramDouble2) { return Math.copySign(paramDouble1, Double.isNaN(paramDouble2) ? 1.0D : paramDouble2); }
  
  public static float copySign(float paramFloat1, float paramFloat2) { return Math.copySign(paramFloat1, Float.isNaN(paramFloat2) ? 1.0F : paramFloat2); }
  
  public static int getExponent(float paramFloat) { return Math.getExponent(paramFloat); }
  
  public static int getExponent(double paramDouble) { return Math.getExponent(paramDouble); }
  
  public static double nextAfter(double paramDouble1, double paramDouble2) { return Math.nextAfter(paramDouble1, paramDouble2); }
  
  public static float nextAfter(float paramFloat, double paramDouble) { return Math.nextAfter(paramFloat, paramDouble); }
  
  public static double nextUp(double paramDouble) { return Math.nextUp(paramDouble); }
  
  public static float nextUp(float paramFloat) { return Math.nextUp(paramFloat); }
  
  public static double nextDown(double paramDouble) { return Math.nextDown(paramDouble); }
  
  public static float nextDown(float paramFloat) { return Math.nextDown(paramFloat); }
  
  public static double scalb(double paramDouble, int paramInt) { return Math.scalb(paramDouble, paramInt); }
  
  public static float scalb(float paramFloat, int paramInt) { return Math.scalb(paramFloat, paramInt); }
  
  private static final class RandomNumberGeneratorHolder {
    static final Random randomNumberGenerator = new Random();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\StrictMath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */