package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
  
  public static void copyStream(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    char c = 'Ѐ';
    byte[] arrayOfByte = new byte[c];
    int i;
    while ((i = paramInputStream.read(arrayOfByte, 0, c)) != -1)
      paramOutputStream.write(arrayOfByte, 0, i); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\ASCIIUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */