package sun.net.util;

public class IPAddressUtil {
  private static final int INADDR4SZ = 4;
  
  private static final int INADDR16SZ = 16;
  
  private static final int INT16SZ = 2;
  
  public static byte[] textToNumericFormatV4(String paramString) {
    byte[] arrayOfByte = new byte[4];
    long l = 0L;
    byte b1 = 0;
    boolean bool = true;
    int i = paramString.length();
    if (i == 0 || i > 15)
      return null; 
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c == '.') {
        if (bool || l < 0L || l > 255L || b1 == 3)
          return null; 
        arrayOfByte[b1++] = (byte)(int)(l & 0xFFL);
        l = 0L;
        bool = true;
      } else {
        int j = Character.digit(c, 10);
        if (j < 0)
          return null; 
        l *= 10L;
        l += j;
        bool = false;
      } 
    } 
    if (bool || l < 0L || l >= 1L << (4 - b1) * 8)
      return null; 
    switch (b1) {
      case 0:
        arrayOfByte[0] = (byte)(int)(l >> 24 & 0xFFL);
      case 1:
        arrayOfByte[1] = (byte)(int)(l >> 16 & 0xFFL);
      case 2:
        arrayOfByte[2] = (byte)(int)(l >> 8 & 0xFFL);
      case 3:
        arrayOfByte[3] = (byte)(int)(l >> false & 0xFFL);
        break;
    } 
    return arrayOfByte;
  }
  
  public static byte[] textToNumericFormatV6(String paramString) {
    if (paramString.length() < 2)
      return null; 
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte1 = new byte[16];
    int j = arrayOfChar.length;
    int k = paramString.indexOf("%");
    if (k == j - 1)
      return null; 
    if (k != -1)
      j = k; 
    byte b1 = -1;
    byte b2 = 0;
    byte b3 = 0;
    if (arrayOfChar[b2] == ':' && arrayOfChar[++b2] != ':')
      return null; 
    byte b = b2;
    boolean bool = false;
    int i = 0;
    while (b2 < j) {
      char c = arrayOfChar[b2++];
      int m = Character.digit(c, 16);
      if (m != -1) {
        i <<= 4;
        i |= m;
        if (i > 65535)
          return null; 
        bool = true;
        continue;
      } 
      if (c == ':') {
        b = b2;
        if (!bool) {
          if (b1 != -1)
            return null; 
          b1 = b3;
          continue;
        } 
        if (b2 == j)
          return null; 
        if (b3 + 2 > 16)
          return null; 
        arrayOfByte1[b3++] = (byte)(i >> 8 & 0xFF);
        arrayOfByte1[b3++] = (byte)(i & 0xFF);
        bool = false;
        i = 0;
        continue;
      } 
      if (c == '.' && b3 + 4 <= 16) {
        String str = paramString.substring(b, j);
        byte b4 = 0;
        for (int n = 0; (n = str.indexOf('.', n)) != -1; n++)
          b4++; 
        if (b4 != 3)
          return null; 
        byte[] arrayOfByte = textToNumericFormatV4(str);
        if (arrayOfByte == null)
          return null; 
        for (byte b5 = 0; b5 < 4; b5++)
          arrayOfByte1[b3++] = arrayOfByte[b5]; 
        bool = false;
        break;
      } 
      return null;
    } 
    if (bool) {
      if (b3 + 2 > 16)
        return null; 
      arrayOfByte1[b3++] = (byte)(i >> 8 & 0xFF);
      arrayOfByte1[b3++] = (byte)(i & 0xFF);
    } 
    if (b1 != -1) {
      byte b4 = b3 - b1;
      if (b3 == 16)
        return null; 
      for (b2 = 1; b2 <= b4; b2++) {
        arrayOfByte1[16 - b2] = arrayOfByte1[b1 + b4 - b2];
        arrayOfByte1[b1 + b4 - b2] = 0;
      } 
      b3 = 16;
    } 
    if (b3 != 16)
      return null; 
    byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
    return (arrayOfByte2 != null) ? arrayOfByte2 : arrayOfByte1;
  }
  
  public static boolean isIPv4LiteralAddress(String paramString) { return (textToNumericFormatV4(paramString) != null); }
  
  public static boolean isIPv6LiteralAddress(String paramString) { return (textToNumericFormatV6(paramString) != null); }
  
  public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfByte) {
    if (isIPv4MappedAddress(paramArrayOfByte)) {
      byte[] arrayOfByte = new byte[4];
      System.arraycopy(paramArrayOfByte, 12, arrayOfByte, 0, 4);
      return arrayOfByte;
    } 
    return null;
  }
  
  private static boolean isIPv4MappedAddress(byte[] paramArrayOfByte) { return (paramArrayOfByte.length < 16) ? false : ((paramArrayOfByte[0] == 0 && paramArrayOfByte[1] == 0 && paramArrayOfByte[2] == 0 && paramArrayOfByte[3] == 0 && paramArrayOfByte[4] == 0 && paramArrayOfByte[5] == 0 && paramArrayOfByte[6] == 0 && paramArrayOfByte[7] == 0 && paramArrayOfByte[8] == 0 && paramArrayOfByte[9] == 0 && paramArrayOfByte[10] == -1 && paramArrayOfByte[11] == -1)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\ne\\util\IPAddressUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */