package sun.security.krb5.internal.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class KrbDataInputStream extends BufferedInputStream {
  private boolean bigEndian = true;
  
  public void setNativeByteOrder() {
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      this.bigEndian = true;
    } else {
      this.bigEndian = false;
    } 
  }
  
  public KrbDataInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public final int readLength4() throws IOException {
    int i = read(4);
    if (i < 0)
      throw new IOException("Invalid encoding"); 
    return i;
  }
  
  public int read(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[paramInt];
    if (read(arrayOfByte, 0, paramInt) != paramInt)
      throw new IOException("Premature end of stream reached"); 
    byte b = 0;
    for (int i = 0; i < paramInt; i++) {
      if (this.bigEndian) {
        b |= (arrayOfByte[i] & 0xFF) << (paramInt - i - 1) * 8;
      } else {
        b |= (arrayOfByte[i] & 0xFF) << i * 8;
      } 
    } 
    return b;
  }
  
  public int readVersion() throws IOException {
    int i = (read() & 0xFF) << 8;
    return i | read() & 0xFF;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\interna\\util\KrbDataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */