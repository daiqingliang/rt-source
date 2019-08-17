package sun.reflect;

class UTF8 {
  static byte[] encode(String paramString) {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[utf8Length(paramString)];
    byte b = 0;
    try {
      for (byte b1 = 0; b1 < i; b1++) {
        char c = paramString.charAt(b1) & 0xFFFF;
        if (c >= '\001' && c <= '') {
          arrayOfByte[b++] = (byte)c;
        } else if (c == '\000' || (c >= '' && c <= '߿')) {
          arrayOfByte[b++] = (byte)('À' + (c >> '\006'));
          arrayOfByte[b++] = (byte)('' + (c & 0x3F));
        } else {
          arrayOfByte[b++] = (byte)('à' + (c >> '\f'));
          arrayOfByte[b++] = (byte)('' + (c >> '\006' & 0x3F));
          arrayOfByte[b++] = (byte)('' + (c & 0x3F));
        } 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new InternalError("Bug in sun.reflect bootstrap UTF-8 encoder", arrayIndexOutOfBoundsException);
    } 
    return arrayOfByte;
  }
  
  private static int utf8Length(String paramString) {
    int i = paramString.length();
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2) & 0xFFFF;
      if (c >= '\001' && c <= '') {
        b1++;
      } else if (c == '\000' || (c >= '' && c <= '߿')) {
        b1 += 2;
      } else {
        b1 += 3;
      } 
    } 
    return b1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */