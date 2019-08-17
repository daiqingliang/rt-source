package sun.invoke.util;

public class BytecodeName {
  static char ESCAPE_C = '\\';
  
  static char NULL_ESCAPE_C = '=';
  
  static String NULL_ESCAPE = ESCAPE_C + "" + NULL_ESCAPE_C;
  
  static final String DANGEROUS_CHARS = "\\/.;:$[]<>";
  
  static final String REPLACEMENT_CHARS = "-|,?!%{}^_";
  
  static final int DANGEROUS_CHAR_FIRST_INDEX = 1;
  
  static char[] DANGEROUS_CHARS_A = "\\/.;:$[]<>".toCharArray();
  
  static char[] REPLACEMENT_CHARS_A = "-|,?!%{}^_".toCharArray();
  
  static final Character[] DANGEROUS_CHARS_CA;
  
  static final long[] SPECIAL_BITMAP;
  
  public static String toBytecodeName(String paramString) {
    String str = mangle(paramString);
    assert str == paramString || looksMangled(str) : str;
    assert paramString.equals(toSourceName(str)) : paramString;
    return str;
  }
  
  public static String toSourceName(String paramString) {
    checkSafeBytecodeName(paramString);
    String str = paramString;
    if (looksMangled(paramString)) {
      str = demangle(paramString);
      assert paramString.equals(mangle(str)) : paramString + " => " + str + " => " + mangle(str);
    } 
    return str;
  }
  
  public static Object[] parseBytecodeName(String paramString) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual length : ()I
    //   4: istore_1
    //   5: aconst_null
    //   6: astore_2
    //   7: iconst_0
    //   8: istore_3
    //   9: iload_3
    //   10: iconst_1
    //   11: if_icmpgt -> 175
    //   14: iconst_0
    //   15: istore #4
    //   17: iconst_0
    //   18: istore #5
    //   20: iconst_0
    //   21: istore #6
    //   23: iload #6
    //   25: iload_1
    //   26: if_icmpgt -> 130
    //   29: iconst_m1
    //   30: istore #7
    //   32: iload #6
    //   34: iload_1
    //   35: if_icmpge -> 60
    //   38: ldc '\/.;:$[]<>'
    //   40: aload_0
    //   41: iload #6
    //   43: invokevirtual charAt : (I)C
    //   46: invokevirtual indexOf : (I)I
    //   49: istore #7
    //   51: iload #7
    //   53: iconst_1
    //   54: if_icmpge -> 60
    //   57: goto -> 124
    //   60: iload #5
    //   62: iload #6
    //   64: if_icmpge -> 95
    //   67: iload_3
    //   68: ifeq -> 86
    //   71: aload_2
    //   72: iload #4
    //   74: aload_0
    //   75: iload #5
    //   77: iload #6
    //   79: invokevirtual substring : (II)Ljava/lang/String;
    //   82: invokestatic toSourceName : (Ljava/lang/String;)Ljava/lang/String;
    //   85: aastore
    //   86: iinc #4, 1
    //   89: iload #6
    //   91: iconst_1
    //   92: iadd
    //   93: istore #5
    //   95: iload #7
    //   97: iconst_1
    //   98: if_icmplt -> 124
    //   101: iload_3
    //   102: ifeq -> 115
    //   105: aload_2
    //   106: iload #4
    //   108: getstatic sun/invoke/util/BytecodeName.DANGEROUS_CHARS_CA : [Ljava/lang/Character;
    //   111: iload #7
    //   113: aaload
    //   114: aastore
    //   115: iinc #4, 1
    //   118: iload #6
    //   120: iconst_1
    //   121: iadd
    //   122: istore #5
    //   124: iinc #6, 1
    //   127: goto -> 23
    //   130: iload_3
    //   131: ifeq -> 137
    //   134: goto -> 175
    //   137: iload #4
    //   139: anewarray java/lang/Object
    //   142: astore_2
    //   143: iload #4
    //   145: iconst_1
    //   146: if_icmpgt -> 169
    //   149: iload #5
    //   151: ifne -> 169
    //   154: iload #4
    //   156: ifeq -> 175
    //   159: aload_2
    //   160: iconst_0
    //   161: aload_0
    //   162: invokestatic toSourceName : (Ljava/lang/String;)Ljava/lang/String;
    //   165: aastore
    //   166: goto -> 175
    //   169: iinc #3, 1
    //   172: goto -> 9
    //   175: aload_2
    //   176: areturn }
  
  public static String unparseBytecodeName(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = paramArrayOfObject;
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      Object object = paramArrayOfObject[b];
      if (object instanceof String) {
        String str = toBytecodeName((String)object);
        if (!b && paramArrayOfObject.length == 1)
          return str; 
        if (str != object) {
          if (paramArrayOfObject == arrayOfObject)
            paramArrayOfObject = (Object[])paramArrayOfObject.clone(); 
          paramArrayOfObject[b] = object = str;
        } 
      } 
    } 
    return appendAll(paramArrayOfObject);
  }
  
  private static String appendAll(Object[] paramArrayOfObject) {
    if (paramArrayOfObject.length <= 1)
      return (paramArrayOfObject.length == 1) ? String.valueOf(paramArrayOfObject[0]) : ""; 
    int i = 0;
    for (Object object : paramArrayOfObject) {
      if (object instanceof String) {
        i += String.valueOf(object).length();
      } else {
        i++;
      } 
    } 
    StringBuilder stringBuilder = new StringBuilder(i);
    for (Object object : paramArrayOfObject)
      stringBuilder.append(object); 
    return stringBuilder.toString();
  }
  
  public static String toDisplayName(String paramString) {
    Object[] arrayOfObject = parseBytecodeName(paramString);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (arrayOfObject[b] instanceof String) {
        String str = (String)arrayOfObject[b];
        if (!isJavaIdent(str) || str.indexOf('$') >= 0)
          arrayOfObject[b] = quoteDisplay(str); 
      } 
    } 
    return appendAll(arrayOfObject);
  }
  
  private static boolean isJavaIdent(String paramString) {
    int i = paramString.length();
    if (i == 0)
      return false; 
    if (!Character.isJavaIdentifierStart(paramString.charAt(0)))
      return false; 
    for (byte b = 1; b < i; b++) {
      if (!Character.isJavaIdentifierPart(paramString.charAt(b)))
        return false; 
    } 
    return true;
  }
  
  private static String quoteDisplay(String paramString) { return "'" + paramString.replaceAll("['\\\\]", "\\\\$0") + "'"; }
  
  private static void checkSafeBytecodeName(String paramString) throws IllegalArgumentException {
    if (!isSafeBytecodeName(paramString))
      throw new IllegalArgumentException(paramString); 
  }
  
  public static boolean isSafeBytecodeName(String paramString) {
    if (paramString.length() == 0)
      return false; 
    for (char c : DANGEROUS_CHARS_A) {
      if (c != ESCAPE_C && paramString.indexOf(c) >= 0)
        return false; 
    } 
    return true;
  }
  
  public static boolean isSafeBytecodeChar(char paramChar) { return ("\\/.;:$[]<>".indexOf(paramChar) < 1); }
  
  private static boolean looksMangled(String paramString) { return (paramString.charAt(0) == ESCAPE_C); }
  
  private static String mangle(String paramString) {
    if (paramString.length() == 0)
      return NULL_ESCAPE; 
    StringBuilder stringBuilder = null;
    byte b = 0;
    int i = paramString.length();
    while (b < i) {
      char c = paramString.charAt(b);
      boolean bool = false;
      if (c == ESCAPE_C) {
        if (b + 1 < i) {
          char c1 = paramString.charAt(b + 1);
          if ((b == 0 && c1 == NULL_ESCAPE_C) || c1 != originalOfReplacement(c1))
            bool = true; 
        } 
      } else {
        bool = isDangerous(c);
      } 
      if (!bool) {
        if (stringBuilder != null)
          stringBuilder.append(c); 
      } else {
        if (stringBuilder == null) {
          stringBuilder = new StringBuilder(paramString.length() + 10);
          if (paramString.charAt(0) != ESCAPE_C && b > 0)
            stringBuilder.append(NULL_ESCAPE); 
          stringBuilder.append(paramString.substring(0, b));
        } 
        stringBuilder.append(ESCAPE_C);
        stringBuilder.append(replacementOf(c));
      } 
      b++;
    } 
    return (stringBuilder != null) ? stringBuilder.toString() : paramString;
  }
  
  private static String demangle(String paramString) {
    StringBuilder stringBuilder = null;
    byte b1 = 0;
    if (paramString.startsWith(NULL_ESCAPE))
      b1 = 2; 
    byte b2 = b1;
    int i = paramString.length();
    while (b2 < i) {
      char c = paramString.charAt(b2);
      if (c == ESCAPE_C && b2 + 1 < i) {
        char c1 = paramString.charAt(b2 + 1);
        char c2 = originalOfReplacement(c1);
        if (c2 != c1) {
          if (stringBuilder == null) {
            stringBuilder = new StringBuilder(paramString.length());
            stringBuilder.append(paramString.substring(b1, b2));
          } 
          b2++;
          c = c2;
        } 
      } 
      if (stringBuilder != null)
        stringBuilder.append(c); 
      b2++;
    } 
    return (stringBuilder != null) ? stringBuilder.toString() : paramString.substring(b1);
  }
  
  static boolean isSpecial(char paramChar) { return (paramChar >>> '\006' < SPECIAL_BITMAP.length) ? (((SPECIAL_BITMAP[paramChar >>> '\006'] >> paramChar & 0x1L) != 0L)) : false; }
  
  static char replacementOf(char paramChar) {
    if (!isSpecial(paramChar))
      return paramChar; 
    int i = "\\/.;:$[]<>".indexOf(paramChar);
    return (i < 0) ? paramChar : "-|,?!%{}^_".charAt(i);
  }
  
  static char originalOfReplacement(char paramChar) {
    if (!isSpecial(paramChar))
      return paramChar; 
    int i = "-|,?!%{}^_".indexOf(paramChar);
    return (i < 0) ? paramChar : "\\/.;:$[]<>".charAt(i);
  }
  
  static boolean isDangerous(char paramChar) { return !isSpecial(paramChar) ? false : (("\\/.;:$[]<>".indexOf(paramChar) >= 1)); }
  
  static int indexOfDangerousChar(String paramString, int paramInt) {
    int i = paramInt;
    int j = paramString.length();
    while (i < j) {
      if (isDangerous(paramString.charAt(i)))
        return i; 
      i++;
    } 
    return -1;
  }
  
  static int lastIndexOfDangerousChar(String paramString, int paramInt) {
    for (int i = Math.min(paramInt, paramString.length() - 1); i >= 0; i--) {
      if (isDangerous(paramString.charAt(i)))
        return i; 
    } 
    return -1;
  }
  
  static  {
    Character[] arrayOfCharacter = new Character["\\/.;:$[]<>".length()];
    for (byte b = 0; b < arrayOfCharacter.length; b++)
      arrayOfCharacter[b] = Character.valueOf("\\/.;:$[]<>".charAt(b)); 
    DANGEROUS_CHARS_CA = arrayOfCharacter;
    SPECIAL_BITMAP = new long[2];
    String str = "\\/.;:$[]<>-|,?!%{}^_";
    for (char c : str.toCharArray())
      SPECIAL_BITMAP[c >>> '\006'] = SPECIAL_BITMAP[c >>> '\006'] | 1L << c; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\invok\\util\BytecodeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */