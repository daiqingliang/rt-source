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

public class LongEncodingAlgorithm extends IntegerEncodingAlgorithm {
  public int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException {
    if (paramInt % 8 != 0)
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfLong", new Object[] { Integer.valueOf(8) })); 
    return paramInt / 8;
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException { return paramInt * 8; }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    long[] arrayOfLong = new long[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToLongArray(arrayOfLong, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfLong;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream) throws IOException { return decodeFromInputStreamToIntArray(paramInputStream); }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof long[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray")); 
    long[] arrayOfLong = (long[])paramObject;
    encodeToOutputStreamFromLongArray(arrayOfLong, paramOutputStream);
  }
  
  public Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    final CharBuffer cb = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList longList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(charBuffer, new BuiltInEncodingAlgorithm.WordListener() {
          public void word(int param1Int1, int param1Int2) {
            String str = cb.subSequence(param1Int1, param1Int2).toString();
            longList.add(Long.valueOf(str));
          }
        });
    return generateArrayFromList(arrayList);
  }
  
  public void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (!(paramObject instanceof long[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray")); 
    long[] arrayOfLong = (long[])paramObject;
    convertToCharactersFromLongArray(arrayOfLong, paramStringBuffer);
  }
  
  public final void decodeFromBytesToLongArray(long[] paramArrayOfLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    int i = paramInt3 / 8;
    for (byte b = 0; b < i; b++)
      paramArrayOfLong[paramInt1++] = (paramArrayOfByte[paramInt2++] & 0xFF) << 56 | (paramArrayOfByte[paramInt2++] & 0xFF) << 48 | (paramArrayOfByte[paramInt2++] & 0xFF) << 40 | (paramArrayOfByte[paramInt2++] & 0xFF) << 32 | (paramArrayOfByte[paramInt2++] & 0xFF) << 24 | (paramArrayOfByte[paramInt2++] & 0xFF) << 16 | (paramArrayOfByte[paramInt2++] & 0xFF) << 8 | (paramArrayOfByte[paramInt2++] & 0xFF); 
  }
  
  public final long[] decodeFromInputStreamToIntArray(InputStream paramInputStream) throws IOException {
    ArrayList arrayList = new ArrayList();
    byte[] arrayOfByte = new byte[8];
    while (true) {
      int i = paramInputStream.read(arrayOfByte);
      if (i != 8) {
        if (i == -1)
          break; 
        while (i != 8) {
          int j = paramInputStream.read(arrayOfByte, i, 8 - i);
          if (j == -1)
            throw new EOFException(); 
          i += j;
        } 
      } 
      long l = (arrayOfByte[0] << 56) + ((arrayOfByte[1] & 0xFF) << 48) + ((arrayOfByte[2] & 0xFF) << 40) + ((arrayOfByte[3] & 0xFF) << 32) + ((arrayOfByte[4] & 0xFF) << 24) + ((arrayOfByte[5] & 0xFF) << 16) + ((arrayOfByte[6] & 0xFF) << 8) + ((arrayOfByte[7] & 0xFF) << 0);
      arrayList.add(Long.valueOf(l));
    } 
    return generateArrayFromList(arrayList);
  }
  
  public final void encodeToOutputStreamFromLongArray(long[] paramArrayOfLong, OutputStream paramOutputStream) throws IOException {
    for (byte b = 0; b < paramArrayOfLong.length; b++) {
      long l = paramArrayOfLong[b];
      paramOutputStream.write((int)(l >>> 56 & 0xFFL));
      paramOutputStream.write((int)(l >>> 48 & 0xFFL));
      paramOutputStream.write((int)(l >>> 40 & 0xFFL));
      paramOutputStream.write((int)(l >>> 32 & 0xFFL));
      paramOutputStream.write((int)(l >>> 24 & 0xFFL));
      paramOutputStream.write((int)(l >>> 16 & 0xFFL));
      paramOutputStream.write((int)(l >>> 8 & 0xFFL));
      paramOutputStream.write((int)(l & 0xFFL));
    } 
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) { encodeToBytesFromLongArray((long[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3); }
  
  public final void encodeToBytesFromLongArray(long[] paramArrayOfLong, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      long l = paramArrayOfLong[j];
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 56 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 48 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 40 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 32 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 24 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 16 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l >>> 8 & 0xFFL);
      paramArrayOfByte[paramInt3++] = (byte)(int)(l & 0xFFL);
    } 
  }
  
  public final void convertToCharactersFromLongArray(long[] paramArrayOfLong, StringBuffer paramStringBuffer) {
    int i = paramArrayOfLong.length - 1;
    for (byte b = 0; b <= i; b++) {
      paramStringBuffer.append(Long.toString(paramArrayOfLong[b]));
      if (b != i)
        paramStringBuffer.append(' '); 
    } 
  }
  
  public final long[] generateArrayFromList(List paramList) {
    long[] arrayOfLong = new long[paramList.size()];
    for (byte b = 0; b < arrayOfLong.length; b++)
      arrayOfLong[b] = ((Long)paramList.get(b)).longValue(); 
    return arrayOfLong;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\LongEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */