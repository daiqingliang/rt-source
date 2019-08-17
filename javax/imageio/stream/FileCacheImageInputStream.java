package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class FileCacheImageInputStream extends ImageInputStreamImpl {
  private InputStream stream;
  
  private File cacheFile;
  
  private RandomAccessFile cache;
  
  private static final int BUFFER_LENGTH = 1024;
  
  private byte[] buf = new byte[1024];
  
  private long length = 0L;
  
  private boolean foundEOF = false;
  
  private final Object disposerReferent;
  
  private final DisposerRecord disposerRecord;
  
  private final StreamCloser.CloseAction closeAction;
  
  public FileCacheImageInputStream(InputStream paramInputStream, File paramFile) throws IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    if (paramFile != null && !paramFile.isDirectory())
      throw new IllegalArgumentException("Not a directory!"); 
    this.stream = paramInputStream;
    if (paramFile == null) {
      this.cacheFile = Files.createTempFile("imageio", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile();
    } else {
      this.cacheFile = Files.createTempFile(paramFile.toPath(), "imageio", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile();
    } 
    this.cache = new RandomAccessFile(this.cacheFile, "rw");
    this.closeAction = StreamCloser.createCloseAction(this);
    StreamCloser.addToQueue(this.closeAction);
    this.disposerRecord = new StreamDisposerRecord(this.cacheFile, this.cache);
    if (getClass() == FileCacheImageInputStream.class) {
      this.disposerReferent = new Object();
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    } else {
      this.disposerReferent = new StreamFinalizer(this);
    } 
  }
  
  private long readUntil(long paramLong) throws IOException {
    if (paramLong < this.length)
      return paramLong; 
    if (this.foundEOF)
      return this.length; 
    long l = paramLong - this.length;
    this.cache.seek(this.length);
    while (l > 0L) {
      int i = this.stream.read(this.buf, 0, (int)Math.min(l, 1024L));
      if (i == -1) {
        this.foundEOF = true;
        return this.length;
      } 
      this.cache.write(this.buf, 0, i);
      l -= i;
      this.length += i;
    } 
    return paramLong;
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    long l1 = this.streamPos + 1L;
    long l2 = readUntil(l1);
    if (l2 >= l1) {
      this.cache.seek(this.streamPos++);
      return this.cache.read();
    } 
    return -1;
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
    long l = readUntil(this.streamPos + paramInt2);
    paramInt2 = (int)Math.min(paramInt2, l - this.streamPos);
    if (paramInt2 > 0) {
      this.cache.seek(this.streamPos);
      this.cache.readFully(paramArrayOfByte, paramInt1, paramInt2);
      this.streamPos += paramInt2;
      return paramInt2;
    } 
    return -1;
  }
  
  public boolean isCached() { return true; }
  
  public boolean isCachedFile() { return true; }
  
  public boolean isCachedMemory() { return false; }
  
  public void close() throws IOException {
    super.close();
    this.disposerRecord.dispose();
    this.stream = null;
    this.cache = null;
    this.cacheFile = null;
    StreamCloser.removeFromQueue(this.closeAction);
  }
  
  protected void finalize() throws IOException {}
  
  private static class StreamDisposerRecord implements DisposerRecord {
    private File cacheFile;
    
    private RandomAccessFile cache;
    
    public StreamDisposerRecord(File param1File, RandomAccessFile param1RandomAccessFile) {
      this.cacheFile = param1File;
      this.cache = param1RandomAccessFile;
    }
    
    public void dispose() throws IOException {
      if (this.cache != null)
        try {
          this.cache.close();
        } catch (IOException iOException) {
        
        } finally {
          this.cache = null;
        }  
      if (this.cacheFile != null) {
        this.cacheFile.delete();
        this.cacheFile = null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\FileCacheImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */