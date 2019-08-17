package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ASCIIUtility {
  public static int parseInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws NumberFormatException {
    if (paramArrayOfByte == null)
      throw new NumberFormatException("null"); 
    int i = 0;
    boolean bool = false;
    int j = paramInt1;
    if (paramInt2 > paramInt1) {
      int k;
      if (paramArrayOfByte[j] == 45) {
        bool = true;
        k = Integer.MIN_VALUE;
        j++;
      } else {
        k = -2147483647;
      } 
      int m = k / paramInt3;
      if (j < paramInt2) {
        int n = Character.digit((char)paramArrayOfByte[j++], paramInt3);
        if (n < 0)
          throw new NumberFormatException("illegal number: " + toString(paramArrayOfByte, paramInt1, paramInt2)); 
        i = -n;
      } 
      while (j < paramInt2) {
        int n = Character.digit((char)paramArrayOfByte[j++], paramInt3);
        if (n < 0)
          throw new NumberFormatException("illegal number"); 
        if (i < m)
          throw new NumberFormatException("illegal number"); 
        i *= paramInt3;
        if (i < k + n)
          throw new NumberFormatException("illegal number"); 
        i -= n;
      } 
    } else {
      throw new NumberFormatException("illegal number");
    } 
    if (bool) {
      if (j > paramInt1 + 1)
        return i; 
      throw new NumberFormatException("illegal number");
    } 
    return -i;
  }
  
  public static String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    char[] arrayOfChar = new char[i];
    byte b = 0;
    int j = paramInt1;
    while (b < i)
      arrayOfChar[b++] = (char)(paramArrayOfByte[j++] & 0xFF); 
    return new String(arrayOfChar);
  }
  
  public static byte[] getBytes(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    byte[] arrayOfByte = new byte[i];
    byte b = 0;
    while (b < i)
      arrayOfByte[b] = (byte)arrayOfChar[b++]; 
    return arrayOfByte;
  }
  
  public static byte[] getBytes(InputStream paramInputStream) throws IOException {
    ByteOutputStream byteOutputStream = new ByteOutputStream();
    try {
      byteOutputStream.write(paramInputStream);
    } finally {
      paramInputStream.close();
    } 
    return byteOutputStream.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\ASCIIUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */