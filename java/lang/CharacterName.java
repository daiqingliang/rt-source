package java.lang;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.InflaterInputStream;

class CharacterName {
  private static SoftReference<byte[]> refStrPool;
  
  private static int[][] lookup;
  
  private static byte[] initNamePool() {
    byte[] arrayOfByte = null;
    if (refStrPool != null && (arrayOfByte = (byte[])refStrPool.get()) != null)
      return arrayOfByte; 
    dataInputStream = null;
    try {
      dataInputStream = new DataInputStream(new InflaterInputStream((InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
                public InputStream run() { return getClass().getResourceAsStream("uniName.dat"); }
              })));
      lookup = new int[4352][];
      i = dataInputStream.readInt();
      int j = dataInputStream.readInt();
      byte[] arrayOfByte1 = new byte[j];
      dataInputStream.readFully(arrayOfByte1);
      byte b1 = 0;
      byte b = 0;
      byte b2 = 0;
      do {
        byte b3 = arrayOfByte1[b++] & 0xFF;
        if (b3 == 0) {
          b3 = arrayOfByte1[b++] & 0xFF;
          b2 = (arrayOfByte1[b++] & 0xFF) << 16 | (arrayOfByte1[b++] & 0xFF) << 8 | arrayOfByte1[b++] & 0xFF;
        } else {
          b2++;
        } 
        byte b4 = b2 >> 8;
        if (lookup[b4] == null)
          lookup[b4] = new int[256]; 
        lookup[b4][b2 & 0xFF] = b1 << 8 | b3;
        b1 += b3;
      } while (b < j);
      arrayOfByte = new byte[i - j];
      dataInputStream.readFully(arrayOfByte);
      refStrPool = new SoftReference(arrayOfByte);
    } catch (Exception exception) {
      throw new InternalError(exception.getMessage(), exception);
    } finally {
      try {
        if (dataInputStream != null)
          dataInputStream.close(); 
      } catch (Exception exception) {}
    } 
    return arrayOfByte;
  }
  
  public static String get(int paramInt) {
    byte[] arrayOfByte = null;
    if (refStrPool == null || (arrayOfByte = (byte[])refStrPool.get()) == null)
      arrayOfByte = initNamePool(); 
    int i = 0;
    return (lookup[paramInt >> 8] == null || (i = lookup[paramInt >> 8][paramInt & 0xFF]) == 0) ? null : new String(arrayOfByte, 0, i >>> 8, i & 0xFF);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharacterName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */