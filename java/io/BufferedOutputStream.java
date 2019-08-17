package java.io;

public class BufferedOutputStream extends FilterOutputStream {
  protected byte[] buf;
  
  protected int count;
  
  public BufferedOutputStream(OutputStream paramOutputStream) { this(paramOutputStream, 8192); }
  
  public BufferedOutputStream(OutputStream paramOutputStream, int paramInt) {
    super(paramOutputStream);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0"); 
    this.buf = new byte[paramInt];
  }
  
  private void flushBuffer() throws IOException {
    if (this.count > 0) {
      this.out.write(this.buf, 0, this.count);
      this.count = 0;
    } 
  }
  
  public void write(int paramInt) throws IOException {
    if (this.count >= this.buf.length)
      flushBuffer(); 
    this.buf[this.count++] = (byte)paramInt;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 >= this.buf.length) {
      flushBuffer();
      this.out.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    } 
    if (paramInt2 > this.buf.length - this.count)
      flushBuffer(); 
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
    this.count += paramInt2;
  }
  
  public void flush() throws IOException {
    flushBuffer();
    this.out.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\BufferedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */