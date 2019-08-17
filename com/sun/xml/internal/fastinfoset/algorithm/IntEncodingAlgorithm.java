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

public class IntEncodingAlgorithm extends IntegerEncodingAlgorithm {
  public final int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException {
    if (paramInt % 4 != 0)
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfInt", new Object[] { Integer.valueOf(4) })); 
    return paramInt / 4;
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException { return paramInt * 4; }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    int[] arrayOfInt = new int[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToIntArray(arrayOfInt, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfInt;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream) throws IOException { return decodeFromInputStreamToIntArray(paramInputStream); }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof int[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray")); 
    int[] arrayOfInt = (int[])paramObject;
    encodeToOutputStreamFromIntArray(arrayOfInt, paramOutputStream);
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    final CharBuffer cb = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList integerList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(charBuffer, new BuiltInEncodingAlgorithm.WordListener() {
          public void word(int param1Int1, int param1Int2) {
            String str = cb.subSequence(param1Int1, param1Int2).toString();
            integerList.add(Integer.valueOf(str));
          }
        });
    return generateArrayFromList(arrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (!(paramObject instanceof int[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray")); 
    int[] arrayOfInt = (int[])paramObject;
    convertToCharactersFromIntArray(arrayOfInt, paramStringBuffer);
  }
  
  public final void decodeFromBytesToIntArray(int[] paramArrayOfInt, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    int i = paramInt3 / 4;
    for (byte b = 0; b < i; b++)
      paramArrayOfInt[paramInt1++] = (paramArrayOfByte[paramInt2++] & 0xFF) << 24 | (paramArrayOfByte[paramInt2++] & 0xFF) << 16 | (paramArrayOfByte[paramInt2++] & 0xFF) << 8 | paramArrayOfByte[paramInt2++] & 0xFF; 
  }
  
  public final int[] decodeFromInputStreamToIntArray(InputStream paramInputStream) throws IOException {
    ArrayList arrayList = new ArrayList();
    byte[] arrayOfByte = new byte[4];
    while (true) {
      int i = paramInputStream.read(arrayOfByte);
      if (i != 4) {
        if (i == -1)
          break; 
        while (i != 4) {
          int j = paramInputStream.read(arrayOfByte, i, 4 - i);
          if (j == -1)
            throw new EOFException(); 
          i += j;
        } 
      } 
      byte b = (arrayOfByte[0] & 0xFF) << 24 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF;
      arrayList.add(Integer.valueOf(b));
    } 
    return generateArrayFromList(arrayList);
  }
  
  public final void encodeToOutputStreamFromIntArray(int[] paramArrayOfInt, OutputStream paramOutputStream) throws IOException {
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      int i = paramArrayOfInt[b];
      paramOutputStream.write(i >>> 24 & 0xFF);
      paramOutputStream.write(i >>> 16 & 0xFF);
      paramOutputStream.write(i >>> 8 & 0xFF);
      paramOutputStream.write(i & 0xFF);
    } 
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) { encodeToBytesFromIntArray((int[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3); }
  
  public final void encodeToBytesFromIntArray(int[] paramArrayOfInt, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      int k = paramArrayOfInt[j];
      paramArrayOfByte[paramInt3++] = (byte)(k >>> 24 & 0xFF);
      paramArrayOfByte[paramInt3++] = (byte)(k >>> 16 & 0xFF);
      paramArrayOfByte[paramInt3++] = (byte)(k >>> 8 & 0xFF);
      paramArrayOfByte[paramInt3++] = (byte)(k & 0xFF);
    } 
  }
  
  public final void convertToCharactersFromIntArray(int[] paramArrayOfInt, StringBuffer paramStringBuffer) {
    int i = paramArrayOfInt.length - 1;
    for (byte b = 0; b <= i; b++) {
      paramStringBuffer.append(Integer.toString(paramArrayOfInt[b]));
      if (b != i)
        paramStringBuffer.append(' '); 
    } 
  }
  
  public final int[] generateArrayFromList(List paramList) {
    int[] arrayOfInt = new int[paramList.size()];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = ((Integer)paramList.get(b)).intValue(); 
    return arrayOfInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\IntEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */