package java.net;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.util.BitSet;
import sun.security.action.GetPropertyAction;

public class URLEncoder {
  static BitSet dontNeedEncoding;
  
  static final int caseDiff = 32;
  
  static String dfltEncName = null;
  
  @Deprecated
  public static String encode(String paramString) {
    String str = null;
    try {
      str = encode(paramString, dfltEncName);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    return str;
  }
  
  public static String encode(String paramString1, String paramString2) throws UnsupportedEncodingException {
    Charset charset;
    boolean bool = false;
    StringBuffer stringBuffer = new StringBuffer(paramString1.length());
    CharArrayWriter charArrayWriter = new CharArrayWriter();
    if (paramString2 == null)
      throw new NullPointerException("charsetName"); 
    try {
      charset = Charset.forName(paramString2);
    } catch (IllegalCharsetNameException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString2);
    } catch (UnsupportedCharsetException unsupportedCharsetException) {
      throw new UnsupportedEncodingException(paramString2);
    } 
    byte b = 0;
    while (b < paramString1.length()) {
      char c = paramString1.charAt(b);
      if (dontNeedEncoding.get(c)) {
        if (c == ' ') {
          c = '+';
          bool = true;
        } 
        stringBuffer.append((char)c);
        b++;
        continue;
      } 
      do {
        charArrayWriter.write(c);
        if (c < '?' || c > '?' || b + 1 >= paramString1.length())
          continue; 
        char c1 = paramString1.charAt(b + 1);
        if (c1 < '?' || c1 > '?')
          continue; 
        charArrayWriter.write(c1);
        b++;
      } while (++b < paramString1.length() && !dontNeedEncoding.get(c = paramString1.charAt(b)));
      charArrayWriter.flush();
      String str = new String(charArrayWriter.toCharArray());
      byte[] arrayOfByte = str.getBytes(charset);
      for (byte b1 = 0; b1 < arrayOfByte.length; b1++) {
        stringBuffer.append('%');
        char c1 = Character.forDigit(arrayOfByte[b1] >> 4 & 0xF, 16);
        if (Character.isLetter(c1))
          c1 = (char)(c1 - ' '); 
        stringBuffer.append(c1);
        c1 = Character.forDigit(arrayOfByte[b1] & 0xF, 16);
        if (Character.isLetter(c1))
          c1 = (char)(c1 - ' '); 
        stringBuffer.append(c1);
      } 
      charArrayWriter.reset();
      bool = true;
    } 
    return bool ? stringBuffer.toString() : paramString1;
  }
  
  static  {
    dontNeedEncoding = new BitSet(256);
    byte b;
    for (b = 97; b <= 122; b++)
      dontNeedEncoding.set(b); 
    for (b = 65; b <= 90; b++)
      dontNeedEncoding.set(b); 
    for (b = 48; b <= 57; b++)
      dontNeedEncoding.set(b); 
    dontNeedEncoding.set(32);
    dontNeedEncoding.set(45);
    dontNeedEncoding.set(95);
    dontNeedEncoding.set(46);
    dontNeedEncoding.set(42);
    dfltEncName = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */