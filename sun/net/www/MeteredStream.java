package sun.net.www;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.net.ProgressSource;

public class MeteredStream extends FilterInputStream {
  protected boolean closed = false;
  
  protected long expected;
  
  protected long count = 0L;
  
  protected long markedCount = 0L;
  
  protected int markLimit = -1;
  
  protected ProgressSource pi;
  
  public MeteredStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong) {
    super(paramInputStream);
    this.pi = paramProgressSource;
    this.expected = paramLong;
    if (paramProgressSource != null)
      paramProgressSource.updateProgress(0L, paramLong); 
  }
  
  private final void justRead(long paramLong) throws IOException {
    if (paramLong == -1L) {
      if (!isMarked())
        close(); 
      return;
    } 
    this.count += paramLong;
    if (this.count - this.markedCount > this.markLimit)
      this.markLimit = -1; 
    if (this.pi != null)
      this.pi.updateProgress(this.count, this.expected); 
    if (isMarked())
      return; 
    if (this.expected > 0L && this.count >= this.expected)
      close(); 
  }
  
  private boolean isMarked() { return (this.markLimit < 0) ? false : (!(this.count - this.markedCount > this.markLimit)); }
  
  public int read() throws IOException {
    if (this.closed)
      return -1; 
    int i = this.in.read();
    if (i != -1) {
      justRead(1L);
    } else {
      justRead(i);
    } 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.closed)
      return -1; 
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    justRead(i);
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    if (this.closed)
      return 0L; 
    if (this.in instanceof sun.net.www.http.ChunkedInputStream) {
      paramLong = this.in.skip(paramLong);
    } else {
      long l = (paramLong > this.expected - this.count) ? (this.expected - this.count) : paramLong;
      paramLong = this.in.skip(l);
    } 
    justRead(paramLong);
    return paramLong;
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    if (this.pi != null)
      this.pi.finishTracking(); 
    this.closed = true;
    this.in.close();
  }
  
  public int available() throws IOException { return this.closed ? 0 : this.in.available(); }
  
  public void mark(int paramInt) {
    if (this.closed)
      return; 
    super.mark(paramInt);
    this.markedCount = this.count;
    this.markLimit = paramInt;
  }
  
  public void reset() throws IOException {
    if (this.closed)
      return; 
    if (!isMarked())
      throw new IOException("Resetting to an invalid mark"); 
    this.count = this.markedCount;
    super.reset();
  }
  
  public boolean markSupported() { return this.closed ? false : super.markSupported(); }
  
  protected void finalize() throws IOException {
    try {
      close();
      if (this.pi != null)
        this.pi.close(); 
    } finally {
      super.finalize();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\MeteredStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */