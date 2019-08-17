package com.sun.xml.internal.org.jvnet.staxex;

class Base64Encoder {
  private static final char[] encodeMap = initEncodeMap();
  
  private static char[] initEncodeMap() {
    char[] arrayOfChar = new char[64];
    byte b;
    for (b = 0; b < 26; b++)
      arrayOfChar[b] = (char)(65 + b); 
    for (b = 26; b < 52; b++)
      arrayOfChar[b] = (char)(97 + b - 26); 
    for (b = 52; b < 62; b++)
      arrayOfChar[b] = (char)(48 + b - 52); 
    arrayOfChar[62] = '+';
    arrayOfChar[63] = '/';
    return arrayOfChar;
  }
  
  public static char encode(int paramInt) { return encodeMap[paramInt & 0x3F]; }
  
  public static byte encodeByte(int paramInt) { return (byte)encodeMap[paramInt & 0x3F]; }
  
  public static String print(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[(paramInt2 + 2) / 3 * 4];
    int i = print(paramArrayOfByte, paramInt1, paramInt2, arrayOfChar, 0);
    assert i == arrayOfChar.length;
    return new String(arrayOfChar);
  }
  
  public static int print(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {
    for (int i = paramInt1; i < paramInt2; i += 3) {
      switch (paramInt2 - i) {
        case 1:
          paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[i] >> 2);
          paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[i] & 0x3) << 4);
          paramArrayOfChar[paramInt3++] = '=';
          paramArrayOfChar[paramInt3++] = '=';
          break;
        case 2:
          paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[i] >> 2);
          paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[i] & 0x3) << 4 | paramArrayOfByte[i + 1] >> 4 & 0xF);
          paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[i + 1] & 0xF) << 2);
          paramArrayOfChar[paramInt3++] = '=';
          break;
        default:
          paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[i] >> 2);
          paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[i] & 0x3) << 4 | paramArrayOfByte[i + 1] >> 4 & 0xF);
          paramArrayOfChar[paramInt3++] = encode((paramArrayOfByte[i + 1] & 0xF) << 2 | paramArrayOfByte[i + 2] >> 6 & 0x3);
          paramArrayOfChar[paramInt3++] = encode(paramArrayOfByte[i + 2] & 0x3F);
          break;
      } 
    } 
    return paramInt3;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\Base64Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */