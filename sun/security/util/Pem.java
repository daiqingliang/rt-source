package sun.security.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Pem {
  public static byte[] decode(String paramString) throws IOException {
    byte[] arrayOfByte = paramString.replaceAll("\\s+", "").getBytes(StandardCharsets.ISO_8859_1);
    try {
      return Base64.getDecoder().decode(arrayOfByte);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException(illegalArgumentException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\Pem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */