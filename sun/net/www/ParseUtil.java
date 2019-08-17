package sun.net.www;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.BitSet;
import sun.nio.cs.ThreadLocalCoders;

public class ParseUtil {
  static BitSet encodedInPath = new BitSet(256);
  
  private static final char[] hexDigits;
  
  private static final long L_DIGIT;
  
  private static final long H_DIGIT = 0L;
  
  private static final long L_HEX;
  
  private static final long H_HEX;
  
  private static final long L_UPALPHA = 0L;
  
  private static final long H_UPALPHA;
  
  private static final long L_LOWALPHA = 0L;
  
  private static final long H_LOWALPHA;
  
  private static final long L_ALPHA = 0L;
  
  private static final long H_ALPHA;
  
  private static final long L_ALPHANUM;
  
  private static final long H_ALPHANUM;
  
  private static final long L_MARK;
  
  private static final long H_MARK;
  
  private static final long L_UNRESERVED;
  
  private static final long H_UNRESERVED;
  
  private static final long L_RESERVED;
  
  private static final long H_RESERVED;
  
  private static final long L_ESCAPED = 1L;
  
  private static final long H_ESCAPED = 0L;
  
  private static final long L_DASH;
  
  private static final long H_DASH;
  
  private static final long L_URIC;
  
  private static final long H_URIC;
  
  private static final long L_PCHAR;
  
  private static final long H_PCHAR;
  
  private static final long L_PATH;
  
  private static final long H_PATH;
  
  private static final long L_USERINFO;
  
  private static final long H_USERINFO;
  
  private static final long L_REG_NAME;
  
  private static final long H_REG_NAME;
  
  private static final long L_SERVER;
  
  private static final long H_SERVER;
  
  public static String encodePath(String paramString) { return encodePath(paramString, true); }
  
  public static String encodePath(String paramString, boolean paramBoolean) {
    char[] arrayOfChar1 = new char[paramString.length() * 2 + 16];
    int i = 0;
    char[] arrayOfChar2 = paramString.toCharArray();
    int j = paramString.length();
    for (byte b = 0; b < j; b++) {
      char c = arrayOfChar2[b];
      if ((!paramBoolean && c == '/') || (paramBoolean && c == File.separatorChar)) {
        arrayOfChar1[i++] = '/';
      } else if (c <= '') {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
          arrayOfChar1[i++] = c;
        } else if (encodedInPath.get(c)) {
          i = escape(arrayOfChar1, c, i);
        } else {
          arrayOfChar1[i++] = c;
        } 
      } else if (c > '߿') {
        i = escape(arrayOfChar1, (char)(0xE0 | c >> '\f' & 0xF), i);
        i = escape(arrayOfChar1, (char)(0x80 | c >> '\006' & 0x3F), i);
        i = escape(arrayOfChar1, (char)(0x80 | c >> Character.MIN_VALUE & 0x3F), i);
      } else {
        i = escape(arrayOfChar1, (char)(0xC0 | c >> '\006' & 0x1F), i);
        i = escape(arrayOfChar1, (char)(0x80 | c >> Character.MIN_VALUE & 0x3F), i);
      } 
      if (i + 9 > arrayOfChar1.length) {
        int k = arrayOfChar1.length * 2 + 16;
        if (k < 0)
          k = Integer.MAX_VALUE; 
        char[] arrayOfChar = new char[k];
        System.arraycopy(arrayOfChar1, 0, arrayOfChar, 0, i);
        arrayOfChar1 = arrayOfChar;
      } 
    } 
    return new String(arrayOfChar1, 0, i);
  }
  
  private static int escape(char[] paramArrayOfChar, char paramChar, int paramInt) {
    paramArrayOfChar[paramInt++] = '%';
    paramArrayOfChar[paramInt++] = Character.forDigit(paramChar >> '\004' & 0xF, 16);
    paramArrayOfChar[paramInt++] = Character.forDigit(paramChar & 0xF, 16);
    return paramInt;
  }
  
  private static byte unescape(String paramString, int paramInt) { return (byte)Integer.parseInt(paramString.substring(paramInt + 1, paramInt + 3), 16); }
  
  public static String decode(String paramString) {
    int i = paramString.length();
    if (i == 0 || paramString.indexOf('%') < 0)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder(i);
    ByteBuffer byteBuffer = ByteBuffer.allocate(i);
    CharBuffer charBuffer = CharBuffer.allocate(i);
    CharsetDecoder charsetDecoder = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    char c = paramString.charAt(0);
    int j = 0;
    while (j < i) {
      assert c == paramString.charAt(j);
      if (c != '%') {
        stringBuilder.append(c);
        if (++j >= i)
          break; 
        c = paramString.charAt(j);
        continue;
      } 
      byteBuffer.clear();
      byte b = j;
      do {
        assert i - j >= 2;
        try {
          byteBuffer.put(unescape(paramString, j));
        } catch (NumberFormatException numberFormatException) {
          throw new IllegalArgumentException();
        } 
        j += 3;
        if (j >= i)
          break; 
        c = paramString.charAt(j);
      } while (c == '%');
      byteBuffer.flip();
      charBuffer.clear();
      charsetDecoder.reset();
      CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, true);
      if (coderResult.isError())
        throw new IllegalArgumentException("Error decoding percent encoded characters"); 
      coderResult = charsetDecoder.flush(charBuffer);
      if (coderResult.isError())
        throw new IllegalArgumentException("Error decoding percent encoded characters"); 
      stringBuilder.append(charBuffer.flip().toString());
    } 
    return stringBuilder.toString();
  }
  
  public String canonizeString(String paramString) {
    int i = 0;
    int j = paramString.length();
    while ((i = paramString.indexOf("/../")) >= 0) {
      if ((j = paramString.lastIndexOf('/', i - 1)) >= 0) {
        paramString = paramString.substring(0, j) + paramString.substring(i + 3);
        continue;
      } 
      paramString = paramString.substring(i + 3);
    } 
    while ((i = paramString.indexOf("/./")) >= 0)
      paramString = paramString.substring(0, i) + paramString.substring(i + 2); 
    while (paramString.endsWith("/..")) {
      i = paramString.indexOf("/..");
      if ((j = paramString.lastIndexOf('/', i - 1)) >= 0) {
        paramString = paramString.substring(0, j + 1);
        continue;
      } 
      paramString = paramString.substring(0, i);
    } 
    if (paramString.endsWith("/."))
      paramString = paramString.substring(0, paramString.length() - 1); 
    return paramString;
  }
  
  public static URL fileToEncodedURL(File paramFile) throws MalformedURLException {
    String str = paramFile.getAbsolutePath();
    str = encodePath(str);
    if (!str.startsWith("/"))
      str = "/" + str; 
    if (!str.endsWith("/") && paramFile.isDirectory())
      str = str + "/"; 
    return new URL("file", "", str);
  }
  
  public static URI toURI(URL paramURL) {
    URI uRI;
    String str1 = paramURL.getProtocol();
    String str2 = paramURL.getAuthority();
    String str3 = paramURL.getPath();
    String str4 = paramURL.getQuery();
    String str5 = paramURL.getRef();
    if (str3 != null && !str3.startsWith("/"))
      str3 = "/" + str3; 
    if (str2 != null && str2.endsWith(":-1"))
      str2 = str2.substring(0, str2.length() - 3); 
    try {
      uRI = createURI(str1, str2, str3, str4, str5);
    } catch (URISyntaxException uRISyntaxException) {
      uRI = null;
    } 
    return uRI;
  }
  
  private static URI createURI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws URISyntaxException {
    String str = toString(paramString1, null, paramString2, null, null, -1, paramString3, paramString4, paramString5);
    checkPath(str, paramString1, paramString3);
    return new URI(str);
  }
  
  private static String toString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt, String paramString6, String paramString7, String paramString8) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramString1 != null) {
      stringBuffer.append(paramString1);
      stringBuffer.append(':');
    } 
    appendSchemeSpecificPart(stringBuffer, paramString2, paramString3, paramString4, paramString5, paramInt, paramString6, paramString7);
    appendFragment(stringBuffer, paramString8);
    return stringBuffer.toString();
  }
  
  private static void appendSchemeSpecificPart(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5, String paramString6) {
    if (paramString1 != null) {
      if (paramString1.startsWith("//[")) {
        int i = paramString1.indexOf("]");
        if (i != -1 && paramString1.indexOf(":") != -1) {
          String str2;
          String str1;
          if (i == paramString1.length()) {
            str2 = paramString1;
            str1 = "";
          } else {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          } 
          paramStringBuffer.append(str2);
          paramStringBuffer.append(quote(str1, L_URIC, H_URIC));
        } 
      } else {
        paramStringBuffer.append(quote(paramString1, L_URIC, H_URIC));
      } 
    } else {
      appendAuthority(paramStringBuffer, paramString2, paramString3, paramString4, paramInt);
      if (paramString5 != null)
        paramStringBuffer.append(quote(paramString5, L_PATH, H_PATH)); 
      if (paramString6 != null) {
        paramStringBuffer.append('?');
        paramStringBuffer.append(quote(paramString6, L_URIC, H_URIC));
      } 
    } 
  }
  
  private static void appendAuthority(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, int paramInt) {
    if (paramString3 != null) {
      paramStringBuffer.append("//");
      if (paramString2 != null) {
        paramStringBuffer.append(quote(paramString2, L_USERINFO, H_USERINFO));
        paramStringBuffer.append('@');
      } 
      boolean bool = (paramString3.indexOf(':') >= 0 && !paramString3.startsWith("[") && !paramString3.endsWith("]")) ? 1 : 0;
      if (bool)
        paramStringBuffer.append('['); 
      paramStringBuffer.append(paramString3);
      if (bool)
        paramStringBuffer.append(']'); 
      if (paramInt != -1) {
        paramStringBuffer.append(':');
        paramStringBuffer.append(paramInt);
      } 
    } else if (paramString1 != null) {
      paramStringBuffer.append("//");
      if (paramString1.startsWith("[")) {
        int i = paramString1.indexOf("]");
        if (i != -1 && paramString1.indexOf(":") != -1) {
          String str2;
          String str1;
          if (i == paramString1.length()) {
            str2 = paramString1;
            str1 = "";
          } else {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          } 
          paramStringBuffer.append(str2);
          paramStringBuffer.append(quote(str1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
        } 
      } else {
        paramStringBuffer.append(quote(paramString1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
      } 
    } 
  }
  
  private static void appendFragment(StringBuffer paramStringBuffer, String paramString) {
    if (paramString != null) {
      paramStringBuffer.append('#');
      paramStringBuffer.append(quote(paramString, L_URIC, H_URIC));
    } 
  }
  
  private static String quote(String paramString, long paramLong1, long paramLong2) {
    int i = paramString.length();
    StringBuffer stringBuffer = null;
    boolean bool = ((paramLong1 & 0x1L) != 0L) ? 1 : 0;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c < '') {
        if (!match(c, paramLong1, paramLong2) && !isEscaped(paramString, b)) {
          if (stringBuffer == null) {
            stringBuffer = new StringBuffer();
            stringBuffer.append(paramString.substring(0, b));
          } 
          appendEscape(stringBuffer, (byte)c);
        } else if (stringBuffer != null) {
          stringBuffer.append(c);
        } 
      } else if (bool && (Character.isSpaceChar(c) || Character.isISOControl(c))) {
        if (stringBuffer == null) {
          stringBuffer = new StringBuffer();
          stringBuffer.append(paramString.substring(0, b));
        } 
        appendEncoded(stringBuffer, c);
      } else if (stringBuffer != null) {
        stringBuffer.append(c);
      } 
    } 
    return (stringBuffer == null) ? paramString : stringBuffer.toString();
  }
  
  private static boolean isEscaped(String paramString, int paramInt) { return (paramString == null || paramString.length() <= paramInt + 2) ? false : ((paramString.charAt(paramInt) == '%' && match(paramString.charAt(paramInt + 1), L_HEX, H_HEX) && match(paramString.charAt(paramInt + 2), L_HEX, H_HEX))); }
  
  private static void appendEncoded(StringBuffer paramStringBuffer, char paramChar) {
    ByteBuffer byteBuffer = null;
    try {
      byteBuffer = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap("" + paramChar));
    } catch (CharacterCodingException characterCodingException) {
      assert false;
    } 
    while (byteBuffer.hasRemaining()) {
      byte b = byteBuffer.get() & 0xFF;
      if (b >= 128) {
        appendEscape(paramStringBuffer, (byte)b);
        continue;
      } 
      paramStringBuffer.append((char)b);
    } 
  }
  
  private static void appendEscape(StringBuffer paramStringBuffer, byte paramByte) {
    paramStringBuffer.append('%');
    paramStringBuffer.append(hexDigits[paramByte >> 4 & 0xF]);
    paramStringBuffer.append(hexDigits[paramByte >> 0 & 0xF]);
  }
  
  private static boolean match(char paramChar, long paramLong1, long paramLong2) { return (paramChar < '@') ? (((1L << paramChar & paramLong1) != 0L)) : ((paramChar < '') ? (((1L << paramChar - '@' & paramLong2) != 0L)) : false); }
  
  private static void checkPath(String paramString1, String paramString2, String paramString3) throws URISyntaxException {
    if (paramString2 != null && paramString3 != null && paramString3.length() > 0 && paramString3.charAt(0) != '/')
      throw new URISyntaxException(paramString1, "Relative path in absolute URI"); 
  }
  
  private static long lowMask(char paramChar1, char paramChar2) {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 63), 0);
    int j = Math.max(Math.min(paramChar2, 63), 0);
    for (int k = i; k <= j; k++)
      l |= 1L << k; 
    return l;
  }
  
  private static long lowMask(String paramString) {
    int i = paramString.length();
    long l = 0L;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c < '@')
        l |= 1L << c; 
    } 
    return l;
  }
  
  private static long highMask(char paramChar1, char paramChar2) {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 127), 64) - 64;
    int j = Math.max(Math.min(paramChar2, 127), 64) - 64;
    for (int k = i; k <= j; k++)
      l |= 1L << k; 
    return l;
  }
  
  private static long highMask(String paramString) {
    int i = paramString.length();
    long l = 0L;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c >= '@' && c < '')
        l |= 1L << c - '@'; 
    } 
    return l;
  }
  
  static  {
    encodedInPath.set(61);
    encodedInPath.set(59);
    encodedInPath.set(63);
    encodedInPath.set(47);
    encodedInPath.set(35);
    encodedInPath.set(32);
    encodedInPath.set(60);
    encodedInPath.set(62);
    encodedInPath.set(37);
    encodedInPath.set(34);
    encodedInPath.set(123);
    encodedInPath.set(125);
    encodedInPath.set(124);
    encodedInPath.set(92);
    encodedInPath.set(94);
    encodedInPath.set(91);
    encodedInPath.set(93);
    encodedInPath.set(96);
    for (byte b = 0; b < 32; b++)
      encodedInPath.set(b); 
    encodedInPath.set(127);
    hexDigits = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F' };
    L_DIGIT = lowMask('0', '9');
    L_HEX = L_DIGIT;
    H_HEX = highMask('A', 'F') | highMask('a', 'f');
    H_UPALPHA = highMask('A', 'Z');
    H_LOWALPHA = highMask('a', 'z');
    H_ALPHA = H_LOWALPHA | H_UPALPHA;
    L_ALPHANUM = L_DIGIT | 0x0L;
    H_ALPHANUM = 0x0L | H_ALPHA;
    L_MARK = lowMask("-_.!~*'()");
    H_MARK = highMask("-_.!~*'()");
    L_UNRESERVED = L_ALPHANUM | L_MARK;
    H_UNRESERVED = H_ALPHANUM | H_MARK;
    L_RESERVED = lowMask(";/?:@&=+$,[]");
    H_RESERVED = highMask(";/?:@&=+$,[]");
    L_DASH = lowMask("-");
    H_DASH = highMask("-");
    L_URIC = L_RESERVED | L_UNRESERVED | 0x1L;
    H_URIC = H_RESERVED | H_UNRESERVED | 0x0L;
    L_PCHAR = L_UNRESERVED | 0x1L | lowMask(":@&=+$,");
    H_PCHAR = H_UNRESERVED | 0x0L | highMask(":@&=+$,");
    L_PATH = L_PCHAR | lowMask(";/");
    H_PATH = H_PCHAR | highMask(";/");
    L_USERINFO = L_UNRESERVED | 0x1L | lowMask(";:&=+$,");
    H_USERINFO = H_UNRESERVED | 0x0L | highMask(";:&=+$,");
    L_REG_NAME = L_UNRESERVED | 0x1L | lowMask("$,;:@&=+");
    H_REG_NAME = H_UNRESERVED | 0x0L | highMask("$,;:@&=+");
    L_SERVER = L_USERINFO | L_ALPHANUM | L_DASH | lowMask(".:@[]");
    H_SERVER = H_USERINFO | H_ALPHANUM | H_DASH | highMask(".:@[]");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\ParseUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */