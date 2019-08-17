package sun.security.krb5.internal.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class KrbDataOutputStream extends BufferedOutputStream {
  public KrbDataOutputStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public void write32(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)((paramInt & 0xFF000000) >> 24 & 0xFF);
    arrayOfByte[1] = (byte)((paramInt & 0xFF0000) >> 16 & 0xFF);
    arrayOfByte[2] = (byte)((paramInt & 0xFF00) >> 8 & 0xFF);
    arrayOfByte[3] = (byte)(paramInt & 0xFF);
    write(arrayOfByte, 0, 4);
  }
  
  public void write16(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)((paramInt & 0xFF00) >> 8 & 0xFF);
    arrayOfByte[1] = (byte)(paramInt & 0xFF);
    write(arrayOfByte, 0, 2);
  }
  
  public void write8(int paramInt) throws IOException { write(paramInt & 0xFF); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\interna\\util\KrbDataOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */