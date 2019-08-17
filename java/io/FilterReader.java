package java.io;

public abstract class FilterReader extends Reader {
  protected Reader in;
  
  protected FilterReader(Reader paramReader) {
    super(paramReader);
    this.in = paramReader;
  }
  
  public int read() throws IOException { return this.in.read(); }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException { return this.in.read(paramArrayOfChar, paramInt1, paramInt2); }
  
  public long skip(long paramLong) throws IOException { return this.in.skip(paramLong); }
  
  public boolean ready() throws IOException { return this.in.ready(); }
  
  public boolean markSupported() throws IOException { return this.in.markSupported(); }
  
  public void mark(int paramInt) throws IOException { this.in.mark(paramInt); }
  
  public void reset() throws IOException { this.in.reset(); }
  
  public void close() throws IOException { this.in.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilterReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */