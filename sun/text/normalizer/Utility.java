package sun.text.normalizer;

public final class Utility {
  private static final char[] UNESCAPE_MAP = { 
      'a', '\007', 'b', '\b', 'e', '\033', 'f', '\f', 'n', '\n', 
      'r', '\r', 't', '\t', 'v', '\013' };
  
  static final char[] DIGITS = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z' };
  
  public static final boolean arrayRegionMatches(char[] paramArrayOfChar1, int paramInt1, char[] paramArrayOfChar2, int paramInt2, int paramInt3) {
    int i = paramInt1 + paramInt3;
    int j = paramInt2 - paramInt1;
    for (int k = paramInt1; k < i; k++) {
      if (paramArrayOfChar1[k] != paramArrayOfChar2[k + j])
        return false; 
    } 
    return true;
  }
  
  public static final String escape(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramString.length()) {
      int j = UTF16.charAt(paramString, i);
      i += UTF16.getCharCount(j);
      if (j >= 32 && j <= 127) {
        if (j == 92) {
          stringBuffer.append("\\\\");
          continue;
        } 
        stringBuffer.append((char)j);
        continue;
      } 
      boolean bool = (j <= 65535) ? 1 : 0;
      stringBuffer.append(bool ? "\\u" : "\\U");
      hex(j, bool ? 4 : 8, stringBuffer);
    } 
    return stringBuffer.toString();
  }
  
  public static int unescapeAt(String paramString, int[] paramArrayOfInt) {
    int m;
    int j = 0;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    int k = 4;
    boolean bool2 = false;
    int n = paramArrayOfInt[0];
    int i1 = paramString.length();
    if (n < 0 || n >= i1)
      return -1; 
    int i = UTF16.charAt(paramString, n);
    n += UTF16.getCharCount(i);
    switch (i) {
      case 117:
        b2 = b3 = 4;
        break;
      case 85:
        b2 = b3 = 8;
        break;
      case 120:
        b2 = 1;
        if (n < i1 && UTF16.charAt(paramString, n) == 123) {
          n++;
          bool2 = true;
          b3 = 8;
          break;
        } 
        b3 = 2;
        break;
      default:
        m = UCharacter.digit(i, 8);
        if (m >= 0) {
          b2 = 1;
          b3 = 3;
          b1 = 1;
          k = 3;
          j = m;
        } 
        break;
    } 
    if (b2 != 0) {
      while (n < i1 && b1 < b3) {
        i = UTF16.charAt(paramString, n);
        m = UCharacter.digit(i, (k == 3) ? 8 : 16);
        if (m < 0)
          break; 
        j = j << k | m;
        n += UTF16.getCharCount(i);
        b1++;
      } 
      if (b1 < b2)
        return -1; 
      if (bool2) {
        if (i != 125)
          return -1; 
        n++;
      } 
      if (j < 0 || j >= 1114112)
        return -1; 
      if (n < i1 && UTF16.isLeadSurrogate((char)j)) {
        int i2 = n + 1;
        i = paramString.charAt(n);
        if (i == 92 && i2 < i1) {
          int[] arrayOfInt = { i2 };
          i = unescapeAt(paramString, arrayOfInt);
          i2 = arrayOfInt[0];
        } 
        if (UTF16.isTrailSurrogate((char)i)) {
          n = i2;
          j = UCharacterProperty.getRawSupplementary((char)j, (char)i);
        } 
      } 
      paramArrayOfInt[0] = n;
      return j;
    } 
    for (boolean bool1 = false; bool1 < UNESCAPE_MAP.length; bool1 += true) {
      if (i == UNESCAPE_MAP[bool1]) {
        paramArrayOfInt[0] = n;
        return UNESCAPE_MAP[bool1 + true];
      } 
      if (i < UNESCAPE_MAP[bool1])
        break; 
    } 
    if (i == 99 && n < i1) {
      i = UTF16.charAt(paramString, n);
      paramArrayOfInt[0] = n + UTF16.getCharCount(i);
      return 0x1F & i;
    } 
    paramArrayOfInt[0] = n;
    return i;
  }
  
  public static StringBuffer hex(int paramInt1, int paramInt2, StringBuffer paramStringBuffer) { return appendNumber(paramStringBuffer, paramInt1, 16, paramInt2); }
  
  public static String hex(int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    return appendNumber(stringBuffer, paramInt1, 16, paramInt2).toString();
  }
  
  public static int skipWhitespace(String paramString, int paramInt) {
    while (paramInt < paramString.length()) {
      int i = UTF16.charAt(paramString, paramInt);
      if (!UCharacterProperty.isRuleWhiteSpace(i))
        break; 
      paramInt += UTF16.getCharCount(i);
    } 
    return paramInt;
  }
  
  private static void recursiveAppendNumber(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 % paramInt2;
    if (paramInt1 >= paramInt2 || paramInt3 > 1)
      recursiveAppendNumber(paramStringBuffer, paramInt1 / paramInt2, paramInt2, paramInt3 - 1); 
    paramStringBuffer.append(DIGITS[i]);
  }
  
  public static StringBuffer appendNumber(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3) throws IllegalArgumentException {
    if (paramInt2 < 2 || paramInt2 > 36)
      throw new IllegalArgumentException("Illegal radix " + paramInt2); 
    int i = paramInt1;
    if (paramInt1 < 0) {
      i = -paramInt1;
      paramStringBuffer.append("-");
    } 
    recursiveAppendNumber(paramStringBuffer, i, paramInt2, paramInt3);
    return paramStringBuffer;
  }
  
  public static boolean isUnprintable(int paramInt) { return (paramInt < 32 || paramInt > 126); }
  
  public static boolean escapeUnprintable(StringBuffer paramStringBuffer, int paramInt) {
    if (isUnprintable(paramInt)) {
      paramStringBuffer.append('\\');
      if ((paramInt & 0xFFFF0000) != 0) {
        paramStringBuffer.append('U');
        paramStringBuffer.append(DIGITS[0xF & paramInt >> 28]);
        paramStringBuffer.append(DIGITS[0xF & paramInt >> 24]);
        paramStringBuffer.append(DIGITS[0xF & paramInt >> 20]);
        paramStringBuffer.append(DIGITS[0xF & paramInt >> 16]);
      } else {
        paramStringBuffer.append('u');
      } 
      paramStringBuffer.append(DIGITS[0xF & paramInt >> 12]);
      paramStringBuffer.append(DIGITS[0xF & paramInt >> 8]);
      paramStringBuffer.append(DIGITS[0xF & paramInt >> 4]);
      paramStringBuffer.append(DIGITS[0xF & paramInt]);
      return true;
    } 
    return false;
  }
  
  public static void getChars(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    if (paramInt1 == paramInt2)
      return; 
    paramStringBuffer.getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */