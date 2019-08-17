package sun.text.normalizer;

public final class UTF16 {
  public static final int CODEPOINT_MIN_VALUE = 0;
  
  public static final int CODEPOINT_MAX_VALUE = 1114111;
  
  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  
  public static final int LEAD_SURROGATE_MIN_VALUE = 55296;
  
  public static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
  
  public static final int LEAD_SURROGATE_MAX_VALUE = 56319;
  
  public static final int TRAIL_SURROGATE_MAX_VALUE = 57343;
  
  public static final int SURROGATE_MIN_VALUE = 55296;
  
  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  
  private static final int TRAIL_SURROGATE_MASK_ = 1023;
  
  private static final int LEAD_SURROGATE_OFFSET_ = 55232;
  
  public static int charAt(String paramString, int paramInt) {
    char c = paramString.charAt(paramInt);
    return (c < '?') ? c : _charAt(paramString, paramInt, c);
  }
  
  private static int _charAt(String paramString, int paramInt, char paramChar) {
    if (paramChar > '?')
      return paramChar; 
    if (paramChar <= '?') {
      if (paramString.length() != ++paramInt) {
        char c = paramString.charAt(paramInt);
        if (c >= '?' && c <= '?')
          return UCharacterProperty.getRawSupplementary(paramChar, c); 
      } 
    } else if (--paramInt >= 0) {
      char c = paramString.charAt(paramInt);
      if (c >= '?' && c <= '?')
        return UCharacterProperty.getRawSupplementary(c, paramChar); 
    } 
    return paramChar;
  }
  
  public static int charAt(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) {
    paramInt3 += paramInt1;
    if (paramInt3 < paramInt1 || paramInt3 >= paramInt2)
      throw new ArrayIndexOutOfBoundsException(paramInt3); 
    char c = paramArrayOfChar[paramInt3];
    if (!isSurrogate(c))
      return c; 
    if (c <= '?') {
      if (++paramInt3 >= paramInt2)
        return c; 
      char c1 = paramArrayOfChar[paramInt3];
      if (isTrailSurrogate(c1))
        return UCharacterProperty.getRawSupplementary(c, c1); 
    } else {
      if (paramInt3 == paramInt1)
        return c; 
      char c1 = paramArrayOfChar[--paramInt3];
      if (isLeadSurrogate(c1))
        return UCharacterProperty.getRawSupplementary(c1, c); 
    } 
    return c;
  }
  
  public static int getCharCount(int paramInt) { return (paramInt < 65536) ? 1 : 2; }
  
  public static boolean isSurrogate(char paramChar) { return ('?' <= paramChar && paramChar <= '?'); }
  
  public static boolean isTrailSurrogate(char paramChar) { return ('?' <= paramChar && paramChar <= '?'); }
  
  public static boolean isLeadSurrogate(char paramChar) { return ('?' <= paramChar && paramChar <= '?'); }
  
  public static char getLeadSurrogate(int paramInt) { return (paramInt >= 65536) ? (char)(55232 + (paramInt >> 10)) : 0; }
  
  public static char getTrailSurrogate(int paramInt) { return (paramInt >= 65536) ? (char)(56320 + (paramInt & 0x3FF)) : (char)paramInt; }
  
  public static String valueOf(int paramInt) {
    if (paramInt < 0 || paramInt > 1114111)
      throw new IllegalArgumentException("Illegal codepoint"); 
    return toString(paramInt);
  }
  
  public static StringBuffer append(StringBuffer paramStringBuffer, int paramInt) {
    if (paramInt < 0 || paramInt > 1114111)
      throw new IllegalArgumentException("Illegal codepoint: " + Integer.toHexString(paramInt)); 
    if (paramInt >= 65536) {
      paramStringBuffer.append(getLeadSurrogate(paramInt));
      paramStringBuffer.append(getTrailSurrogate(paramInt));
    } else {
      paramStringBuffer.append((char)paramInt);
    } 
    return paramStringBuffer;
  }
  
  public static int moveCodePointOffset(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int j;
    int i = paramArrayOfChar.length;
    null = paramInt3 + paramInt1;
    if (paramInt1 < 0 || paramInt2 < paramInt1)
      throw new StringIndexOutOfBoundsException(paramInt1); 
    if (paramInt2 > i)
      throw new StringIndexOutOfBoundsException(paramInt2); 
    if (paramInt3 < 0 || null > paramInt2)
      throw new StringIndexOutOfBoundsException(paramInt3); 
    if (paramInt4 > 0) {
      if (paramInt4 + null > i)
        throw new StringIndexOutOfBoundsException(null); 
      j = paramInt4;
      while (null < paramInt2 && j > 0) {
        char c = paramArrayOfChar[null];
        if (isLeadSurrogate(c) && null + 1 < paramInt2 && isTrailSurrogate(paramArrayOfChar[null + 1]))
          null++; 
        j--;
        null++;
      } 
    } else {
      if (null + paramInt4 < paramInt1)
        throw new StringIndexOutOfBoundsException(null); 
      for (j = -paramInt4; j > 0 && --null >= paramInt1; j--) {
        char c = paramArrayOfChar[null];
        if (isTrailSurrogate(c) && null > paramInt1 && isLeadSurrogate(paramArrayOfChar[null - 1]))
          null--; 
      } 
    } 
    if (j != 0)
      throw new StringIndexOutOfBoundsException(paramInt4); 
    return paramInt1;
  }
  
  private static String toString(int paramInt) {
    if (paramInt < 65536)
      return String.valueOf((char)paramInt); 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getLeadSurrogate(paramInt));
    stringBuffer.append(getTrailSurrogate(paramInt));
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\UTF16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */