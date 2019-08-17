package java.io;

public class FilterInputStream extends InputStream {
  protected FilterInputStream(InputStream paramInputStream) { this.in = paramInputStream; }
  
  public int read() throws IOException { return this.in.read(); }
  
  public int read(byte[] paramArrayOfByte) throws IOException { return read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return this.in.read(paramArrayOfByte, paramInt1, paramInt2); }
  
  public long skip(long paramLong) throws IOException { return this.in.skip(paramLong); }
  
  public int available() throws IOException { return this.in.available(); }
  
  public void close() throws IOException { this.in.close(); }
  
  public void mark(int paramInt) { this.in.mark(paramInt); }
  
  public void reset() throws IOException { this.in.reset(); }
  
  public boolean markSupported() { return this.in.markSupported(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */