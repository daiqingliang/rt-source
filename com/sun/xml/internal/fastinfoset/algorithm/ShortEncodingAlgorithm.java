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

public class ShortEncodingAlgorithm extends IntegerEncodingAlgorithm {
  public final int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException {
    if (paramInt % 2 != 0)
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfShort", new Object[] { Integer.valueOf(2) })); 
    return paramInt / 2;
  }
  
  public int getOctetLengthFromPrimitiveLength(int paramInt) throws EncodingAlgorithmException { return paramInt * 2; }
  
  public final Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException {
    short[] arrayOfShort = new short[getPrimtiveLengthFromOctetLength(paramInt2)];
    decodeFromBytesToShortArray(arrayOfShort, 0, paramArrayOfByte, paramInt1, paramInt2);
    return arrayOfShort;
  }
  
  public final Object decodeFromInputStream(InputStream paramInputStream) throws IOException { return decodeFromInputStreamToShortArray(paramInputStream); }
  
  public void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof short[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray")); 
    short[] arrayOfShort = (short[])paramObject;
    encodeToOutputStreamFromShortArray(arrayOfShort, paramOutputStream);
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    final CharBuffer cb = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList shortList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(charBuffer, new BuiltInEncodingAlgorithm.WordListener() {
          public void word(int param1Int1, int param1Int2) {
            String str = cb.subSequence(param1Int1, param1Int2).toString();
            shortList.add(Short.valueOf(str));
          }
        });
    return generateArrayFromList(arrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (!(paramObject instanceof short[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray")); 
    short[] arrayOfShort = (short[])paramObject;
    convertToCharactersFromShortArray(arrayOfShort, paramStringBuffer);
  }
  
  public final void decodeFromBytesToShortArray(short[] paramArrayOfShort, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    int i = paramInt3 / 2;
    for (byte b = 0; b < i; b++)
      paramArrayOfShort[paramInt1++] = (short)((paramArrayOfByte[paramInt2++] & 0xFF) << 8 | paramArrayOfByte[paramInt2++] & 0xFF); 
  }
  
  public final short[] decodeFromInputStreamToShortArray(InputStream paramInputStream) throws IOException {
    ArrayList arrayList = new ArrayList();
    byte[] arrayOfByte = new byte[2];
    while (true) {
      int i = paramInputStream.read(arrayOfByte);
      if (i != 2) {
        if (i == -1)
          break; 
        while (i != 2) {
          int j = paramInputStream.read(arrayOfByte, i, 2 - i);
          if (j == -1)
            throw new EOFException(); 
          i += j;
        } 
      } 
      byte b = (arrayOfByte[0] & 0xFF) << 8 | arrayOfByte[1] & 0xFF;
      arrayList.add(Short.valueOf((short)b));
    } 
    return generateArrayFromList(arrayList);
  }
  
  public final void encodeToOutputStreamFromShortArray(short[] paramArrayOfShort, OutputStream paramOutputStream) throws IOException {
    for (byte b = 0; b < paramArrayOfShort.length; b++) {
      short s = paramArrayOfShort[b];
      paramOutputStream.write(s >>> 8 & 0xFF);
      paramOutputStream.write(s & 0xFF);
    } 
  }
  
  public final void encodeToBytes(Object paramObject, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) { encodeToBytesFromShortArray((short[])paramObject, paramInt1, paramInt2, paramArrayOfByte, paramInt3); }
  
  public final void encodeToBytesFromShortArray(short[] paramArrayOfShort, int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3) {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      short s = paramArrayOfShort[j];
      paramArrayOfByte[paramInt3++] = (byte)(s >>> 8 & 0xFF);
      paramArrayOfByte[paramInt3++] = (byte)(s & 0xFF);
    } 
  }
  
  public final void convertToCharactersFromShortArray(short[] paramArrayOfShort, StringBuffer paramStringBuffer) {
    int i = paramArrayOfShort.length - 1;
    for (byte b = 0; b <= i; b++) {
      paramStringBuffer.append(Short.toString(paramArrayOfShort[b]));
      if (b != i)
        paramStringBuffer.append(' '); 
    } 
  }
  
  public final short[] generateArrayFromList(List paramList) {
    short[] arrayOfShort = new short[paramList.size()];
    for (byte b = 0; b < arrayOfShort.length; b++)
      arrayOfShort[b] = ((Short)paramList.get(b)).shortValue(); 
    return arrayOfShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\ShortEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */