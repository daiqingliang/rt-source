package java.util.prefs;

import java.util.Arrays;
import java.util.Random;

class Base64 {
  private static final char[] intToBase64 = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static final char[] intToAltBase64 = { 
      '!', '"', '#', '$', '%', '&', '\'', '(', ')', ',', 
      '-', '.', ':', ';', '<', '>', '@', '[', ']', '^', 
      '`', '_', '{', '|', '}', '~', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '?' };
  
  private static final byte[] base64ToInt = { 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 
      54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
      -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
      15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
      25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 
      29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
      39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
      49, 50, 51 };
  
  private static final byte[] altBase64ToInt = { 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 
      7, 8, -1, 62, 9, 10, 11, -1, 52, 53, 
      54, 55, 56, 57, 58, 59, 60, 61, 12, 13, 
      14, -1, 15, 63, 16, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
      -1, 17, -1, 18, 19, 21, 20, 26, 27, 28, 
      29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
      39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
      49, 50, 51, 22, 23, 24, 25 };
  
  static String byteArrayToBase64(byte[] paramArrayOfByte) { return byteArrayToBase64(paramArrayOfByte, false); }
  
  static String byteArrayToAltBase64(byte[] paramArrayOfByte) { return byteArrayToBase64(paramArrayOfByte, true); }
  
  private static String byteArrayToBase64(byte[] paramArrayOfByte, boolean paramBoolean) {
    int i = paramArrayOfByte.length;
    int j = i / 3;
    int k = i - 3 * j;
    int m = 4 * (i + 2) / 3;
    StringBuffer stringBuffer = new StringBuffer(m);
    char[] arrayOfChar = paramBoolean ? intToAltBase64 : intToBase64;
    byte b = 0;
    byte b1;
    for (b1 = 0; b1 < j; b1++) {
      byte b2 = paramArrayOfByte[b++] & 0xFF;
      byte b3 = paramArrayOfByte[b++] & 0xFF;
      byte b4 = paramArrayOfByte[b++] & 0xFF;
      stringBuffer.append(arrayOfChar[b2 >> 2]);
      stringBuffer.append(arrayOfChar[b2 << 4 & 0x3F | b3 >> 4]);
      stringBuffer.append(arrayOfChar[b3 << 2 & 0x3F | b4 >> 6]);
      stringBuffer.append(arrayOfChar[b4 & 0x3F]);
    } 
    if (k != 0) {
      b1 = paramArrayOfByte[b++] & 0xFF;
      stringBuffer.append(arrayOfChar[b1 >> 2]);
      if (k == 1) {
        stringBuffer.append(arrayOfChar[b1 << 4 & 0x3F]);
        stringBuffer.append("==");
      } else {
        byte b2 = paramArrayOfByte[b++] & 0xFF;
        stringBuffer.append(arrayOfChar[b1 << 4 & 0x3F | b2 >> 4]);
        stringBuffer.append(arrayOfChar[b2 << 2 & 0x3F]);
        stringBuffer.append('=');
      } 
    } 
    return stringBuffer.toString();
  }
  
  static byte[] base64ToByteArray(String paramString) { return base64ToByteArray(paramString, false); }
  
  static byte[] altBase64ToByteArray(String paramString) { return base64ToByteArray(paramString, true); }
  
  private static byte[] base64ToByteArray(String paramString, boolean paramBoolean) {
    byte[] arrayOfByte1 = paramBoolean ? altBase64ToInt : base64ToInt;
    int i = paramString.length();
    int j = i / 4;
    if (4 * j != i)
      throw new IllegalArgumentException("String length must be a multiple of four."); 
    int k = 0;
    int m = j;
    if (i != 0) {
      if (paramString.charAt(i - 1) == '=') {
        k++;
        m--;
      } 
      if (paramString.charAt(i - 2) == '=')
        k++; 
    } 
    byte[] arrayOfByte2 = new byte[3 * j - k];
    byte b1 = 0;
    byte b2 = 0;
    int n;
    for (n = 0; n < m; n++) {
      int i1 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      int i2 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      int i3 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      int i4 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      arrayOfByte2[b2++] = (byte)(i1 << 2 | i2 >> 4);
      arrayOfByte2[b2++] = (byte)(i2 << 4 | i3 >> 2);
      arrayOfByte2[b2++] = (byte)(i3 << 6 | i4);
    } 
    if (k != 0) {
      n = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      int i1 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
      arrayOfByte2[b2++] = (byte)(n << 2 | i1 >> 4);
      if (k == 1) {
        int i2 = base64toInt(paramString.charAt(b1++), arrayOfByte1);
        arrayOfByte2[b2++] = (byte)(i1 << 4 | i2 >> 2);
      } 
    } 
    return arrayOfByte2;
  }
  
  private static int base64toInt(char paramChar, byte[] paramArrayOfByte) {
    byte b = paramArrayOfByte[paramChar];
    if (b < 0)
      throw new IllegalArgumentException("Illegal character " + paramChar); 
    return b;
  }
  
  public static void main(String[] paramArrayOfString) {
    int i = Integer.parseInt(paramArrayOfString[0]);
    int j = Integer.parseInt(paramArrayOfString[1]);
    Random random = new Random();
    for (byte b = 0; b < i; b++) {
      for (byte b1 = 0; b1 < j; b1++) {
        byte[] arrayOfByte1 = new byte[b1];
        for (byte b2 = 0; b2 < b1; b2++)
          arrayOfByte1[b2] = (byte)random.nextInt(); 
        String str = byteArrayToBase64(arrayOfByte1);
        byte[] arrayOfByte2 = base64ToByteArray(str);
        if (!Arrays.equals(arrayOfByte1, arrayOfByte2))
          System.out.println("Dismal failure!"); 
        str = byteArrayToAltBase64(arrayOfByte1);
        arrayOfByte2 = altBase64ToByteArray(str);
        if (!Arrays.equals(arrayOfByte1, arrayOfByte2))
          System.out.println("Alternate dismal failure!"); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */