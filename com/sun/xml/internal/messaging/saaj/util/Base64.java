package com.sun.xml.internal.messaging.saaj.util;

public final class Base64 {
  private static final int BASELENGTH = 255;
  
  private static final int LOOKUPLENGTH = 63;
  
  private static final int TWENTYFOURBITGROUP = 24;
  
  private static final int EIGHTBIT = 8;
  
  private static final int SIXTEENBIT = 16;
  
  private static final int SIXBIT = 6;
  
  private static final int FOURBYTE = 4;
  
  private static final byte PAD = 61;
  
  private static byte[] base64Alphabet = new byte[255];
  
  private static byte[] lookUpBase64Alphabet = new byte[63];
  
  static final int[] base64;
  
  static boolean isBase64(byte paramByte) { return (paramByte == 61 || base64Alphabet[paramByte] != -1); }
  
  static boolean isArrayByteBase64(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    if (i == 0)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (!isBase64(paramArrayOfByte[b]))
        return false; 
    } 
    return true;
  }
  
  public static byte[] encode(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length * 8;
    int j = i % 24;
    int k = i / 24;
    byte[] arrayOfByte = null;
    if (j != 0) {
      arrayOfByte = new byte[(k + 1) * 4];
    } else {
      arrayOfByte = new byte[k * 4];
    } 
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    byte b5 = 0;
    byte b6 = 0;
    byte b7 = 0;
    byte b8 = 0;
    for (b8 = 0; b8 < k; b8++) {
      b7 = b8 * 3;
      b3 = paramArrayOfByte[b7];
      b4 = paramArrayOfByte[b7 + 1];
      b5 = paramArrayOfByte[b7 + 2];
      b2 = (byte)(b4 & 0xF);
      b1 = (byte)(b3 & 0x3);
      b6 = b8 * 4;
      arrayOfByte[b6] = lookUpBase64Alphabet[b3 >> 2];
      arrayOfByte[b6 + 1] = lookUpBase64Alphabet[b4 >> 4 | b1 << 4];
      arrayOfByte[b6 + 2] = lookUpBase64Alphabet[b2 << 2 | b5 >> 6];
      arrayOfByte[b6 + 3] = lookUpBase64Alphabet[b5 & 0x3F];
    } 
    b7 = b8 * 3;
    b6 = b8 * 4;
    if (j == 8) {
      b3 = paramArrayOfByte[b7];
      b1 = (byte)(b3 & 0x3);
      arrayOfByte[b6] = lookUpBase64Alphabet[b3 >> 2];
      arrayOfByte[b6 + 1] = lookUpBase64Alphabet[b1 << 4];
      arrayOfByte[b6 + 2] = 61;
      arrayOfByte[b6 + 3] = 61;
    } else if (j == 16) {
      b3 = paramArrayOfByte[b7];
      b4 = paramArrayOfByte[b7 + 1];
      b2 = (byte)(b4 & 0xF);
      b1 = (byte)(b3 & 0x3);
      arrayOfByte[b6] = lookUpBase64Alphabet[b3 >> 2];
      arrayOfByte[b6 + 1] = lookUpBase64Alphabet[b4 >> 4 | b1 << 4];
      arrayOfByte[b6 + 2] = lookUpBase64Alphabet[b2 << 2];
      arrayOfByte[b6 + 3] = 61;
    } 
    return arrayOfByte;
  }
  
  public byte[] decode(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length / 4;
    byte[] arrayOfByte = null;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    byte b5 = 0;
    byte b6 = 0;
    boolean bool = false;
    byte b7 = 0;
    arrayOfByte = new byte[i * 3 + 1];
    for (byte b8 = 0; b8 < i; b8++) {
      b7 = b8 * 4;
      b5 = paramArrayOfByte[b7 + 2];
      b6 = paramArrayOfByte[b7 + 3];
      b1 = base64Alphabet[paramArrayOfByte[b7]];
      b2 = base64Alphabet[paramArrayOfByte[b7 + 1]];
      if (b5 != 61 && b6 != 61) {
        b3 = base64Alphabet[b5];
        b4 = base64Alphabet[b6];
        arrayOfByte[bool] = (byte)(b1 << 2 | b2 >> 4);
        arrayOfByte[bool + true] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
        arrayOfByte[bool + 2] = (byte)(b3 << 6 | b4);
      } else if (b5 == 61) {
        arrayOfByte[bool] = (byte)(b1 << 2 | b2 >> 4);
        arrayOfByte[bool + true] = (byte)((b2 & 0xF) << 4);
        arrayOfByte[bool + 2] = 0;
      } else if (b6 == 61) {
        b3 = base64Alphabet[b5];
        arrayOfByte[bool] = (byte)(b1 << 2 | b2 >> 4);
        arrayOfByte[bool + true] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
        arrayOfByte[bool + 2] = (byte)(b3 << 6);
      } 
      bool += true;
    } 
    return arrayOfByte;
  }
  
  public static String base64Decode(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    int i = 0;
    int j = 0;
    for (b = 0; b < arrayOfChar.length; b++) {
      int k = base64[arrayOfChar[b] & 0xFF];
      if (k >= 64) {
        if (arrayOfChar[b] != '=')
          System.out.println("Wrong char in base64: " + arrayOfChar[b]); 
      } else {
        j = j << 6 | k;
        i += true;
        if (i >= 8) {
          i -= true;
          stringBuffer.append((char)(j >> i & 0xFF));
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  static  {
    byte b1;
    for (b1 = 0; b1 < 'ÿ'; b1++)
      base64Alphabet[b1] = -1; 
    for (b1 = 90; b1 >= 65; b1--)
      base64Alphabet[b1] = (byte)(b1 - 65); 
    for (b1 = 122; b1 >= 97; b1--)
      base64Alphabet[b1] = (byte)(b1 - 97 + 26); 
    for (b1 = 57; b1 >= 48; b1--)
      base64Alphabet[b1] = (byte)(b1 - 48 + 52); 
    base64Alphabet[43] = 62;
    base64Alphabet[47] = 63;
    for (b1 = 0; b1 <= 25; b1++)
      lookUpBase64Alphabet[b1] = (byte)(65 + b1); 
    b1 = 26;
    byte b2;
    for (b2 = 0; b1 <= 51; b2++) {
      lookUpBase64Alphabet[b1] = (byte)(97 + b2);
      b1++;
    } 
    b1 = 52;
    for (b2 = 0; b1 <= 61; b2++) {
      lookUpBase64Alphabet[b1] = (byte)(48 + b2);
      b1++;
    } 
    base64 = new int[] { 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 
        64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 
        64, 64, 64, 64, 64, 64 };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */