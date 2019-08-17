package java.io;

public class PipedWriter extends Writer {
  private PipedReader sink;
  
  private boolean closed = false;
  
  public PipedWriter(PipedReader paramPipedReader) throws IOException { connect(paramPipedReader); }
  
  public PipedWriter() {}
  
  public void connect(PipedReader paramPipedReader) throws IOException {
    if (paramPipedReader == null)
      throw new NullPointerException(); 
    if (this.sink != null || paramPipedReader.connected)
      throw new IOException("Already connected"); 
    if (paramPipedReader.closedByReader || this.closed)
      throw new IOException("Pipe closed"); 
    this.sink = paramPipedReader;
    paramPipedReader.in = -1;
    paramPipedReader.out = 0;
    paramPipedReader.connected = true;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.sink == null)
      throw new IOException("Pipe not connected"); 
    this.sink.receive(paramInt);
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (this.sink == null)
      throw new IOException("Pipe not connected"); 
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfChar.length - paramInt1 + paramInt2) < 0)
      throw new IndexOutOfBoundsException(); 
    this.sink.receive(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void flush() {
    if (this.sink != null) {
      if (this.sink.closedByReader || this.closed)
        throw new IOException("Pipe closed"); 
      synchronized (this.sink) {
        this.sink.notifyAll();
      } 
    } 
  }
  
  public void close() {
    this.closed = true;
    if (this.sink != null)
      this.sink.receivedLast(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PipedWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */