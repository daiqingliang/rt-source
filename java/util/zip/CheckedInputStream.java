package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheckedInputStream extends FilterInputStream {
  private Checksum cksum;
  
  public CheckedInputStream(InputStream paramInputStream, Checksum paramChecksum) {
    super(paramInputStream);
    this.cksum = paramChecksum;
  }
  
  public int read() throws IOException {
    int i = this.in.read();
    if (i != -1)
      this.cksum.update(i); 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    paramInt2 = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 != -1)
      this.cksum.update(paramArrayOfByte, paramInt1, paramInt2); 
    return paramInt2;
  }
  
  public long skip(long paramLong) throws IOException {
    byte[] arrayOfByte = new byte[512];
    long l;
    for (l = 0L; l < paramLong; l += l1) {
      long l1 = paramLong - l;
      l1 = read(arrayOfByte, 0, (l1 < arrayOfByte.length) ? (int)l1 : arrayOfByte.length);
      if (l1 == -1L)
        return l; 
    } 
    return l;
  }
  
  public Checksum getChecksum() { return this.cksum; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\CheckedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */