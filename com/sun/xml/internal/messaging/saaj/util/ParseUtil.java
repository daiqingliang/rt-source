package com.sun.xml.internal.messaging.saaj.util;

public class ParseUtil {
  private static char unescape(String paramString, int paramInt) { return (char)Integer.parseInt(paramString.substring(paramInt + 1, paramInt + 3), 16); }
  
  public static String decode(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    while (b < paramString.length()) {
      char c = paramString.charAt(b);
      if (c != '%') {
        b++;
      } else {
        try {
          c = unescape(paramString, b);
          b += 3;
          if ((c & 0x80) != '\000') {
            char c2;
            char c1;
            switch (c >> '\004') {
              case '\f':
              case '\r':
                c1 = unescape(paramString, b);
                b += 3;
                c = (char)((c & 0x1F) << '\006' | c1 & 0x3F);
                break;
              case '\016':
                c1 = unescape(paramString, b);
                b += 3;
                c2 = unescape(paramString, b);
                b += 3;
                c = (char)((c & 0xF) << '\f' | (c1 & 0x3F) << '\006' | c2 & 0x3F);
                break;
              default:
                throw new IllegalArgumentException();
            } 
          } 
        } catch (NumberFormatException numberFormatException) {
          throw new IllegalArgumentException();
        } 
      } 
      stringBuffer.append(c);
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\ParseUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */