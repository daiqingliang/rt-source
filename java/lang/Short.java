package java.lang;

public final class Short extends Number implements Comparable<Short> {
  public static final short MIN_VALUE = -32768;
  
  public static final short MAX_VALUE = 32767;
  
  public static final Class<Short> TYPE = Class.getPrimitiveClass("short");
  
  private final short value;
  
  public static final int SIZE = 16;
  
  public static final int BYTES = 2;
  
  private static final long serialVersionUID = 7515723908773894738L;
  
  public static String toString(short paramShort) { return Integer.toString(paramShort, 10); }
  
  public static short parseShort(String paramString, int paramInt) throws NumberFormatException {
    int i = Integer.parseInt(paramString, paramInt);
    if (i < -32768 || i > 32767)
      throw new NumberFormatException("Value out of range. Value:\"" + paramString + "\" Radix:" + paramInt); 
    return (short)i;
  }
  
  public static short parseShort(String paramString) throws NumberFormatException { return parseShort(paramString, 10); }
  
  public static Short valueOf(String paramString, int paramInt) throws NumberFormatException { return valueOf(parseShort(paramString, paramInt)); }
  
  public static Short valueOf(String paramString) throws NumberFormatException { return valueOf(paramString, 10); }
  
  public static Short valueOf(short paramShort) {
    short s = paramShort;
    return (s >= -128 && s <= 127) ? ShortCache.cache[s + 128] : new Short(paramShort);
  }
  
  public static Short decode(String paramString) throws NumberFormatException {
    int i = Integer.decode(paramString).intValue();
    if (i < -32768 || i > 32767)
      throw new NumberFormatException("Value " + i + " out of range from input " + paramString); 
    return valueOf((short)i);
  }
  
  public Short(short paramShort) { this.value = paramShort; }
  
  public Short(String paramString) throws NumberFormatException { this.value = parseShort(paramString, 10); }
  
  public byte byteValue() { return (byte)this.value; }
  
  public short shortValue() { return this.value; }
  
  public int intValue() { return this.value; }
  
  public long longValue() { return this.value; }
  
  public float floatValue() { return this.value; }
  
  public double doubleValue() { return this.value; }
  
  public String toString() { return Integer.toString(this.value); }
  
  public int hashCode() { return hashCode(this.value); }
  
  public static int hashCode(short paramShort) { return paramShort; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Short) ? ((this.value == ((Short)paramObject).shortValue())) : false; }
  
  public int compareTo(Short paramShort) { return compare(this.value, paramShort.value); }
  
  public static int compare(short paramShort1, short paramShort2) { return paramShort1 - paramShort2; }
  
  public static short reverseBytes(short paramShort) { return (short)((paramShort & 0xFF00) >> 8 | paramShort << 8); }
  
  public static int toUnsignedInt(short paramShort) { return paramShort & 0xFFFF; }
  
  public static long toUnsignedLong(short paramShort) { return paramShort & 0xFFFFL; }
  
  private static class ShortCache {
    static final Short[] cache = new Short[256];
    
    static  {
      for (byte b = 0; b < cache.length; b++)
        cache[b] = new Short((short)(b - '')); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Short.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */