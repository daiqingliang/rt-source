package java.io;

public class PipedOutputStream extends OutputStream {
  private PipedInputStream sink;
  
  public PipedOutputStream(PipedInputStream paramPipedInputStream) throws IOException { connect(paramPipedInputStream); }
  
  public PipedOutputStream() {}
  
  public void connect(PipedInputStream paramPipedInputStream) throws IOException {
    if (paramPipedInputStream == null)
      throw new NullPointerException(); 
    if (this.sink != null || paramPipedInputStream.connected)
      throw new IOException("Already connected"); 
    this.sink = paramPipedInputStream;
    paramPipedInputStream.in = -1;
    paramPipedInputStream.out = 0;
    paramPipedInputStream.connected = true;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.sink == null)
      throw new IOException("Pipe not connected"); 
    this.sink.receive(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.sink == null)
      throw new IOException("Pipe not connected"); 
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    this.sink.receive(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void flush() {
    if (this.sink != null)
      synchronized (this.sink) {
        this.sink.notifyAll();
      }  
  }
  
  public void close() {
    if (this.sink != null)
      this.sink.receivedLast(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PipedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */