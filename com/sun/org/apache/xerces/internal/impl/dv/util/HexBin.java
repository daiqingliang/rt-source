package com.sun.org.apache.xerces.internal.impl.dv.util;

public final class HexBin {
  private static final int BASELENGTH = 128;
  
  private static final int LOOKUPLENGTH = 16;
  
  private static final byte[] hexNumberTable = new byte[128];
  
  private static final char[] lookUpHexAlphabet = new char[16];
  
  public static String encode(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return null; 
    int i = paramArrayOfByte.length;
    int j = i * 2;
    char[] arrayOfChar = new char[j];
    for (byte b = 0; b < i; b++) {
      byte b1 = paramArrayOfByte[b];
      if (b1 < 0)
        b1 += 256; 
      arrayOfChar[b * 2] = lookUpHexAlphabet[b1 >> 4];
      arrayOfChar[b * 2 + 1] = lookUpHexAlphabet[b1 & 0xF];
    } 
    return new String(arrayOfChar);
  }
  
  public static byte[] decode(String paramString) {
    if (paramString == null)
      return null; 
    int i = paramString.length();
    if (i % 2 != 0)
      return null; 
    char[] arrayOfChar = paramString.toCharArray();
    int j = i / 2;
    byte[] arrayOfByte = new byte[j];
    for (byte b = 0; b < j; b++) {
      char c = arrayOfChar[b * 2];
      byte b1 = (c < '') ? hexNumberTable[c] : -1;
      if (b1 == -1)
        return null; 
      c = arrayOfChar[b * 2 + 1];
      byte b2 = (c < '') ? hexNumberTable[c] : -1;
      if (b2 == -1)
        return null; 
      arrayOfByte[b] = (byte)(b1 << 4 | b2);
    } 
    return arrayOfByte;
  }
  
  static  {
    byte b;
    for (b = 0; b < ''; b++)
      hexNumberTable[b] = -1; 
    for (b = 57; b >= 48; b--)
      hexNumberTable[b] = (byte)(b - 48); 
    for (b = 70; b >= 65; b--)
      hexNumberTable[b] = (byte)(b - 65 + 10); 
    for (b = 102; b >= 97; b--)
      hexNumberTable[b] = (byte)(b - 97 + 10); 
    for (b = 0; b < 10; b++)
      lookUpHexAlphabet[b] = (char)(48 + b); 
    for (b = 10; b <= 15; b++)
      lookUpHexAlphabet[b] = (char)(65 + b - 10); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\d\\util\HexBin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */