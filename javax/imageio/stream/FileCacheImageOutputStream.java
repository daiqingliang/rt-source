package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;

public class FileCacheImageOutputStream extends ImageOutputStreamImpl {
  private OutputStream stream;
  
  private File cacheFile;
  
  private RandomAccessFile cache;
  
  private long maxStreamPos = 0L;
  
  private final StreamCloser.CloseAction closeAction;
  
  public FileCacheImageOutputStream(OutputStream paramOutputStream, File paramFile) throws IOException {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    if (paramFile != null && !paramFile.isDirectory())
      throw new IllegalArgumentException("Not a directory!"); 
    this.stream = paramOutputStream;
    if (paramFile == null) {
      this.cacheFile = Files.createTempFile("imageio", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile();
    } else {
      this.cacheFile = Files.createTempFile(paramFile.toPath(), "imageio", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile();
    } 
    this.cache = new RandomAccessFile(this.cacheFile, "rw");
    this.closeAction = StreamCloser.createCloseAction(this);
    StreamCloser.addToQueue(this.closeAction);
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.cache.read();
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
    int i = this.cache.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i != -1)
      this.streamPos += i; 
    return i;
  }
  
  public void write(int paramInt) throws IOException {
    flushBits();
    this.cache.write(paramInt);
    this.streamPos++;
    this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    flushBits();
    this.cache.write(paramArrayOfByte, paramInt1, paramInt2);
    this.streamPos += paramInt2;
    this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
  }
  
  public long length() {
    try {
      checkClosed();
      return this.cache.length();
    } catch (IOException iOException) {
      return -1L;
    } 
  }
  
  public void seek(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException(); 
    this.cache.seek(paramLong);
    this.streamPos = this.cache.getFilePointer();
    this.maxStreamPos = Math.max(this.maxStreamPos, this.streamPos);
    this.bitOffset = 0;
  }
  
  public boolean isCached() { return true; }
  
  public boolean isCachedFile() { return true; }
  
  public boolean isCachedMemory() { return false; }
  
  public void close() throws IOException {
    this.maxStreamPos = this.cache.length();
    seek(this.maxStreamPos);
    flushBefore(this.maxStreamPos);
    super.close();
    this.cache.close();
    this.cache = null;
    this.cacheFile.delete();
    this.cacheFile = null;
    this.stream.flush();
    this.stream = null;
    StreamCloser.removeFromQueue(this.closeAction);
  }
  
  public void flushBefore(long paramLong) throws IOException {
    long l1 = this.flushedPos;
    super.flushBefore(paramLong);
    long l2 = this.flushedPos - l1;
    if (l2 > 0L) {
      char c = 'Ȁ';
      byte[] arrayOfByte = new byte[c];
      this.cache.seek(l1);
      while (l2 > 0L) {
        int i = (int)Math.min(l2, c);
        this.cache.readFully(arrayOfByte, 0, i);
        this.stream.write(arrayOfByte, 0, i);
        l2 -= i;
      } 
      this.stream.flush();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\FileCacheImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */