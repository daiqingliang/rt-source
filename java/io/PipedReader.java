package java.io;

public class PipedReader extends Reader {
  boolean closedByWriter = false;
  
  boolean closedByReader = false;
  
  boolean connected = false;
  
  Thread readSide;
  
  Thread writeSide;
  
  private static final int DEFAULT_PIPE_SIZE = 1024;
  
  char[] buffer;
  
  int in = -1;
  
  int out = 0;
  
  public PipedReader(PipedWriter paramPipedWriter) throws IOException { this(paramPipedWriter, 1024); }
  
  public PipedReader(PipedWriter paramPipedWriter, int paramInt) throws IOException {
    initPipe(paramInt);
    connect(paramPipedWriter);
  }
  
  public PipedReader() { initPipe(1024); }
  
  public PipedReader(int paramInt) { initPipe(paramInt); }
  
  private void initPipe(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Pipe size <= 0"); 
    this.buffer = new char[paramInt];
  }
  
  public void connect(PipedWriter paramPipedWriter) throws IOException { paramPipedWriter.connect(this); }
  
  void receive(int paramInt) {
    if (!this.connected)
      throw new IOException("Pipe not connected"); 
    if (this.closedByWriter || this.closedByReader)
      throw new IOException("Pipe closed"); 
    if (this.readSide != null && !this.readSide.isAlive())
      throw new IOException("Read end dead"); 
    this.writeSide = Thread.currentThread();
    while (this.in == this.out) {
      if (this.readSide != null && !this.readSide.isAlive())
        throw new IOException("Pipe broken"); 
      notifyAll();
      try {
        wait(1000L);
      } catch (InterruptedException interruptedException) {
        throw new InterruptedIOException();
      } 
    } 
    if (this.in < 0) {
      this.in = 0;
      this.out = 0;
    } 
    this.buffer[this.in++] = (char)paramInt;
    if (this.in >= this.buffer.length)
      this.in = 0; 
  }
  
  void receive(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    while (--paramInt2 >= 0)
      receive(paramArrayOfChar[paramInt1++]); 
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
    char c = this.buffer[this.out++];
    if (this.out >= this.buffer.length)
      this.out = 0; 
    if (this.in == this.out)
      this.in = -1; 
    return c;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (!this.connected)
      throw new IOException("Pipe not connected"); 
    if (this.closedByReader)
      throw new IOException("Pipe closed"); 
    if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0)
      throw new IOException("Write end dead"); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = read();
    if (i < 0)
      return -1; 
    paramArrayOfChar[paramInt1] = (char)i;
    int j = 1;
    while (this.in >= 0 && --paramInt2 > 0) {
      paramArrayOfChar[paramInt1 + j] = this.buffer[this.out++];
      j++;
      if (this.out >= this.buffer.length)
        this.out = 0; 
      if (this.in == this.out)
        this.in = -1; 
    } 
    return j;
  }
  
  public boolean ready() throws IOException {
    if (!this.connected)
      throw new IOException("Pipe not connected"); 
    if (this.closedByReader)
      throw new IOException("Pipe closed"); 
    if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0)
      throw new IOException("Write end dead"); 
    return !(this.in < 0);
  }
  
  public void close() {
    this.in = -1;
    this.closedByReader = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PipedReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */