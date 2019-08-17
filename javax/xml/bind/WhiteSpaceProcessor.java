package javax.xml.bind;

abstract class WhiteSpaceProcessor {
  public static String replace(String paramString) { return replace(paramString).toString(); }
  
  public static CharSequence replace(CharSequence paramCharSequence) {
    int i;
    for (i = paramCharSequence.length() - 1; i >= 0 && !isWhiteSpaceExceptSpace(paramCharSequence.charAt(i)); i--);
    if (i < 0)
      return paramCharSequence; 
    StringBuilder stringBuilder = new StringBuilder(paramCharSequence);
    stringBuilder.setCharAt(i--, ' ');
    while (i >= 0) {
      if (isWhiteSpaceExceptSpace(stringBuilder.charAt(i)))
        stringBuilder.setCharAt(i, ' '); 
      i--;
    } 
    return new String(stringBuilder);
  }
  
  public static CharSequence trim(CharSequence paramCharSequence) {
    int i = paramCharSequence.length();
    byte b;
    for (b = 0; b < i && isWhiteSpace(paramCharSequence.charAt(b)); b++);
    int j;
    for (j = i - 1; j > b && isWhiteSpace(paramCharSequence.charAt(j)); j--);
    return (b == 0 && j == i - 1) ? paramCharSequence : paramCharSequence.subSequence(b, j + 1);
  }
  
  public static String collapse(String paramString) { return collapse(paramString).toString(); }
  
  public static CharSequence collapse(CharSequence paramCharSequence) {
    int i = paramCharSequence.length();
    byte b1;
    for (b1 = 0; b1 < i && !isWhiteSpace(paramCharSequence.charAt(b1)); b1++);
    if (b1 == i)
      return paramCharSequence; 
    StringBuilder stringBuilder = new StringBuilder(i);
    if (b1 != 0) {
      for (byte b = 0; b < b1; b++)
        stringBuilder.append(paramCharSequence.charAt(b)); 
      stringBuilder.append(' ');
    } 
    boolean bool = true;
    for (byte b2 = b1 + 1; b2 < i; b2++) {
      char c = paramCharSequence.charAt(b2);
      boolean bool1 = isWhiteSpace(c);
      if (!bool || !bool1) {
        bool = bool1;
        if (bool) {
          stringBuilder.append(' ');
        } else {
          stringBuilder.append(c);
        } 
      } 
    } 
    i = stringBuilder.length();
    if (i > 0 && stringBuilder.charAt(i - 1) == ' ')
      stringBuilder.setLength(i - 1); 
    return stringBuilder;
  }
  
  public static final boolean isWhiteSpace(CharSequence paramCharSequence) {
    for (int i = paramCharSequence.length() - 1; i >= 0; i--) {
      if (!isWhiteSpace(paramCharSequence.charAt(i)))
        return false; 
    } 
    return true;
  }
  
  public static final boolean isWhiteSpace(char paramChar) { return (paramChar > ' ') ? false : ((paramChar == '\t' || paramChar == '\n' || paramChar == '\r' || paramChar == ' ')); }
  
  protected static final boolean isWhiteSpaceExceptSpace(char paramChar) { return (paramChar >= ' ') ? false : ((paramChar == '\t' || paramChar == '\n' || paramChar == '\r')); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\WhiteSpaceProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */