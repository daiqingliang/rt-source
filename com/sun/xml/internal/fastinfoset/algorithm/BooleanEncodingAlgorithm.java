package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class BooleanEncodingAlgorithm extends BuiltInEncodingAlgorithm {
  private static final int[] BIT_TABLE = { 128, 64, 32, 16, 8, 4, 2, 1 };
  
  public int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException { throw new UnsupportedOperationException(); }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException {
    if (paramInt < 5)
      return 1; 
    int i = paramInt / 8;
    return (i == 0) ? 2 : (1 + i);
  }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    int i = getPrimtiveLengthFromOctetLength(paramInt2, paramArrayOfByte[paramInt1]);
    boolean[] arrayOfBoolean = new boolean[i];
    decodeFromBytesToBooleanArray(arrayOfBoolean, 0, i, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfBoolean;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream) throws IOException {
    ArrayList arrayList = new ArrayList();
    int i = paramInputStream.read();
    if (i == -1)
      throw new EOFException(); 
    int j = i >> 4 & 0xFF;
    byte b = 4;
    int k = 8;
    int m = 0;
    do {
      m = paramInputStream.read();
      if (m == -1)
        k -= j; 
      while (b < k)
        arrayList.add(Boolean.valueOf(((i & BIT_TABLE[b++]) > 0))); 
      i = m;
    } while (i != -1);
    return generateArrayFromList(arrayList);
  }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof boolean[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean")); 
    boolean[] arrayOfBoolean = (boolean[])paramObject;
    int i = arrayOfBoolean.length;
    int j = (i + 4) % 8;
    boolean bool = (j == 0) ? 0 : (8 - j);
    int k = 4;
    int m = bool << 4;
    byte b = 0;
    while (b < i) {
      if (arrayOfBoolean[b++])
        m |= BIT_TABLE[k]; 
      if (++k == 8) {
        paramOutputStream.write(m);
        k = m = 0;
      } 
    } 
    if (k != 8)
      paramOutputStream.write(m); 
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return new boolean[0]; 
    final CharBuffer cb = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList booleanList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(charBuffer, new BuiltInEncodingAlgorithm.WordListener() {
          public void word(int param1Int1, int param1Int2) {
            if (cb.charAt(param1Int1) == 't') {
              booleanList.add(Boolean.TRUE);
            } else {
              booleanList.add(Boolean.FALSE);
            } 
          }
        });
    return generateArrayFromList(arrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (paramObject == null)
      return; 
    boolean[] arrayOfBoolean = (boolean[])paramObject;
    if (arrayOfBoolean.length == 0)
      return; 
    paramStringBuffer.ensureCapacity(arrayOfBoolean.length * 5);
    int i = arrayOfBoolean.length - 1;
    for (byte b = 0; b <= i; b++) {
      if (arrayOfBoolean[b]) {
        paramStringBuffer.append("true");
      } else {
        paramStringBuffer.append("false");
      } 
      if (b != i)
        paramStringBuffer.append(' '); 
    } 
  }
  
  public int getPrimtiveLengthFromOctetLength(int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    int i = paramInt2 >> 4 & 0xFF;
    if (paramInt1 == 1) {
      if (i > 3)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits4")); 
      return 4 - i;
    } 
    if (i > 7)
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits8")); 
    return paramInt1 * 8 - 4 - i;
  }
  
  public final void decodeFromBytesToBooleanArray(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, int paramInt4) {
    byte b = paramArrayOfByte[paramInt3++] & 0xFF;
    byte b1 = 4;
    int i = paramInt1 + paramInt2;
    while (paramInt1 < i) {
      if (b1 == 8) {
        b = paramArrayOfByte[paramInt3++] & 0xFF;
        b1 = 0;
      } 
      paramArrayOfBoolean[paramInt1++] = ((b & BIT_TABLE[b1++]) > 0);
    } 
  }
  
  public void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    if (!(paramObject instanceof boolean[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean")); 
    encodeToBytesFromBooleanArray((boolean[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3);
  }
  
  public void encodeToBytesFromBooleanArray(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    int i = (paramInt2 + 4) % 8;
    boolean bool = (i == 0) ? 0 : (8 - i);
    int j = 4;
    int k = bool << 4;
    int m = paramInt1 + paramInt2;
    while (paramInt1 < m) {
      if (paramArrayOfBoolean[paramInt1++])
        k |= BIT_TABLE[j]; 
      if (++j == 8) {
        paramArrayOfByte[paramInt3++] = (byte)k;
        j = k = 0;
      } 
    } 
    if (j > 0)
      paramArrayOfByte[paramInt3] = (byte)k; 
  }
  
  private boolean[] generateArrayFromList(List paramList) {
    boolean[] arrayOfBoolean = new boolean[paramList.size()];
    for (byte b = 0; b < arrayOfBoolean.length; b++)
      arrayOfBoolean[b] = ((Boolean)paramList.get(b)).booleanValue(); 
    return arrayOfBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\BooleanEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */