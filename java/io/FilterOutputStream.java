package java.io;

public class FilterOutputStream extends OutputStream {
  protected OutputStream out;
  
  public FilterOutputStream(OutputStream paramOutputStream) { this.out = paramOutputStream; }
  
  public void write(int paramInt) throws IOException { this.out.write(paramInt); }
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if ((paramInt1 | paramInt2 | paramArrayOfByte.length - paramInt2 + paramInt1 | paramInt1 + paramInt2) < 0)
      throw new IndexOutOfBoundsException(); 
    for (int i = 0; i < paramInt2; i++)
      write(paramArrayOfByte[paramInt1 + i]); 
  }
  
  public void flush() throws IOException { this.out.flush(); }
  
  public void close() throws IOException {
    try (OutputStream null = this.out) {
      flush();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */