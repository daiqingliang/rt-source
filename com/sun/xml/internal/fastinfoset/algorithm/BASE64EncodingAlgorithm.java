package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BASE64EncodingAlgorithm extends BuiltInEncodingAlgorithm {
  static final char[] encodeBase64 = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  static final int[] decodeBase64 = { 
      62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 
      57, 58, 59, 60, 61, -1, -1, -1, -1, -1, 
      -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 
      8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 
      18, 19, 20, 21, 22, 23, 24, 25, -1, -1, 
      -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 
      32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 
      42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream) throws IOException { throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented")); }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof byte[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotByteArray")); 
    paramOutputStream.write((byte[])paramObject);
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return new byte[0]; 
    StringBuilder stringBuilder = removeWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    int i = stringBuilder.length();
    if (i == 0)
      return new byte[0]; 
    int j = i / 4;
    int k = 3;
    if (stringBuilder.charAt(i - 1) == '=') {
      k--;
      if (stringBuilder.charAt(i - 2) == '=')
        k--; 
    } 
    int m = (j - 1) * 3 + k;
    byte[] arrayOfByte = new byte[m];
    byte b1 = 0;
    byte b2 = 0;
    for (byte b3 = 0; b3 < j; b3++) {
      int n = decodeBase64[stringBuilder.charAt(b2++) - '+'];
      int i1 = decodeBase64[stringBuilder.charAt(b2++) - '+'];
      int i2 = decodeBase64[stringBuilder.charAt(b2++) - '+'];
      int i3 = decodeBase64[stringBuilder.charAt(b2++) - '+'];
      arrayOfByte[b1++] = (byte)(n << 2 | i1 >> 4);
      if (b1 < m)
        arrayOfByte[b1++] = (byte)((i1 & 0xF) << 4 | i2 >> 2); 
      if (b1 < m)
        arrayOfByte[b1++] = (byte)((i2 & 0x3) << 6 | i3); 
    } 
    return arrayOfByte;
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (paramObject == null)
      return; 
    byte[] arrayOfByte = (byte[])paramObject;
    convertToCharacters(arrayOfByte, 0, arrayOfByte.length, paramStringBuffer);
  }
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException { return paramInt; }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException { return paramInt; }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) { System.arraycopy((byte[])paramObject, paramInt1, paramArrayOfByte, paramInt3, paramInt2); }
  
  public final void convertToCharacters(byte[] paramArrayOfByte, int paramInt1, int paramInt2, StringBuffer paramStringBuffer) {
    if (paramArrayOfByte == null)
      return; 
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramInt2 == 0)
      return; 
    int i = paramInt2 % 3;
    int j = (i != 0) ? (paramInt2 / 3 + 1) : (paramInt2 / 3);
    int k = j * 4;
    int m = paramStringBuffer.length();
    paramStringBuffer.ensureCapacity(k + m);
    int n = paramInt1;
    int i1 = paramInt1 + paramInt2;
    for (byte b = 0; b < j; b++) {
      byte b1 = arrayOfByte[n++] & 0xFF;
      byte b2 = (n < i1) ? (arrayOfByte[n++] & 0xFF) : 0;
      byte b3 = (n < i1) ? (arrayOfByte[n++] & 0xFF) : 0;
      paramStringBuffer.append(encodeBase64[b1 >> 2]);
      paramStringBuffer.append(encodeBase64[(b1 & 0x3) << 4 | b2 >> 4]);
      paramStringBuffer.append(encodeBase64[(b2 & 0xF) << 2 | b3 >> 6]);
      paramStringBuffer.append(encodeBase64[b3 & 0x3F]);
    } 
    switch (i) {
      case 1:
        paramStringBuffer.setCharAt(m + k - 1, '=');
        paramStringBuffer.setCharAt(m + k - 2, '=');
        break;
      case 2:
        paramStringBuffer.setCharAt(m + k - 1, '=');
        break;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\BASE64EncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */