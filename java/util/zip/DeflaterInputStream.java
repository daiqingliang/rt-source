package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DeflaterInputStream extends FilterInputStream {
  protected final Deflater def;
  
  protected final byte[] buf;
  
  private byte[] rbuf = new byte[1];
  
  private boolean usesDefaultDeflater = false;
  
  private boolean reachEOF = false;
  
  private void ensureOpen() throws IOException {
    if (this.in == null)
      throw new IOException("Stream closed"); 
  }
  
  public DeflaterInputStream(InputStream paramInputStream) {
    this(paramInputStream, new Deflater());
    this.usesDefaultDeflater = true;
  }
  
  public DeflaterInputStream(InputStream paramInputStream, Deflater paramDeflater) { this(paramInputStream, paramDeflater, 512); }
  
  public DeflaterInputStream(InputStream paramInputStream, Deflater paramDeflater, int paramInt) {
    super(paramInputStream);
    if (paramInputStream == null)
      throw new NullPointerException("Null input"); 
    if (paramDeflater == null)
      throw new NullPointerException("Null deflater"); 
    if (paramInt < 1)
      throw new IllegalArgumentException("Buffer size < 1"); 
    this.def = paramDeflater;
    this.buf = new byte[paramInt];
  }
  
  public void close() throws IOException {
    if (this.in != null)
      try {
        if (this.usesDefaultDeflater)
          this.def.end(); 
        this.in.close();
      } finally {
        this.in = null;
      }  
  }
  
  public int read() throws IOException {
    int i = read(this.rbuf, 0, 1);
    return (i <= 0) ? -1 : (this.rbuf[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramArrayOfByte == null)
      throw new NullPointerException("Null buffer for read"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = 0;
    while (paramInt2 > 0 && !this.def.finished()) {
      if (this.def.needsInput()) {
        int k = this.in.read(this.buf, 0, this.buf.length);
        if (k < 0) {
          this.def.finish();
        } else if (k > 0) {
          this.def.setInput(this.buf, 0, k);
        } 
      } 
      int j = this.def.deflate(paramArrayOfByte, paramInt1, paramInt2);
      i += j;
      paramInt1 += j;
      paramInt2 -= j;
    } 
    if (i == 0 && this.def.finished()) {
      this.reachEOF = true;
      i = -1;
    } 
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("negative skip length"); 
    ensureOpen();
    if (this.rbuf.length < 512)
      this.rbuf = new byte[512]; 
    int i = (int)Math.min(paramLong, 2147483647L);
    long l = 0L;
    while (i > 0) {
      int j = read(this.rbuf, 0, (i <= this.rbuf.length) ? i : this.rbuf.length);
      if (j < 0)
        break; 
      l += j;
      i -= j;
    } 
    return l;
  }
  
  public int available() throws IOException {
    ensureOpen();
    return this.reachEOF ? 0 : 1;
  }
  
  public boolean markSupported() { return false; }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\DeflaterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */