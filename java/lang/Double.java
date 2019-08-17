package java.lang;

import sun.misc.FloatingDecimal;

public final class Double extends Number implements Comparable<Double> {
  public static final double POSITIVE_INFINITY = InfinityD;
  
  public static final double NEGATIVE_INFINITY = -InfinityD;
  
  public static final double NaN = NaND;
  
  public static final double MAX_VALUE = 1.7976931348623157E308D;
  
  public static final double MIN_NORMAL = 2.2250738585072014E-308D;
  
  public static final double MIN_VALUE = 4.9E-324D;
  
  public static final int MAX_EXPONENT = 1023;
  
  public static final int MIN_EXPONENT = -1022;
  
  public static final int SIZE = 64;
  
  public static final int BYTES = 8;
  
  public static final Class<Double> TYPE = Class.getPrimitiveClass("double");
  
  private final double value;
  
  private static final long serialVersionUID = -9172774392245257468L;
  
  public static String toString(double paramDouble) { return FloatingDecimal.toJavaFormatString(paramDouble); }
  
  public static String toHexString(double paramDouble) {
    if (!isFinite(paramDouble))
      return toString(paramDouble); 
    StringBuilder stringBuilder = new StringBuilder(24);
    if (Math.copySign(1.0D, paramDouble) == -1.0D)
      stringBuilder.append("-"); 
    stringBuilder.append("0x");
    paramDouble = Math.abs(paramDouble);
    if (paramDouble == 0.0D) {
      stringBuilder.append("0.0p0");
    } else {
      boolean bool = (paramDouble < 2.2250738585072014E-308D) ? 1 : 0;
      long l = doubleToLongBits(paramDouble) & 0xFFFFFFFFFFFFFL | 0x1000000000000000L;
      stringBuilder.append(bool ? "0." : "1.");
      String str = Long.toHexString(l).substring(3, 16);
      stringBuilder.append(str.equals("0000000000000") ? "0" : str.replaceFirst("0{1,12}$", ""));
      stringBuilder.append('p');
      stringBuilder.append(bool ? -1022 : Math.getExponent(paramDouble));
    } 
    return stringBuilder.toString();
  }
  
  public static Double valueOf(String paramString) throws NumberFormatException { return new Double(parseDouble(paramString)); }
  
  public static Double valueOf(double paramDouble) { return new Double(paramDouble); }
  
  public static double parseDouble(String paramString) throws NumberFormatException { return FloatingDecimal.parseDouble(paramString); }
  
  public static boolean isNaN(double paramDouble) { return (paramDouble != paramDouble); }
  
  public static boolean isInfinite(double paramDouble) { return (paramDouble == Double.POSITIVE_INFINITY || paramDouble == Double.NEGATIVE_INFINITY); }
  
  public static boolean isFinite(double paramDouble) { return (Math.abs(paramDouble) <= Double.MAX_VALUE); }
  
  public Double(double paramDouble) { this.value = paramDouble; }
  
  public Double(String paramString) throws NumberFormatException { this.value = parseDouble(paramString); }
  
  public boolean isNaN() { return isNaN(this.value); }
  
  public boolean isInfinite() { return isInfinite(this.value); }
  
  public String toString() { return toString(this.value); }
  
  public byte byteValue() { return (byte)(int)this.value; }
  
  public short shortValue() { return (short)(int)this.value; }
  
  public int intValue() { return (int)this.value; }
  
  public long longValue() { return (long)this.value; }
  
  public float floatValue() { return (float)this.value; }
  
  public double doubleValue() { return this.value; }
  
  public int hashCode() { return hashCode(this.value); }
  
  public static int hashCode(double paramDouble) {
    long l = doubleToLongBits(paramDouble);
    return (int)(l ^ l >>> 32);
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Double && doubleToLongBits(((Double)paramObject).value) == doubleToLongBits(this.value)); }
  
  public static long doubleToLongBits(double paramDouble) {
    long l = doubleToRawLongBits(paramDouble);
    if ((l & 0x7FF0000000000000L) == 9218868437227405312L && (l & 0xFFFFFFFFFFFFFL) != 0L)
      l = 9221120237041090560L; 
    return l;
  }
  
  public static native long doubleToRawLongBits(double paramDouble);
  
  public static native double longBitsToDouble(long paramLong);
  
  public int compareTo(Double paramDouble) { return compare(this.value, paramDouble.value); }
  
  public static int compare(double paramDouble1, double paramDouble2) {
    if (paramDouble1 < paramDouble2)
      return -1; 
    if (paramDouble1 > paramDouble2)
      return 1; 
    long l1 = doubleToLongBits(paramDouble1);
    long l2 = doubleToLongBits(paramDouble2);
    return (l1 == l2) ? 0 : ((l1 < l2) ? -1 : 1);
  }
  
  public static double sum(double paramDouble1, double paramDouble2) { return paramDouble1 + paramDouble2; }
  
  public static double max(double paramDouble1, double paramDouble2) { return Math.max(paramDouble1, paramDouble2); }
  
  public static double min(double paramDouble1, double paramDouble2) { return Math.min(paramDouble1, paramDouble2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Double.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */