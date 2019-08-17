package java.io;

public abstract class OutputStream implements Closeable, Flushable {
  public abstract void write(int paramInt) throws IOException;
  
  public void write(byte[] paramArrayOfByte) throws IOException { write(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    for (int i = 0; i < paramInt2; i++)
      write(paramArrayOfByte[paramInt1 + i]); 
  }
  
  public void flush() {}
  
  public void close() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\OutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */