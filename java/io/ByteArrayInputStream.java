package java.io;

public class ByteArrayInputStream extends InputStream {
  protected byte[] buf;
  
  protected int pos;
  
  protected int mark = 0;
  
  protected int count;
  
  public ByteArrayInputStream(byte[] paramArrayOfByte) {
    this.buf = paramArrayOfByte;
    this.pos = 0;
    this.count = paramArrayOfByte.length;
  }
  
  public ByteArrayInputStream(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.buf = paramArrayOfByte;
    this.pos = paramInt1;
    this.count = Math.min(paramInt1 + paramInt2, paramArrayOfByte.length);
    this.mark = paramInt1;
  }
  
  public int read() { return (this.pos < this.count) ? (this.buf[this.pos++] & 0xFF) : -1; }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (this.pos >= this.count)
      return -1; 
    int i = this.count - this.pos;
    if (paramInt2 > i)
      paramInt2 = i; 
    if (paramInt2 <= 0)
      return 0; 
    System.arraycopy(this.buf, this.pos, paramArrayOfByte, paramInt1, paramInt2);
    this.pos += paramInt2;
    return paramInt2;
  }
  
  public long skip(long paramLong) {
    long l = (this.count - this.pos);
    if (paramLong < l)
      l = (paramLong < 0L) ? 0L : paramLong; 
    this.pos = (int)(this.pos + l);
    return l;
  }
  
  public int available() { return this.count - this.pos; }
  
  public boolean markSupported() { return true; }
  
  public void mark(int paramInt) { this.mark = this.pos; }
  
  public void reset() { this.pos = this.mark; }
  
  public void close() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ByteArrayInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */