package sun.security.util;

import java.util.Comparator;

public class ByteArrayTagOrder extends Object implements Comparator<byte[]> {
  public final int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) { return (paramArrayOfByte1[0] | 0x20) - (paramArrayOfByte2[0] | 0x20); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ByteArrayTagOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */