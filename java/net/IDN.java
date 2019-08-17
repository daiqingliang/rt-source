package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import sun.net.idn.Punycode;
import sun.net.idn.StringPrep;
import sun.text.normalizer.UCharacterIterator;

public final class IDN {
  public static final int ALLOW_UNASSIGNED = 1;
  
  public static final int USE_STD3_ASCII_RULES = 2;
  
  private static final String ACE_PREFIX = "xn--";
  
  private static final int ACE_PREFIX_LENGTH = "xn--".length();
  
  private static final int MAX_LABEL_LENGTH = 63;
  
  private static StringPrep namePrep = null;
  
  public static String toASCII(String paramString, int paramInt) {
    int i = 0;
    int j = 0;
    StringBuffer stringBuffer = new StringBuffer();
    if (isRootLabel(paramString))
      return "."; 
    while (i < paramString.length()) {
      j = searchDots(paramString, i);
      stringBuffer.append(toASCIIInternal(paramString.substring(i, j), paramInt));
      if (j != paramString.length())
        stringBuffer.append('.'); 
      i = j + 1;
    } 
    return stringBuffer.toString();
  }
  
  public static String toASCII(String paramString) { return toASCII(paramString, 0); }
  
  public static String toUnicode(String paramString, int paramInt) {
    int i = 0;
    int j = 0;
    StringBuffer stringBuffer = new StringBuffer();
    if (isRootLabel(paramString))
      return "."; 
    while (i < paramString.length()) {
      j = searchDots(paramString, i);
      stringBuffer.append(toUnicodeInternal(paramString.substring(i, j), paramInt));
      if (j != paramString.length())
        stringBuffer.append('.'); 
      i = j + 1;
    } 
    return stringBuffer.toString();
  }
  
  public static String toUnicode(String paramString) { return toUnicode(paramString, 0); }
  
  private static String toASCIIInternal(String paramString, int paramInt) {
    StringBuffer stringBuffer;
    boolean bool = isAllASCII(paramString);
    if (!bool) {
      UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(paramString);
      try {
        stringBuffer = namePrep.prepare(uCharacterIterator, paramInt);
      } catch (ParseException parseException) {
        throw new IllegalArgumentException(parseException);
      } 
    } else {
      stringBuffer = new StringBuffer(paramString);
    } 
    if (stringBuffer.length() == 0)
      throw new IllegalArgumentException("Empty label is not a legal name"); 
    boolean bool1 = ((paramInt & 0x2) != 0) ? 1 : 0;
    if (bool1) {
      for (byte b = 0; b < stringBuffer.length(); b++) {
        char c = stringBuffer.charAt(b);
        if (isNonLDHAsciiCodePoint(c))
          throw new IllegalArgumentException("Contains non-LDH ASCII characters"); 
      } 
      if (stringBuffer.charAt(0) == '-' || stringBuffer.charAt(stringBuffer.length() - 1) == '-')
        throw new IllegalArgumentException("Has leading or trailing hyphen"); 
    } 
    if (!bool && !isAllASCII(stringBuffer.toString()))
      if (!startsWithACEPrefix(stringBuffer)) {
        try {
          stringBuffer = Punycode.encode(stringBuffer, null);
        } catch (ParseException parseException) {
          throw new IllegalArgumentException(parseException);
        } 
        stringBuffer = toASCIILower(stringBuffer);
        stringBuffer.insert(0, "xn--");
      } else {
        throw new IllegalArgumentException("The input starts with the ACE Prefix");
      }  
    if (stringBuffer.length() > 63)
      throw new IllegalArgumentException("The label in the input is too long"); 
    return stringBuffer.toString();
  }
  
  private static String toUnicodeInternal(String paramString, int paramInt) {
    StringBuffer stringBuffer;
    Object object = null;
    boolean bool = isAllASCII(paramString);
    if (!bool) {
      try {
        UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(paramString);
        stringBuffer = namePrep.prepare(uCharacterIterator, paramInt);
      } catch (Exception exception) {
        return paramString;
      } 
    } else {
      stringBuffer = new StringBuffer(paramString);
    } 
    if (startsWithACEPrefix(stringBuffer)) {
      String str = stringBuffer.substring(ACE_PREFIX_LENGTH, stringBuffer.length());
      try {
        StringBuffer stringBuffer1 = Punycode.decode(new StringBuffer(str), null);
        String str1 = toASCII(stringBuffer1.toString(), paramInt);
        if (str1.equalsIgnoreCase(stringBuffer.toString()))
          return stringBuffer1.toString(); 
      } catch (Exception exception) {}
    } 
    return paramString;
  }
  
  private static boolean isNonLDHAsciiCodePoint(int paramInt) { return ((0 <= paramInt && paramInt <= 44) || (46 <= paramInt && paramInt <= 47) || (58 <= paramInt && paramInt <= 64) || (91 <= paramInt && paramInt <= 96) || (123 <= paramInt && paramInt <= 127)); }
  
  private static int searchDots(String paramString, int paramInt) {
    int i;
    for (i = paramInt; i < paramString.length() && !isLabelSeparator(paramString.charAt(i)); i++);
    return i;
  }
  
  private static boolean isRootLabel(String paramString) { return (paramString.length() == 1 && isLabelSeparator(paramString.charAt(0))); }
  
  private static boolean isLabelSeparator(char paramChar) { return (paramChar == '.' || paramChar == '。' || paramChar == '．' || paramChar == '｡'); }
  
  private static boolean isAllASCII(String paramString) {
    boolean bool = true;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c > '') {
        bool = false;
        break;
      } 
    } 
    return bool;
  }
  
  private static boolean startsWithACEPrefix(StringBuffer paramStringBuffer) {
    boolean bool = true;
    if (paramStringBuffer.length() < ACE_PREFIX_LENGTH)
      return false; 
    for (byte b = 0; b < ACE_PREFIX_LENGTH; b++) {
      if (toASCIILower(paramStringBuffer.charAt(b)) != "xn--".charAt(b))
        bool = false; 
    } 
    return bool;
  }
  
  private static char toASCIILower(char paramChar) { return ('A' <= paramChar && paramChar <= 'Z') ? (char)(paramChar + 'a' - 'A') : paramChar; }
  
  private static StringBuffer toASCIILower(StringBuffer paramStringBuffer) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramStringBuffer.length(); b++)
      stringBuffer.append(toASCIILower(paramStringBuffer.charAt(b))); 
    return stringBuffer;
  }
  
  static  {
    InputStream inputStream = null;
    try {
      if (System.getSecurityManager() != null) {
        inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
              public InputStream run() { return StringPrep.class.getResourceAsStream("uidna.spp"); }
            });
      } else {
        inputStream = StringPrep.class.getResourceAsStream("uidna.spp");
      } 
      namePrep = new StringPrep(inputStream);
      inputStream.close();
    } catch (IOException iOException) {
      assert false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\IDN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */