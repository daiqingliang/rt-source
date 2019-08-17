package java.net;

import java.io.UnsupportedEncodingException;

public class URLDecoder {
  static String dfltEncName = URLEncoder.dfltEncName;
  
  @Deprecated
  public static String decode(String paramString) {
    String str = null;
    try {
      str = decode(paramString, dfltEncName);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    return str;
  }
  
  public static String decode(String paramString1, String paramString2) throws UnsupportedEncodingException {
    boolean bool = false;
    int i = paramString1.length();
    StringBuffer stringBuffer = new StringBuffer((i > 500) ? (i / 2) : i);
    int j = 0;
    if (paramString2.length() == 0)
      throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter"); 
    byte[] arrayOfByte = null;
    while (j < i) {
      char c = paramString1.charAt(j);
      switch (c) {
        case '+':
          stringBuffer.append(' ');
          j++;
          bool = true;
          continue;
        case '%':
          try {
            if (arrayOfByte == null)
              arrayOfByte = new byte[(i - j) / 3]; 
            byte b = 0;
            while (j + 2 < i && c == '%') {
              int k = Integer.parseInt(paramString1.substring(j + 1, j + 3), 16);
              if (k < 0)
                throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value"); 
              arrayOfByte[b++] = (byte)k;
              j += 3;
              if (j < i)
                c = paramString1.charAt(j); 
            } 
            if (j < i && c == '%')
              throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern"); 
            stringBuffer.append(new String(arrayOfByte, 0, b, paramString2));
          } catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + numberFormatException.getMessage());
          } 
          bool = true;
          continue;
      } 
      stringBuffer.append(c);
      j++;
    } 
    return bool ? stringBuffer.toString() : paramString1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */