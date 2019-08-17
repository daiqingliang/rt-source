package java.util.zip;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InflaterInputStream extends FilterInputStream {
  protected Inflater inf;
  
  protected byte[] buf;
  
  protected int len;
  
  private boolean closed = false;
  
  private boolean reachEOF = false;
  
  boolean usesDefaultInflater = false;
  
  private byte[] singleByteBuf = new byte[1];
  
  private byte[] b = new byte[512];
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
  
  public InflaterInputStream(InputStream paramInputStream, Inflater paramInflater, int paramInt) {
    super(paramInputStream);
    if (paramInputStream == null || paramInflater == null)
      throw new NullPointerException(); 
    if (paramInt <= 0)
      throw new IllegalArgumentException("buffer size <= 0"); 
    this.inf = paramInflater;
    this.buf = new byte[paramInt];
  }
  
  public InflaterInputStream(InputStream paramInputStream, Inflater paramInflater) { this(paramInputStream, paramInflater, 512); }
  
  public InflaterInputStream(InputStream paramInputStream) {
    this(paramInputStream, new Inflater());
    this.usesDefaultInflater = true;
  }
  
  public int read() throws IOException {
    ensureOpen();
    return (read(this.singleByteBuf, 0, 1) == -1) ? -1 : Byte.toUnsignedInt(this.singleByteBuf[0]);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    try {
      int i;
      while ((i = this.inf.inflate(paramArrayOfByte, paramInt1, paramInt2)) == 0) {
        if (this.inf.finished() || this.inf.needsDictionary()) {
          this.reachEOF = true;
          return -1;
        } 
        if (this.inf.needsInput())
          fill(); 
      } 
      return i;
    } catch (DataFormatException dataFormatException) {
      String str = dataFormatException.getMessage();
      throw new ZipException((str != null) ? str : "Invalid ZLIB data format");
    } 
  }
  
  public int available() throws IOException {
    ensureOpen();
    return this.reachEOF ? 0 : 1;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("negative skip length"); 
    ensureOpen();
    int i = (int)Math.min(paramLong, 2147483647L);
    int j;
    for (j = 0; j < i; j += k) {
      int k = i - j;
      if (k > this.b.length)
        k = this.b.length; 
      k = read(this.b, 0, k);
      if (k == -1) {
        this.reachEOF = true;
        break;
      } 
    } 
    return j;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      if (this.usesDefaultInflater)
        this.inf.end(); 
      this.in.close();
      this.closed = true;
    } 
  }
  
  protected void fill() throws IOException {
    ensureOpen();
    this.len = this.in.read(this.buf, 0, this.buf.length);
    if (this.len == -1)
      throw new EOFException("Unexpected end of ZLIB input stream"); 
    this.inf.setInput(this.buf, 0, this.len);
  }
  
  public boolean markSupported() { return false; }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\InflaterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */