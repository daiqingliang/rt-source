package java.io;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class BufferedInputStream extends FilterInputStream {
  private static int DEFAULT_BUFFER_SIZE = 8192;
  
  private static int MAX_BUFFER_SIZE = 2147483639;
  
  private static final AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater.newUpdater(BufferedInputStream.class, byte[].class, "buf");
  
  protected int count;
  
  protected int pos;
  
  protected int markpos = -1;
  
  protected int marklimit;
  
  private InputStream getInIfOpen() throws IOException {
    InputStream inputStream = this.in;
    if (inputStream == null)
      throw new IOException("Stream closed"); 
    return inputStream;
  }
  
  private byte[] getBufIfOpen() throws IOException {
    byte[] arrayOfByte = this.buf;
    if (arrayOfByte == null)
      throw new IOException("Stream closed"); 
    return arrayOfByte;
  }
  
  public BufferedInputStream(InputStream paramInputStream) { this(paramInputStream, DEFAULT_BUFFER_SIZE); }
  
  public BufferedInputStream(InputStream paramInputStream, int paramInt) {
    super(paramInputStream);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0"); 
    this.buf = new byte[paramInt];
  }
  
  private void fill() throws IOException {
    byte[] arrayOfByte = getBufIfOpen();
    if (this.markpos < 0) {
      this.pos = 0;
    } else if (this.pos >= arrayOfByte.length) {
      if (this.markpos > 0) {
        int j = this.pos - this.markpos;
        System.arraycopy(arrayOfByte, this.markpos, arrayOfByte, 0, j);
        this.pos = j;
        this.markpos = 0;
      } else if (arrayOfByte.length >= this.marklimit) {
        this.markpos = -1;
        this.pos = 0;
      } else {
        if (arrayOfByte.length >= MAX_BUFFER_SIZE)
          throw new OutOfMemoryError("Required array size too large"); 
        int j = (this.pos <= MAX_BUFFER_SIZE - this.pos) ? (this.pos * 2) : MAX_BUFFER_SIZE;
        if (j > this.marklimit)
          j = this.marklimit; 
        byte[] arrayOfByte1 = new byte[j];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, this.pos);
        if (!bufUpdater.compareAndSet(this, arrayOfByte, arrayOfByte1))
          throw new IOException("Stream closed"); 
        arrayOfByte = arrayOfByte1;
      } 
    } 
    this.count = this.pos;
    int i = getInIfOpen().read(arrayOfByte, this.pos, arrayOfByte.length - this.pos);
    if (i > 0)
      this.count = i + this.pos; 
  }
  
  public int read() throws IOException {
    if (this.pos >= this.count) {
      fill();
      if (this.pos >= this.count)
        return -1; 
    } 
    return getBufIfOpen()[this.pos++] & 0xFF;
  }
  
  private int read1(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.count - this.pos;
    if (i <= 0) {
      if (paramInt2 >= getBufIfOpen().length && this.markpos < 0)
        return getInIfOpen().read(paramArrayOfByte, paramInt1, paramInt2); 
      fill();
      i = this.count - this.pos;
      if (i <= 0)
        return -1; 
    } 
    int j = (i < paramInt2) ? i : paramInt2;
    System.arraycopy(getBufIfOpen(), this.pos, paramArrayOfByte, paramInt1, j);
    this.pos += j;
    return j;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    InputStream inputStream;
    getBufIfOpen();
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfByte.length - paramInt1 + paramInt2) < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = 0;
    do {
      int j = read1(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j <= 0)
        return (i == 0) ? j : i; 
      i += j;
      if (i >= paramInt2)
        return i; 
      inputStream = this.in;
    } while (inputStream == null || inputStream.available() > 0);
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    getBufIfOpen();
    if (paramLong <= 0L)
      return 0L; 
    long l1 = (this.count - this.pos);
    if (l1 <= 0L) {
      if (this.markpos < 0)
        return getInIfOpen().skip(paramLong); 
      fill();
      l1 = (this.count - this.pos);
      if (l1 <= 0L)
        return 0L; 
    } 
    long l2 = (l1 < paramLong) ? l1 : paramLong;
    this.pos = (int)(this.pos + l2);
    return l2;
  }
  
  public int available() throws IOException {
    int i = this.count - this.pos;
    int j = getInIfOpen().available();
    return (i > Integer.MAX_VALUE - j) ? Integer.MAX_VALUE : (i + j);
  }
  
  public void mark(int paramInt) {
    this.marklimit = paramInt;
    this.markpos = this.pos;
  }
  
  public void reset() throws IOException {
    getBufIfOpen();
    if (this.markpos < 0)
      throw new IOException("Resetting to invalid mark"); 
    this.pos = this.markpos;
  }
  
  public boolean markSupported() { return true; }
  
  public void close() throws IOException {
    byte[] arrayOfByte;
    while ((arrayOfByte = this.buf) != null) {
      if (bufUpdater.compareAndSet(this, arrayOfByte, null)) {
        InputStream inputStream = this.in;
        this.in = null;
        if (inputStream != null)
          inputStream.close(); 
        return;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\BufferedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */