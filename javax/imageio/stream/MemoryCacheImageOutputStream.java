package javax.imageio.stream;

import java.io.IOException;
import java.io.OutputStream;

public class MemoryCacheImageOutputStream extends ImageOutputStreamImpl {
  private OutputStream stream;
  
  private MemoryCache cache = new MemoryCache();
  
  public MemoryCacheImageOutputStream(OutputStream paramOutputStream) {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    this.stream = paramOutputStream;
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.cache.read(this.streamPos);
    if (i != -1)
      this.streamPos++; 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    checkClosed();
    if (paramArrayOfByte == null)
      throw new NullPointerException("b == null!"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!"); 
    this.bitOffset = 0;
    if (paramInt2 == 0)
      return 0; 
    long l = this.cache.getLength() - this.streamPos;
    if (l <= 0L)
      return -1; 
    paramInt2 = (int)Math.min(l, paramInt2);
    this.cache.read(paramArrayOfByte, paramInt1, paramInt2, this.streamPos);
    this.streamPos += paramInt2;
    return paramInt2;
  }
  
  public void write(int paramInt) throws IOException {
    flushBits();
    this.cache.write(paramInt, this.streamPos);
    this.streamPos++;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    flushBits();
    this.cache.write(paramArrayOfByte, paramInt1, paramInt2, this.streamPos);
    this.streamPos += paramInt2;
  }
  
  public long length() {
    try {
      checkClosed();
      return this.cache.getLength();
    } catch (IOException iOException) {
      return -1L;
    } 
  }
  
  public boolean isCached() { return true; }
  
  public boolean isCachedFile() { return false; }
  
  public boolean isCachedMemory() { return true; }
  
  public void close() throws IOException {
    long l = this.cache.getLength();
    seek(l);
    flushBefore(l);
    super.close();
    this.cache.reset();
    this.cache = null;
    this.stream = null;
  }
  
  public void flushBefore(long paramLong) throws IOException {
    long l1 = this.flushedPos;
    super.flushBefore(paramLong);
    long l2 = this.flushedPos - l1;
    this.cache.writeToStream(this.stream, l1, l2);
    this.cache.disposeBefore(this.flushedPos);
    this.stream.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\MemoryCacheImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */