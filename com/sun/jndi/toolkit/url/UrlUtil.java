package com.sun.jndi.toolkit.url;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

public final class UrlUtil {
  public static final String decode(String paramString) throws MalformedURLException {
    try {
      return decode(paramString, "8859_1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new MalformedURLException("ISO-Latin-1 decoder unavailable");
    } 
  }
  
  public static final String decode(String paramString1, String paramString2) throws MalformedURLException, UnsupportedEncodingException {
    try {
      return URLDecoder.decode(paramString1, paramString2);
    } catch (IllegalArgumentException illegalArgumentException) {
      MalformedURLException malformedURLException = new MalformedURLException("Invalid URI encoding: " + paramString1);
      malformedURLException.initCause(illegalArgumentException);
      throw malformedURLException;
    } 
  }
  
  public static final String encode(String paramString1, String paramString2) throws MalformedURLException, UnsupportedEncodingException {
    byte[] arrayOfByte = paramString1.getBytes(paramString2);
    int i = arrayOfByte.length;
    char[] arrayOfChar = new char[3 * i];
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      if ((arrayOfByte[b2] >= 97 && arrayOfByte[b2] <= 122) || (arrayOfByte[b2] >= 65 && arrayOfByte[b2] <= 90) || (arrayOfByte[b2] >= 48 && arrayOfByte[b2] <= 57) || "=,+;.'-@&/$_()!~*:".indexOf(arrayOfByte[b2]) >= 0) {
        arrayOfChar[b1++] = (char)arrayOfByte[b2];
      } else {
        arrayOfChar[b1++] = '%';
        arrayOfChar[b1++] = Character.forDigit(0xF & arrayOfByte[b2] >>> 4, 16);
        arrayOfChar[b1++] = Character.forDigit(0xF & arrayOfByte[b2], 16);
      } 
    } 
    return new String(arrayOfChar, 0, b1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolki\\url\UrlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */