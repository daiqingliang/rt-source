package java.io;

public class PipedInputStream extends InputStream {
  boolean closedByWriter = false;
  
  boolean connected = false;
  
  Thread readSide;
  
  Thread writeSide;
  
  private static final int DEFAULT_PIPE_SIZE = 1024;
  
  protected static final int PIPE_SIZE = 1024;
  
  protected byte[] buffer;
  
  protected int in = -1;
  
  protected int out = 0;
  
  public PipedInputStream(PipedOutputStream paramPipedOutputStream) throws IOException { this(paramPipedOutputStream, 1024); }
  
  public PipedInputStream(PipedOutputStream paramPipedOutputStream, int paramInt) throws IOException {
    initPipe(paramInt);
    connect(paramPipedOutputStream);
  }
  
  public PipedInputStream() { initPipe(1024); }
  
  public PipedInputStream(int paramInt) { initPipe(paramInt); }
  
  private void initPipe(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Pipe Size <= 0"); 
    this.buffer = new byte[paramInt];
  }
  
  public void connect(PipedOutputStream paramPipedOutputStream) throws IOException { paramPipedOutputStream.connect(this); }
  
  protected void receive(int paramInt) {
    checkStateForReceive();
    this.writeSide = Thread.currentThread();
    if (this.in == this.out)
      awaitSpace(); 
    if (this.in < 0) {
      this.in = 0;
      this.out = 0;
    } 
    this.buffer[this.in++] = (byte)(paramInt & 0xFF);
    if (this.in >= this.buffer.length)
      this.in = 0; 
  }
  
  void receive(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    checkStateForReceive();
    this.writeSide = Thread.currentThread();
    int i = paramInt2;
    while (i > 0) {
      if (this.in == this.out)
        awaitSpace(); 
      int j = 0;
      if (this.out < this.in) {
        j = this.buffer.length - this.in;
      } else if (this.in < this.out) {
        if (this.in == -1) {
          this.in = this.out = 0;
          j = this.buffer.length - this.in;
        } else {
          j = this.out - this.in;
        } 
      } 
      if (j > i)
        j = i; 
      assert j > 0;
      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.in, j);
      i -= j;
      paramInt1 += j;
      this.in += j;
      if (this.in >= this.buffer.length)
        this.in = 0; 
    } 
  }
  
  private void checkStateForReceive() {
    if (!this.connected)
      throw new IOException("Pipe not connected"); 
    if (this.closedByWriter || this.closedByReader)
      throw new IOException("Pipe closed"); 
    if (this.readSide != null && !this.readSide.isAlive())
      throw new IOException("Read end dead"); 
  }
  
  private void awaitSpace() {
    while (this.in == this.out) {
      checkStateForReceive();
      notifyAll();
      try {
        wait(1000L);
      } catch (InterruptedException interruptedException) {
        throw new InterruptedIOException();
      } 
    } 
  }
  
  void receivedLast() {
    this.closedByWriter = true;
    notifyAll();
  }
  
  public int read() throws IOException {
    if (!this.connected)
      throw new IOException("Pipe not connected"); 
    if (this.closedByReader)
      throw new IOException("Pipe closed"); 
    if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0)
      throw new IOException("Write end dead"); 
    this.readSide = Thread.currentThread();
    byte b = 2;
    while (this.in < 0) {
      if (this.closedByWriter)
        return -1; 
      if (this.writeSide != null && !this.writeSide.isAlive() && --b < 0)
        throw new IOException("Pipe broken"); 
      notifyAll();
      try {
        wait(1000L);
      } catch (InterruptedException interruptedException) {
        throw new InterruptedIOException();
      } 
    } 
    byte b1 = this.buffer[this.out++] & 0xFF;
    if (this.out >= this.buffer.length)
      this.out = 0; 
    if (this.in == this.out)
      this.in = -1; 
    return b1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = read();
    if (i < 0)
      return -1; 
    paramArrayOfByte[paramInt1] = (byte)i;
    int j = 1;
    while (this.in >= 0 && paramInt2 > 1) {
      int k;
      if (this.in > this.out) {
        k = Math.min(this.buffer.length - this.out, this.in - this.out);
      } else {
        k = this.buffer.length - this.out;
      } 
      if (k > paramInt2 - 1)
        k = paramInt2 - 1; 
      System.arraycopy(this.buffer, this.out, paramArrayOfByte, paramInt1 + j, k);
      this.out += k;
      j += k;
      paramInt2 -= k;
      if (this.out >= this.buffer.length)
        this.out = 0; 
      if (this.in == this.out)
        this.in = -1; 
    } 
    return j;
  }
  
  public int available() throws IOException { return (this.in < 0) ? 0 : ((this.in == this.out) ? this.buffer.length : ((this.in > this.out) ? (this.in - this.out) : (this.in + this.buffer.length - this.out))); }
  
  public void close() {
    this.closedByReader = true;
    synchronized (this) {
      this.in = -1;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PipedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */