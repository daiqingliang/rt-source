package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HexadecimalEncodingAlgorithm extends BuiltInEncodingAlgorithm {
  private static final char[] NIBBLE_TO_HEXADECIMAL_TABLE = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private static final int[] HEXADECIMAL_TO_NIBBLE_TABLE = { 
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
      -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 
      13, 14, 15, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 
      11, 12, 13, 14, 15 };
  
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
    int j = stringBuilder.length() / 2;
    byte[] arrayOfByte = new byte[j];
    byte b1 = 0;
    for (byte b2 = 0; b2 < j; b2++) {
      int k = HEXADECIMAL_TO_NIBBLE_TABLE[stringBuilder.charAt(b1++) - '0'];
      int m = HEXADECIMAL_TO_NIBBLE_TABLE[stringBuilder.charAt(b1++) - '0'];
      arrayOfByte[b2] = (byte)(k << 4 | m);
    } 
    return arrayOfByte;
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (paramObject == null)
      return; 
    byte[] arrayOfByte = (byte[])paramObject;
    if (arrayOfByte.length == 0)
      return; 
    paramStringBuffer.ensureCapacity(arrayOfByte.length * 2);
    for (byte b = 0; b < arrayOfByte.length; b++) {
      paramStringBuffer.append(NIBBLE_TO_HEXADECIMAL_TABLE[arrayOfByte[b] >>> 4 & 0xF]);
      paramStringBuffer.append(NIBBLE_TO_HEXADECIMAL_TABLE[arrayOfByte[b] & 0xF]);
    } 
  }
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException { return paramInt * 2; }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException { return paramInt / 2; }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) { System.arraycopy((byte[])paramObject, paramInt1, paramArrayOfByte, paramInt3, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\HexadecimalEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */