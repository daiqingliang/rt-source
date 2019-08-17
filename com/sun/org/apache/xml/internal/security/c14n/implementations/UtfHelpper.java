package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UtfHelpper {
  static final void writeByte(String paramString, OutputStream paramOutputStream, Map<String, byte[]> paramMap) throws IOException {
    byte[] arrayOfByte = (byte[])paramMap.get(paramString);
    if (arrayOfByte == null) {
      arrayOfByte = getStringInUtf8(paramString);
      paramMap.put(paramString, arrayOfByte);
    } 
    paramOutputStream.write(arrayOfByte);
  }
  
  static final void writeCharToUtf8(char paramChar, OutputStream paramOutputStream) throws IOException {
    char c2;
    char c1;
    if (paramChar < '') {
      paramOutputStream.write(paramChar);
      return;
    } 
    if ((paramChar >= '?' && paramChar <= '?') || (paramChar >= '?' && paramChar <= '?')) {
      paramOutputStream.write(63);
      return;
    } 
    if (paramChar > '߿') {
      char c3 = (char)(paramChar >>> '\f');
      c2 = 'à';
      if (c3 > '\000')
        c2 |= c3 & 0xF; 
      paramOutputStream.write(c2);
      c2 = '';
      c1 = '?';
    } else {
      c2 = 'À';
      c1 = '\037';
    } 
    char c = (char)(paramChar >>> '\006');
    if (c > '\000')
      c2 |= c & c1; 
    paramOutputStream.write(c2);
    paramOutputStream.write(0x80 | paramChar & 0x3F);
  }
  
  static final void writeStringToUtf8(String paramString, OutputStream paramOutputStream) throws IOException {
    int i = paramString.length();
    byte b = 0;
    while (b < i) {
      char c4;
      char c3;
      char c1 = paramString.charAt(b++);
      if (c1 < '') {
        paramOutputStream.write(c1);
        continue;
      } 
      if ((c1 >= '?' && c1 <= '?') || (c1 >= '?' && c1 <= '?')) {
        paramOutputStream.write(63);
        continue;
      } 
      if (c1 > '߿') {
        char c = (char)(c1 >>> '\f');
        c4 = 'à';
        if (c > '\000')
          c4 |= c & 0xF; 
        paramOutputStream.write(c4);
        c4 = '';
        c3 = '?';
      } else {
        c4 = 'À';
        c3 = '\037';
      } 
      char c2 = (char)(c1 >>> '\006');
      if (c2 > '\000')
        c4 |= c2 & c3; 
      paramOutputStream.write(c4);
      paramOutputStream.write(0x80 | c1 & 0x3F);
    } 
  }
  
  public static final byte[] getStringInUtf8(String paramString) {
    int i = paramString.length();
    boolean bool = false;
    byte[] arrayOfByte = new byte[i];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      int j;
      char c;
      char c1 = paramString.charAt(b1++);
      if (c1 < '') {
        arrayOfByte[b2++] = (byte)c1;
        continue;
      } 
      if ((c1 >= '?' && c1 <= '?') || (c1 >= '?' && c1 <= '?')) {
        arrayOfByte[b2++] = 63;
        continue;
      } 
      if (!bool) {
        byte[] arrayOfByte1 = new byte[3 * i];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, b2);
        arrayOfByte = arrayOfByte1;
        bool = true;
      } 
      if (c1 > '߿') {
        char c3 = (char)(c1 >>> '\f');
        j = -32;
        if (c3 > '\000')
          j = (byte)(j | c3 & 0xF); 
        arrayOfByte[b2++] = j;
        j = -128;
        c = '?';
      } else {
        j = -64;
        c = '\037';
      } 
      char c2 = (char)(c1 >>> '\006');
      if (c2 > '\000')
        j = (byte)(j | c2 & c); 
      arrayOfByte[b2++] = j;
      arrayOfByte[b2++] = (byte)(0x80 | c1 & 0x3F);
    } 
    if (bool) {
      byte[] arrayOfByte1 = new byte[b2];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, b2);
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\UtfHelpper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */