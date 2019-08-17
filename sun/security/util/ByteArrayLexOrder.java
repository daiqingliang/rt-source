package sun.security.util;

import java.util.Comparator;

public class ByteArrayLexOrder extends Object implements Comparator<byte[]> {
  public final int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    for (byte b = 0; b < paramArrayOfByte1.length && b < paramArrayOfByte2.length; b++) {
      byte b1 = (paramArrayOfByte1[b] & 0xFF) - (paramArrayOfByte2[b] & 0xFF);
      if (b1 != 0)
        return b1; 
    } 
    return paramArrayOfByte1.length - paramArrayOfByte2.length;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ByteArrayLexOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */