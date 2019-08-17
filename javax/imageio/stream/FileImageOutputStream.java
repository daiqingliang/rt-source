package javax.imageio.stream;

import com.sun.imageio.stream.CloseableDisposerRecord;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import sun.java2d.Disposer;

public class FileImageOutputStream extends ImageOutputStreamImpl {
  private RandomAccessFile raf;
  
  private final Object disposerReferent;
  
  private final CloseableDisposerRecord disposerRecord;
  
  public FileImageOutputStream(File paramFile) throws FileNotFoundException, IOException { this((paramFile == null) ? null : new RandomAccessFile(paramFile, "rw")); }
  
  public FileImageOutputStream(RandomAccessFile paramRandomAccessFile) {
    if (paramRandomAccessFile == null)
      throw new IllegalArgumentException("raf == null!"); 
    this.raf = paramRandomAccessFile;
    this.disposerRecord = new CloseableDisposerRecord(paramRandomAccessFile);
    if (getClass() == FileImageOutputStream.class) {
      this.disposerReferent = new Object();
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    } else {
      this.disposerReferent = new StreamFinalizer(this);
    } 
  }
  
  public int read() throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.raf.read();
    if (i != -1)
      this.streamPos++; 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    checkClosed();
    this.bitOffset = 0;
    int i = this.raf.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i != -1)
      this.streamPos += i; 
    return i;
  }
  
  public void write(int paramInt) throws IOException {
    flushBits();
    this.raf.write(paramInt);
    this.streamPos++;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    flushBits();
    this.raf.write(paramArrayOfByte, paramInt1, paramInt2);
    this.streamPos += paramInt2;
  }
  
  public long length() {
    try {
      checkClosed();
      return this.raf.length();
    } catch (IOException iOException) {
      return -1L;
    } 
  }
  
  public void seek(long paramLong) throws IOException {
    checkClosed();
    if (paramLong < this.flushedPos)
      throw new IndexOutOfBoundsException("pos < flushedPos!"); 
    this.bitOffset = 0;
    this.raf.seek(paramLong);
    this.streamPos = this.raf.getFilePointer();
  }
  
  public void close() throws IOException {
    super.close();
    this.disposerRecord.dispose();
    this.raf = null;
  }
  
  protected void finalize() throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\FileImageOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */