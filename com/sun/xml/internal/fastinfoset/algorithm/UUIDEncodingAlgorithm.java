package com.sun.xml.internal.fastinfoset.algorithm;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm {
  private long _msb;
  
  private long _lsb;
  
  public final int getPrimtiveLengthFromOctetLength(int paramInt) throws EncodingAlgorithmException {
    if (paramInt % 16 != 0)
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfUUID", new Object[] { Integer.valueOf(16) })); 
    return paramInt / 8;
  }
  
  public final Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    final CharBuffer cb = CharBuffer.wrap(paramArrayOfChar, paramInt1, paramInt2);
    final ArrayList longList = new ArrayList();
    matchWhiteSpaceDelimnatedWords(charBuffer, new BuiltInEncodingAlgorithm.WordListener() {
          public void word(int param1Int1, int param1Int2) {
            String str = cb.subSequence(param1Int1, param1Int2).toString();
            UUIDEncodingAlgorithm.this.fromUUIDString(str);
            longList.add(Long.valueOf(UUIDEncodingAlgorithm.this._msb));
            longList.add(Long.valueOf(UUIDEncodingAlgorithm.this._lsb));
          }
        });
    return generateArrayFromList(arrayList);
  }
  
  public final void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) {
    if (!(paramObject instanceof long[]))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray")); 
    long[] arrayOfLong = (long[])paramObject;
    int i = arrayOfLong.length - 2;
    for (boolean bool = false; bool <= i; bool += true) {
      paramStringBuffer.append(toUUIDString(arrayOfLong[bool], arrayOfLong[bool + true]));
      if (bool != i)
        paramStringBuffer.append(' '); 
    } 
  }
  
  final void fromUUIDString(String paramString) {
    String[] arrayOfString = paramString.split("-");
    if (arrayOfString.length != 5)
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.invalidUUID", new Object[] { paramString })); 
    for (byte b = 0; b < 5; b++)
      arrayOfString[b] = "0x" + arrayOfString[b]; 
    this._msb = Long.parseLong(arrayOfString[0], 16);
    this._msb <<= 16;
    this._msb |= Long.parseLong(arrayOfString[1], 16);
    this._msb <<= 16;
    this._msb |= Long.parseLong(arrayOfString[2], 16);
    this._lsb = Long.parseLong(arrayOfString[3], 16);
    this._lsb <<= 48;
    this._lsb |= Long.parseLong(arrayOfString[4], 16);
  }
  
  final String toUUIDString(long paramLong1, long paramLong2) { return digits(paramLong1 >> 32, 8) + "-" + digits(paramLong1 >> 16, 4) + "-" + digits(paramLong1, 4) + "-" + digits(paramLong2 >> 48, 4) + "-" + digits(paramLong2, 12); }
  
  final String digits(long paramLong, int paramInt) {
    long l = 1L << paramInt * 4;
    return Long.toHexString(l | paramLong & l - 1L).substring(1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\UUIDEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */